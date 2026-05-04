package com.hexagonal.records;

import java.util.Locale;

record Temperature(double value, String unit) {
    // compact constructor
    Temperature {
        if(value < -273.15) throw new IllegalArgumentException("Temperature value cannot be negative");
        unit = unit.toUpperCase();
    }
}

record Age(int value) {
    Age {
        if(value < 0  || value > 150 ) throw new IllegalArgumentException("Age value must be between 0 and 150");
    }
}

public class RecordWithValidation {

    static void main(String[] args) {
        var temperature = new Temperature(23, "U");
        System.out.println(temperature);
        var temperature1 = new Temperature(350, "U");
        System.out.println(temperature1);

        var age = new Age(1);
        var  age2 = new Age( -1);
        System.out.println(age);
        System.out.println(age2);


    }

}
