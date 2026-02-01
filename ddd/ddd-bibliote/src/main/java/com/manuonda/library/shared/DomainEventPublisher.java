package com.manuonda.library.shared;

import java.util.Collection;

/**
 *
 */
public interface DomainEventPublisher {
  void publish(Collection<DomainEvent> events);
}
