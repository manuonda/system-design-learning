package com.java.practice.sealed;

import com.java.practice.sealed.shapes.Circle;
import com.java.practice.sealed.shapes.Rectangle;
import com.java.practice.sealed.shapes.Shape;
import com.java.practice.sealed.shapes.Triangle;

import java.util.List;

public class SealedClasDemo {

    static void main(String[] args) {
        Shape circle = new Circle(5);
        Shape rectangle = new Rectangle(4,5);
        Shape triangle = new Triangle(3,4,5);

        var shapes = List.of(circle, rectangle, triangle);
        shapes.forEach(shape -> {
            System.out.println(shape.toString());
        });
    }
}
