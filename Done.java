package cs2030.simulator;

import cs2030.util.Pair;
import cs2030.util.PQ;
import cs2030.util.ImList;
import java.util.Optional;
import java.util.function.Supplier;

public class Done extends Event {
    private final Server workingServer;

    Done(Customer customer,double eventtime,Server server) {
        super(customer,eventtime);
        this.workingServer = server;
    }

    @Override
    Pair<Optional<Event>,Shop> execute(Shop shop) {

        Server updatedServer = shop.getUpdatedServer(this.workingServer);

        if (updatedServer.type() == "counter") {
            updatedServer = new SelfCheckOut(updatedServer.getID(),false,
                    updatedServer.getNextAvailableTime() + updatedServer.getRestTime().get(),
                    updatedServer.getWaitingQueue(),updatedServer.getCapacity(),
                    updatedServer.getRestTime());
        } else {
            updatedServer = new Server(updatedServer.getID(), false,
                    updatedServer.getNextAvailableTime() + updatedServer.getRestTime().get(),
                    updatedServer.getWaitingQueue(), updatedServer.getCapacity(),
                    updatedServer.getRestTime());  // cannot use getresttime twice?
        }

        // update the shop
        Shop updatedShop = shop.updateStatus(updatedServer);

        // return a rest event pass in the new server
        return Pair.<Optional<Event>,Shop>of(Optional.<Event>of(
                new Resting(this.getCustomer(),updatedServer.getNextAvailableTime(),
                        updatedServer)),
                updatedShop);

    }

    @Override
    public String toString() {
        return String.format("%.3f %s done serving by %s",
            this.getEventTime(),this.getCustomer().toString(),this.workingServer.toString());
    }

    @Override
    public String type() {
        return "DONE";
    }

    @Override
    public Server getServer() {
        return this.workingServer;
    }
}

//not sure if event are that simple tho//
    
