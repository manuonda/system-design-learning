package com.hexagonal.kafka.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import com.hexagonal.kafka.entities.Product;

/**
 * Repositorio de productos
 * @author dgarcia
 * @version 1.0
 * @since 1.0
 * @see Product
 * @see JpaRepository
 * @see Long
 * @see ProductRepository
 */
public interface ProductRepository extends CrudRepository<Product,Long>{

}
