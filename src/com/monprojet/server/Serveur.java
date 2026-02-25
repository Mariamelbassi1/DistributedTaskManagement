package com.monprojet.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDate;
import java.util.List;

public class Serveur {
    private static final int PORT = 12345;
    private TaskManager taskManager;
    private boolean estActif = true;

    public Serveur() {
        taskManager = TaskManager.getInstance();
    }

    public void demarrer() {
        System.out.println("🚀 SERVEUR DE GESTION DE TÂCHES");
        System.out.println("=".repeat(50));

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("✅ Serveur démarré sur le port " + PORT);
            System.out.println("📋 En attente de connexions clients...");
            System.out.println("=".repeat(50));

            while (estActif) {
                try {
                    // Attendre qu'un client se connecte
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("✅ Nouveau client connecté : " +
                            clientSocket.getInetAddress().getHostAddress());

                    // Créer un thread pour gérer ce client
                    ClientHandler handler = new ClientHandler(clientSocket, taskManager);
                    new Thread(handler).start();

                } catch (IOException e) {
                    System.out.println("❌ Erreur avec un client : " + e.getMessage());
                }
            }

        } catch (IOException e) {
            System.out.println("❌ Erreur du serveur : " + e.getMessage());
        }
    }

    public void arreter() {
        estActif = false;
    }

    public static void main(String[] args) {
        Serveur serveur = new Serveur();
        serveur.demarrer();
    }
}