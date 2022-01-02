package model;

import java.util.*;

public class Game implements Board {

    private Player[][] gameBoard;
    private Player firstPlayer;
    private Player currentPlayer;
    private int level;
    private boolean gameIsRunning;
    private Set<Coordinates2D> witness;

    // TODO: 14.12.2021 LEVEL WIEDER AUF STANDARD 4 SETZTEN!
    public Game(boolean isFirstPlayerHuman) {
        createBoard();
        level = 1;
        setPlayers(isFirstPlayerHuman);
        gameIsRunning = true;
    }

    /**
     * Getter for current player of {@code this} game.
     *
     * @return {@code currentPlayer}
     */
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

    /**
     * {@inheritDoc}
     */
    @Override
    public Player getFirstPlayer() {
        return firstPlayer;
    }

    // TODO: 20.12.2021 Testen was passiert wenn move nach ende des spiels aufgerufen wird
    // TODO: Es sollte dabei gleich überprüft werden ob in der Spalte noch was frei ist.
    /**
     * {@inheritDoc}
     */
    @Override
    public Board move(int col) {
        Game gameAfterMove = null;
        try {
            if (Validate.moveIsValid(this, col)
                    && Validate.isHumansTurn(this)) {
                gameAfterMove = (Game) universalMove(col);
                gameAfterMove.gameIsRunning = !gameAfterMove.isGameOver();
            }
        } catch (IllegalMoveException e) {
            e.printStackTrace();
        }
        return gameAfterMove;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Board machineMove() {
        Board clone = clone();
        Node node = new Node(null, level, (Game) clone);
        System.out.println(node.getValueOfBoard());
        Game gameAfterMove = (Game) universalMove(node.getChosenCol());
        gameAfterMove.gameIsRunning = !gameAfterMove.isGameOver();
        return gameAfterMove;
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
        }
        // By counting getting the P value, the gameBoard will be checked for
        gameAfterMove.getP();
        return gameAfterMove;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setLevel(int level) {
        if (Validate.levelIsValid(level)) {
            this.level = level;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isGameOver() {
        int humGroup = getNumberOfGroups(4, 0);
        int botGroup = getNumberOfGroups(4, 1);
        boolean isBoardFull = isBoardFull();
        boolean isGameOver = false;
        if(humGroup > 0) {
            isGameOver = true;
            currentPlayer = Player.HUMAN;
        } else if(botGroup > 0) {
            isGameOver = true;
            currentPlayer = Player.BOT;
        } else if(isBoardFull) {
            isGameOver = true;
            currentPlayer = Player.NOBODY;
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

    /**
     * {@inheritDoc}
     */
    @Override
    public Player getWinner() {
        return currentPlayer;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<Coordinates2D> getWitness() {
        return witness;
    }

    // TODO: 16.12.2021     // Validate.colIsValid(col) &&
    //  Validate.rowIsValid(row) ist noch nicht ganz richtig weil col oder row = 0 nicht geht
    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
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

    // TODO: 20.12.2021 "groups" of 1 chip neseccarily dont get counted
    //  correctly because I dont need to. Fürs Doc. Kann man vlt auch irgendwie
    //  beheben. Indem man keine 1er gruppen mehr zählt?
    private int[][] getStraightGroups(boolean searchingHorizontal) {
        int i = searchingHorizontal ? 0 : 1;
        int[][] groups = new int[2][4];
        int counter = 1;
        for (int line = 0 ; line < ROWS + i; line++) {
            Player typeOfCurrentGroup = Player.NOBODY;
            for (int indexOfLineElement = 0; indexOfLineElement < COLS - i;
                 indexOfLineElement++) {
                Player currentField = searchingHorizontal
                        ? getSlot(line, indexOfLineElement)
                        : getSlot(indexOfLineElement, line);
                boolean groupChanged = currentField != typeOfCurrentGroup;
                boolean isEndOfArr = indexOfLineElement == COLS - i - 1;
                boolean isGroupMax = counter < 4;
                if (groupChanged || isEndOfArr) {
                    if (isEndOfArr && !groupChanged && isGroupMax) {
                        counter++;
                    }
                    if (typeOfCurrentGroup == Player.BOT) {
                        groups[1][counter - 1]++;
                    } else if (typeOfCurrentGroup == Player.HUMAN) {
                        groups[0][counter - 1]++;
                    }
                    if(counter == 4 && isWitnessNull() &&
                            typeOfCurrentGroup != Player.NOBODY) {
                        Group group = searchingHorizontal ? Group.HORIZONTAL
                                : Group.VERTICAL;
                        setWitness(line, indexOfLineElement, group);
                    }
                    typeOfCurrentGroup = currentField;
                    counter = 1;
                } else {
                    if(isGroupMax) {
                        counter++;
                    }
                }
            }
        }
        return groups;
    }

    private void setWitness(int line, int indexOfLineElement, Group group) {
        TreeSet<Coordinates2D> witness = new TreeSet<>();
        switch (group) {
            case VERTICAL:
                for (int i = 0; i < 4; i++) {
                    witness.add(new Coordinates2D(indexOfLineElement - i,
                                line));
                }
                break;
            case HORIZONTAL:
                for (int i = 0; i < 4; i++) {
                    witness.add(new Coordinates2D(line,
                            indexOfLineElement - i));
                }
                break;
            case ASCENDING:
                for (int i = 0; i < 4; i++) {
                    witness.add(new Coordinates2D(line + i + 1,
                            indexOfLineElement - i - 1));
                }
                break;
            case DESCENDING:
                for (int i = 0; i < 4; i++) {
                    witness.add(new Coordinates2D(line - i,
                            indexOfLineElement - i));
                }
                break;
        }
        if(isWitnessNull()) {
            this.witness = witness;
        }
    }

    private int[][] getDiagonalGroups(boolean isAscending) {
        int[][] groups = new int[2][4];
        int diagonals = Board.COLS + Board.ROWS - 1;
        for (int i = 0; i < diagonals; i++) {
            int counter = 1;
            Player typeOfCurrentGroup = Player.NOBODY;
            int col = isAscending ? Integer.max(i - ROWS + 1, 0)
                                  : Integer.max(COLS - 1 - i, 0);
            int row = isAscending ? Integer.max(i - col, 0)
                                  : Integer.max(i - COLS + 1, 0);
            boolean nextFieldExists = true;
            while (nextFieldExists) {
                Player currentField = getSlot(row, col);
                boolean isGroupMax = counter < 4;
                boolean groupChanged = currentField != typeOfCurrentGroup;
                boolean isEndRightSide = col == COLS - 1;
                boolean isEndBottom = row == ROWS - 1;
                boolean isEndTop = row == 0;
                if (groupChanged || isEndRightSide || isEndTop || isEndBottom) {
                    if (isEndRightSide || (isEndTop && isAscending)
                            || (isEndBottom && !isAscending)) { // es liegt einerseits an der row == ROWS - 1 abprüfung
                        nextFieldExists = false;
                        if(!groupChanged && isGroupMax) {
                            counter++;
                        }
                    }
                    if (typeOfCurrentGroup == Player.BOT) {
                        groups[1][counter - 1]++;
                    } else if (typeOfCurrentGroup == Player.HUMAN) {
                        groups[0][counter - 1]++;
                    }
                    if(counter == 4 && isWitnessNull()
                            && typeOfCurrentGroup != Player.NOBODY) {
                        Group group = isAscending ? Group.ASCENDING
                                                  : Group.DESCENDING;
                        setWitness(row, col, group);
                    }
                    typeOfCurrentGroup = currentField;
                    counter = 1;
                } else if(isGroupMax) {
                    counter++;
                }
                col++;
                row = isAscending ? row - 1 : row + 1;
            }
        }
        return groups;
    }

    private boolean isWitnessNull() {
        return witness == null;
    }


    public int evaluateGame(boolean isFirstMove) {
        return getP() + getQ() + getR(isFirstMove);
    }

    private int getR(boolean isFirstMove) {
        int r = 0;
        if(currentPlayer == Player.BOT && isFirstMove) {
            int value = getNumberOfGroups(4, 1);
            r = value == 0 ? 0 : 5000000;
        }
        return r;
    }
    
    private int getQ() {
        int[][] counter = numberOfChipsPerCol();
        return counter[1][1] + 2 * counter[1][2] + 3 * counter[1][3] + 2
                * counter[1][4] + counter[1][5] - (counter[0][1] + 2
                * counter[0][2] + 3 * counter[0][3] + 2 * counter[0][4]
                + counter[0][5]);
    }

    private int getP() {
        int h2 = getNumberOfGroups(2, 0);
        int h3 = getNumberOfGroups(3, 0);
        int h4 = getNumberOfGroups(4, 0);
        int m2 = getNumberOfGroups(2, 1);
        int m3 = getNumberOfGroups(3, 1);
        int m4 = getNumberOfGroups(4, 1);
        return 50 + m2 + 4 * m3 + 5000 * m4 - h2 - 4 * h3 - 500000 * h4;
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

    private int getNumberOfGroups(int groupSize, int player) {
        assert groupSize > 1 && groupSize < 5 && (player == 1 || player == 0);
        int[][] vertGroups = this.getStraightGroups(false);
        int[][] horGroups = this.getStraightGroups(true);
        int[][] ascGroups = this.getDiagonalGroups(true);
        int[][] descGroups = this.getDiagonalGroups(false);
        int i = groupSize - 1;
        return vertGroups[player][i] + horGroups[player][i]
                + ascGroups[player][i] + descGroups[player][i];
    }


    public static void main(String[] args) {

        Game game = new Game(true);
        game.gameBoard[0][0] = Player.HUMAN;
        game.gameBoard[0][1] = Player.BOT;
        game.gameBoard[0][2] = Player.HUMAN;
        game.gameBoard[0][3] = Player.BOT;
        game.gameBoard[0][4] = Player.HUMAN;
        game.gameBoard[0][5] = Player.HUMAN;
        game.gameBoard[0][6] = Player.HUMAN;
        //game.gameBoard[3][0] = Player.HUMAN;
        //game.gameBoard[4][0] = Player.HUMAN;
        //game.gameBoard[5][0] = Player.HUMAN;

        Coordinates2D c1 = new Coordinates2D(5,0);
        Coordinates2D c2 = new Coordinates2D(3, 1);
        Coordinates2D c3 = new Coordinates2D(2, 1);
        Coordinates2D c4 = new Coordinates2D(1, 1);

        String init = "";
        String[] k = init.split(" ");

        int i = game.getP();

        //game.gameBoard[5][2] = Player.BOT;
        //game.gameBoard[3][3] = Player.BOT;
        //game.gameBoard[2][3] = Player.BOT;
        //game.gameBoard[4][3] = Player.BOT;
        ////game.gameBoard[1][4] = Player.BOT;
        //game.gameBoard[2][4] = Player.BOT;
        //game.gameBoard[3][4] = Player.BOT;

        //game.setLevel(4);
        game.currentPlayer = Player.BOT;
        Board board = game.machineMove();
        int[][] arr1 = game.getDiagonalGroups(true);
        int[][] arr2 = game.getDiagonalGroups(false);
        //int[][] arr3 = game.getAscendingGroups();
        //int[][] arr4 = game.getDescendingGroups();
        int[][] arr5 = game.getStraightGroups(true);
        int[][] arr6 = game.getStraightGroups(false);
        //int[][] arr7 = game.getHorizontalGroups();
        //int[][] arr8 = game.getVerticalGroups();
        //boolean i = Arrays.deepEquals(arr1, arr3);
        //boolean i2 = Arrays.deepEquals(arr2, arr4);
        //boolean i3 = Arrays.deepEquals(arr5, arr7);
        //boolean i4 = Arrays.deepEquals(arr6, arr8);

        TreeSet set = new TreeSet();
        set.add(4);
        set.add(1);
        System.out.println(set);
    }

}
