package org.example;

import java.util.Scanner;

public class PlayerThread extends Thread {
    private final GameBoard board;
    private final Players playerType;
    private final Scanner scanner;

    public PlayerThread(GameBoard board, Players playerType, Scanner scanner) {
        this.board = board;
        this.playerType = playerType;
        this.scanner = scanner;
    }

    @Override
    public void run() {
        boolean gameWon = false;
        while (!gameWon) {
            try {
                int column = getPlayerInput();
                gameWon = board.move(column, playerType);

                if (gameWon) {
                    System.out.println("****** " + playerType + " WINS! ******");
                    break;
                }

            } catch (IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
            } catch (InterruptedException e) {
                System.out.println(playerType + " was interrupted.");
                Thread.currentThread().interrupt();
                break;
            } catch (java.util.InputMismatchException e) {
                System.out.println("Invalid input. Please enter an integer column number.");
                scanner.next();
            }
        }
    }

    private int getPlayerInput() {
        synchronized (scanner) {
            System.out.println("\n" + playerType + ": Enter column number (0-" + (board.getColumns() - 1) + "): ");
            while (!scanner.hasNextInt()) {
                System.out.println("Invalid input. Please enter an integer.");
                scanner.next();
            }
            return scanner.nextInt();
        }
    }
}