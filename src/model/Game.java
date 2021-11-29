package model;

import java.util.Collection;

public class Game implements Board{

    String[][] gameBoard;

    public Game(){
        createBoard();
    }

    private void createBoard(){
        gameBoard = new String[Board.ROWS][Board.COLS];
        for (int i = 0; i < gameBoard.length; i++) {
            for (int j = 0; j < gameBoard[0].length; j++) {
                gameBoard[i][j] = ".";
            }
        }
    }

    @Override
    public Player getFirstPlayer() {
        return null;
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
        return null;
    }

    @Override
    public Board clone() {
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
        return null;
    }
}
