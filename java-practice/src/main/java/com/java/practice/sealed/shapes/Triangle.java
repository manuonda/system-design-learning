package com.java.practice.sealed.shapes;

public record Triangle(double a, double b, double c) implements Shape {


    @Override
    public double area() {
        double s = (a + b + c) / 2;
        return Math.sqrt(s * (s - a) * (s - b) * (s - c));
    }

    @Override
    public double perimeter() {
        return a +  b + c;
    }
}
