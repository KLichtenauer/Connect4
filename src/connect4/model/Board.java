package connect4.model;

import java.util.Collection;

/**
 * The Connect Four game originally published by Milton Bradley (MB) in 1974.
 * The game is also known as Four in a Row, Four in a Line, Lineup Four, Four
 * Wins, or Captain's Mistress.
 *
 * A human plays against the machine.
 */
public interface Board extends Cloneable {

    /**
     * The number of rows of the game grid. Originally 6.
     */
    int ROWS = 6;

    /**
     * The number of columns of the game grid. Originally 7.
     */
    int COLS = 7;

    /**
     * The number of how many tiles must be lined up to win. Originally 4.
     */
    int CONNECT = 4;

    /**
     * Gets the player who should start or already has started the game.
     *
     * @return The player who makes the initial move.
     */
    Player getFirstPlayer();

    /**
     * Executes a human move. This method does not change the state of this
     * instance, which is treated here as immutable. Instead, a new board/game
     * is returned, which is a copy of {@code this} with the move executed.
     *
     * @param col The column where to put the tile of the human.
     * @return A new board with the move executed. If the move is not valid,
     *         i.e., {@code col} was full before, then {@code null} will be
     *         returned.
     * @throws IllegalMoveException The game is already over, or it is not the
     *         the human's turn.
     * @throws IllegalArgumentException The provided column {@code col} is
     *         invalid, i.e., not found on the grid.
     */
    Board move(int col);

    /**
     * Executes a machine move. This method does not change the state of this
     * instance, which is treated here as immutable. Instead, a new board/game
     * is returned, which is a copy of {@code this} with the move executed.
     *
     * @return A new board with the move executed.
     * @throws IllegalMoveException The game is already over, or it is not the
     *         the machine's turn.
     * @throws InterruptedException {@link Thread#interrupt()} was called on the
     *         executing thread. Thus, the execution stops prematurely.
     */
    Board machineMove() throws InterruptedException;

    /**
     * Sets the skill level of the machine.
     *
     * @param level The skill as number, must be at least 1.
     */
    void setLevel(int level);

    /**
     * Checks if game is over. Either one player has won or there is a tie and
     * all slots are filled with tiles.
     *
     * @return {@code true} if and only if the game is over.
     */
    boolean isGameOver();

    /**
     * Checks if the game state is won.
     *
     * @return The winner or {@code null} in case of a tie or if the game is
     *         not finished yet.
     */
    Player getWinner();

    /**
     * Gets the coordinates of the {@code CONNECT} tiles which are in a line,
     * i.e., a witness of victory. The left lower corner has the smallest
     * coordinates. Should only be called if {@link #getWinner()} returns a
     * value unequal {@code null}. Coordinates are 2-tuples of rows x columns.
     *
     * The result may not be unique!
     *
     * @throws IllegalStateException There is no winner available.
     * @return The list of coordinates.
     */
    Collection<Coordinates2D> getWitness();

    /**
     * Gets the content of the slot at the specified coordinates. Either it
     * contains a tile of one of the two players already or it is empty.
     *
     * @param row The row of the slot in the game grid.
     * @param col The column of the slot in the game grid.
     * @return The slot's content.
     */
    Player getSlot(int row, int col);

    /**
     * Creates and returns a deep copy of this board.
     *
     * @return A clone.
     */
    Board clone();

    /**
     * Gets the string representation of this board as row x column matrix. Each
     * slot is represented by one the three chars '.', 'X', or 'O'. '.' means
     * that the slot currently contains no tile. 'X' means that it contains a
     * tile of the human player. 'O' means that it contains a machine tile. In
     * contrast to the rows, the columns are whitespace separated.
     *
     * @return The string representation of the current Connect Four game.
     */
    @Override
    String toString();

}