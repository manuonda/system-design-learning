package com.java.practice.sealed.shapes;

import com.java.practice.sealed.SealedWithPatterns;

/**
 * Interfaz sellada (sealed) que define la jerarquía cerrada de figuras geométricas.
 *
 * <p>Al declarar la interfaz como {@code sealed} y listar explícitamente los subtipos
 * permitidos con {@code permits}, el compilador garantiza que no existan implementaciones
 * desconocidas en tiempo de compilación. Esto hace que el {@code switch} de pattern
 * matching en {@link SealedWithPatterns} sea exhaustivo sin
 * necesidad de un caso {@code default}, evitando errores silenciosos cuando se añade
 * una nueva figura.
 *
 * <p>Subtypes permitidos: {@link Circle}, {@link Rectangle}, {@link Triangle}.
 */
public sealed interface Shape
  permits Circle, Rectangle, Triangle {

    double area();
    double perimeter();

}
