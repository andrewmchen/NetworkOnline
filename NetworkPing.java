import java.util.*;
import java.net.*;
import java.io.*;
import java.util.concurrent.*;
public class NetworkPing implements Runnable {
    public HashMap<Integer, Integer> playersInGame;
    public ServerSocket pingSocket;


    public NetworkPing(HashMap<Integer, Integer> playersInGame){
        this.playersInGame = playersInGame;
    }

    @Override
    public void run() {
        try {
            this.pingSocket = new ServerSocket(12344);
            while (true) {
                Socket clientSocket = pingSocket.accept();
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream());                   
                out.println(getGames());
                out.flush();
                clientSocket.close();
            }
        }
        catch (IOException e) {
            System.out.println(e);
        }
    }

    public String getGames() {
        Iterator it = playersInGame.entrySet().iterator();
        String ret = "The following game rooms are open:\n";
        int count = 0;
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry)it.next();
            ret += "GameNumber: "+pairs.getKey() + " has "+pairs.getValue()+" player(s)";
            ret += "\n";
            //it.remove();
        }
        return ret;
    }   
}



