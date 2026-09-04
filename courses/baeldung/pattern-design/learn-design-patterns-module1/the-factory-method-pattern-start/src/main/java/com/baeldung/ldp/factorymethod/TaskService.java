package com.baeldung.ldp.factorymethod;

public class TaskService {

    private final TaskCreator taskCreator;

    public TaskService(TaskCreator taskCreator) {
        this.taskCreator = taskCreator;
    }

}
