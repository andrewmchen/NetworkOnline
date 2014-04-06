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
    static int TIMEOUT = 30;
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
        catch (IOException e) {
            System.out.println("IOException in PlayerRunnable with IP " + clientSocket.getInetAddress() + " in game " + gameNumber + ":" + e);
        }
        messageQueue = gameToQueue.get(gameNumber);
    }

    public String listen() throws SocketTimeoutException {
        return listen(TIMEOUT);
    }

    public String listen(int time) throws SocketTimeoutException {
        String message = "";
        try {
            message = ((String) messageQueue.poll(time, TimeUnit.SECONDS));
            if (message == null) {
                throw new SocketTimeoutException();
            }
        }
        catch(InterruptedException e) {}
        return message;
    }

    public void say(String message) throws IOException {
        try {
            messageQueue.put(message);
        }
        catch (InterruptedException e) {}
        long start = System.currentTimeMillis();
        while (!messageQueue.isEmpty()) {
            if (System.currentTimeMillis() - start > 1000*TIMEOUT) {
                throw new SocketTimeoutException();
            }
        }
    }

    @Override
    public void run() {
        String opponentsMove, myMove;
        try {
            if (!start) {
                this.listen(180);
                outStream.println("white");
                outStream.flush();
                // String filename = "log/game/" + System.currentTimeMillis() + "-game" + gameNumber;
                // log = new Log(filename);
                // log.log(otherColor + " is " + clientSocket.getInetAddress());
                // say(filename);
                // Get first players move and reply
                // System.out.println("waiting for move");
                clientSocket.setSoTimeout(1000*TIMEOUT);
                String input = inStream.readLine();
                say(input);
            }
            else {
                outStream.println("black");
                outStream.flush();
                say("Second player in game " + gameNumber + " begins game");
                clientSocket.setSoTimeout(1000*TIMEOUT);
                try {
                    Thread.sleep(100);
                    messageQueue.element();
                    outStream.println("The opponent has quit.");
                    outStream.flush();
                    quit();
                    return;
                } catch (Exception e) {}
                // String filename = this.listen();
                // log = new Log(filename);
                // log.log(otherColor + " is " + clientSocket.getInetAddress());
            }
            while (true) {
                opponentsMove = this.listen();
                outStream.println(opponentsMove);
                outStream.flush();
                // log.log("" + color + " " + opponentsMove);
                String input = inStream.readLine();
                if (input.equals("QUIT")) {
                    quit();
                    return;
                }
                say(input);
            }
        }
        catch (SocketTimeoutException e) {
            System.out.println("Opponent timed out in gameroom " + gameNumber);
            // serverLog.log("Opponent timed out in room " + gameNumber);
            try {
                say("Opponent timed out (30s limit)");
            } catch (IOException ex) {
                outStream.println("Opponent *probably* quit.");
                outStream.flush();
            }
            outStream.println("You have timed out (30s limit).");
            outStream.flush();
        }
        catch (IOException e) {
            outStream.println("Opponent *probably* quit.");
            outStream.flush();
        }
        catch (Exception e) {
            // System.out.println(e);
            // serverLog.log(e.toString());
        }
        finally {
            quit();
            return;
        }
    }

    public void quit() {
        NetworkServer.log.logPrint("Client with IP " + clientSocket.getInetAddress() + " has quit game " + gameNumber);
        outStream.println("Opponent *probably* quit.");
        outStream.flush();
        while (messageQueue.poll() != null) {}
        messageQueue.add("Opponent *probably* quit.");
        try {
            Thread.sleep(50);
        } catch (Exception e) {}
        playersInGame.remove(gameNumber);
        gameToQueue.remove(gameNumber);
        try {
            clientSocket.close();
        }
        catch(IOException e) {}
        return;
    }
}

