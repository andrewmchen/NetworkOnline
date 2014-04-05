import java.util.*;
import java.net.*;
import java.io.*;
import java.util.concurrent.*;

public class PlayerRunnable implements Runnable {
    public Socket clientSocket;
    public boolean start;
    public int gameNumber;
    public HashMap<Integer, Integer> playersInGame;
    public HashMap<Integer, BlockingQueue> gameToQueue;
    public PrintWriter outStream;
    public BufferedReader inStream;
    public BlockingQueue messageQueue;
    public String color;
    public String otherColor;
    public Log log;
    // public Log serverLog = new Log("log/serverlog");

    public PlayerRunnable(Socket clientSocket, boolean start, int gameNumber, HashMap<Integer, Integer> playersInGame, HashMap<Integer, BlockingQueue> gameToQueue) {
        this.clientSocket = clientSocket;
        this.start = start;
        this.gameNumber = gameNumber;
        this.playersInGame = playersInGame;
        this.gameToQueue = gameToQueue;
        if (!start) {
            color = "BLACK";
            otherColor = "WHITE";
        } else {
            color = "WHITE";
            otherColor = "BLACK";
        }
        try {
            this.outStream = new PrintWriter(clientSocket.getOutputStream());                   
            this.inStream = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        }
        catch(IOException e) {
            System.out.println(e);
            // serverLog.log(e.toString());
        }
        messageQueue = gameToQueue.get(gameNumber);
    }

    public String listen() {
        String message = "";
        try {
            message = ((String) messageQueue.take());
        }
        catch(InterruptedException e) {
            System.out.println(e);
            // serverLog.log(e.toString());
        }
        return message;
    }

    public void say(String message) {
        try {
            messageQueue.put(message);
        }
        catch (InterruptedException e) {
            System.out.println(e);
            // serverLog.log(e.toString());
        }
        while (!messageQueue.isEmpty()) {}
    }

    @Override
    public void run() {
        String opponentsMove, myMove;
        try {
            if (!start) {
                System.out.println(this.listen());
                outStream.println("white");
                outStream.flush();
                //String filename = "log/game/" + System.currentTimeMillis() + "-game" + gameNumber;
                //log = new Log(filename);
                //log.log(otherColor + " is " + clientSocket.getInetAddress());
                //say(filename);
                // Get first players move and reply
                // System.out.println("waiting for move");
                String input = inStream.readLine();
                say(input);
            }
            else {
                outStream.println("black");
                outStream.flush();
                say("Second player in game " + gameNumber + " begins game");
                //String filename = this.listen();
                //log = new Log(filename);
                //log.log(otherColor + " is " + clientSocket.getInetAddress());
            }
            clientSocket.setSoTimeout(30000);
            while (true) {
                opponentsMove = this.listen();
                outStream.println(opponentsMove);
                outStream.flush();
      //          log.log("" + color + " " + opponentsMove);
                String input = inStream.readLine();
                if (input.equals("QUIT")) {
                    quit();
                    return;
                }
                say(input);
            }
        }
        catch (SocketTimeoutException e) {
            System.out.println("Opponent timed out");
            // serverLog.log("Opponent timed out in room " + gameNumber);
            outStream.println("The opponent has quit");
            outStream.flush();
            quit();
            return;
        }
        catch (IOException e) {
            System.out.println(e);
            // serverLog.log(e.toString());
        }
        catch (Exception e) {
            System.out.println(e);
            // serverLog.log(e.toString());
            quit();
            return;
        }
    }

    public void quit() {
        System.out.println("Person quit");
        outStream.println("QUIT");
        outStream.flush();
        playersInGame.remove(gameNumber);
        gameToQueue.remove(gameNumber);
        try {
            clientSocket.close();
        }
        catch(IOException e) {
            System.out.println(e);
        }
        return;
    }
}

