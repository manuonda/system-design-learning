package com.hexagonal.records;

// Record: class inmutable,
//       :

public class BasicRecordDemo {

    record Point(int x , int y) {

    }

    public static void main(String[] args) {
        var p1 = new Point(3, 4);
        var p2 = new Point(3,4);
        var p3 = new Point(0,0);

        System.out.println(p1);
        System.out.println(p2);
        System.out.println(p3);
        System.out.println(p1.equals(p2)); // true
        System.out.println(p1.equals(p3)); // false
    }

}
