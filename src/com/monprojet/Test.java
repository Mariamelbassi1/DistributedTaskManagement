package com.monprojet;

import com.monprojet.models.Task;
import com.monprojet.models.User;
import com.monprojet.server.TaskManager;

import java.time.LocalDate;
import java.util.List;

public class Test {
    public static void main(String[] args) {
        System.out.println("=== TEST DE NOTRE SYSTÈME ===\n");

        // Récupérer le gestionnaire (Singleton)
        TaskManager manager = TaskManager.getInstance();

        // Tester la connexion
        System.out.println("Test connexion admin:");
        User admin = manager.connecterUtilisateur("admin", "admin123");
        if (admin != null) {
            System.out.println("✅ Connexion réussie: " + admin);
        } else {
            System.out.println("❌ Échec connexion");
        }

        // Tester l'inscription
        System.out.println("\nTest inscription nouvel utilisateur:");
        boolean inscrit = manager.inscrireUtilisateur("etudiant", "pass123");
        if (inscrit) {
            System.out.println("✅ Inscription réussie");
        } else {
            System.out.println("❌ Échec inscription");
        }

        // Tester la création de tâche
        System.out.println("\nTest création tâche:");
        Task nouvelleTask = manager.creerTask(
                "Réviser pour l'examen",
                "Chapitres 1 à 5",
                LocalDate.now().plusDays(10),
                "HAUTE",
                "etudiant"
        );
        System.out.println("✅ Tâche créée: " + nouvelleTask);

        // Afficher toutes les tâches
        System.out.println("\nToutes les tâches (triées par deadline):");
        List<Task> toutesLesTasks = manager.getAllTasksTrieParDeadline();
        for (Task task : toutesLesTasks) {
            System.out.println("  " + task);
        }

        // Tester le filtrage
        System.out.println("\nTâches de l'étudiant:");
        List<Task> tasksEtudiant = manager.getTasksParUtilisateur("etudiant");
        for (Task task : tasksEtudiant) {
            System.out.println("  " + task);
        }
    }
}
