package com.baeldung.ldp.decorator;

import java.util.List;


// Component: La interfaz que define el contrato
public interface TaskRepository {

    void save(Task task);
    List<Task> findAll();
}
