package com.java.practice.sealed.shapes;

public record Rectangle(double width, double height) implements
 Shape{

    @Override
    public double area() {
        return width * height;
    }

    @Override
    public double perimeter() {
        return 2 * width * height;
    }
}
