package model;

public class Validate {

    private Validate() { }

    public static boolean colIsValid(int col) {
        if (col >= 1 && col <= Board.COLS) {
            return true;
        } else {
            throw new IllegalArgumentException("Given column is invalid.");
        }
    }

    public static boolean levelIsValid(int level) {
        if (level >= 1) {
            return true;
        } else {
            throw new IllegalArgumentException("Given level is invalid.");
        }
    }

    public static boolean rowIsValid(int row) {
        if (row >= 1 && row <= Board.ROWS) {
            return true;
        } else {
            throw new IllegalArgumentException("Given row is invalid.");
        }
    }

    public static boolean isHumansTurn(Game game) throws IllegalMoveException {
        if(game.getCurrentPlayer() == Player.HUMAN) {
            return true;
        } else {
            throw new IllegalMoveException();
        }
    }

    public static boolean moveIsValid(Game game, int col) throws IllegalMoveException {
        //if (!game.gameIsRunning) { // wurde zum testen raus gelöscht!
        //    throw new IllegalMoveException("Invalid move");
        //    else
            if (!(game.getSlot(0, col - 1) == Player.NOBODY)
                || !colIsValid(col)) {
            throw new IllegalArgumentException("Invalid move!");
        } else {
            return true;
        }
    }

    public static boolean colIsNotFull(Game game, int col) {
        boolean b = game.getSlot(0, col - 1) == Player.NOBODY;
        return b;
    }
}
