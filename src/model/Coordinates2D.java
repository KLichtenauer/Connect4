package model;

/**
 * This class represents the location of tiles in a readable form and makes it
 * possible to compare them.
 */
public class Coordinates2D implements Comparable<Coordinates2D> {

    private final int row;
    private final int col;

    /**
     * Constructor gets row and col for of a chip in index form and converts
     * them into the more readable form (Chip in bottom left corner has
     * coordinates (1, 1)).
     *
     * @param row The row of the observed tile.
     * @param col The column of the observed tile.
     */
    Coordinates2D(int row, int col) {
        this.row = (Board.ROWS - row);
        this.col = (col);
    }

    /**
     * Transforms the index values of row and col to the corresponding table
     * value and puts it in brackets for better readability.
     *
     * @return The String created by correctly aligning the values and adding
     *         the needed brackets and comma.
     */
    @Override
    public String toString() {
        return "(" + row + ", " + col + ")";
    }

    /**
     * Compares this object with the specified object for order.  Returns a
     * negative integer, zero, or a positive integer as this object is less
     * than, equal to, or greater than the specified object.
     *
     * @param comparator the object to be compared
     * @return A negative integer, zero, or a positive integer as this object
     *         is less than, equal to, or greater than the specified object.
     */
    @Override
    public int compareTo(Coordinates2D comparator) {
        if (comparator == null) {
            throw new IllegalArgumentException();
        }
        int compareResultRow = Integer.compare(row, comparator.getRow());
        int compareResultCol = Integer.compare(col, comparator.getCol());
        int result;
        if (compareResultRow < 0) {
            result = compareResultRow;
        } else {
            result = compareResultCol;
        }
        return result;
    }

    /**
     * Getter for row Value of Coordinate.
     * @return {@code row} value
     */
    public int getRow() {
        return row;
    }

    /**
     * Getter for column Value of Coordinate.
     * @return {@code col} value
     */
    public int getCol() {
        return col;
    }
}
