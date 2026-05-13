package com.java.practice.records;


interface Describable {
    String describe();
}

interface Printable {
    void print();
}

record Employee(String name, String role, double salary)
implements  Describable , Printable {

    @Override
    public String describe(){
      return name + " " + role + " " + salary;
    }
    @Override
    public void print(){
      System.out.println(describe());
    }
}

public class RecordsImplementInterface {

    static void main(String[] args) {
        var employee = new Employee("David", "Developer", 23.4);
        System.out.println(employee.describe());
    }
}
