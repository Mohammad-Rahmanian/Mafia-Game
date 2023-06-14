# Mafia Game 

## Description
The Mafia Game is a Java project that implements a multi-threaded socket programming architecture, allowing clients to connect to a server and participate in a virtual social game. The game assigns players different roles and revolves around the objective of the Townspeople to identify and eliminate the Mafia members through strategic discussions and voting.

## Features
- **Client-Server Architecture:** The game follows a client-server model where multiple clients can connect to a central server to play the game.
- **Multi-threaded Design:** The server utilizes multi-threading to handle multiple client connections concurrently, enabling simultaneous gameplay among multiple players.
- **Socket Programming:** The project employs Java socket programming to establish communication channels between the server and clients, enabling real-time interaction and updates.
- **Object-Oriented Approach:** The game utilizes object-oriented principles to model different roles, allowing each player to have a specific role with unique abilities and responsibilities.
- **Day-Night Cycle:** The game implements a day-night cycle, where players engage in discussions and voting during the day while the Mafia members perform covert actions during the night.
- **Real-time Updates:** The server provides real-time updates to all connected clients, ensuring that everyone stays informed about the game's progress and the actions of other players.
- **Player Actions:** Players can perform various actions during their turn, such as voting to eliminate a player, using special abilities unique to their assigned role, or engaging in strategic conversations with other players.
- **Win Conditions:** The game incorporates win conditions for both the Townspeople and the Mafia, providing a challenging and dynamic gameplay experience.


## Usage
1. Clone the repository: `git clone https://github.com/your-username/mafia-game.git`
2. Navigate to the project directory: `cd mafia-game`
3. Compile the Java source files: `javac *.java`
4. Start the server: `java Server`
5. Clients can connect to the server using the provided IP address and port number.
6. Enjoy playing the Mafia Game with your friends!
