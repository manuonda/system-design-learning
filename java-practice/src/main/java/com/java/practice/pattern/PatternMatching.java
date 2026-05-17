package com.java.practice.pattern;

import com.java.practice.sealed.shapes.Circle;
import com.java.practice.sealed.shapes.Rectangle;
import com.java.practice.sealed.shapes.Shape;
import com.java.practice.sealed.shapes.Triangle;

public class PatternMatching {

     static void main(String args[]) {
        System.out.println(describe("Hola mundo"));
        var holaMundo = "Hola mundo";
        if(holaMundo instanceof String s) {
            System.out.println(s);
        }

         System.out.println(classifyNumber(10));
         System.out.println(classifyNumber(0));
         System.out.println(classifyNumber(-5));
         System.out.println(classifyNumber(3.14));
         System.out.println(classifyNumber("texto"));
         System.out.println(describe(new Circle(5)));   // pequeño
         System.out.println(describe(new Circle(20)));  // grande
         System.out.println(describe(new Rectangle(5, 5)));  // cuadrado


     }

    private static String describe(Object object){
        return switch (object) {
            case String s -> "texto. " + s.toUpperCase();
            case Integer i -> " numero : " + (i + 2);
            case Boolean b -> "boolean : " + b;
            default -> "desconocido "+ object.getClass().getSimpleName();
        };
    }

    static String classifyNumber(Object object){
         return switch (object){
             case Integer i when i > 0 -> "Position" + 1;
             case Integer i when i ==  0 -> "Cero";
             case Integer i -> "Negativo: " + i;
             default -> "desconocido "+ object.getClass().getSimpleName();
         };
    }

    static String describe(Shape shape) {
        return switch (shape) {
            case Circle c    when c.radius() > 10 -> "Circulo grande, radio:"    + c.radius();
            case Circle c                         -> "Circulo pequeño,radio: "   + c.radius();
            case Rectangle r when r.width() == r.height() -> "Cuadrado: "
                    + r.width();
            case Rectangle r                      -> "Rectangulo: "
                    + r.toString();
            case Triangle t                       -> "Triangulo: "
                    + t.toString();
        };
    }


}
