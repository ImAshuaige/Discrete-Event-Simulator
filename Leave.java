package cs2030.simulator;

import cs2030.util.Pair;
import cs2030.util.ImList;
import cs2030.util.PQ;
import java.util.Optional;

public class Leave extends Event {

    Leave(Customer customer,double eventtime) {
        super(customer,eventtime);
    }
    
    
    @Override
    public Pair<Optional<Event>,Shop> execute(Shop shop) {
        return Pair.<Optional<Event>,Shop>of(Optional.<Event>of(new EventStub()),shop);
    }

    @Override
    public String toString() {
        return String.format("%.3f %s leaves",this.getEventTime(),this.getCustomer().toString());
    }

    @Override
    public String type() {
        return "LEAVE";
    }
}
  
