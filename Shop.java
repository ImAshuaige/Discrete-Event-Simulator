package cs2030.simulator;

import cs2030.util.ImList;
import cs2030.util.Pair;

import java.util.List;

public class Shop {
    private final ImList<Server> servers;

    Shop(List<Server> servers) {
        //this.servers = ImList.of(servers);
        this(ImList.<Server>of(servers));
    }

    Shop(ImList<Server> servers) {
        this.servers = servers;
    }

    @Override
    public String toString() {
        return this.servers.toString();
    }

    public ImList<Server> getList() {
        return this.servers;
    }

    
    public Shop updateStatus(Server server) {
        ImList<Server> updatedServers = this.servers;
    
        for (int i = 0;i < this.servers.size();i++) {
            if (this.servers.get(i).getID() == server.getID()) {
                updatedServers = updatedServers.set(i,server);
            }
        }

        return new Shop(updatedServers);
    }


    public Server getUpdatedServer(Server s) {
        Server output = s;
        for (int i = 0; i < this.servers.size();i++) {
            if (servers.get(i).getID() == s.getID()) { //it's the same server
                output = servers.get(i);//update the server
            }
        }
        return output;
    }

    //not sure if need this method
    public boolean checkAvailability(double eventtime) {
        int c = 0;
        for (int i = 0; i < this.servers.size(); i++) {
            Server s = this.servers.get(i);
            if (s.isAvailable() && eventtime >=
                s.getNextAvailableTime()) {
                c++;
            }
        }
        return c != 0;
    }
}    
