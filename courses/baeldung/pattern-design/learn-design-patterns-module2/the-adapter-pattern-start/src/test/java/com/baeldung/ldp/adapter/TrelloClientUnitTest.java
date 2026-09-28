package com.baeldung.ldp.adapter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TrelloClientUnitTest {

    private TaskImporter taskImporter;

    @BeforeEach
    void setUP(){
        this.taskImporter = new TrelloTaskAdapter(new TrelloClient());
    }

    /**
     * Observamos que la prueba se realiza a traves de la interfaz
     * TaskImporter , no de TrelloTaskAdapter, el codigo
     * de cliente no necesita conocer los detalles
     * internos del adaptador
     */
    @Test
    void givenTrelloCardo_whenImportTasks_thenFieldsMappedCorrectly(){
        List<Task> tasks = this.taskImporter.importTasks();

        Task task = tasks.get(0);
        assertEquals(task.getTitle(), "Design homepage");
        assertEquals(task.getDescription(), "Create wireframes for the new homepage");
        assertEquals(task.getStatus(),TaskStatus.TO_DO);


    }


}
