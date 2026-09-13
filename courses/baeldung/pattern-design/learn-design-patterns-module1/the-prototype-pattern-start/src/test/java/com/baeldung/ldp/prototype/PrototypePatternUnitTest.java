package com.baeldung.ldp.prototype;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

public class PrototypePatternUnitTest {

    @Test
    public void givenCampaign_whenCopy_thenCopyHasSameData(){
        Campaign original = new Campaign("Spring Launch","Q1 Campaign");
        original.addTask(new Task("Write blog post ", LocalDate.of(2025,1,11), TaskStatus.TO_DO));
        original.addTask(new Task("Send newsLetter", LocalDate.of(2025,1,11), TaskStatus.TO_DO));

        Campaign copy = original.copy();

        Assertions.assertEquals(original.getName(), copy.getName());
        Assertions.assertEquals(original.getDescription(), copy.getDescription());
        Assertions.assertEquals(original.getTasks().size(), copy.getTasks().size());

    }


    @Test
    public void givenCopiedCampaign_whenModifyCopyTask_thenOriginalUnchanged(){
        Campaign original = new Campaign("Spring Launch","Q1 Campaign");
        original.addTask(new Task("Write blog post", LocalDate.of(2025,1,11), TaskStatus.TO_DO));

        Campaign copy = original.copy();
        copy.getTasks().get(0).setName("Write blog post 2");

        Assertions.assertEquals("Write blog post",original.getTasks().get(0).getName());
        Assertions.assertNotSame(original.getTasks().get(0).getName(),copy.getTasks().get(0).getName());
    }

}
