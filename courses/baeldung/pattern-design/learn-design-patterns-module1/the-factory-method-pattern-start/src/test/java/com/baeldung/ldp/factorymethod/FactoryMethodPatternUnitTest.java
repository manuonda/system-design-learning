package com.baeldung.ldp.factorymethod;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class FactoryMethodPatternUnitTest {



    @DisplayName("Given Pdf ExportTaskCreator Task Create")
   @Test
    void givenPdfExportTaskCreator_whenCreateTask_thenReturnsTask(){
       TaskService taskService = new TaskService(new PdfExportTaskCreator());

       Task task = taskService.createTask("Task1");

       Assertions.assertInstanceOf(PdfExportTask.class, task);
       Assertions.assertNotNull(task);
       Assertions.assertEquals("Task1", task.getName());

   }


   @Test
    void givenCsvExportTaskCreator_whenCreateTask_thenReturnsTask(){
        TaskService taskService = new TaskService(new CsvExportTaskCreator());
        Task task = taskService.createTask("Task1");

        Assertions.assertInstanceOf(CsvExportTask.class, task);
        Assertions.assertNotNull(task);
        Assertions.assertEquals("Task1", task.getName());
   }


}
