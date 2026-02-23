package com.monprojet.server;

import com.monprojet.models.Task;
import com.monprojet.models.User;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class TaskManager {
    // Instance unique (pattern Singleton)
    private static TaskManager instance;

    // Les collections DEMANDÉES dans le projet
    private Map<Integer, Task> tasksParId;           // Map pour accès rapide par ID
    private List<User> utilisateurs;                   // List pour la liste des users
    private PriorityQueue<Task> tasksParDeadline;      // PriorityQueue pour tri auto

    // Collection supplémentaire utile
    private Map<String, List<Task>> tasksParUtilisateur; // Tâches par user

    // Constructeur PRIVÉ (car c'est un Singleton)
    private TaskManager() {
        // Initialisation des collections thread-safe
        tasksParId = new ConcurrentHashMap<>();
        utilisateurs = Collections.synchronizedList(new ArrayList<>());
        tasksParDeadline = new PriorityQueue<>();
        tasksParUtilisateur = new ConcurrentHashMap<>();

        // Ajouter un admin par défaut
        utilisateurs.add(new User("admin", "admin123", "ADMIN"));

        // Ajouter quelques tâches pour tester
        creerTachesTest();
    }

    // Pour obtenir l'instance unique (Singleton)
    public static synchronized TaskManager getInstance() {
        if (instance == null) {
            instance = new TaskManager();
        }
        return instance;
    }

    // Ajouter des tâches de test
    private void creerTachesTest() {
        creerTask(
                "Apprendre Java",
                "Suivre un tutoriel Java",
                LocalDate.now().plusDays(7),
                "HAUTE",
                "admin"
        );
        creerTask(
                "Faire les courses",
                "Acheter du lait et du pain",
                LocalDate.now().plusDays(2),
                "MOYENNE",
                "admin"
        );
    }

    // === GESTION DES UTILISATEURS ===

    public synchronized boolean inscrireUtilisateur(String username, String password) {
        // Vérifier si l'utilisateur existe déjà
        for (User user : utilisateurs) {
            if (user.getUsername().equals(username)) {
                return false; // existe déjà
            }
        }

        // Créer et ajouter le nouvel utilisateur
        User nouvelUser = new User(username, password, "USER");
        utilisateurs.add(nouvelUser);
        return true;
    }

    public synchronized User connecterUtilisateur(String username, String password) {
        for (User user : utilisateurs) {
            if (user.getUsername().equals(username) &&
                    user.getPassword().equals(password)) {
                return user; // Connexion réussie
            }
        }
        return null; // Échec connexion
    }

    // === GESTION DES TÂCHES ===

    public synchronized Task creerTask(String titre, String description,
                                       LocalDate deadline, String priorite,
                                       String assigneA) {
        Task task = new Task(titre, description, deadline, priorite, assigneA);

        // Ajouter dans la Map par ID
        tasksParId.put(task.getId(), task);

        // Ajouter dans la PriorityQueue (tri automatique)
        tasksParDeadline.add(task);

        // Ajouter dans la Map par utilisateur
        tasksParUtilisateur
                .computeIfAbsent(assigneA, k -> new ArrayList<>())
                .add(task);

        return task;
    }

    public synchronized boolean modifierTask(Task task) {
        if (tasksParId.containsKey(task.getId())) {
            tasksParId.put(task.getId(), task);

            // Mettre à jour la PriorityQueue
            tasksParDeadline.removeIf(t -> t.getId() == task.getId());
            tasksParDeadline.add(task);

            return true;
        }
        return false;
    }

    public synchronized boolean supprimerTask(int taskId, User user) {
        // Seul l'admin peut supprimer
        if (!user.isAdmin()) {
            return false;
        }

        Task task = tasksParId.remove(taskId);
        if (task != null) {
            // Supprimer de la PriorityQueue
            tasksParDeadline.removeIf(t -> t.getId() == taskId);

            // Supprimer des tâches de l'utilisateur
            List<Task> userTasks = tasksParUtilisateur.get(task.getAssigneA());
            if (userTasks != null) {
                userTasks.removeIf(t -> t.getId() == taskId);
            }
            return true;
        }
        return false;
    }

    // === FILTRAGE ET TRI ===

    public synchronized List<Task> filtrerParStatut(String statut) {
        List<Task> resultat = new ArrayList<>();
        for (Task task : tasksParId.values()) {
            if (task.getStatut().equals(statut)) {
                resultat.add(task);
            }
        }
        return resultat;
    }

    public synchronized List<Task> filtrerParPriorite(String priorite) {
        List<Task> resultat = new ArrayList<>();
        for (Task task : tasksParId.values()) {
            if (task.getPriorite().equals(priorite)) {
                resultat.add(task);
            }
        }
        return resultat;
    }

    public synchronized List<Task> getTasksParUtilisateur(String username) {
        return tasksParUtilisateur.getOrDefault(username, new ArrayList<>());
    }

    public synchronized List<Task> getAllTasksTrieParDeadline() {
        return new ArrayList<>(tasksParDeadline);
    }

    public synchronized Task getTask(int id) {
        return tasksParId.get(id);
    }

    public synchronized List<Task> getAllTasks() {
        return new ArrayList<>(tasksParId.values());
    }
}