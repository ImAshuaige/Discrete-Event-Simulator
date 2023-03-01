package cs2030.simulator;

import cs2030.util.Pair;
import cs2030.util.PQ;
import cs2030.util.ImList;
import java.util.Optional;

public class Resting extends Event {
    private final Server restingServer;

    Resting(Customer customer,double eventtime,Server server) {
        super(customer,eventtime);
        this.restingServer = server;
    }

    @Override
    Pair<Optional<Event>,Shop> execute(Shop shop) {
        //update the current server
        Server updatedServer = shop.getUpdatedServer(this.restingServer);

        //after get the new server,finish resting means set availabilty to true
        if (updatedServer.type() == "counter") {
            updatedServer = new SelfCheckOut(updatedServer.getID(),true,
                    updatedServer.getNextAvailableTime(),
                    updatedServer.getWaitingQueue(),
                    updatedServer.getCapacity(),
                    updatedServer.getRestTime());
        } else {
            updatedServer = new Server(updatedServer.getID(), true,
                    updatedServer.getNextAvailableTime(),
                    updatedServer.getWaitingQueue(),
                    updatedServer.getCapacity(),
                    updatedServer.getRestTime());
        }
        //to update the shop
        Shop updatedShop = shop.updateStatus(updatedServer);

        return Pair.<Optional<Event>,Shop>of(Optional.<Event>of(new EventStub()),updatedShop);

    }

    @Override
    public String toString() {
        return String.format("%.3f %s resting at %s",
                this.getEventTime(),this.getCustomer().toString(),this.restingServer.toString());
    }

    @Override
    public String type() {
        return "RESTING";
    }
}