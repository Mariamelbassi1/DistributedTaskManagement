package com.monprojet.server;

import com.monprojet.common.Observer;
import com.monprojet.models.Task;
import com.monprojet.models.User;

import java.io.*;
import java.net.Socket;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class ClientHandler implements Runnable, Observer {
    private Socket socket;
    private TaskManager taskManager;
    private PrintWriter out;
    private BufferedReader in;
    private User utilisateurConnecte;

    public ClientHandler(Socket socket, TaskManager taskManager) {
        this.socket = socket;
        this.taskManager = taskManager;

        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // S'enregistrer comme observateur pour recevoir les notifications
            taskManager.getTaskSubject().registerObserver(this);

        } catch (IOException e) {
            System.out.println("❌ Erreur lors de la création des flux : " + e.getMessage());
        }
    }

    @Override
    public void run() {
        try {
            String requete;
            while ((requete = in.readLine()) != null) {
                System.out.println("📨 Requête reçue : " + requete);
                String reponse = traiterRequete(requete);
                out.println(reponse);
            }
        } catch (IOException e) {
            System.out.println("❌ Client déconnecté : " + e.getMessage());
        } finally {
            // Nettoyage
            taskManager.getTaskSubject().removeObserver(this);
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String traiterRequete(String requete) {
        String[] parties = requete.split("\\|");
        String commande = parties[0];

        switch (commande) {
            case "REGISTER":
                return register(parties);
            case "LOGIN":
                return login(parties);
            case "LOGOUT":
                return logout();
            case "CREATE_TASK":
                return createTask(parties);
            case "GET_ALL_TASKS":
                return getAllTasks();
            case "UPDATE_TASK":
                return updateTask(parties);
            case "DELETE_TASK":
                return deleteTask(parties);
            case "FILTER_BY_STATUS":
                return filterByStatus(parties);
            case "FILTER_BY_PRIORITY":
                return filterByPriority(parties);
            case "FILTER_BY_USER":
                return filterByUser(parties);
            case "GET_ALL_USERS":
                return getAllUsers();
            default:
                return "COMMANDE_INCONNUE";
        }
    }

    private String register(String[] parties) {
        if (parties.length != 3) {
            return "ERREUR: Format invalide";
        }

        String username = parties[1];
        String password = parties[2];

        boolean success = taskManager.inscrireUtilisateur(username, password);

        if (success) {
            System.out.println("✅ Nouvel utilisateur inscrit : " + username);
            return "INSCRIPTION_REUSSIE";
        } else {
            return "INSCRIPTION_ECHOUEE: Nom d'utilisateur déjà pris";
        }
    }

    private String login(String[] parties) {
        if (parties.length != 3) {
            return "ERREUR: Format invalide";
        }

        String username = parties[1];
        String password = parties[2];

        utilisateurConnecte = taskManager.connecterUtilisateur(username, password);

        if (utilisateurConnecte != null) {
            System.out.println("✅ Connexion réussie : " + username);
            return "CONNEXION_REUSSIE|" + utilisateurConnecte.getRole();
        } else {
            return "CONNEXION_ECHOUEE: Nom d'utilisateur ou mot de passe incorrect";
        }
    }

    private String logout() {
        if (utilisateurConnecte != null) {
            System.out.println("✅ Déconnexion : " + utilisateurConnecte.getUsername());
            utilisateurConnecte = null;
        }
        return "DECONNEXION_REUSSIE";
    }

    private String createTask(String[] parties) {
        if (utilisateurConnecte == null) {
            return "ERREUR: Vous devez être connecté";
        }

        if (parties.length != 6) {
            return "ERREUR: Format invalide";
        }

        try {
            String titre = parties[1];
            String description = parties[2];
            String dateStr = parties[3];
            String priorite = parties[4];
            String assigneA = parties[5];

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate deadline = LocalDate.parse(dateStr, formatter);

            Task task = taskManager.creerTask(titre, description, deadline, priorite, assigneA);

            System.out.println("✅ Tâche créée : " + task.getId() + " - " + titre);
            return "TACHE_CREEE|" + task.getId();

        } catch (DateTimeParseException e) {
            return "ERREUR: Format de date invalide (utilisez JJ/MM/AAAA)";
        }
    }

    private String getAllTasks() {
        if (utilisateurConnecte == null) {
            return "ERREUR: Vous devez être connecté";
        }

        List<Task> tasks = taskManager.getAllTasksTrieParDeadline();

        if (tasks.isEmpty()) {
            return "AUCUNE_TACHE";
        }

        StringBuilder sb = new StringBuilder("LISTE_TACHES");
        for (Task task : tasks) {
            sb.append("|").append(task.toString());
        }

        return sb.toString();
    }

    private String updateTask(String[] parties) {
        if (utilisateurConnecte == null) {
            return "ERREUR: Vous devez être connecté";
        }

        if (parties.length != 3) {
            return "ERREUR: Format invalide";
        }

        try {
            int taskId = Integer.parseInt(parties[1]);
            String nouveauStatut = parties[2];

            Task task = taskManager.getTask(taskId);
            if (task == null) {
                return "ERREUR: Tâche non trouvée";
            }

            task.setStatut(nouveauStatut);
            taskManager.modifierTask(task);

            System.out.println("✅ Tâche " + taskId + " modifiée : statut = " + nouveauStatut);
            return "TACHE_MODIFIEE";

        } catch (NumberFormatException e) {
            return "ERREUR: ID de tâche invalide";
        }
    }

    private String deleteTask(String[] parties) {
        if (utilisateurConnecte == null) {
            return "ERREUR: Vous devez être connecté";
        }

        if (!utilisateurConnecte.isAdmin()) {
            return "ERREUR: Seuls les administrateurs peuvent supprimer des tâches";
        }

        if (parties.length != 2) {
            return "ERREUR: Format invalide";
        }

        try {
            int taskId = Integer.parseInt(parties[1]);

            boolean success = taskManager.supprimerTask(taskId, utilisateurConnecte);

            if (success) {
                System.out.println("✅ Tâche " + taskId + " supprimée par admin");
                return "TACHE_SUPPRIMEE";
            } else {
                return "ERREUR: Tâche non trouvée";
            }

        } catch (NumberFormatException e) {
            return "ERREUR: ID de tâche invalide";
        }
    }

    private String filterByStatus(String[] parties) {
        if (utilisateurConnecte == null) {
            return "ERREUR: Vous devez être connecté";
        }

        if (parties.length != 2) {
            return "ERREUR: Format invalide";
        }

        String statut = parties[1];
        List<Task> tasks = taskManager.filtrerParStatut(statut);

        if (tasks.isEmpty()) {
            return "AUCUNE_TACHE";
        }

        StringBuilder sb = new StringBuilder("RESULTAT_FILTRE");
        for (Task task : tasks) {
            sb.append("|").append(task.toString());
        }

        return sb.toString();
    }

    private String filterByPriority(String[] parties) {
        if (utilisateurConnecte == null) {
            return "ERREUR: Vous devez être connecté";
        }

        if (parties.length != 2) {
            return "ERREUR: Format invalide";
        }

        String priorite = parties[1];
        List<Task> tasks = taskManager.filtrerParPriorite(priorite);

        if (tasks.isEmpty()) {
            return "AUCUNE_TACHE";
        }

        StringBuilder sb = new StringBuilder("RESULTAT_FILTRE");
        for (Task task : tasks) {
            sb.append("|").append(task.toString());
        }

        return sb.toString();
    }

    private String filterByUser(String[] parties) {
        if (utilisateurConnecte == null) {
            return "ERREUR: Vous devez être connecté";
        }

        if (parties.length != 2) {
            return "ERREUR: Format invalide";
        }

        String username = parties[1];
        List<Task> tasks = taskManager.getTasksParUtilisateur(username);

        if (tasks.isEmpty()) {
            return "AUCUNE_TACHE";
        }

        StringBuilder sb = new StringBuilder("RESULTAT_FILTRE");
        for (Task task : tasks) {
            sb.append("|").append(task.toString());
        }

        return sb.toString();
    }

    private String getAllUsers() {
        if (utilisateurConnecte == null || !utilisateurConnecte.isAdmin()) {
            return "ERREUR: Accès réservé aux administrateurs";
        }

        List<User> users = taskManager.getAllUsers();

        StringBuilder sb = new StringBuilder("LISTE_UTILISATEURS");
        for (User user : users) {
            sb.append("|").append(user.toString());
        }

        return sb.toString();
    }

    @Override
    public void update(Task task, String action) {
        // Envoyer la notification au client
        if (utilisateurConnecte != null) {
            out.println("NOTIFICATION: La tâche " + task.getId() +
                    " [" + task.getTitre() + "] a été " + action);
        }
    }
}