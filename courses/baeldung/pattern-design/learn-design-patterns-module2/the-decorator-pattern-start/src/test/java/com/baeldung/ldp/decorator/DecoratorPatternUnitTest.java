package com.baeldung.ldp.decorator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

class DecoratorPatternUnitTest {


    @Test
    void givenCachingDecorator_whenFindAllCalledTwice_thenReturnsSameData(){


        // GIVEN: un repositorio base envuelto por el decorador de caché
        // 'base' es el ConcreteComponent (la implementación real, en memoria).
        TaskRepository base = new InMemoryTaskRepository();

        // 'cached' es el ConcreteDecorator: envuelve a 'base' y le suma caché.
        // Ambos se usan como TaskRepository (misma interfaz).
        TaskRepository cached = new CachingTaskRepositoryDecorator(base);

        // GIVEN: una tarea ya guardada a través del decorador.
        // El decorador delega el guardado en 'base' e invalida su caché (cache = null).
        Task task = new Task(1L, "Designed review", TaskStatus.TO_DO);
        cached.save(task);

        // WHEN: se llama a findAll() dos veces seguidas
        // Primera llamada: la caché está vacía, así que el decorador delega
        // en 'base.findAll()' y guarda el resultado en su campo 'cache'.
        List<Task> firstCall = cached.findAll();

        // Segunda llamada: la caché ya tiene datos, así que devuelve
        // la misma lista sin consultar a 'base'.
        List<Task> secondCall = cached.findAll();

        // THEN: la lista contiene la tarea guardada
        Assertions.assertEquals(1, firstCall.size());

        // THEN: ambas llamadas devuelven la misma referencia.
        // assertSame compara con == (no con equals), así que solo pasa
        // si la segunda llamada salió de la caché.
        Assertions.assertSame(firstCall, secondCall);

    }

    @Test
    void givenStackDecorators_whenPrecio_thenSumsAllExtras(){
        //Given una cafe simple envuelto por dos decoradores
        ICafe cafe = new CafeConLecheDecorator(new CafeConLecheDecorator(new CafeSimple()));

        //When
        double precio = cafe.precio();
        String descripcion = cafe.descripcion();

        // Then : 0.5 + 0.2 +0.2
        Assertions.assertEquals(0.9,precio,0.0001);
        Assertions.assertEquals("CafeSimple Con Leche Con Leche", descripcion);

    }

    @Test
    void givenStackDecorators_whenSavedAndFind_thenBothBehaviorsApply(){
        TaskRepository base = new InMemoryTaskRepository(); //Concrete Componente
        TaskRepository repository = new TimestampingTaskRepositoryDecorator(base); //Decorator
        repository =  new CachingTaskRepositoryDecorator(repository); // ConcreteDecorator

        Task task = new Task(1L, "Designed review", TaskStatus.TO_DO);
        repository.save(task);

        Assertions.assertNotNull(task.getUpdatedAt());
        Assertions.assertNotNull(task.getCreatedAt());

        Assertions.assertEquals(1, repository.findAll().size());
        Assertions.assertSame(repository.findAll(), repository.findAll());
    }
}