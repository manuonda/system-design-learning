package com.tutorial.two.modulith.notifications.internal;


import com.tutorial.two.modulith.events.RsvpReceived;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

@Component
public class NotificationListener {

   private static final Logger log = LoggerFactory.getLogger(NotificationListener.class);

   @Value("${notifications.fail}")
   boolean failOnPurpose;


   private final ConfirmationRepository confirmationRepository;

   public NotificationListener(ConfirmationRepository confirmationRepository) {
      this.confirmationRepository = confirmationRepository;
   }


   @ApplicationModuleListener
   void on(RsvpReceived event) {
      if(failOnPurpose) {
         throw new RuntimeException("Failing on purpose");
      }
      Confirmation saved = confirmationRepository.save(Confirmation.of(event.meetupId(),event.email()));
      log.info("Confirmation saved: id={}, email={}", event.meetupId(), event.email());
   }
}
