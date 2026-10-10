package com.baeldung.ldp.decorator;


import java.util.List;

/***
 * Decorator: Una clase que implementa Component, mantiene una referencia a un
 * component y delete en el mientras agregar comportamiento
 */
public class CachingTaskRepositoryDecorator  implements
TaskRepository {
    private final TaskRepository wrapped;
    private List<Task> cache;

    public CachingTaskRepositoryDecorator(TaskRepository wrapped) {
        this.wrapped = wrapped;
    }


    @Override
    public void save(Task task) {
        this.wrapped.save(task);
        cache = null;
    }


    @Override
    public List<Task> findAll() {
        if (cache == null) {
            this.cache = this.wrapped.findAll();
        }
        return cache;
    }
}
