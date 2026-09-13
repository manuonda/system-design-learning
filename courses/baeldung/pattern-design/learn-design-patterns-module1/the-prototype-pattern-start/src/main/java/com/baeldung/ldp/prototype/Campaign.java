package com.baeldung.ldp.prototype;

import java.util.ArrayList;
import java.util.List;

public class Campaign implements Prototype<Campaign> {

    private Long id;
    private String name;
    private String description;
    private List<Task> tasks;

    public Campaign(String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.tasks = new ArrayList<>();
    }



    public Campaign(Campaign source) {
        this.name = source.name;
        this.description = source.description;
        this.tasks = source.tasks.stream()
                .map(Task::copy)
                .toList();
    }



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    public void addTask(Task task) {
        this.tasks.add(task);
    }


    @Override
    public Campaign copy() {
        return new Campaign(this);
    }
}
