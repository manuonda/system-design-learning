package com.baeldung.ldp.decorator;


import java.util.List;

/***
 * Decorator
 */
public abstract class CachingTaskRepositoryDecorator  implements
TaskRepository {
    private final TaskRepository wrapped;
    private List<Task> cache;

    public CachingTaskRepositoryDecorator(TaskRepository wrapped) {
        this.wrapped = wrapped;
    }


}
