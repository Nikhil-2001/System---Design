package org.example;

import java.util.ArrayList;
import java.util.List;

public class GameBoard {
    private final List<List<Players>> columns;
    private Players currentPlayer = Players.PLAYER_1;
    private final int rows;

    public GameBoard(int columns, int rows) {
        this.rows = rows;
        this.columns = new ArrayList<>();

        for (int i = 0; i < columns; ++i) {
            this.columns.add(new ArrayList<>());
        }
    }

    public int getRows() {
        return rows;
    }

    public int getColumns() {
        return columns.size();
    }

    public Players getCell(int x, int y) {
        assert(x >= 0 && x < getColumns());
        assert(y >= 0 && y < getRows());

        List<Players> column = columns.get(x);

        if (column.size() > y) {
            return column.get(y);
        } else {
            return null;
        }
    }

    public synchronized boolean move(int x, Players player) throws InterruptedException {
        while (this.currentPlayer != player) {
            System.out.println(player + " is waiting for " + this.currentPlayer + " to move...");
            this.wait();
        }

        assert(x >= 0 && x < getColumns());
        List<Players> columnList = columns.get(x);

        if (columnList.size() >= this.rows) {
            this.notifyAll();
            throw new IllegalArgumentException("That column is full. Try another column.");
        }

        columnList.add(player);
        boolean win = checkWin(x, columnList.size() - 1, player);

        // 3. Switch turns
        if (!win) { // Only switch turns if the game is ongoing
            this.currentPlayer = (player == Players.PLAYER_1) ? Players.PLAYER_2 : Players.PLAYER_1;
        }

        // 4. Notify the waiting thread that the board state has changed
        this.notifyAll();

        return win;
    }

    private boolean checkLine(int x1, int y1, int xDiff, int yDiff, Players player) {
        for (int i = 0; i < 4; ++i) {
            int x = x1 + (xDiff * i);
            int y = y1 + (yDiff * i);

            if (x < 0 || x > columns.size() - 1) {
                return false;
            }

            if (y < 0 || y > rows - 1) {
                return false;
            }

            if (player != getCell(x, y)) {
                return false;
            }
        }

        return true;
    }

    private boolean checkWin(int x, int y, Players player) {
        if (checkLine(x, y, 0, -1, player)) {
            return true;
        }

        for (int offset = 0; offset < 4; ++offset) {
            if (checkLine(x - 3 + offset, y, 1, 0, player)) {
                return true;
            }

            if (checkLine(x - 3 + offset, y + 3 - offset, 1, -1, player)) {
                return true;
            }

            if (checkLine(x - 3 + offset, y - 3 + offset, 1, 1, player)) {
                return true;
            }
        }

        return false;
    }
}
