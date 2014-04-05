import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.io.*;
import java.util.concurrent.*;

public class NetworkServer {
    public HashMap<Integer, Integer> playersInGame;
    public HashMap<Integer, BlockingQueue> gameToQueue;
    public int serverPort = 12345;
    public ServerSocket serverSocket;

    public NetworkServer() {
        playersInGame = new HashMap<Integer, Integer>();
        gameToQueue = new HashMap<Integer, BlockingQueue>();
    }
    public void openServerSocket() {
        try {
            this.serverSocket = new ServerSocket(this.serverPort);
        }
        catch (IOException e) {
            throw new RuntimeException("Cannot open port 12345", e);
        }
    }

    public static void main(String[] args) {
        NetworkServer ns = new NetworkServer();
        ns.openServerSocket();
        System.out.println("Server has started");
        new Thread(new NetworkPing(ns.playersInGame)).start();
        while (true) {
            Socket clientSocket = null;
            try {
                clientSocket = ns.serverSocket.accept();
                clientSocket.setSoTimeout(300000);
                System.out.println("Client has connected");
                int gameNumber = Integer.parseInt(new BufferedReader(new InputStreamReader(clientSocket.getInputStream())).readLine());
                System.out.println("Game number is "+ gameNumber);
                if (ns.playersInGame.containsKey(gameNumber)) {
                    int players = ns.playersInGame.get(gameNumber);
                    if (players == 1) {
                        ns.playersInGame.put(gameNumber, 2);
                        System.out.println("Two players in " + gameNumber + " beginning game");
                        Thread a = new Thread(new PlayerRunnable(clientSocket, true, gameNumber, ns.playersInGame, ns.gameToQueue));
                        a.start();
                    }
                    if (players > 1) {
                        clientSocket.close();  
                        PrintWriter out = new PrintWriter(clientSocket.getOutputStream());
                        out.println("Closing connection more than two players");
                        out.flush();
                        System.out.println("More than two players in game");
                    }
                }
                else {
                    ns.playersInGame.put(gameNumber, 1);
                    ns.gameToQueue.put(gameNumber, new LinkedBlockingQueue<String>());
                    Thread b = new Thread(new PlayerRunnable(clientSocket, false, gameNumber, ns.playersInGame, ns.gameToQueue));
                    b.start();
                }
            }
            catch (IOException e) {
                //handle error
            }
        }
    }
}
