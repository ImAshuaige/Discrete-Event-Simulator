package cs2030.simulator;

import cs2030.util.Pair;
import cs2030.util.ImList;
import cs2030.util.PQ;
import java.util.Optional;

public class Arrive extends Event {
    //private static final double SERVING_TIME = 1.0;
    // private static final int DEFAULT = 0;

    Arrive(Customer customer,double eventtime) {
        super(customer,eventtime);
    }
    
    //hello test can save or not
    @Override
    public Pair<Optional<Event>,Shop> execute(Shop shop) {
        ImList<Server> servers = shop.getList();
        Shop updatedShop = shop;
    
        //the first for loop is to check if there's available servers then returns a serve event 
        for (int i = 0; i < servers.size(); i++) {
            if (servers.get(i).type() == "counter") {
                if (servers.get(i).isAvailable() && servers.get(i).getWaitingQueue().size() == 0) {
                    return Pair.<Optional<Event>,Shop>of(
                            Optional.<Event>of(
                                    new Serve(this.getCustomer(),this.getEventTime(),
                                            servers.get(i))),
                            shop);
                }
            } else if (servers.get(i).isAvailable() && servers.get(i).type() != "counter") {
                //for other human servers
                return Pair.<Optional<Event>,Shop>of(
                    Optional.<Event>of(
                            new Serve(this.getCustomer(),this.getEventTime(),
                            servers.get(i))),
                            shop);
            }
        }

        // if there is no available server, return WAIT
        for (int i = 0; i < servers.size(); i++) {
            if (servers.get(i).getWaitingQueue().size() != servers.get(i).getCapacity()) {
                return Pair.<Optional<Event>, Shop>of(
                        Optional.<Event>of(
                                new Wait(this.getCustomer(), this.getEventTime(),
                                        servers.get(i))),
                        shop);
            }
        }
        //else, return a leave event
        return Pair.<Optional<Event>,Shop>of(Optional.<Event>of(
            new Leave(this.getCustomer(),this.getEventTime())),shop);

    }

  
    @Override
    public String toString() {
        return String.format("%.3f %s arrives",this.getEventTime(),this.getCustomer().toString());
    }

    @Override
    public String type() {
        return "ARRIVE";
    }
}       
        
       
