package com.baeldung.ldp.factorymethod;

public class PdfExportTaskCreator extends TaskCreator {

    @Override
    public Task createTask(String taskName) {
        return new PdfExportTask(taskName);
    }
}
