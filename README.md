# TrisAppJava

TrisAppJava is a Java application that implements the game of Tris (also known as Tic Tac Toe). The application offers several features, including the possibility to play against another person or against an artificial intelligence, the possibility to choose the difficulty of the AI, score management and much more.

## Resources 
A number of resources are used for the tris, such as images, [icons](https://www.flaticon.com), sound and music files for the game sounds. ([Figma Project](https://www.figma.com/design/bYZojFe9e1x8YV0ZshRBhh/Tris?node-id=0-1&t=GQSYocgY6SxaQhuY-1)

## Requirements

- JDK 19 or higher
- NetBeans IDE

## Installation

To run the application, you must have the Java Development Kit (JDK) installed. The application was developed and tested with JDK 8, but should work with later versions of JDK.

Furthermore, the application was developed using the NetBeans IDE, so it is recommended to use NetBeans to open and edit the project. However, the source code should be compatible with other Java IDEs.

## Project structure

The project is structured in various Java files, each of which has a specific role in the application:

- `TrisGui.java`: This is the main file of the application. It contains the code for the application's GUI and handles user interaction with the application.
- `TrisAI.java`: This file contains the code for the game's artificial intelligence. The AI can play Tris at different difficulty levels.
- `ScoreManager.java`: This file manages the players' scores. The scores are saved in a file and can be viewed by the user.
- `LoginManager.java`: This file manages the login of users. Users can create an account, log in and change their credentials.
- `TrisNormal.java`: This file contains the code for a normal game of Tris. It handles the game logic and checks if there is a winner.

## How to play

After logging in, the user can choose to play a game against another player or against the AI. During the game, players take turns placing their marks on a 3x3 grid. The first player who manages to place three of his or her signs in a row (horizontally, vertically or diagonally) wins the game. If the grid fills up without any of the players winning, the game ends in a draw.


## Licence

TrisAppJava is released under the MIT licence. For more details, please consult the LICENSE file.
