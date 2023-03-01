package cs2030.simulator;

import cs2030.util.Pair;
import java.util.Optional;

public class EventStub extends Event {
    private static final double DEFAULT = 0.0;
    
    EventStub(Customer customer,double eventtime) {
        super(customer,eventtime);
    }

    EventStub() {
        super(new Customer(),DEFAULT);
    }

    @Override
    public Pair<Optional<Event>,Shop> execute(Shop shop) {
        return Pair.<Optional<Event>,Shop>of(Optional.empty(),shop);
    }

    @Override
    public String type() {
        return "EventStub";
    }
}
    
