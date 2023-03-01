package cs2030.util;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;

public class PQ<T> {
    private final PriorityQueue<T> pq;
    

    public PQ(Comparator<? super T> cmp) {
        this.pq = new PriorityQueue<T>(cmp);
    }

    public PQ(PriorityQueue<T> pq) {
        this.pq = pq;
    }

    
    public PQ<T> add(T elem) {
        PriorityQueue<T> updatedPQ = new PriorityQueue<T>(this.pq);
        updatedPQ.add(elem);
        return new PQ<T>(updatedPQ);
    }

    public boolean isEmpty() {
        return this.pq.isEmpty();
    }
    
    public Pair<T,PQ<T>> poll() {
        PriorityQueue<T> newPQ = new PriorityQueue<T>(this.pq);
        T removedElem = newPQ.poll();
        return Pair.<T,PQ<T>>of(removedElem, new PQ<T>(newPQ));
    }

    public PQ<T> remove(Object o) {
        PriorityQueue<T> updatedPQ = new PriorityQueue<T>(this.pq);
        updatedPQ.remove(o);
        return new PQ<T>(updatedPQ);
    }


    @Override
    public String toString() {
        return this.pq.toString();
    }
}    
