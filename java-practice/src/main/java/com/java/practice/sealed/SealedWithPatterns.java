package com.java.practice.sealed;

import com.java.practice.sealed.shapes.Circle;
import com.java.practice.sealed.shapes.Rectangle;
import com.java.practice.sealed.shapes.Shape;
import com.java.practice.sealed.shapes.Triangle;

/**
 *
 */
public class SealedWithPatterns {

    static String describe(Shape shape){
        return switch (shape) {
            case Circle c -> "Circle " + c.radius();
            case Rectangle r -> "Rectangle" + r.toString();
            case Triangle  t -> "Triangle " + t.toString();
        };
    }

    static void main(String[] args) {

        System.out.print(describe(new Circle(20)));
        System.out.print(describe(new Rectangle(5,5)));
        System.out.print(describe(new Triangle(3,4,5)));
    }
}
