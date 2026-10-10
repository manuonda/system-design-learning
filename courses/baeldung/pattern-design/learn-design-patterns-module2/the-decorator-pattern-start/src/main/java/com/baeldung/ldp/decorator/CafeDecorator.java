package com.baeldung.ldp.decorator;

/**
 * Decorator: Una clase que implemente el component y tiene
 * una referencia al concrete component
 * */
public abstract class CafeDecorator implements ICafe{

    // referencia a componente
    private final ICafe cafe;

    public CafeDecorator(ICafe cafe) {
        this.cafe = cafe;
    }

    public double precio() {
        return this.cafe.precio();
    }

    public String descripcion() {
        return this.cafe.descripcion();
    }
}
