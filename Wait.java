package cs2030.simulator;

import cs2030.util.Pair;
import cs2030.util.ImList;
import cs2030.util.PQ;
import java.util.Optional;

public class Wait extends Event {
    private static final double SERVING_TIME = 1.0;
    private final Server waitingServer;

    Wait(Customer customer,double eventtime,Server server) {
        super(customer,eventtime);
        this.waitingServer = server;
    }
    

    //returns a serve event
    @Override
    Pair<Optional<Event>,Shop> execute(Shop shop) {
        Server updatedServer = shop.getUpdatedServer(this.waitingServer);
        Shop updatedShop = shop.updateStatus(updatedServer);


        if (updatedServer.type() == "counter") { //self check out case
            int index = updatedServer.getID() - 1;//first counter : k + 1
            double minTime = updatedServer.getNextAvailableTime();
            for (int i = index; i < shop.getList().size(); i++) {
                if (shop.getList().get(i).getNextAvailableTime() < minTime) {
                    minTime = shop.getList().get(i).getNextAvailableTime();
                    updatedServer = shop.getList().get(i);
                }
            }
            updatedShop = shop.updateStatus(updatedServer);

            return Pair.<Optional<Event>, Shop>of(
                    Optional.<Event>of(
                            new Pending(this.getCustomer(), minTime, updatedServer)),
                    updatedShop);

        } else { // human server case

            updatedServer = updatedServer.addWaitingCustomer(this.getCustomer());
            updatedShop = shop.updateStatus(updatedServer);
            double time = updatedServer.getNextAvailableTime();

            return Pair.<Optional<Event>, Shop>of(
                    Optional.<Event>of(
                            new Pending(this.getCustomer(), time, updatedServer)),
                    updatedShop);
        }
    }

    @Override
    public Server getServer() {
        return this.waitingServer;
    }

    @Override
    //not sure if need check the toString method to fulfill the waiting list
    public String toString() {
        int id = this.waitingServer.getID();
        if (this.waitingServer.type() == "counter") {
            return String.format("%.3f %s waits at %s",
                    this.getEventTime(),this.getCustomer().toString(),
                    this.waitingServer.toString());
        }
        return String.format("%.3f %s waits at %s",
            this.getEventTime(),this.getCustomer().toString(),
                this.waitingServer.toString());
    }

    @Override
    public String type() {
        return "WAIT";
    }
}    
    
