package com.hexagonal.kafka.models;

// Mensaje que viaja por Kafka. type indica la operacion (CREATE/UPDATE/DELETE),
// id identifica la entidad afectada (null en creacion), body contiene los datos.
public record Command<T>(String type, Long id, T body) {
}
