package com.java.practice.expression;

public class SwtichExpression {


    enum Season { SPRING, SUMMER, AUTUMN, WINTER }

    static void main(String args[]) {

       classicSwitch("MONDAY");
       System.out.println(modernSwitch("SATURDAY"));
       classicSwitch("SUNDAY");
       System.out.println(modernSwitch("SUNDAY"));
       swithcWithYield("MONDAY");
       describeSeasonWitch(Season.SUMMER);
    }

    static void classicSwitch(String day ) {
        String type;
        switch (day) {
            case "MONDAY":
            case "TUESDAY":
               type ="Weekday";
               break;
            case "SATURDAY":
            case "SUNDAY":
              type="Weekend";
              break;
            default:
                 type="Unknown";

        }
        System.out.println(type);
    }

    static String modernSwitch(String day) {
        return switch (day) {
            case "MONDAY","TUESDAY" -> "Weekday";
            case "SATURDAY","SUNDAY" ->"Weekend";
            default -> "Unknown";
        };
    }


    /**
     * yield es obligatorio dentro de un bloque  {}
     * sin el no compila
     * @param day
     * @return
     */
    static String swithcWithYield(String day) {
        return switch(day) {
            case "MONDAY" ->  {
                System.out.println("MONDAY");
                yield "Weekday";
            }
            case "FRIDAY" -> {
                System.out.println("FRIDAY");
                yield "Weekend";
            }
            default -> "Unknown";
        };
    }



    static String describeSeasonWitch(Season season) {
        return switch (season) {
            case SPRING -> "Flowers blooming ";
            case SUMMER -> "Hot and sunny ";
            case AUTUMN -> "Leaves falling";
            case WINTER -> "Winter ";
            default -> "Unknown";
        };
    }





}
