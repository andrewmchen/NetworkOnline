import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.io.*;
import java.util.concurrent.*;

public class NetworkServer {

    static final String CLIENT_VERSION = "v1.0";

    public HashMap<Integer, Integer> playersInGame;
    public HashMap<Integer, BlockingQueue> gameToQueue;
    public int publicRoom = 0;
    public int serverPort = 12345;
    public ServerSocket serverSocket;
    public static Log log = new Log("log/serverlog");
    public int gamesPlayed = 0;

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

    public int assignGameNumber() {
        if (publicRoom != 0) {
            int ret = publicRoom;
            publicRoom = 0;
            return ret;
        }
        int room;
        while (true) {
            room = (int)(Math.random() * 89999 + 10000);
            if (playersInGame.containsKey(room)) {
                continue;
            }
            publicRoom = room;
            return room;
        }
    }

    public static void main(String[] args) {
        NetworkServer ns = new NetworkServer();
        ns.openServerSocket();
        System.out.println("Server has started");
        if (args.length == 1) {
            int games = Integer.parseInt(args[0]);
            System.out.println("" + games + " games have been played.");
            ns.gamesPlayed = games;
        }
        new Thread(new NetworkPing(ns.playersInGame)).start();

        while (true) {
            Socket clientSocket = null;
            try {
                clientSocket = ns.serverSocket.accept();
                PrintWriter clientSocketOut = new PrintWriter(clientSocket.getOutputStream(), true);
                clientSocketOut.println(CLIENT_VERSION);
                clientSocketOut.flush();
                clientSocket.setSoTimeout(300000);
                int gameNumber = Integer.parseInt(new BufferedReader(new InputStreamReader(clientSocket.getInputStream())).readLine());
                if (gameNumber == 0) {
                    gameNumber = ns.assignGameNumber();
                    log.logPrint("Client has connected with IP " + clientSocket.getInetAddress() + " for public game, assigned to room "+ gameNumber);
                } else {
                    log.logPrint("Client has connected with IP " + clientSocket.getInetAddress() + ", game number is "+ gameNumber);
                }

                if (ns.playersInGame.containsKey(gameNumber)) {
                    int players = ns.playersInGame.get(gameNumber);
                    if (players == 1) {
                        ns.gamesPlayed++;
                        ns.playersInGame.put(gameNumber, 2);
                        log.logPrint("Game " + ns.gamesPlayed + ": Two players in room " + gameNumber + " beginning game");
                        Thread a = new Thread(new PlayerRunnable(clientSocket, true, gameNumber, ns.playersInGame, ns.gameToQueue));
                        a.start();
                    }
                    if (players > 1) {
                        clientSocket.close();  
                        PrintWriter out = new PrintWriter(clientSocket.getOutputStream());
                        out.println("Closing connection, more than two players");
                        out.flush();
                    }
                } else {
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
