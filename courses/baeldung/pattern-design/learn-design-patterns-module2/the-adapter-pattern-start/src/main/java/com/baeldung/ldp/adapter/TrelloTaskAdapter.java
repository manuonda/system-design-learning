package com.baeldung.ldp.adapter;

import javax.smartcardio.Card;
import java.util.List;

/**
 * El adaptador entre 2 mundos
 * Adapter
 */
public class TrelloTaskAdapter implements TaskImporter{


    //Adaptee
    private final TrelloClient trelloClient;

    public TrelloTaskAdapter(TrelloClient trelloClient) {
        this.trelloClient = trelloClient;
    }

    @Override
    public List<Task> importTasks() {
        return this.trelloClient.fetchCards()
                .stream()
                .map(this::toTask)
                .toList();
    }

    private Task toTask(TrelloCard trelloCard) {
        return new Task(
                trelloCard.getName(),
                trelloCard.getDescription(),
                mapStatus(trelloCard.getListName()),
                trelloCard.getCardId(),
                "NORMAL"
        );
    }

    private TaskStatus mapStatus(String listName) {
        return switch (listName) {
            case "To Do" -> TaskStatus.TO_DO;
            case "In Progress" -> TaskStatus.IN_PROGRESS;
            case "Done" -> TaskStatus.DONE;
            default -> TaskStatus.TO_DO;
        };
    }
}

