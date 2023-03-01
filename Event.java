package cs2030.simulator;

import cs2030.util.Pair;
import java.lang.Comparable;
import java.util.Comparator;
import java.util.Optional;

//private static final Server = new Server(-1);

abstract class Event implements Comparable<Event> {
    protected final Customer customer;
    protected final double eventtime;
 
    Event(Customer c,double t) {
        this.customer = c;
        this.eventtime = t;
    }

    @Override
    public String toString() {
        return String.format("%.3f",this.eventtime);
    }

    @Override
    public int compareTo(Event other) {
        double dif = this.getEventTime() - other.getEventTime();
        if (dif > 0) {
            return 1;
        } else if (dif < 0) {
            return -1;
        } else {
            int iD1 = this.getCustomer().getID();
            int iD2 = other.getCustomer().getID();
            return Integer.compare(iD1,iD2);
        }
    }

    public double getCustomerTime() {
        return this.customer.getArrTime();
    }

    public Customer getCustomer() {
        return this.customer;
    }

    public double getEventTime() {
        return this.eventtime;
    }

    abstract String type();

    abstract Pair<Optional<Event>, Shop> execute(Shop shop);
    //lambda expression?

    // to be override for WAIT
    public Server getServer() {
        return new Server(-1);
    }

}
