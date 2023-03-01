package cs2030.simulator;

import cs2030.util.ImList;
import cs2030.util.PQ;
import cs2030.util.Pair;
import cs2030.util.Lazy;

import java.util.Queue;
import java.util.PriorityQueue;
import java.util.List;
import java.util.function.Supplier;
import java.util.Optional;

public class Simulate7 {
    private final Shop shop;
    private final ImList<Double> arrTime;
    private final PQ<Event> queue;
    private final ImList<Customer> customers;
    private final List<Pair<Double, Supplier<Double>>> arrAndServeTimes;
    private final Supplier<Double> restTime;

    //not sure if above attribute is needed if i have my new customer and arrtime

    public Simulate7(int num, List<Pair<Double, Supplier<Double>>> aast,
                     int qmax,Supplier<Double> restTime) {
        this.restTime = restTime;
        this.arrAndServeTimes = aast;
        ImList<Server> list = ImList.<Server>of();
        for (int i = 0; i < num; i++) {
            list = list.add(new Server((i + 1),qmax,this.restTime));
        }
        this.shop = new Shop(list);
        this.arrTime = getArrTime(aast);
        this.customers = getCustomerList(aast);


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

    //poll out each arrival event, execute it and add back to the PQ
    public String run() {
        String output = "";
        PQ<Event> runQueue = this.queue;
        Shop updatedShop = this.shop;
        Statistic statistic = new Statistic();

        //poll out each arrival event, execute it and add back to the PQ
        while (!runQueue.isEmpty()) {
            //Event temp = runQueue.poll().first();
            //System.out.println(temp.toString());
            //tempQueue = tempQueue.add(temp);
            if (runQueue.poll().first().type() != "PENDING" &&
                    runQueue.poll().first().type() != "RESTING") {
                output += String.format("%s\n", runQueue.poll().first().toString());
            }
            //runQueue = runQueue.poll().second();
            Event temp = runQueue.poll().first();
            Pair<Optional<Event>,Shop> tempPair =
                    runQueue.poll().first().execute(updatedShop);
            Event nextEvent = tempPair.first().orElse(new EventStub());
            updatedShop = tempPair.second();

            double eventTime = temp.getEventTime();
            if (temp.type() == "ARRIVE" && nextEvent.type() != "LEAVE") {
                //can serve
                statistic = statistic.subTime(eventTime);
            } else if (temp.type() == "SERVE") {
                statistic = statistic.addServed().addTime(eventTime);
            } else if (temp.type() == "LEAVE") {
                statistic = statistic.addLeft();
            }

            //Pair<Optional<Event>,Shop> pair = run
            //updatedShop = temp.execute(updatedShop).second();
            runQueue = runQueue.poll().second();

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