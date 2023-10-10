package edu.school21.models;

import java.util.StringJoiner;

public class Car {
    private String carManufacturer;
    private String modelName;
    private int year;
    private double mileage;
    private String owner;

    public Car() {
        this.carManufacturer = "Default - Opel";
        this.modelName = "Default - Astra";
        this.year = 2000;
        this.mileage = 0.0;
        this.owner = "Tito";
    }

    public Car(String companyName, String modelName, int year, double mileage, String owner) {
        this.carManufacturer = companyName;
        this.modelName = modelName;
        this.year = year;
        this.mileage = mileage;
        this.owner = owner;
    }

    public double IncreaseMileage(double value) {
        this.mileage += value;
        return mileage;
    }

    public void justVoidMethod(double value) {
        this.mileage += value;
    }

    public double JustForFun(double value1, double value2) {
        this.mileage += value1 * value2;
        return mileage;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Car.class.getSimpleName() + "[", "]")
                .add("companyName='" + carManufacturer + "'")
                .add("modelName='" + modelName + "'")
                .add("year=" + year)
                .add("mileage=" + mileage)
                .add("owner=" + owner)
                .toString();
    }
}
