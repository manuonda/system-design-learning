package com.baeldung.ldp.prototype;

import java.time.LocalDate;

/**
 * Patter Builder: Clase
 */
public class Task implements Prototype<Task>{

    private String name;
    private String description;
    private LocalDate dueDate;
    private TaskStatus status;


    public Task(Task source) {
        this.name = source.name;
        this.description = source.description;
        this.dueDate = source.dueDate;
        this.status = source.status;
    }

    public Task(String writeBlogPost, LocalDate of, TaskStatus taskStatus) {
       this.name = writeBlogPost;
       this.description = writeBlogPost;
       this.dueDate = of;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    @Override
    public Task copy() {
        return new Task(this);
    }
}
