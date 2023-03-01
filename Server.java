package cs2030.simulator;

import cs2030.util.ImList;
import cs2030.util.Pair;
import java.util.function.Supplier;

public class Server {
    private final int serverID;
    private final boolean stage;
    private final double nextAvailableTime;
    private final ImList<Customer> waitingQueue;
    private final int queueCapacity;
    private final Supplier<Double> restTime;

    private static final boolean AVAILABLE = true;
    private static final boolean SERVING = false;
    private static final double DEFAULT_AVAIL_TIME = 0.0;
    private static final int DEFAULT_CAPACITY = 1;
    private static final double SERVING_TIME = 1.0;
    private static final Supplier<Double> DEFAULT_REST_TIME = () -> 0.0;

    private static final int ZERO = 0;
    private static final int ONE = 1;

    Server(int id) {
        this(id,DEFAULT_CAPACITY);
    }

    Server(int id, boolean stage,double nextAvailableTime,
           ImList<Customer> waitingQueue,int queueCapacity,Supplier<Double> restTime) {
        this.serverID = id;
        this.stage = stage;
        this.nextAvailableTime = nextAvailableTime;
        this.waitingQueue = waitingQueue;
        this.queueCapacity = queueCapacity;
        this.restTime = restTime;
    }

    //constructor for level 6
    Server(int id, int qmax) {
        this(id,AVAILABLE,DEFAULT_AVAIL_TIME,
                ImList.<Customer>of(),qmax,DEFAULT_REST_TIME);
    }

    //constructor for level 7
    Server(int id, int qmax, Supplier<Double> restTime) {
        this(id,AVAILABLE,DEFAULT_AVAIL_TIME,ImList.<Customer>of(),qmax,restTime);
    }

    //DONT USE startwork or finishwork, since the time maybe different
    //when serve event is called 
    public Server startWork() {
        return new Server(this.serverID,SERVING,
            this.nextAvailableTime,this.waitingQueue,
                this.queueCapacity,this.restTime);
    }

    // when done event is called 
    public Server finishWork() {
        return new Server(this.serverID,AVAILABLE,
            this.nextAvailableTime,this.waitingQueue,
                this.queueCapacity,this.restTime);
    }

    public Server updateStage(boolean stage) {
        return new Server(this.serverID,stage,this.nextAvailableTime,
            this.waitingQueue,this.queueCapacity,this.restTime);
    }

    public Server updateQueue(ImList<Customer> updatedQueue) {
        return new Server(this.serverID,this.stage,this.nextAvailableTime,
            updatedQueue,this.queueCapacity,this.restTime);
    }
        
    //add waiting customer to the queue
    public Server addWaitingCustomer(Customer c) {
        return this.updateQueue(this.waitingQueue.add(c));
    }

    //remove waiting customer from the queue
    public Server removeWaitingCustomer(Customer c) {
        ImList<Customer> updatedCustomer = this.waitingQueue;

        for (int i = 0; i < this.waitingQueue.size();i++) {
            if (i == this.waitingQueue.indexOf(c)) {
                updatedCustomer = this.waitingQueue.remove(i).second();
            }
        }

        double time = this.nextAvailableTime;
        if (c.getArrTime() > this.nextAvailableTime) {
            time = c.getArrTime();
        }
        //set to false
        return new Server(this.serverID,SERVING,
            time + c.getServiceTime().get(),
            updatedCustomer,this.queueCapacity,this.restTime);
    }

    public Server updateTime(double time) { //useless
        return new Server(this.serverID,this.stage,time,
                this.waitingQueue,this.queueCapacity,this.restTime);
    }

    public boolean checkAvailability(double time) {
        return this.isAvailable()
                && (time >= this.nextAvailableTime);
    }

    @Override
    public String toString() {
        return this.type() == "human"
                ? String.format("%d",this.serverID)
                : String.format("self-check %d",this.getID());
    }

    //getter methods 
    public int getID() {
        return this.serverID;
    }

    public boolean isAvailable() {
        return this.stage;
    }

    public double getNextAvailableTime() {
        return this.nextAvailableTime;
    }

    public ImList<Customer> getWaitingQueue() {
        return this.waitingQueue;
    }

    public int getCapacity() {
        return this.queueCapacity;
    }

    public Supplier<Double> getRestTime() {
        return this.restTime;
    }

    public String type() {
        return "human";
    }

    // SHLD NOT BE CALLED,to be override by the child class
    public Server updateSharingQueue(ImList<Customer> sharingQueue) {
        return this;
    }
}
