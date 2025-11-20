package org.example;

import java.util.Scanner;

public class Game {
    public static void main(String[] args) {
        GameBoard gameBoard = new GameBoard(7, 6);
        Scanner sharedScanner = new Scanner(System.in);

        PlayerThread player1 = new PlayerThread(gameBoard, Players.PLAYER_1, sharedScanner);
        PlayerThread player2 = new PlayerThread(gameBoard, Players.PLAYER_2, sharedScanner);

        player1.start();
        player2.start();

        try {
            player1.join();
            player2.join();
        } catch (InterruptedException e) {
            System.out.println("Game main thread interrupted.");
        }

        System.out.println("Game Over.");
        sharedScanner.close();

//        GameBoard gameBoard = new GameBoard(7, 6);
//        Scanner scanner = new Scanner(System.in);
//        Players currentPlayer = Players.PLAYER_1;
//        boolean gameWon = false;
//
//        System.out.println("Connect 4 Game Start!");
//
//        while (!gameWon) {
//            boolean moveMade = false;
//            while (!moveMade) {
//                System.out.println("\n" + currentPlayer + "'s turn. Enter column (0-6): ");
//
//                if (!scanner.hasNextInt()) {
//                    System.out.println("Invalid input. Please enter an integer column number.");
//                    scanner.next();
//                    continue;
//                }
//
//                int column = scanner.nextInt();
//
//                try {
//                    gameWon = gameBoard.move(column, currentPlayer);
//                    moveMade = true;
//
//                } catch (IllegalArgumentException e) {
//                    System.out.println("Error: " + e.getMessage());
//                }
//            }
//
//            if (gameWon) {
//                System.out.println("****** " + currentPlayer + " WINS! ******");
//            } else {
//                // Switch players for the next iteration of the *outer* while loop
//                currentPlayer = (currentPlayer == Players.PLAYER_1) ? Players.PLAYER_2 : Players.PLAYER_1;
//            }
//        }
//
//        scanner.close();
//        System.out.println("Game Over.");
//    }
    }
}
