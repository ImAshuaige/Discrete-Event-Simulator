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

public class Simulate8 {
    private final Shop shop;
    private final ImList<Double> arrTime;
    private final PQ<Event> queue;
    private final ImList<Customer> customers;
    private final List<Pair<Double, Supplier<Double>>> arrAndServeTimes;
    private final Supplier<Double> restTime;
    private final int qmax;
    private final int numOfHumanServers;
    private final ImList<Customer> waitList;
    //private final int numOfSelfChecks;

    //not sure if above attribute is needed if i have my new customer and arrtime

    public Simulate8(int num, int numOfSelfChecks, List<Pair<Double,
            Supplier<Double>>> aast,int qmax,Supplier<Double> restTime) {
        this.numOfHumanServers = num;
        this.restTime = restTime;
        //this.numOfSelfChecks = numOfSelfChecks;
        this.arrAndServeTimes = aast;
        this.qmax = qmax;
        this.waitList = ImList.<Customer>of();

        ImList<Server> list = ImList.<Server>of();
        for (int i = 0; i < num; i++) {
            list = list.add(new Server((i + 1),qmax,this.restTime));
        }
        for (int i = num; i < numOfSelfChecks + numOfHumanServers; i++) {
            list = list.add(new SelfCheckOut((i + 1),qmax));
        }
        //maintain a shop with human servers and selfcheckout counters
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
        //PQ<Event> tempQueue = new PQ<Event>(new EventComparator());
        Shop updatedShop = this.shop;
        Statistic statistic = new Statistic();

        //poll out each arrival event, execute it and add back to the PQ
        while (!runQueue.isEmpty()) {
            if (runQueue.poll().first().type() != "PENDING" &&
                    runQueue.poll().first().type() != "RESTING") {
                output += String.format("%s\n", runQueue.poll().first().toString());
            }

            Event temp = runQueue.poll().first();

            Pair<Optional<Event>, Shop> tempPair = runQueue.poll().first().execute(updatedShop);
            Event nextEvent = tempPair.first().orElse(new EventStub());
            updatedShop = tempPair.second();

            if (nextEvent.type() == "WAIT" &&
                    nextEvent.getServer().getID() > this.numOfHumanServers) {
                ImList<Customer> waitList = nextEvent.getServer().getWaitingQueue();
                if (waitList.size() != this.qmax) {
                    ImList<Customer> updatedList = nextEvent.getServer().getWaitingQueue();
                    updatedList = updatedList.add(nextEvent.getCustomer());
                    //update the queue for every counters
                    ImList<Server> servers = updatedShop.getList();
                    for (int i = 0;i < servers.size();i++) {
                        int id = servers.get(i).getID();
                        if (id > numOfHumanServers && id == (i + 1)) {
                            Server newServer = servers.get(i);
                            newServer = newServer.updateSharingQueue(updatedList);
                            updatedShop = updatedShop.updateStatus(newServer);
                        }
                    }


                    int num = this.numOfHumanServers;
                    Server firstSelfCheck = updatedShop.getList().get(num);


                    nextEvent = new Wait(nextEvent.getCustomer(),nextEvent.getEventTime(),
                            firstSelfCheck.updateSharingQueue(updatedList));

                } else {
                    nextEvent = new Leave(nextEvent.getCustomer(),nextEvent.getEventTime());
                }
            }

            if (nextEvent.type() == "DONE" &&
                    nextEvent.getServer().getID() > this.numOfHumanServers) {
                ImList<Customer> updatedList = nextEvent.getServer().getWaitingQueue();

                for (int i = 0; i < updatedList.size(); i++) {
                    if (i == updatedList.indexOf(nextEvent.getCustomer())) {
                        updatedList = updatedList.remove(i).second();
                    }
                }

                //to update the sharingqueue for all the counters
                ImList<Server> servers = updatedShop.getList();
                for (int i = 0;i < servers.size();i++) {
                    int id = servers.get(i).getID();
                    if (id > numOfHumanServers && id == (i + 1)) {
                        Server newServer = servers.get(i);
                        newServer = newServer.updateSharingQueue(updatedList);
                        updatedShop = updatedShop.updateStatus(newServer);
                    }
                }

                nextEvent = new Done(nextEvent.getCustomer(), nextEvent.getEventTime(),
                        nextEvent.getServer().updateSharingQueue(updatedList));
            }

            double eventTime = temp.getEventTime();
            if (temp.type() == "ARRIVE" && nextEvent.type() != "LEAVE") {
                //can serve
                statistic = statistic.subTime(eventTime);
            } else if (temp.type() == "SERVE") {
                statistic = statistic.addServed().addTime(eventTime);
            } else if (temp.type() == "LEAVE") {
                statistic = statistic.addLeft();
            }

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
        return String.format("Queue: %s; Shop: %s",
                this.queue.toString(),this.shop.toString());
    }
}