package com.java.practice.sealed.shapes;

public record Circle(double radius) implements Shape{


    @Override
    public double area(){
        return Math.PI * radius * radius;
    }

    @Override
    public double perimeter() {
        return 2 * Math.PI * radius;
    }
}
