package model;

public class IllegalMoveException extends Exception{


    public IllegalMoveException() {
        super();
    }

    public IllegalMoveException(final String message) {
        super(message);
    }
}
