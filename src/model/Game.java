package model;

import java.util.Collection;

public class Game implements Board {

    String[][] gameBoard;
    Player firstPlayer;
    Player humanPlayer;
    Player botPlayer;
    int level;

    public Game(boolean isFirstPlayerHuman) {
        createBoard();
        level = 4;
        setPlayers(isFirstPlayerHuman);
    }

    private void setPlayers(boolean isFirstPlayerHuman) {
        humanPlayer = new Player();
        botPlayer = new Player();
        if (isFirstPlayerHuman) {
            firstPlayer = humanPlayer;
        } else {
            firstPlayer = botPlayer;
        }
    }

    private void createBoard() {
        gameBoard = new String[Board.ROWS][Board.COLS];
        for (int i = 0; i < gameBoard.length; i++) {
            for (int j = 0; j < gameBoard[0].length; j++) {
                gameBoard[i][j] = ".";
            }
        }
    }

    @Override
    public Player getFirstPlayer() {
        return firstPlayer;
    }

    @Override
    public Board move(int col) {
        return null;
    }

    @Override
    public Board machineMove() {
        return null;
    }

    @Override
    public void setLevel(int level) {
        this.level = level;
    }

    @Override
    public boolean isGameOver() {
        return false;
    }

    @Override
    public Player getWinner() {
        return null;
    }

    @Override
    public Collection<Coordinates2D> getWitness() {
        return null;
    }

    @Override
    public Player getSlot(int row, int col) {
        Player player;
        String content = gameBoard[row][col];
        if (content.equals("O")) {
            player = botPlayer;
        } else if (content.equals("X")) {
            player = humanPlayer;
        } else {
            player = null;
        }
        return player;
    }

    @Override
    public Board clone() {
        Board cloneBoard = new Game(humanPlayer == firstPlayer);
        return null;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < gameBoard.length; i++) {
            for (int j = 0; j < gameBoard[0].length; j++) {
                builder.append(gameBoard[i][j]).append(" ");
            }
            builder.append("\n");
        }
        return builder.toString();
    }
}
