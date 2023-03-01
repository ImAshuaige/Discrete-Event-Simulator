package cs2030.simulator;

import cs2030.util.ImList;
import cs2030.util.PQ;
import cs2030.util.Pair;

import java.util.Queue;
import java.util.PriorityQueue;
import java.util.List;

public class Simulate3 {
    private final Shop shop;
    private final ImList<Double> arrTime;
    private final PQ<Event> queue;
    private final ImList<Customer> customers;
    

    public Simulate3(int num, List<Double> arrTime) {
        ImList<Server> list = ImList.<Server>of();
        for (int i = 0; i < num; i++) {
            list = list.add(new Server(i + 1));
        }
        this.shop = new Shop(list);
        this.arrTime = ImList.<Double>of(arrTime);
        this.customers = getCustomerList();
        this.queue = this.getQueue();
    }

    ImList<Customer> getCustomerList() {
        ImList<Customer> customers = ImList.<Customer>of();

        for (int i = 0;i < this.arrTime.size();i++) {
            double time = this.arrTime.get(i);
            customers = customers.add(new Customer(i + 1, time));
        }
        return customers;
    }



    // need to change the getQueue() so that the queue take 
    // different events ????????
    // update using execute() here ????
    // create event queue will all arrive 
    public PQ<Event> getQueue() { 
        PQ<Event> pq = new PQ<Event>(new EventComparator());
        
        for (int i = 0;i < this.arrTime.size();i++) {
            double time = this.arrTime.get(i);
            pq = pq.add(new Arrive(this.customers.get(i),time));
        }
        return pq;
    }

    public String run() {
        String output = "";
        PQ<Event> runQueue = this.queue;
        PQ<Event> tempQueue = new PQ<Event>(new EventComparator());
        Shop updatedShop = this.shop;

        //poll out each arrival event, execute it and add back to the PQ
        while (!runQueue.isEmpty()) {
            Event temp = runQueue.poll().first();
            tempQueue = tempQueue.add(temp);
            //do not print addtional events
            if (temp.type() != "PENDING" && temp.type() != "RESTING") {
                output += String.format("%s\n", temp.toString());
            }
            runQueue = runQueue.poll().second();
            Event nextEvent = temp.execute(updatedShop).first().orElse(new EventStub());
            updatedShop = temp.execute(updatedShop).second();

            Event testEvent = new EventStub();
            if (nextEvent.getCustomer().getID() != testEvent.getCustomer().getID()) { 
                runQueue = runQueue.add(nextEvent);
            } 
        }
        return output + "-- End of Simulation --";
    }

     
    @Override
    public String toString() {
        return String.format("Queue: %s; Shop: %s",this.queue.toString(),this.shop.toString());
    }
}
