package com.monprojet.client;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 12345;

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private Scanner scanner;
    private boolean connecte = false;

    public Client() {
        scanner = new Scanner(System.in);
    }

    public void demarrer() {
        try {
            // 1. Connexion au serveur
            socket = new Socket(SERVER_HOST, SERVER_PORT);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            System.out.println("✅ Connecté au serveur");

            // 2. Menu principal
            while (true) {
                if (!connecte) {
                    menuNonConnecte();
                } else {
                    menuTaches();
                }
            }

        } catch (IOException e) {
            System.out.println("❌ Erreur: " + e.getMessage());
        }
    }

    private void menuNonConnecte() throws IOException {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("MENU PRINCIPAL");
        System.out.println("=".repeat(50));
        System.out.println("1. S'inscrire");
        System.out.println("2. Se connecter");
        System.out.println("3. Quitter");
        System.out.print("Choix: ");

        String choix = scanner.nextLine();

        switch (choix) {
            case "1":
                inscrire();
                break;
            case "2":
                connecter();
                break;
            case "3":
                System.out.println("Au revoir !");
                System.exit(0);
                break;
            default:
                System.out.println("Choix invalide");
        }
    }

    private void inscrire() throws IOException {
        System.out.print("Nom d'utilisateur: ");
        String username = scanner.nextLine();
        System.out.print("Mot de passe: ");
        String password = scanner.nextLine();

        out.println("REGISTER|" + username + "|" + password);

        String reponse = in.readLine();
        System.out.println("Réponse: " + reponse);
    }

    private void connecter() throws IOException {
        System.out.print("Nom d'utilisateur: ");
        String username = scanner.nextLine();
        System.out.print("Mot de passe: ");
        String password = scanner.nextLine();

        out.println("LOGIN|" + username + "|" + password);

        String reponse = in.readLine();
        System.out.println("Réponse: " + reponse);

        if (reponse.startsWith("CONNEXION_REUSSIE")) {
            connecte = true;
            System.out.println("✅ CONNEXION RÉUSSIE !");
        }
    }

    private void menuTaches() throws IOException {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("GESTION DES TÂCHES");
        System.out.println("=".repeat(50));
        System.out.println("1. Créer une tâche");
        System.out.println("2. Voir les tâches");
        System.out.println("3. Modifier statut");
        System.out.println("4. Se déconnecter");
        System.out.print("Choix: ");

        String choix = scanner.nextLine();

        switch (choix) {
            case "1":
                creerTache();
                break;
            case "2":
                out.println("GET_ALL_TASKS");
                String reponse = in.readLine();
                System.out.println("Résultat: " + reponse);
                break;
            case "3":
                modifierStatut();
                break;
            case "4":
                connecte = false;
                System.out.println("Déconnecté");
                break;
            default:
                System.out.println("Choix invalide");
        }
    }

    private void creerTache() throws IOException {
        System.out.print("Titre: ");
        String titre = scanner.nextLine();
        System.out.print("Description: ");
        String description = scanner.nextLine();
        System.out.print("Date (JJ/MM/AAAA): ");
        String date = scanner.nextLine();
        System.out.print("Priorité (BASSE/MOYENNE/HAUTE): ");
        String priorite = scanner.nextLine().toUpperCase();
        System.out.print("Assigné à: ");
        String assigne = scanner.nextLine();

        out.println("CREATE_TASK|" + titre + "|" + description + "|" + date + "|" + priorite + "|" + assigne);

        String reponse = in.readLine();
        System.out.println("Réponse: " + reponse);
    }

    private void modifierStatut() throws IOException {
        System.out.print("ID de la tâche: ");
        String id = scanner.nextLine();
        System.out.print("Nouveau statut: ");
        String statut = scanner.nextLine().toUpperCase();

        out.println("UPDATE_TASK|" + id + "|" + statut);

        String reponse = in.readLine();
        System.out.println("Réponse: " + reponse);
    }

    public static void main(String[] args) {
        new Client().demarrer();
    }
}