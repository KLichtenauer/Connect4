package model;

import javax.swing.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

public class Game implements Board {

    Player[][] gameBoard;
    Player firstPlayer;
    private Player currentPlayer;
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

    // TODO: Es sollte dabei gleich überprüft werden ob in der Spalte noch was frei ist.
    @Override
    public Board move(int col) {
        Game gameAfterMove = null;
        try {
            if (Validate.moveIsValid(this, col)
                    && Validate.isHumansTurn(this)) {
                gameAfterMove = (Game) universalMove(col);
            }
        } catch (IllegalMoveException e) {
            e.printStackTrace();
        }
        return gameAfterMove;
    }

    @Override
    public Board machineMove() {
        Node node = new Node(null, level, this);
        System.out.println(node.valueOfBoard);
        return universalMove(node.getChosenCol());
    }

    // TODO: 17.12.2021 Game is running = false wirft exception 
    // TODO: 17.12.2021 es darf keine Excption geschmissen werden wenn der bot
    //      moves simuliert und welche davon in eine Volle spalte gehen
    // TODO: 17.12.2021 würde hier gerne prüfen ob game noch runnt aber ich
    //      glaube das löst im baum dann exceptions aus, weil da in der vorhersage
    //      nicht überprüft wird wer gerade drann ist
    public Board universalMove(int col) {
        Game gameAfterMove = null;
        if (Validate.colIsValid(col) && gameIsRunning) {
            gameAfterMove = (Game) clone();
            int colConvToIndex = col - 1;
            boolean iterate = true;
            for (int i = Board.ROWS - 1; i >= 0 && iterate; i--) {
                if(getSlot(i, colConvToIndex) == Player.NOBODY) {
                    gameAfterMove.gameBoard[i][colConvToIndex] = currentPlayer;
                    iterate = false;
                }
            }
            if (currentPlayer == Player.BOT) {
                gameAfterMove.currentPlayer = Player.HUMAN;
            } else {
                gameAfterMove.currentPlayer = Player.BOT;
            }
            gameAfterMove.gameIsRunning = !gameAfterMove.isGameOver();
        }
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
        int humGroup = getNumberOfGroups(4, 0);
        int botGroup = getNumberOfGroups(4, 1);
        boolean isBoardFull = isBoardFull();
        boolean isGameOver = false;
        if(isBoardFull) {
            isGameOver = true;
            currentPlayer = Player.NOBODY;
        }
        if(humGroup > 0) {
            isGameOver = true;
            currentPlayer = Player.HUMAN;
        } else if(botGroup > 0) {
            isGameOver = true;
            currentPlayer = Player.BOT;
        }
        return isGameOver;
    }

    private boolean isBoardFull() {
        boolean result = true;
        for (int i = 0; i < COLS && result; i++) {
            if(getSlot(0, i) == Player.NOBODY) {
                result = false;
            }
        }
        return result;
    }

    @Override
    public Player getWinner() {
        return currentPlayer;
    }

    @Override
    public Collection<Coordinates2D> getWitness() {
        return null;
    }

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
                builder.append(getSlot(row, col));
                if(!(col == Board.COLS - 1)) {
                    builder.append(" ");
                }
            }
            builder.append("\n");
        }
        return builder.toString();
    }

    private int[][] getHorizontalGroups() {
        int[][] groups = new int[2][4];
        int counter = 1;
        Player currentPlayer = Player.NOBODY;
        for (int row = 0 ; row < ROWS ; row++) {
            for (int col = 0; col < COLS; col++) {
                Player currentField = getSlot(row, col);
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
        int[][] groups = new int[2][4];
        int counter = 1;
        Player currentPlayer = Player.NOBODY;
        for (int col = 0; col < COLS ; col++) {
            for (int row = 0; row < ROWS; row++) {
                Player currentField = getSlot(row, col);
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
        int[][] groups = new int[2][4];
        int counter = 1;
        Player currentPlayer = Player.NOBODY;
        for (int row = 0 ; row < ROWS + i; row++) {
            for (int col = 0; col < COLS - i; col++) {
                Player currentField = searchingHorizontal ? getSlot(row, col) : getSlot(col, row);
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
        int[][] groups = new int[2][4];
        int diagonals = COLS + Board.ROWS - 1;
        for (int i = 0; i < diagonals; i++) {
            int counter = 1;
            Player currentPlayer = Player.NOBODY;
            int col = Integer.max(i - ROWS + 1, 0);
            int row = Integer.max(i - col, 0);
            boolean nextFieldExists = true;
            while (nextFieldExists) {
                Player currentField = getSlot(row, col);
                if (currentPlayer != currentField || col == COLS - 1
                        || row == 0) {
                    if (col == COLS - 1 || row == 0) {
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
                row--;
            }
        }
        return groups;
    }


    private int[][] getDescendingGroups() {
        int[][] groups = new int[2][4];
        for (int i = 0; i < diagonals; i++) {
            int counter = 1;
            Player currentPlayer = Player.NOBODY;
            int col = Integer.max(COLS - 1 - i, 0);
            int row = Integer.max(i - COLS + 1, 0);
            boolean nextFieldExists = true;
            while (nextFieldExists) {
                Player currentField = getSlot(row, col);
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
        int[][] groups = new int[2][4];
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
                row = isAscending ? row - 1 : row + 1;
            }
        }
        return groups;
    }


    public int evaluateMove () {
        return getP() + getQ() + getR();
    }

    private int getR() {
        //int r = 0;
        //if (!gameIsRunning) {
        //    r = 5000000;
        //}
        //return r;
        int value = getNumberOfGroups(4, 1);
        return value == 0 ? 0 : 5000000;
    }
    
    private int getQ() {
        int[][] counter = numberOfChipsPerCol();
        return counter[1][1] + 2 * counter[1][2] + 3 * counter[1][3] + 2
                * counter[1][4] + counter[1][5] - (counter[0][1] + 2
                * counter[0][2] + 3 * counter[0][3] + 2 * counter[0][4]
                + counter[0][5]);
    }

    private int[][] numberOfChipsPerCol() {
        int[][] counter = new int[2][7];
        for (int col = 0; col < COLS; col++) {
            for (int row = ROWS - 1; row >= 0; row--) {
                Player currentField = getSlot(row, col);
                if (currentField == Player.HUMAN) {
                    counter[0][col]++;
                } else if (currentField == Player.BOT) {
                    counter[1][col]++;
                } else {
                    break;
                }
            }
        }
        return counter;
    }

    // TODO: 16.12.2021 nach testen von den zusammengefassten methoden testen!
    private int getP() {
        int h2 = getNumberOfGroups(2, 0);
        int h3 = getNumberOfGroups(3, 0);
        int h4 = getNumberOfGroups(4, 0);
        int m2 = getNumberOfGroups(2, 1);
        int m3 = getNumberOfGroups(3, 1);
        int m4 = getNumberOfGroups(4, 1);
        return 50 + m2 + 4 * m3 + 5000 * m4 - h2 - 4 * h3 - 500000 * h4;
    }

    private int getNumberOfGroups(int groupSize, int player) {
        assert groupSize > 1 && groupSize < 5 && (player == 1 || player == 0);
        int[][] vertGroups = this.getVerticalGroups();
        int[][] horGroups = this.getHorizontalGroups();
        int[][] ascGroups = this.getAscendingGroups();
        int[][] descGroups = this.getDescendingGroups();
        int i = groupSize - 1;
        return vertGroups[player][i] + horGroups[player][i]
                + ascGroups[player][i] + descGroups[player][i];
    }




    public static void main(String[] args) {
        Game game = new Game(true);
        game.gameBoard[5][6] = Player.HUMAN;
        game.gameBoard[4][6] = Player.HUMAN;
        game.gameBoard[4][5] = Player.HUMAN;
        game.gameBoard[5][5] = Player.HUMAN;
        game.gameBoard[3][5] = Player.HUMAN;
        game.gameBoard[4][4] = Player.HUMAN;
        game.gameBoard[5][1] = Player.HUMAN;
        game.gameBoard[2][2] = Player.HUMAN;
        game.gameBoard[2][3] = Player.HUMAN;

        //game.gameBoard[3][4] = Player.HUMAN;

        //Game game2 = new Game(true);
        game.gameBoard[5][4] = Player.BOT;
        game.gameBoard[3][4] = Player.BOT;
        game.gameBoard[3][3] = Player.BOT;
        game.gameBoard[5][3] = Player.BOT;
        game.gameBoard[4][3] = Player.BOT;
        game.gameBoard[3][2] = Player.BOT;
        game.gameBoard[5][2] = Player.BOT;
        game.gameBoard[4][2] = Player.BOT;
        game.gameBoard[4][1] = Player.BOT;

        //game.gameBoard[4][4] = Player.BOT; // ÜBELTÄTER

        int value = game.evaluateMove();
        game.setLevel(2);
        game.currentPlayer = Player.BOT;
        Board board = game.machineMove();
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
