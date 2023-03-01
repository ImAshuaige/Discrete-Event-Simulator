package cs2030.simulator;

import cs2030.util.Pair;
import cs2030.util.ImList;
import cs2030.util.PQ;

import java.util.Optional;

public class Serve extends Event {
    private final Server workingServer;
    private static final double SERVING_TIME = 1.0;
    //private static final int DEFAULT = 0;

    Serve(Customer customer,double eventtime,Server server) {
        super(customer,eventtime);
        this.workingServer = server;
    }

    @Override
    //return a done event with eventtime + servetime
    Pair<Optional<Event>,Shop> execute(Shop shop) {

        Server updatedServer = shop.getUpdatedServer(this.workingServer);
        updatedServer = this.workingServer.removeWaitingCustomer(this.getCustomer());

        Shop updatedShop = shop.updateStatus(updatedServer);

        //return DONE event 
        //the serving time is not longer 1.0
        return Pair.<Optional<Event>,Shop>of(
            Optional.<Event>of(
                new Done(this.getCustomer(),
                this.getEventTime() + this.getCustomer().getServiceTime().get(),
                updatedServer)),
                    updatedShop);
    }

    @Override
    public String toString() {
        return String.format("%.3f %s serves by %s",
            this.getEventTime(), this.getCustomer().toString(),this.workingServer.toString());
    }

    @Override
    public String type() {
        return "SERVE";
    }

    @Override
    public Server getServer() {
        return this.workingServer;
    }

}    
    
