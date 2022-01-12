package connect4.model;

import java.util.Objects;

/**
 * This class represents the location of tiles in a readable form and makes it
 * possible to compare them.
 *
 * @param row The needed row component of the coordinate.
 * @param col The needed column component of the coordinate.
 */
public record Coordinates2D(int row,
                            int col) implements Comparable<Coordinates2D> {

    /**
     * Constructor gets row and col for of a chip in index form and converts
     * them into the more readable form (Chip in bottom left corner has
     * coordinates (1, 1)).
     *
     * @param row The row of the observed tile.
     * @param col The column of the observed tile.
     */
    public Coordinates2D(int row, int col) {
        this.row = (Board.ROWS - row);
        this.col = (col + 1);
    }

    /**
     * Transforms the index values of row and col to the corresponding table
     * value and puts it in brackets for better readability.
     *
     * @return The String created by correctly aligning the values and adding
     * the needed brackets and comma.
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
     * Checks for equality of {@code this} any other given {@code Object}, by
     * checking if they have the same type and both row and columna are the
     * same.
     *
     * @param o The {@code Object} for the comparison.
     * @return {@code true} if both elements have the same type, row and column
     *         value, {@code false} otherwise.
     */
    @Override
    public boolean equals(Object o) {
        boolean isEqual = false;
        if (o instanceof Coordinates2D) {
            isEqual = ((Coordinates2D) o).getCol() == col
                    && ((Coordinates2D) o).getRow() == row;
            System.out.println(true);
        } else {
            System.out.println("wtf man pls just work");
        }
        return isEqual;
    }

    /**
     * Creates the needed hash code.
     *
     * @return The hash code depending on {@code row} {@code col}.
     */
    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }

    /**
     * Getter for row Value of Coordinate.
     *
     * @return The {@code row} value.
     */
    public int getRow() {
        return row;
    }

    /**
     * Getter for column Value of Coordinate.
     *
     * @return The {@code col} value.
     */
    public int getCol() {
        return col;
    }
}
