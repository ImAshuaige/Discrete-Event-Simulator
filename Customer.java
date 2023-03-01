package cs2030.simulator;

import cs2030.util.Lazy;

import java.util.function.Supplier;

public class Customer {
    private final int id;
    private final double arrtime;
    private final Lazy<Double> servicetime;

    private static final int DEFAULT_ID = 0;
    private static final double DEFAULT_T = 0.0;
    private static final double DEFAULT_SERVING_TIME = 1.0;

    public Customer(int id, double arrtime) {
        this(id,arrtime,new Lazy<Double>(() -> DEFAULT_SERVING_TIME));
    }

    public Customer() {
        this(DEFAULT_ID,DEFAULT_T);
    }

    public Customer(int id, double arrtime, Supplier<Double> servicetime) {
        this(id,arrtime,Lazy.<Double>of(servicetime)); 
    }

    public Customer(int id, double arrtime, Lazy<Double> servicetime) {
        this.id = id;
        this.arrtime = arrtime;
        this.servicetime = servicetime;
    }

    @Override
    public String toString() {
        return String.format("%d",this.id);
    }

    //getter methods
    public double getArrTime() {
        return this.arrtime;
    }

    public int getID() {
        return this.id;
    }

    public Lazy<Double> getServiceTime() {
        return this.servicetime;
    }
}








