package cs2030.simulator;

import cs2030.util.ImList;
import cs2030.util.PQ;
import cs2030.util.Pair;
import cs2030.util.Lazy;

import java.util.Queue;
import java.util.PriorityQueue;
import java.util.List;
import java.util.function.Supplier;

public class Simulate6 {
    private final Shop shop;
    private final ImList<Double> arrTime;
    private final PQ<Event> queue;
    private final ImList<Customer> customers;
    private final List<Pair<Double, Supplier<Double>>> arrAndServeTimes;

    //not sure if above attribute is needed if i have my new customer and arrtime

    public Simulate6(int num, List<Pair<Double, Supplier<Double>>> aast,int qmax) {
        ImList<Server> list = ImList.<Server>of();
        for (int i = 0; i < num; i++) {
            list = list.add(new Server((i + 1),qmax));
        }
        this.shop = new Shop(list);

        this.arrTime = getArrTime(aast);
        this.customers = getCustomerList(aast);
        this.arrAndServeTimes = aast;

        this.queue = this.getQueue();
    }


    ImList<Double> getArrTime(List<Pair<Double, Supplier<Double>>> aast) {
        ImList<Double> times = ImList.<Double>of();    
        for (int i = 0;i < aast.size();i++) {
            times = times.add(aast.get(i).first());
        }
        return times;
    }
    
    ImList<Customer> getCustomerList(List<Pair<Double, Supplier<Double>>> aast) {
        ImList<Customer> customers = ImList.<Customer>of();
        for (int i = 0;i < aast.size();i++) {
            double time = this.arrTime.get(i);
            customers = customers.add(new Customer(i + 1, time,aast.get(i).second()));
        }
        return customers;
    }


 
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
        Statistic statistic = new Statistic();

        //poll out each arrival event, execute it and add back to the PQ
        while (!runQueue.isEmpty()) {
            Event temp = runQueue.poll().first();
            tempQueue = tempQueue.add(temp);
            if (temp.type() != "PENDING" && temp.type() != "RESTING") {
                output += String.format("%s\n", temp.toString());
            }
            runQueue = runQueue.poll().second();
            Event nextEvent = temp.execute(updatedShop).first().orElse(new EventStub());
            
            double tempTime = temp.getEventTime();
            if (temp.type() == "ARRIVE" && nextEvent.type() != "LEAVE") {
                //can serve
                statistic = statistic.subTime(tempTime);
            } else if (temp.type() == "SERVE") {
                statistic = statistic.addServed().addTime(tempTime);
            } else if (temp.type() == "LEAVE") {
                statistic = statistic.addLeft();
            }

            updatedShop = temp.execute(updatedShop).second();


            Event testEvent = new EventStub();
            if (nextEvent.getCustomer().getID() != testEvent.getCustomer().getID()) { 
                runQueue = runQueue.add(nextEvent);
            }

        }
        return output + statistic.toString();
    }

     
    @Override
    public String toString() {
        return String.format("Queue: %s; Shop: %s",this.queue.toString(),this.shop.toString());
    }
}










