package com.monprojet;

import com.monprojet.models.User;
import com.monprojet.server.TaskManager;

public class TestUtilisateurs {
    public static void main(String[] args) {
        System.out.println("🧪 TEST DE LA GESTION DES UTILISATEURS\n");

        // Récupérer l'instance du TaskManager
        TaskManager manager = TaskManager.getInstance();

        // Test 1: Connexion admin par défaut
        System.out.println("Test 1: Connexion admin");
        User admin = manager.connecterUtilisateur("admin", "admin123");
        if (admin != null && admin.isAdmin()) {
            System.out.println("  ✅ Admin connecté: " + admin);
        } else {
            System.out.println("  ❌ Échec connexion admin");
        }

        // Test 2: Inscription nouvel utilisateur
        System.out.println("\nTest 2: Inscription nouvel utilisateur");
        boolean inscrit = manager.inscrireUtilisateur("alice", "password123");
        if (inscrit) {
            System.out.println("  ✅ Alice inscrite avec succès");
        } else {
            System.out.println("  ❌ Échec inscription Alice");
        }

        // Test 3: Inscription même utilisateur (doit échouer)
        System.out.println("\nTest 3: Inscription Alice encore (doit échouer)");
        inscrit = manager.inscrireUtilisateur("alice", "autre");
        if (!inscrit) {
            System.out.println("  ✅ Échec comme prévu (Alice existe déjà)");
        } else {
            System.out.println("  ❌ Problème: inscription réussie alors qu'Alice existe");
        }

        // Test 4: Connexion Alice
        System.out.println("\nTest 4: Connexion Alice");
        User alice = manager.connecterUtilisateur("alice", "password123");
        if (alice != null && !alice.isAdmin()) {
            System.out.println("  ✅ Alice connectée: " + alice);
        } else {
            System.out.println("  ❌ Échec connexion Alice");
        }

        // Test 5: Connexion avec mauvais mot de passe
        System.out.println("\nTest 5: Connexion avec mauvais mot de passe");
        User faux = manager.connecterUtilisateur("alice", "faux");
        if (faux == null) {
            System.out.println("  ✅ Échec connexion comme prévu (mauvais mot de passe)");
        } else {
            System.out.println("  ❌ Problème: connexion réussie avec mauvais mot de passe");
        }

        // Test 6: Liste tous les utilisateurs
        System.out.println("\nTest 6: Liste des utilisateurs");
        System.out.println("  " + manager.getAllUsers());

        System.out.println("\n✅ TESTS TERMINÉS");
    }
}