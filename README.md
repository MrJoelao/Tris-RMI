# Tris RMI - A Distributed Tic-Tac-Toe Game

## Overview
Tris RMI is a distributed Tic-Tac-Toe game implemented in Java using Remote Method Invocation (RMI). The project consists of a server application that manages game logic, player authentication, and score tracking, and a client application that connects to the server to play the game.

## Features
- **Multiplayer Mode**: Play against another player over the network
- **AI Mode**: Play against an AI with adjustable difficulty levels
- **Score Tracking**: Track player victories and defeats
- **User Authentication**: Secure login system with password strength validation
- **Server Management**: GUI for server control and monitoring

## Components
### Server
- **TrisServer**: Main server class that implements RMI interface
- **TrisServerGUI**: Graphical interface for server management
- **ScoreManager**: Manages player scores and statistics
- **LoginManager**: Handles user authentication and registration

### Game Logic
- **TrisNormal**: Implements standard Tic-Tac-Toe game logic for multiplayer
- **TrisAI**: Implements AI opponent with three difficulty levels

### Client Interface
- **ServerConnectionDialog**: Handles server connection and configuration
- **TrisServerInterface**: RMI interface for client-server communication

## Installation
1. Clone the repository
2. Compile the Java source files
3. Start the server using `TrisServerGUI`
4. Connect clients using the `ServerConnectionDialog`

## Usage
### Server
- Start the server using the GUI
- Monitor connections and game activity through the log
- Manage server port and restart as needed

### Client
- Connect to the server using IP and port
- Login or register new account
- Choose between multiplayer or AI mode
- Play Tic-Tac-Toe and track your scores

## Technical Details
- **RMI**: Used for client-server communication
- **Serialization**: Used for saving game scores and user credentials
- **Swing**: Used for graphical interfaces
- **AI Algorithm**: Uses minimax algorithm with alpha-beta pruning for optimal moves

## Requirements
- Java 8 or higher
- RMI registry running on the server machine

## Contributors
This project was developed as a school exercise by:
- Joel S
- Jacapo L

## License
This project is for educational purposes only. All rights reserved to the authors.
