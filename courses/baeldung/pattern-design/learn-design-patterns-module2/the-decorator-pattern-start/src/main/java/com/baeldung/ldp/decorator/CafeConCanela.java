package com.baeldung.ldp.decorator;

// ConcreteDecorator
public class CafeConCanela extends CafeDecorator {

    public CafeConCanela(ICafe cafe) {
        super(cafe);   // pasa el ICafe que envuelve al Decorator abstracto
    }

    @Override
    public double precio() {
        return super.precio() + 0.1;
    }

    @Override
    public String descripcion() {
        return super.descripcion() + " con canela";
    }
}