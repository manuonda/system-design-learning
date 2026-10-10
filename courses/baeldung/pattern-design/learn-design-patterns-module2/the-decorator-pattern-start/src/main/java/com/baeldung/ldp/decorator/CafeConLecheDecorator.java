package com.baeldung.ldp.decorator;

//ConcreteDecorator
public class CafeConLecheDecorator implements  ICafe {

    private final ICafe cafe;

    public CafeConLecheDecorator(ICafe cafe) {
        this.cafe = cafe;
    }

    public double precio() {return cafe.precio() + 0.2 ; }
    public String descripcion() {return cafe.descripcion() +" Con Leche"; };
}
