package cs2030.simulator;

import cs2030.util.Pair;
import cs2030.util.ImList;
import cs2030.util.PQ;
import java.util.Optional;

//for level6 to deal witht the queue
public class Pending extends Event {
    private static final double SERVING_TIME = 1.0;
    private final Server waitingServer;
    //private static final int DEFAULT = 0;

    Pending(Customer customer,double eventtime,Server server) {
        super(customer,eventtime);
        this.waitingServer = server;
    }
    

    //returns a serve event
    @Override
    Pair<Optional<Event>,Shop> execute(Shop shop) {
        //check if the waiting customer is first one in the queue
        //if yes, then returns a new Serve event
        //else, return another pending event that updates the waiting time
        //ImList<Server> servers = shop.getList();
        Server updatedServer = shop.getUpdatedServer(this.waitingServer);


        //check if available
        //this for loop a bit messy, may simplify

        if (updatedServer.type() == "counter") { // self check out case
            if (updatedServer.checkAvailability(this.getEventTime())) {
                //if true, means server is available
                if (updatedServer.getWaitingQueue().size() == 0 ||
                        (updatedServer.getWaitingQueue().get(0).getID() ==
                                this.getCustomer().getID())) {
                    return Pair.<Optional<Event>, Shop>of(
                            Optional.<Event>of(
                                    new Serve(this.getCustomer(), this.getEventTime(),
                                            updatedServer)),
                            shop);
                }
            } else { //update the min time and return another pending

                double minTime = updatedServer.getNextAvailableTime();
                for (int i = 0; i < shop.getList().size(); i++) {
                    Server currentServer = shop.getList().get(i);
                    if (currentServer.type() == "counter" &&
                            currentServer.getNextAvailableTime() < minTime) {
                        minTime = currentServer.getNextAvailableTime();
                        updatedServer = currentServer;
                    }
                }

                return Pair.<Optional<Event>, Shop>of(
                        Optional.<Event>of(
                                new Pending(this.getCustomer(), minTime,
                                        updatedServer)),
                        shop);

            }
        } else { //human server case
            //case 2, for all human servers
            if (updatedServer.checkAvailability(this.getEventTime())) {
                if (updatedServer.getWaitingQueue().size() == 0 ||
                        (updatedServer.getWaitingQueue().get(0).getID() ==
                                this.getCustomer().getID())) {
                    return Pair.<Optional<Event>, Shop>of(
                            Optional.<Event>of(
                                    new Serve(this.getCustomer(), this.getEventTime(),
                                            updatedServer)),
                            shop);
                }
            } else {
                //else return another pending event with update time
                //not sure abt how to update the time here
                // it is because the pending event the updatedServer is 2
                double time = updatedServer.getNextAvailableTime();
                return Pair.<Optional<Event>, Shop>of(
                        Optional.<Event>of(
                                new Pending(this.getCustomer(), time,
                                        updatedServer)),
                        shop);
            }
        }
        return Pair.<Optional<Event>, Shop>of(
                Optional.<Event>of(
                        new Pending(this.getCustomer(), updatedServer.getNextAvailableTime(),
                                updatedServer)),
                shop);
    }


    @Override
    public String toString() {
        return String.format("%.3f %s pending at %s",
            this.getEventTime(),this.getCustomer().toString(),this.waitingServer.toString());
    }

    @Override
    public String type() {
        return "PENDING";
    }
}    
 
 
