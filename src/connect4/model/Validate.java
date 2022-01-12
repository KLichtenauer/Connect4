package connect4.model;

/**
 * Utility class for verifying attributes needed in the project.
 */
public final class Validate {

    private Validate() { } // utility class implies private constructor

    /**
     * Verifying the correctness of the given column.
     *
     * @param col The column to be checked.
     * @return True if column is in between 1 and the highest column of the game
     *         board, false otherwise.
     */
    public static boolean colIsValid(int col) {
        return col >= 1 && col <= Board.COLS;
    }

    /**
     * Verifying the correctness of the given level.
     *
     * @param level The level to be checked.
     * @return True if the level is greater than 0, false otherwise.
     */
    public static boolean levelIsValid(int level) {
        return level >= 1;
    }

    /**
     * Verifying the correctness of the given row.
     *
     * @param row The level to be checked.
     * @return True if the row is in the interval between 1 and the highest row
     *         of the game board, false otherwise.
     */
    public static boolean rowIsValid(int row) {
        return row >= 1 && row <= Board.ROWS;
    }

    /**
     * Verifying if the chosen column is not full, by checking if the highest
     * field in the column is not free. If so the column is full and can not be
     * chosen for another move.
     *
     * @param game The current game.
     * @param col The chosen column.
     * @return True if the column still has room for another tile, false
     *         otherwise.
     */
    public static boolean colIsNotFull(Game game, int col) {
        return game.getSlot(0, col - 1) == Player.NOBODY;
    }
}
