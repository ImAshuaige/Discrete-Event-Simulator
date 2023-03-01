package cs2030.simulator;

import cs2030.util.ImList;
import cs2030.util.PQ;
import cs2030.util.Pair;

import java.util.Queue;
import java.util.PriorityQueue;
import java.util.List;

public class Simulate2 {
    private final Shop shop;
    private final ImList<Double> arrTime;
    private final PQ<EventStub> queue;
    

    public Simulate2(int num, List<Double> arrTime) {
        this.shop = new Shop(List.<Server>of(new Server(num)));
        this.arrTime = ImList.<Double>of(arrTime);
        this.queue = this.getQueue();
    }

    public PQ<EventStub> getQueue() { 
        PQ<EventStub> pq = new PQ<EventStub>(new EventComparator());
        for (int i = 0;i < this.arrTime.size();i++) {
            double time = this.arrTime.get(i);
            pq = pq.add(new EventStub(new Customer(i + 1, time),time));
        }
        return pq;
    }

    /*private or public>>*/
    public String run() {
        String output = "";
        PQ<EventStub> runQueue = this.queue;
        while (!runQueue.isEmpty()) {
            output += String.format("%s\n", runQueue.poll().first());
            runQueue = runQueue.poll().second();
            /*Pair<EventStub,PQ<EventStub>> es = runQueue.poll();
            runQueue = runQueue.poll().;
            output += String.format("%s\n",es.toString());*/
        }
        return output + "-- End of Simulation --";
    }

    @Override
    public String toString() {
        return String.format("Queue: %s; Shop: %s",
                this.queue.toString(),this.shop.toString());
    }
}   
