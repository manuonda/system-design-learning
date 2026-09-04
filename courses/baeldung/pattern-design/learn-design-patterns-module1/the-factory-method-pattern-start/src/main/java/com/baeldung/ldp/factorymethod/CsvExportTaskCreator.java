package com.baeldung.ldp.factorymethod;

public class CsvExportTaskCreator extends TaskCreator{
    @Override
    public Task createTask(String taskName) {
        return new CsvExportTask(taskName);
    }
}
