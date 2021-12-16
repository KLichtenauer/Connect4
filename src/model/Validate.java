package model;

public class Validate {

    private Validate() {}

    public static boolean colIsValid(int col) {
        if (col >= 1 && col <= 7 ) {
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

    public static boolean rowIsValid(int col) {
        if (col >= 1 && col <= 6) {
            return true;
        } else {
            throw new IllegalArgumentException("Given row is invalid.");
        }
    }

    // TODO: 14.12.2021 Illegal move exception muss auch mal geschmissen werden
    //blic static boolean moveIsValid (Game game, int col) {
    //  boolean moveIsValid = false;
    //  if (game.gameIsRunning && game.gameBoard[0][col - 1] == Player.NOBODY
    //          && colIsValid(col)) { //&& game.humansTurn wurde zum testen raus gelöscht!
    //      moveIsValid = true;
    //  } else {
    //      throw new IllegalArgumentException("Invalid move!");
    //  }
    //  return moveIsValid;
    //

    public static boolean moveIsValid (Game game, int col) throws IllegalMoveException {
        boolean moveIsValid;
        if (!game.gameIsRunning /*|| game.currentPlayer != Player.HUMAN*/) { // wurde zum testen raus gelöscht!
            throw new IllegalMoveException("Invalid move");
        } else if (!(game.gameBoard[0][col - 1] == Player.NOBODY)
                || !colIsValid(col)) {
            throw new IllegalArgumentException("Invalid move!");
        } else {
            moveIsValid = true;
        }
        return moveIsValid;
    }

}
