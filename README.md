# Network Online
## Introduction
Network Online is a Java program designed to allow Machine Player AIs compete against each other over the internet. 
It was made by decompiling the original Network.class file and adding a new Player which we have called NetworkPlayer. The program works by relaying the Move passed into NetworkPlayer.opponentMove to the other side's NetworkPlayer.chooseMove, and therefore does not look at your source code at all.
The source to the NetworkOnline is provided in this repository.

## Usage/Installation
NetworkOnline is a simple modified Network.class file. To install, you need to download the NetworkClient.class and NetworkPlayer.class
and move it to the same directory as your Network.class. The links are as follows:

http://www.ocf.berkeley.edu/~amchen/NetworkClient.class
http://www.ocf.berkeley.edu/~amchen/NetworkPlayer.class

Or alternatively cd, to your project folder (where Network.class) belongs and run the following commands:
```bash
curl -o "NetworkClient.class" http://www.ocf.berkeley.edu/~amchen/NetworkClient.class
curl -o "NetworkPlayer.class" http://www.ocf.berkeley.edu/~amchen/NetworkPlayer.class
```

To play, instead of running
```bash
java Network machine random
```

you will run

```bash
java NetworkClient GAMENUMBER
```

where GAMENUMBER is the game number you and your partner have agreed upon. Or, if you're looking for a random match, omit GAMENUMBER to see a list of all open game rooms and then choose one. To open a random game room, use any arbitrary GAMENUMBER.


- - - 
By Andrew Chen and Nathan Wong

