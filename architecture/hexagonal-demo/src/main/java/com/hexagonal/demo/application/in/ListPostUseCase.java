package com.hexagonal.demo.application.in;

import com.hexagonal.demo.domain.model.Post;

import java.util.List;

/**
 * Puerto de entrada para el caso de uso de obtener la lista de
 * de todos los posts : funcionalidad que permite trabajar
 *
 */
public interface ListPostUseCase {
    List<Post> list();
}
