package model;

import javax.swing.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

public class Game implements Board {

    Player[][] gameBoard;
    Player firstPlayer;
    Player currentPlayer;
    int level;
    int diagonals = Board.COLS + Board.ROWS - 1;
    boolean gameIsRunning;
    Set<Coordinates2D> witness;

    // TODO: 14.12.2021 LEVEL WIEDER AUF STANDARD 4 SETZTEN!
    public Game(boolean isFirstPlayerHuman) {
        createBoard();
        level = 1;
        setPlayers(isFirstPlayerHuman);
        gameIsRunning = true;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    private void setPlayers(boolean isFirstPlayerHuman) {
        if (isFirstPlayerHuman) {
            firstPlayer = Player.HUMAN;
            currentPlayer = Player.HUMAN;
        } else {
            firstPlayer = Player.BOT;
            currentPlayer = Player.BOT;
        }
    }

    private void createBoard() {
        gameBoard = new Player[Board.ROWS][Board.COLS];
        for (int i = 0; i < gameBoard.length; i++) {
            for (int j = 0; j < gameBoard[0].length; j++) {
                gameBoard[i][j] = Player.NOBODY;
            }
        }
    }

    @Override
    public Player getFirstPlayer() {
        return firstPlayer;
    }

    // TODO: 11.12.2021 Utility klasse mit überprüfung ob move zulässig ist.
    // TODO: Es sollte dabei gleich überprüft werden ob in der Spalte noch was frei ist.
    // TODO: Nur mgl. wenn er auch dran ist. this.clone() oder nur clone() ???
    @Override
    public Board move(int col) {
        Game gameAfterMove = null;
        try {
            if (Validate.moveIsValid(this, col)) {
                gameAfterMove = (Game) universalMove(col, Player.HUMAN);
            }
        } catch (IllegalMoveException e) {
            e.printStackTrace();
        }
        return gameAfterMove;
    }

    @Override
    public Board machineMove() {
        int valueOfBestMove;
        Node node = new Node(null, level, this);
        currentPlayer = Player.HUMAN;

        return universalMove(node.getChosenCol(), Player.BOT);
    }

    public Board universalMove(int col, Player currentPlayer) {
        Game gameAfterMove = (Game) clone();
        int colConvToIndex = col - 1;
        boolean iterate = true;
        for (int i = Board.ROWS - 1; i >= 0 && iterate; i--) {
            if(gameBoard[i][colConvToIndex] == Player.NOBODY) {
                gameAfterMove.gameBoard[i][colConvToIndex] = currentPlayer;
                iterate = false;
            }
        }
        this.currentPlayer = currentPlayer;
        return gameAfterMove;
    }


    @Override
    public void setLevel(int level) {
        if (Validate.levelIsValid(level)) {
            this.level = level;
        }
    }

    @Override
    public boolean isGameOver() {
        return true;
    }

    @Override
    public Player getWinner() {
        Player winner;
        if(currentPlayer == Player.HUMAN) {
            winner = Player.BOT;
        } else {
            winner = Player.HUMAN;
        }
        return winner;
    }

    @Override
    public Collection<Coordinates2D> getWitness() {
        return null;
    }

    // TODO: 14.12.2021 alle gameBoard[row][col] mit getSlot auswechseln
    // TODO: 16.12.2021     // Validate.colIsValid(col) &&
    //  Validate.rowIsValid(row) ist noch nicht ganz richtig weil col oder row = 0 nicht geht
    @Override
    public Player getSlot(int row, int col) {
        Player player = null;
        if(true) {
            Player content = gameBoard[row][col];
            if (content.equals(Player.BOT)) {
                player = Player.BOT;
            } else if (content.equals(Player.HUMAN)) {
                player = Player.HUMAN;
            } else {
                player = Player.NOBODY;
            }
        }
        return player;
    }

    @Override
    public Board clone() {
        Game clonedBoard;
        try {
            clonedBoard = (Game) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new IllegalArgumentException("Cloning failed");
        }
        clonedBoard.gameBoard = gameBoard.clone();
        for (int i = 0; i < gameBoard.length; i++) {
            clonedBoard.gameBoard[i] = gameBoard[i].clone();
        }
        return clonedBoard;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (int row = 0; row < Board.ROWS; row++) {
            for (int col = 0; col < Board.COLS; col++) {
                builder.append(gameBoard[row][col]);
                if(!(col == Board.COLS - 1)) {
                    builder.append(" ");
                }
            }
            builder.append("\n");
        }
        return builder.toString();
    }

    // TODO: 13.12.2021 groups größe muss wahrscheinlich nicht mehr 7 sein, da
    //  counter max auf  4 gestellt wurde
    private int[][] getHorizontalGroups() {
        int[][] groups = new int[2][7];
        int counter = 1;
        Player currentPlayer = Player.NOBODY;
        for (int row = 0 ; row < ROWS ; row++) {
            for (int col = 0; col < COLS; col++) {
                Player currentField = gameBoard[row][col];
                if (currentField != currentPlayer
                        || col == COLS - 1) {
                    if (col == COLS - 1) {
                        if(currentPlayer == currentField) {
                            counter++;
                        }
                    }
                    if (currentPlayer == Player.BOT) {
                        groups[1][counter - 1]++;
                    } else if (currentPlayer == Player.HUMAN) {
                        groups[0][counter - 1]++;
                    }
                    currentPlayer = currentField;
                    counter = 1;
                } else {
                    if(counter < 4) {
                        counter++;
                    }
                }
            }
        }
        return groups;
    }

    private int[][] getVerticalGroups() {
        int[][] groups = new int[2][6];
        int counter = 1;
        Player currentPlayer = Player.NOBODY;
        for (int col = 0; col < COLS ; col++) {
            for (int row = 0; row < ROWS; row++) {
                Player currentField = gameBoard[row][col];
                if (currentField != currentPlayer
                        || row == ROWS - 1) {
                    if (row == ROWS - 1) {
                        if(currentPlayer == currentField) {
                            counter++;
                        }
                    }
                    if (currentPlayer == Player.BOT) {
                        groups[1][counter - 1]++;
                    } else if (currentPlayer == Player.HUMAN) {
                        groups[0][counter - 1]++;
                    }
                    currentPlayer = currentField;
                    counter = 1;
                } else {
                    if(counter < 4) {
                        counter++;
                    }
                }
            }
        }
        return groups;
    }


    private int[][] getStraightGroups(boolean searchingHorizontal) {
        int i = searchingHorizontal ? 0 : 1;
        int[][] groups = new int[2][7];
        int counter = 1;
        Player currentPlayer = Player.NOBODY;
        for (int row = 0 ; row < ROWS + i; row++) {
            for (int col = 0; col < COLS - i; col++) {
                Player currentField = searchingHorizontal ? gameBoard[row][col] : gameBoard[col][row];
                if (currentField != currentPlayer
                        || col == COLS - i - 1) {
                    if (col == COLS - i - 1) {
                        if(currentPlayer == currentField) {
                            counter++;
                        }
                    }
                    if (currentPlayer == Player.BOT) {
                        groups[1][counter - 1]++;
                    } else if (currentPlayer == Player.HUMAN) {
                        groups[0][counter - 1]++;
                    }
                    currentPlayer = currentField;
                    counter = 1;
                } else {
                    if(counter < 4) {
                        counter++;
                    }
                }
            }
        }
        return groups;
    }

    // TODO: 14.12.2021 currentPlayer und currentField nen besseren Namen geben
    private int[][] getAscendingGroups() {
        int[][] groups = new int[2][6];
        int diagonals = COLS + Board.ROWS - 1;
        for (int i = 0; i < diagonals; i++) {
            int counter = 1;
            Player currentPlayer = Player.NOBODY;
            int col = Integer.max(i - ROWS + 1, 0);
            int row = Integer.max(i - col, 0);
            boolean nextFieldExists = true;
            while (nextFieldExists) {
                Player currentField = gameBoard[row][col];
                if (currentPlayer != currentField || col == COLS - 1
                        || row == 0) {
                    if (col == COLS - 1 || row == 0) {
                        nextFieldExists = false;
                        if(currentPlayer == gameBoard[row][col]) {
                            counter++;
                        }
                    }
                    if (currentPlayer == Player.BOT) {
                        groups[1][counter - 1]++;
                    } else if (currentPlayer == Player.HUMAN) {
                        groups[0][counter - 1]++;
                    }
                    currentPlayer = currentField;
                    counter = 1;
                }
                else {
                    if(counter < 4) {
                        counter++;
                    }
                }
                col++;
                row--;
            }
        }
        return groups;
    }


    private int[][] getDescendingGroups() {
        int[][] groups = new int[2][6];
        for (int i = 0; i < diagonals; i++) {
            int counter = 1;
            Player currentPlayer = Player.NOBODY;
            int col = Integer.max(COLS - 1 - i, 0);
            int row = Integer.max(i - COLS + 1, 0);
            boolean nextFieldExists = true;
            while (nextFieldExists) {
                Player currentField = gameBoard[row][col];
                if (currentPlayer != currentField || col == COLS - 1
                        || row == ROWS - 1) {
                    if (col == COLS - 1 || row == ROWS - 1) {
                        nextFieldExists = false;
                        if(currentPlayer == currentField) {
                            counter++;
                        }
                    }
                    if (currentPlayer == Player.BOT) {
                        groups[1][counter - 1]++;
                    } else if (currentPlayer == Player.HUMAN) {
                        groups[0][counter - 1]++;
                    }
                    currentPlayer = currentField;
                    counter = 1;
                }
                else {
                    if(counter < 4) {
                        counter++;
                    }
                }
                col++;
                row++;
            }
        }
        return groups;
    }

    private int[][] getDiagonalGroups(boolean isAscending) {
        int[][] groups = new int[2][6];
        for (int i = 0; i < diagonals; i++) {
            int counter = 1;
            Player currentPlayer = Player.NOBODY;
            int col = isAscending ? Integer.max(i - ROWS + 1, 0)
                    : Integer.max(COLS - 1 - i, 0);
            int row = isAscending ? Integer.max(i - col, 0)
                    : Integer.max(i - COLS + 1, 0);
            boolean nextFieldExists = true;
            while (nextFieldExists) {
                Player currentField = getSlot(row, col);
                if (currentPlayer != currentField || col == COLS - 1
                        || row == 0 || row == ROWS - 1) {
                    if (col == COLS - 1 || (row == 0 && isAscending)
                            || (row == ROWS - 1 && !isAscending)) { // es liegt einerseits an der row == ROWS - 1 abprüfung
                        nextFieldExists = false;
                        if(currentPlayer == getSlot(row, col)) {
                            counter++;
                        }
                    }
                    if (currentPlayer == Player.BOT) {
                        groups[1][counter - 1]++;
                    } else if (currentPlayer == Player.HUMAN) {
                        groups[0][counter - 1]++;
                    }
                    currentPlayer = currentField;
                    counter = 1;
                }
                else {
                    if(counter < 4) {
                        counter++;
                    }
                }
                col++;
                //just changed
                row = isAscending ? row - 1 : row + 1;
            }
        }
        return groups;
    }


    public int evaluateMove () {
        return getP() + getQ() + getR();
    }

    private int getR() {
        int r = 0;
        if (!gameIsRunning) {
            r = 5000000;
        }
        return r;
    }

    private int getQ() {
        int[][] counter = new int[2][6];
            for (int col = 1; col < COLS - 1; col++) {
                for (int row = ROWS - 1; row >= 0; row--) {
                    Player currentField = gameBoard[row][col];
                if (currentField == Player.HUMAN) {
                    counter[0][col]++;
                } else if (currentField == Player.BOT) {
                    counter[1][col]++;
                } else {
                    break;
                }
            }
        }
        return counter[1][1] + 2 * counter[1][2] + 3 * counter[1][3] + 2
                * counter[1][4] + counter[1][5] - (counter[0][1] + 2
                * counter[0][2] + 3 * counter[0][3] + 2 * counter[0][4]
                + counter[0][5]);
    }

    private int getP() {
        int[][] vertGroups = this.getVerticalGroups();
        int[][] horGroups = this.getHorizontalGroups();
        int[][] ascGroups = this.getAscendingGroups();
        int[][] descGroups = this.getDescendingGroups();

        int h2 = vertGroups[0][1] + horGroups[0][1]
                + ascGroups[0][1] + descGroups[0][1];
        int h3 = vertGroups[0][2] + horGroups[0][2]
                + ascGroups[0][2] + descGroups[0][2];
        int h4 = vertGroups[0][3] + horGroups[0][3]
                + ascGroups[0][3] + descGroups[0][3];

        int m2 = vertGroups[1][1] + horGroups[1][1]
                + ascGroups[1][1] + descGroups[1][1];
        int m3 = vertGroups[1][2] + horGroups[1][2]
                + ascGroups[1][2] + descGroups[1][2];
        int m4 = vertGroups[1][3] + horGroups[1][3]
                + ascGroups[1][3] + descGroups[1][3];

        return 50 + m2 + 4 * m3 + 5000 * m4 - h2 - 4 * h3 - 500000 * h4;
    }


    public static void main(String[] args) {
        Game game = new Game(true);
        game.gameBoard[3][3] = Player.BOT;
        game.gameBoard[5][3] = Player.BOT;
        game.gameBoard[4][3] = Player.BOT;

        int[][] arr1 = game.getDiagonalGroups(true);
        int[][] arr2 = game.getDiagonalGroups(false);
        int[][] arr3 = game.getAscendingGroups();
        int[][] arr4 = game.getDescendingGroups();
        int[][] arr5 = game.getStraightGroups(true);
        int[][] arr6 = game.getStraightGroups(false);
        int[][] arr7 = game.getHorizontalGroups();
        int[][] arr8 = game.getVerticalGroups();
        boolean i = Arrays.deepEquals(arr1, arr3);
        boolean i2 = Arrays.deepEquals(arr2, arr4);
        boolean i3 = Arrays.deepEquals(arr5, arr7);
        boolean i4 = Arrays.deepEquals(arr6, arr8);
    }

}
