package com.baeldung.ldp.decorator;

// ConcreteComponent: Implementa el contrato
public class CafeSimple implements ICafe {

    @Override
    public double precio() {
        return 0.5;
    }
    @Override
    public String descripcion() {
        return "CafeSimple";
    }
}
