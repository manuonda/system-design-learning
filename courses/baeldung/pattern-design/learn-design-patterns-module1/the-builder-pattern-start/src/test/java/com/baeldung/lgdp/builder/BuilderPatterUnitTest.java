package com.baeldung.lgdp.builder;

import com.baeldung.ldp.builder.Priority;
import com.baeldung.ldp.builder.Task;
import com.baeldung.ldp.builder.TaskStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class BuilderPatterUnitTest {


    @Test
    void givenAllFieldsSet_whenBuild_thenTaskHasAllValues(){

        Task task = new Task.Builder("Design login page")
                .id(1L)
                .description("Create wireframes and implement the login UI with OAuth support")
                .dueDate(LocalDate.of(2050,6,1))
                .status(TaskStatus.IN_PROGRESS)
                .priority(Priority.HIGH)
                .assignee("Alice")
                .project("Website Redesign")
                .estimatedHours(12.0)
                .buid();
    }


    @Test
    void givenOnlyRequiredField_whenBuild_thenDefaultsApplied(){
        Task task = new Task.Builder("Quick task").buid();
        assertEquals("Quick task", task.getName());
        assertEquals(TaskStatus.TO_DO, task.getStatus());
        assertEquals(Priority.MEDIUM, task.getPriority());
        assertNull(task.getId());
        assertNull(task.getDescription());
        assertNull(task.getDueDate());
        assertNull(task.getAssignee());
        assertNull(task.getProject());
        assertNull(task.getEstimatedHours());
    }

}
