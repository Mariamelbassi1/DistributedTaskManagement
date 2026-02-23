package com.monprojet.models;

import java.io.Serializable;

// "implements Serializable" permet de sauvegarder l'objet dans un fichier
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    // Les attributs (les données que l'utilisateur a)
    private String username;  // nom d'utilisateur
    private String password;  // mot de passe
    private String role;      // "ADMIN" ou "USER"

    // Le constructeur (comment on crée un nouvel utilisateur)
    public User(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    // Les getters (pour récupérer les valeurs)
    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

    // Les setters (pour modifier les valeurs)
    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRole(String role) {
        this.role = role;
    }

    // Une méthode utilitaire pour vérifier si c'est un admin
    public boolean isAdmin() {
        return "ADMIN".equals(role);
    }

    // Pour afficher proprement l'utilisateur
    @Override
    public String toString() {
        return username + " (" + role + ")";
    }
}