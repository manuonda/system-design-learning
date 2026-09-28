package com.baeldung.ldp.adapter;

import java.util.List;

/**
 * Interfaz que espera el cliente
 * Target
 */
public interface TaskImporter {

    List<Task> importTasks();
}
