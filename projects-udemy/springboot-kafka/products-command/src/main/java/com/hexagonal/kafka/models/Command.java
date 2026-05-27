package com.hexagonal.kafka.models;

/**
 * DTO genérico para comandos que se envían a Kafka. Contiene el tipo de operación (CREATE, UPDATE, DELETE),
 * un ID opcional para identificar la entidad afectada (null en caso de creación) y
 * @param type
 * @param id
 * @param body
 * @param <T>
 */
public record Command<T>(String type, Long id , T body) {


}
