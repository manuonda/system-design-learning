package com.baeldung.ldp.decorator;

import java.time.LocalDateTime;
import java.util.List;


//ConcreteDecorator
public class TimestampingTaskRepositoryDecorator implements TaskRepository {

    private final TaskRepository taskRepository;

    public TimestampingTaskRepositoryDecorator(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public void save(Task task) {

        if(task.getCreatedAt() == null) {
            task.setCreatedAt(LocalDateTime.now());
        }
        task.setUpdatedAt(LocalDateTime.now());
        this.taskRepository.save(task);
    }



    @Override
    public List<Task> findAll() {
        return this.taskRepository.findAll();
    }
}
