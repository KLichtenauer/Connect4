package connect4.model;

/**
 * Exception for making a move which is not allowed.
 */
public class IllegalMoveException extends RuntimeException {

    /**
     * Will be thrown if an illegal move was done, which means the game is
     * already over, or it is not the human's turn.
     */
    public IllegalMoveException() {
        super();
    }

    /**
     * Will be thrown if an illegal move was done, which means the game is
     * already over, or it is not the human's turn.
     *
     * @param message The message given for a more precise picture of the error.
     */
    public IllegalMoveException(final String message) {
        super(message);
    }
}
