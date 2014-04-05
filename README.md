# NetworkOnline

## Introduction
Network Online is a Java program designed to allow Machine Player AIs compete against each other. 
It was made by decompiling the originally made network.class file and adding a drop-in replacement 
for one of the two players that relays the moves to the other player. The program works by relaying the
moves from one MachinePlayer to your opponents MachinePlayer and therefore does not look at your sourcecode at all.
The source to the NetworkOnline is provided in this repository.

## Usage/Installation
NetworkOnline is a simple modified network.class file so to install you need to download the NetworkOnline.class
and move it to the same directory as your Network.class. Then instead of running 
'''bash
java Network machine random
'''

you will run

'''bash
java NetworkClient <gamenumber>
'''

Where gamenumber is the game number you and your partner have agreed upon.
