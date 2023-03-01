package cs2030.simulator;

import cs2030.util.ImList;
import cs2030.util.Pair;
import java.util.function.Supplier;

public class SelfCheckOut extends Server {
    //decided not to have the sharing queue in this selfcheckout class

    private static final boolean AVAILABLE = true;
    private static final boolean SERVING = false;
    private static final double DEFAULT_AVAIL_TIME = 0.0;
    private static final int DEFAULT_CAPACITY = 1;
    private static final double SERVING_TIME = 1.0;
    private static final Supplier<Double> DEFAULT_REST_TIME = () -> 0.0;

    public SelfCheckOut(int id, boolean stage,double nextAvailableTime,
                        ImList<Customer> waitingQueue,int queueCapacity,
                        Supplier<Double> restTime) {
        super(id,stage,nextAvailableTime,waitingQueue,queueCapacity,restTime);
    }

    public SelfCheckOut(int id,int qmax) {
        this(id,AVAILABLE,DEFAULT_AVAIL_TIME,ImList.<Customer>of(),qmax,DEFAULT_REST_TIME);
    }

    @Override
    public SelfCheckOut updateSharingQueue(ImList<Customer> sharingQueue) {
        return new SelfCheckOut(this.getID(), this.isAvailable(),
                this.getNextAvailableTime(), sharingQueue, this.getCapacity(), DEFAULT_REST_TIME);
    }

    @Override
    public String toString() {
        return String.format("self-check %d",this.getID());
    }

    @Override
    public SelfCheckOut addWaitingCustomer(Customer c) {
        return this.updateSharingQueue(this.getWaitingQueue().add(c));
    }



    @Override
    public SelfCheckOut removeWaitingCustomer(Customer c) {
        ImList<Customer> updatedCustomer = super.getWaitingQueue();

        for (int i = 0; i < super.getWaitingQueue().size();i++) {
            if (i == super.getWaitingQueue().indexOf(c)) {
                updatedCustomer = super.getWaitingQueue().remove(i).second();
            }
        }

        double time = super.getNextAvailableTime();
        if (c.getArrTime() > super.getNextAvailableTime()) {
            time = c.getArrTime();
        }
        //set to false
        return new SelfCheckOut(super.getID(),SERVING,
                time + c.getServiceTime().get(),
                updatedCustomer,super.getCapacity(),DEFAULT_REST_TIME);
    }

    @Override
    public String type() {
        return "counter";
    }

}