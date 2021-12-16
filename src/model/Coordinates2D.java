package model;

public class Coordinates2D<Row, Col> {

    private final int row;
    private final int col;

    // TODO: 16.12.2021 <Row, Col> kann wahrscheinlich weg
    Coordinates2D (int row, int col) {
        this.row = row;
        this.col = col;
    }

    /**
     *
     * @return
     */
    @Override
    public String toString() {
        return "(" + (Board.ROWS - row) + ", " + (col + 1) + ")";
    }

    /**
     *
     * @param comparer
     * @return
     */
    public int compareTo(Coordinates2D comparer) {
        if (comparer == null) {
            throw new IllegalArgumentException();
        }
        int compareResultRow = Integer.compare(comparer.row, row);
        int compareResultCol = Integer.compare(comparer.col, col);
        int result;
        if (compareResultRow != 0) {
            result = compareResultRow;
        } else {
            result = compareResultCol;
        }
        return result;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }
}
