package com.monprojet.models;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Task implements Serializable, Comparable<Task> {
    private static final long serialVersionUID = 1L;

    // static = partagé par toutes les tâches
    private static int compteurId = 1;

    // Les attributs d'une tâche
    private int id;
    private String titre;
    private String description;
    private LocalDate dateCreation;
    private LocalDate deadline;
    private String priorite;  // "BASSE", "MOYENNE", "HAUTE"
    private String statut;     // "A_FAIRE", "EN_COURS", "TERMINE"
    private String assigneA;   // nom de l'utilisateur assigné

    // Constructeur
    public Task(String titre, String description, LocalDate deadline,
                String priorite, String assigneA) {
        this.id = compteurId++;  // ID auto-incrémenté
        this.titre = titre;
        this.description = description;
        this.dateCreation = LocalDate.now();  // date du jour
        this.deadline = deadline;
        this.priorite = priorite;
        this.statut = "A_FAIRE";  // par défaut
        this.assigneA = assigneA;
    }

    // Getters et Setters
    public int getId() { return id; }

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getDateCreation() { return dateCreation; }

    public LocalDate getDeadline() { return deadline; }
    public void setDeadline(LocalDate deadline) { this.deadline = deadline; }

    public String getPriorite() { return priorite; }
    public void setPriorite(String priorite) { this.priorite = priorite; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    public String getAssigneA() { return assigneA; }
    public void setAssigneA(String assigneA) { this.assigneA = assigneA; }

    // Pour trier les tâches par deadline (date limite)
    @Override
    public int compareTo(Task autre) {
        return this.deadline.compareTo(autre.deadline);
    }

    // Pour afficher une tâche proprement
    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return String.format("[%d] %s | %s | %s | Assigné: %s | Deadline: %s",
                id, titre, priorite, statut, assigneA,
                deadline.format(formatter));
    }
}