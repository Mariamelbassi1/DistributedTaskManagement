package com.monprojet.server;

import com.monprojet.common.Observer;
import com.monprojet.common.Subject;
import com.monprojet.models.Task;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class TaskSubject implements Subject {
    // CopyOnWriteArrayList est thread-safe pour les observers
    private List<Observer> observers = new CopyOnWriteArrayList<>();

    @Override
    public void registerObserver(Observer observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
            System.out.println("✅ Nouvel observateur enregistré. Total: " + observers.size());
        }
    }

    @Override
    public void removeObserver(Observer observer) {
        observers.remove(observer);
        System.out.println("✅ Observateur retiré. Restants: " + observers.size());
    }

    @Override
    public void notifyObservers(Object object, String action) {
        // Vérifier que l'objet est bien une Task
        if (!(object instanceof Task)) {
            System.out.println("❌ Erreur: l'objet n'est pas une Task");
            return;
        }

        Task task = (Task) object;
        System.out.println("📢 Notification à " + observers.size() + " observateurs - Action: " + action);

        // Notifier tous les observateurs
        for (Observer observer : observers) {
            observer.update(task, action);
        }
    }

    // Méthode utilitaire pour voir les observateurs
    public int getNbObservers() {
        return observers.size();
    }

    // Méthode pour vérifier si un observer est déjà enregistré
    public boolean hasObserver(Observer observer) {
        return observers.contains(observer);
    }

    // Méthode pour nettoyer tous les observateurs (utile pour les tests)
    public void clearAllObservers() {
        observers.clear();
        System.out.println("🧹 Tous les observateurs ont été retirés");
    }
}