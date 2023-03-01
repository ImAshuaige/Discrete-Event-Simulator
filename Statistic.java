package cs2030.simulator;

public class Statistic {

    private final double waittime;
    private final int numOfServed;
    private final int numOfLeft;

    private static final double DEFAULT_TIME = 0.0;
    private static final int DEFAULT_NUM = 0;
    private static final int A_CUSTOMER = 1; 

    Statistic() {
        this(DEFAULT_TIME,DEFAULT_NUM,DEFAULT_NUM);
    }

    Statistic(double waittime,int numOfServed,int numOfLeft) {
        this.waittime = waittime;
        this.numOfServed = numOfServed;
        this.numOfLeft = numOfLeft;
    }

    public Statistic addTime(double time) {
        double updatedTime = this.waittime + time;
        return new Statistic(updatedTime,this.numOfServed,this.numOfLeft);
    }
 
    public Statistic subTime(double time) {
        double updatedTime = this.waittime - time;
        return new Statistic(updatedTime,this.numOfServed,this.numOfLeft);
    }

    public Statistic addServed() {
        return new Statistic(this.waittime,
                this.numOfServed + A_CUSTOMER,this.numOfLeft);
    }

    public Statistic addLeft() {
        return new Statistic(this.waittime,
                this.numOfServed,this.numOfLeft + A_CUSTOMER);
    }

    @Override
    public String toString() {
        double avgtime = this.waittime / this.numOfServed;
        return String.format("[%.3f %d %d]",avgtime,this.numOfServed,this.numOfLeft);
    }
}
    
    
    

