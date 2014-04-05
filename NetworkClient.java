import java.lang.reflect.Constructor;
import network.X2;
import network.X3;
import network.X6;
import network.X7;
import player.*;
import java.net.*;
import java.io.*;

final class NetworkClient {
    static final double ooox = 5.5D;
    static double time = 0.0D;
    static boolean quit = false;
    static boolean waitKey = false;
    static int optionOffset = 0;
    static boolean server = false;
    static Socket opponentSocket = null;
    static ServerSocket serverSocket = null;
    static PrintWriter out = null;
    static BufferedReader in = null;

    private static String playGame() {
        int color = 1;
        try {
            String input = NetworkClient.in.readLine(); 
            if (input.equals("white")) {
                color = 1;
                System.out.println("Setting color to white");
            }
            else if (input.equals("black")) {
                color = 0;
                System.out.println("Setting color to black");
            }
            else {
                System.out.println(input);
                throw new IOException("No color was received");
            }
        }
        catch (IOException e) {
            System.out.println(e);
        } 
        catch (Exception e) {
            System.out.println("More than two players for this game number");
            System.exit(0);
        }
        Object localObject1 = null;
        Object localObject2 = null;
        if (color == 1) {
            localObject1 = new MachinePlayer(color);
            ((Player) localObject1).myName = "My Machine";
            localObject2 = new NetworkPlayer();
            ((Player) localObject2).myName = "Other Machine";
        }
        if (color == 0) {
            localObject2 = new MachinePlayer(color);
            ((Player) localObject2).myName = "My Machine";
            localObject1 = new NetworkPlayer();
            ((Player) localObject1).myName = "Other Machine";
        }
        X3 localX3 = null;
        if (color == 1) {
            localX3 = new X3((Player)localObject1, (Player)localObject2, true, time);
        } else {
            localX3 = new X3((Player)localObject1, (Player)localObject2, true, time);
        }

        X3.setWait(waitKey);
        while (localX3.xooox()) {
            localX3.oxxxo();
        }
        return localX3.xxoooName;
    }

    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.out.println("Usage To Play Game: java NetworkClient <gamenumber>");
            System.out.println();
            opponentSocket = new Socket("localhost", 12344);
            in = new BufferedReader(new InputStreamReader(opponentSocket.getInputStream()));
            StringBuilder output = new StringBuilder();
            int next = in.read();
            while(next != -1){
                output.append((char)next);
                next = in.read();
            }
            System.out.println(output.toString());
            System.exit(1);
        }
        try {
            opponentSocket = new Socket("localhost", 12345);
            out = new PrintWriter(opponentSocket.getOutputStream(), true);                   
            in = new BufferedReader(new InputStreamReader(opponentSocket.getInputStream()));
            System.out.println("You've connected to " + opponentSocket.getInetAddress() + " to play. Waiting for second player...");
            System.out.println("Joining game number " + args[optionOffset]);
            out.println(args[optionOffset]);
            out.flush();
        } catch (UnknownHostException e) {
            System.err.println("Don't know about input host");
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection");
            System.exit(1);
        } catch (Exception e) {
            System.out.println("More than 2 players for this game number");
        }

        try {
            playGame();
        }
        catch (Exception localException1) {
            localException1.printStackTrace();
        }
        
        if (quit) {
            System.exit(0);
        }
    }

    static class NetworkPlayer extends Player {

        public Move chooseMove() {
            try {
                String input;
                input = NetworkClient.in.readLine();
                String[] inputLine = input.split(" ");
                if (inputLine[0].equals("[add")) {
                    int x = Character.getNumericValue(inputLine[2].charAt(0));
                    int y = Character.getNumericValue(inputLine[2].charAt(1));
                    return new Move(x, y);
                }
                if (inputLine[0].equals("[step")) {
                    int x2 = Character.getNumericValue(inputLine[2].charAt(0));
                    int y2 = Character.getNumericValue(inputLine[2].charAt(1));
                    int x1 = Character.getNumericValue(inputLine[4].charAt(0));
                    int y1 = Character.getNumericValue(inputLine[4].charAt(1));
                    return new Move(x1, y1, x2, y2);
                }
            } catch (IOException e) {
                System.err.println("Something went wrong trying to receive a Move.");
                System.exit(1);
            }
            return new Move();
        }

        public boolean opponentMove(Move m) {
            NetworkClient.out.write(m.toString() + "\r\n");
            NetworkClient.out.flush();
            return true;
        }

        public boolean forceMove(Move m) {
            return false;
        }
    }
}


