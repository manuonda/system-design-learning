package com.baeldung.ldp.factorymethod;


/**
 * TaskCreator
 */
public abstract  class TaskCreator {

    public abstract Task createTask(String taskName);
}
