package com.monprojet.common;

import com.monprojet.models.Task;

public interface Observer {
    void update(Task task, String action); // Notifié quand une tâche change
}
