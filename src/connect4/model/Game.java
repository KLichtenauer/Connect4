package connect4.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Creates and controls the Game and its functionalities like the enemy bot.
 * The bot can change its level which is a direct implication of the difficulty
 * of the computer, because it tells how many moves will be simulated. Out of
 * all the possible moves, the best move for the bot / the worst move for the
 * human will be chosen.
 * The value of a move will be calculated by a steady mini-max algorithm.
 */
public class Game implements Board {

    private Player[][] gameBoard;
    private Player firstPlayer;
    private Player currentPlayer;
    private int level;
    private boolean gameIsRunning;
    private List<Coordinates2D> witness;
    private static final int IMMEDIATE_WIN_BOT = 5_000_000;
    private static final int WIN_BOT = 5000;
    private static final int WIN_HUMAN = 500_000;
    private static final int BASIC_VALUE = 50;
    private static final int GROUP_SIZE_2 = 2;
    private static final int GROUP_SIZE_3 = 3;
    private static final int GROUP_SIZE_4 = 4;
    private static final int MULTIPLIER_FOR_TRIPLE = 4;
    private static final int PLAYER_HUMAN = 0;
    private static final int PLAYER_BOT = 1;
    private static final int INDEX_OF_COLUMN_2 = 1;
    private static final int INDEX_OF_COLUMN_3 = 2;
    private static final int INDEX_OF_COLUMN_4 = 3;
    private static final int INDEX_OF_COLUMN_5 = 4;
    private static final int INDEX_OF_COLUMN_6 = 5;




    /**
     * Creates a game instance by setting the first player to move and the level
     * which equals to the depth of the tree, which the bot uses to calculate
     * his next move.
     *
     * @param isFirstPlayerHuman Tells if the first player is human or not.
     * @param level The level to be set.
     */
    public Game(boolean isFirstPlayerHuman, int level) {
        if (!Validate.levelIsValid(level)) {
            throw new IllegalArgumentException("Level not valid in"
                    + " Constructor");
        }
        createBoard();
        this.level = level;
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

    /**
     * Creates board by instantiating a new Array of {@code Player}'s and
     * filling it up with {@code Player.NOBODY}.
     */
    private void createBoard() {
        gameBoard = new Player[ROWS][COLS];
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

    /**
     * {@inheritDoc}
     */
    @Override
    public Board move(int col) {
        if (!Validate.colIsValid(col)) {
            throw new IllegalArgumentException();
        } else if (!gameIsRunning || currentPlayer != Player.HUMAN) {
            throw new IllegalMoveException();
        }
        Game gameAfterMove = (Game) universalMove(col);
        if (gameAfterMove != null) {
            setGameIsRunning(gameAfterMove);
        }
        return gameAfterMove;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Board machineMove() {
        if (!gameIsRunning || currentPlayer != Player.BOT) {
            throw new IllegalMoveException();
        }
        Game clone = (Game) clone();
        Game gameAfterMove = clone.buildUpGameTree(level).getGame();
        setGameIsRunning(gameAfterMove);
        return gameAfterMove;
    }

    /**
     * Sets for a given {@code Game} the {@code gameIsRunning} attribute.
     *
     * @param gameAfterMove The game on which the attribute should be set.
     */
    private void setGameIsRunning(Game gameAfterMove) {
        gameAfterMove.gameIsRunning = !gameAfterMove.isGameOver();
    }

    /**
     * Manages the recursive creation of the needed children and getting the
     * best values depending on the current player. That is why a distinction is
     * made when it comes to initializing the {@code bestValue}. The column,
     * which creates the most desired board will be indirectly saved with
     * {@code chosenCol}.
     * Can simulate both human and bot moves. Tries to get a high value child
     * when the bot moves in the children games (best move for the bot) and a
     * low value child when the human moves in the children games (small number
     * of points for bot equals to good move for human).
     * If the level is == 0 the breaking case got reached
     * because the recursion reached the needed depth and that is why it can
     * just set the {@code valueOfBoard} and do not create any other children.
     * \n
     * Children get created by simulating up to the width of the board amount of
     * moves and passing the column and level to {@link #createChild(int, int)}
     * (except level == 0).
     *
     * @param currentLevel The current level of the {@code this} game-board.
     *                     Tells how many recursive steps still need to be made.
     * @return The {@code Node} of the simulated moves with the best outcome for
     *         the bot.
     */
    private Node buildUpGameTree(int currentLevel) {
        Node recursNode;
        Game forNode = null;
        int chosenCol;
        int currentValue = evaluateGame(isFirstMove());
        Player currentPlayer = getCurrentPlayer();
        boolean isHumanMove = currentPlayer == Player.HUMAN;
        int bestValue = isHumanMove ? Integer.MAX_VALUE : Integer.MIN_VALUE;
        if (currentLevel == 0) {
            recursNode = new Node(currentValue, this);
        } else {
            Node[] children = new Node[COLS];
            for (int i = 0; i < COLS; i++) {
                Game cloneForValidation = (Game) clone();
                /*
                 * + 1 because in the method universalMove and Validate-class
                 * need a column between 1 - 7
                 */
                if (Validate.colIsNotFull(cloneForValidation, i + 1)) {
                    children[i] = createChild(i + 1, currentLevel);
                    if (children[i] != null) {
                        int valueOfChild = children[i].getValueOfNode();
                        if ((valueOfChild < bestValue && isHumanMove)
                                || (valueOfChild > bestValue && !isHumanMove)) {
                            bestValue = valueOfChild;
                            chosenCol = i;
                            forNode = (Game) universalMove(chosenCol + 1);
                        }
                    }
                }
            }

            currentValue = currentLevel != level ? currentValue + bestValue
                    : bestValue;
            recursNode = new Node(currentValue, forNode);
        }
        return recursNode;
    }

    /**
     * Creates {@code Node} children by making valid moves and calling
     * {@link #buildUpGameTree(int)} recursively.
     *
     * @param col The column for simulated move.
     * @param currentLevel The current level of recursion. Needs to be decreased
     *                     because it is the parameter the recursion is
     *                     dedicated on.
     * @return {@code Node} after recursions of lower levels come back.
     */
    private Node createChild(int col, int currentLevel) {
        assert Validate.colIsValid(col);
        Game boardAfterMove;
        /*
         * The level value needs to be decreased because it is the heart of the
         * recursion.
         */
        int levelForRecursion = currentLevel - 1;
        Node node = null;
        Game cloneForValidation = (Game) clone();
        if (Validate.colIsNotFull(cloneForValidation, col)) {
            Game gameForNewNode = (Game) clone();
            boardAfterMove = (Game) gameForNewNode.universalMove(col);
            node = boardAfterMove.buildUpGameTree(levelForRecursion);
        }
        return node;
    }

    /**
     * Figuring out which simulated move is the first one is important for
     * resolving the points amount a board gets. Because if an immediate win is
     * possible for the bot he gets 5_000_000 extra points.
     *
     * @return If the currently simulated move is the first by comparing the
     *         current and default level.
     */
    private boolean isFirstMove() {
        return this.level == this.getLevel() - 1;
    }

    /**
     * Responsible for performing a move. Throws depending on the
     * {@code currentPlayer} a {@code Player.HUMAN} or {@code Player.BOT} tile
     * in the given column, if the column is not full and has a valid value.
     *
     * @param col The given column for the next move.
     * @return A new board instance with the made move.
     */
    public Board universalMove(int col) {
        if (!Validate.colIsValid(col)) {
            throw new IllegalArgumentException("Invalid column given.");
        }
        Game cloneForValidation = (Game) clone();
        Game gameAfterMove = null;
        if (Validate.colIsNotFull(cloneForValidation, col)) {
            // Creating clone for new instance after move.
            gameAfterMove = (Game) clone();
            int colConvToIndex = col - 1;
            boolean iterate = true;
            for (int i = ROWS - 1; i >= 0 && iterate; i--) {
                if (getSlot(i, colConvToIndex) == Player.NOBODY) {
                    gameAfterMove.gameBoard[i][colConvToIndex] = currentPlayer;
                    iterate = false;
                }
            }
            if (currentPlayer == Player.BOT) {
                gameAfterMove.currentPlayer = Player.HUMAN;
            } else {
                gameAfterMove.currentPlayer = Player.BOT;
            }
            /*
             * By getting the P value, the gameBoard will be checked if a group
             * of 4 or more tiles exists.
             */
            gameAfterMove.getP();
        }
        return gameAfterMove;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setLevel(int level) {
        if (Validate.levelIsValid(level)) {
            this.level = level;
        } else {
            throw new IllegalArgumentException("Invalid level");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isGameOver() {
        int humGroup = getNumberOfGroups(CONNECT, 0);
        int botGroup = getNumberOfGroups(CONNECT, 1);
        boolean isBoardFull = isBoardFull();
        boolean isGameOver = false;
        if (humGroup > 0) {
            // Human wins.
            isGameOver = true;
            currentPlayer = Player.HUMAN;
        } else if (botGroup > 0) {
            // Bot wins.
            isGameOver = true;
            currentPlayer = Player.BOT;
        } else if (isBoardFull) {
            // Nobody wins => Tie.
            isGameOver = true;
            currentPlayer = Player.NOBODY;
        }
        return isGameOver;
    }

    /**
     * Checks if the current game - board is full by looking up the fields in
     * the first row. If every field is full, there can't fit in another tile
     * and the board is full.
     *
     * @return True if board is full, false otherwise.
     */
    private boolean isBoardFull() {
        boolean result = true;
        for (int i = 0; i < COLS && result; i++) {
            if (getSlot(0, i) == Player.NOBODY) {
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

    /**
     * {@inheritDoc}
     */
    @Override
    public Player getSlot(int row, int col) {
        Player player = null;
        if (Validate.colIsValid(col + 1) && Validate.rowIsValid(row + 1)) {
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
        // Deep cloning needed.
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
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                builder.append(getSlot(row, col));
                if (!(col == COLS - 1)) {
                    builder.append(" ");
                }
            }
            builder.append("\n");
        }
        return builder.toString();
    }

    /**
     * If I search horizontal {@code i} has the value 0 because in the first I
     * want to search through every column in each row, which means first loop
     * iterates rows and second columns. If I search horizontal {@code i} has
     * the value 1 because I want to search through every row in each column,
     * which means the first loop iterates (rows + 1 =) columns and second loop
     * (columns - 1 =) rows.
     * After the way of iteration is set, the tiles from the same {@code Player}
     * gets counted and the maximal groups get saved in a array which measrues
     * the amounts of groups from size 1 - needed group size to win. Higher
     * groups don't get detected because there is no need to, if it is higher it
     * still equals the same points.
     *
     * @param searchingHorizontal Telling the search direction. If true,
     *                            horizontal groups will be searched, vertical
     *                            groups otherwise.
     * @return An 2 dimensional array with 2 rows. The groups for
     *         {@code Player.HUMAN} will be saved in the first row
     *         ({@code groups[0]}) and groups for {@code Player.BOT} will be
     *         saved in the second row ({@code groups[1]}).
     */
    private int[][] getStraightGroups(boolean searchingHorizontal) {
        int i = searchingHorizontal ? 0 : 1;
        int[][] groups = new int[2][CONNECT];
        int counter = 1;
        for (int line = 0; line < ROWS + i; line++) {
            Player typeOfCurrentGroup = Player.NOBODY;
            for (int indexOfLineElement = 0; indexOfLineElement < COLS - i;
                 indexOfLineElement = indexOfLineElement + 1) {
                Player currentField = searchingHorizontal
                        ? getSlot(line, indexOfLineElement)
                        : getSlot(indexOfLineElement, line);
                boolean groupChanged = currentField != typeOfCurrentGroup;
                boolean isEndOfArr = indexOfLineElement == COLS - i - 1;
                boolean isGroupMax = counter < CONNECT;
                /*
                 * groupChanged or isEndOfArr equals true implies the current
                 * group must be saved.
                 */
                if (groupChanged || isEndOfArr) {
                    if (isEndOfArr && !groupChanged && isGroupMax) {
                        counter++;
                    }
                    if (typeOfCurrentGroup == Player.BOT) {
                        groups[1][counter - 1]++;
                    } else if (typeOfCurrentGroup == Player.HUMAN) {
                        groups[0][counter - 1]++;
                    }
                    /*
                     * The if below implies a group of 4 was found from a player
                     * unequal to Player.NOBODY, which means a winner was found.
                     */
                    if (counter == CONNECT && isWitnessNull()
                            && typeOfCurrentGroup != Player.NOBODY) {
                        Group group = searchingHorizontal ? Group.HORIZONTAL
                                : Group.VERTICAL;
                        /*
                         * groupChanged equals true says that the group does not
                         * lay on the border of the array and a move was done
                         * before finding out a group of min 4 was found. That's
                         * why 1 must be subtracted from the index.
                         */
                        if (groupChanged) {
                            setWitness(line, indexOfLineElement
                                    - 1, group);
                        } else {
                            setWitness(line, indexOfLineElement, group);
                        }
                    }
                    typeOfCurrentGroup = currentField;
                    counter = 1;
                } else {
                    if (isGroupMax) {
                        counter++;
                    }
                }
            }
        }
        return groups;
    }

    /**
     * Saves winning group in a {@code TreeSet}.
     * The exact starting point of the winning group is given by the attributes.
     * Depending on the {@code Group}-type, the group will then be completed by
     * iterating individually through the board and converting each tile
     * locations to a {@code Coordinates2D}.
     *
     * @param line Each group lies on a specific line. A vertical group lies on
     *             a vertical line, an ascending on an ascending line etc.
     *             The attribute shows in which line the search for the winning
     *             group starts.
     * @param indexOfLineElement The index of the first tile in the given
     *                           {@code line} for the exact starting point.
     * @param group The type of the group. Important for the correct iteration
     *              through the field, for finding the winning-groups tiles.
     */
    private void setWitness(int line, int indexOfLineElement, Group group) {
        ArrayList<Coordinates2D> witness = new ArrayList<>();
        switch (group) {
            case VERTICAL:
                for (int i = 0; i < CONNECT; i++) {
                    witness.add(new Coordinates2D(indexOfLineElement - i,
                            line));
                }
                break;
            case HORIZONTAL:
                for (int i = 0; i < CONNECT; i++) {
                    witness.add(0, new Coordinates2D(line,
                            indexOfLineElement - i));
                }
                break;
            case ASCENDING:
                for (int i = 0; i < CONNECT; i++) {
                    witness.add(0, new Coordinates2D(line + i + 1,
                            indexOfLineElement - i));
                }
                break;
            case DESCENDING:
                for (int i = 0; i < CONNECT; i++) {
                    witness.add(new Coordinates2D(line - i,
                            indexOfLineElement - i));
                }
                break;
            default:
                throw new IllegalArgumentException("Unexpected group given.");
        }
        if (isWitnessNull()) {
            this.witness = witness;
        }
    }

    /**
     * Getter for the current level chosen by the user.
     *
     * @return The value of the current {@code level}.
     */
    public int getLevel() {
        return level;
    }

    /**
     * Depending on the type of diagonal group (ascending or descending) the row
     * and column iterators will be assigned and iterated individually. \n
     * The iteration of one diagonal will stop if one of the borders of the
     * board got reached. This will be checked by the boolean expressions
     * {@code isEndRightSide}, {@code isEndBottom} and {@code isEndTop}.
     * The ascending groups have the borders top and right side while
     * descending groups have the borders right side and bottom and will
     * therefore be checked for these.
     * Found groups will be saved in an integer
     * array, one column for each group size. Higher groups don't get detected
     * because there is no need to, if it is higher it still equals the same
     * points.
     *
     * @param isAscending Telling the search direction. If true,
     *                    ascending groups will be searched, descending
     *                    groups otherwise.
     * @return An 2 dimensional array with 2 rows. The groups for
     *         {@code Player.HUMAN} will be saved in the first row
     *         ({@code groups[0]}) and groups for {@code Player.BOT} will be
     *         saved in the second row ({@code groups[1]}).
     */
    private int[][] getDiagonalGroups(boolean isAscending) {
        int[][] groups = new int[2][CONNECT];
        int diagonals = COLS + ROWS - 1;
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
                boolean isGroupNotMax = counter < CONNECT;
                boolean groupChanged = currentField != typeOfCurrentGroup;
                boolean isEndRightSide = col == COLS - 1;
                boolean isEndBottom = row == ROWS - 1;
                boolean isEndTop = row == 0;
                /*
                 * If groupChanged or asking if it is the end of the array
                 * equals true implies the current group must be saved.
                 */
                if (groupChanged || isEndRightSide || isEndTop || isEndBottom) {
                    if (isEndRightSide || (isEndTop && isAscending)
                            || (isEndBottom && !isAscending)) {
                        nextFieldExists = false;
                        if (!groupChanged && isGroupNotMax) {
                            counter++;
                        }
                    }
                    if (typeOfCurrentGroup == Player.BOT) {
                        groups[1][counter - 1]++;
                    } else if (typeOfCurrentGroup == Player.HUMAN) {
                        groups[0][counter - 1]++;
                    }
                    if (counter == CONNECT && isWitnessNull()
                            && typeOfCurrentGroup != Player.NOBODY) {
                        Group group = isAscending ? Group.ASCENDING
                                                  : Group.DESCENDING;
                        if (groupChanged) {
                            setWitness(row, col - 1, group);
                        } else {
                            setWitness(row, col, group);
                        }
                    }
                    typeOfCurrentGroup = currentField;
                    counter = 1;
                } else if (isGroupNotMax) {
                    counter++;
                }
                col++;
                row = isAscending ? row - 1 : row + 1;
            }
        }
        return groups;
    }

    /**
     * Checks if {@code witness} equals null.
     *
     * @return True if {@code witness} equals null, false otherwise.
     */
    private boolean isWitnessNull() {
        return witness == null;
    }


    /**
     * Unites the different evaluation formulas for calculating the game's
     * current board-value.
     *
     * @param isFirstMove Tells if the board which calls the methode is the
     *                    first simulated move. That's important because the R
     *                    formula only applies if the bot can win in one move,
     *                    which means the winning simulated move is first.
     * @return The current board-value, depending on a constant evaluation
     *         formula.
     */
    public int evaluateGame(boolean isFirstMove) {
        return getP() + getQ() + getR(isFirstMove);
    }

    private int getR(boolean isFirstMove) {
        int r = 0;
        /*
         * Checks for currentPlayer == Player.HUMAN, because this means the last
         * move was made by the bot and the board has to be evaluated for the in
         * perspective of the bot.
         */
        if (currentPlayer == Player.HUMAN && isFirstMove) {
            int value = getNumberOfGroups(CONNECT, 1);

            r = (value == 0) ? 0 : IMMEDIATE_WIN_BOT;
        }
        return r;
    }

    private int getQ() {
        int[][] counter = numberOfChipsPerCol();
        return counter[PLAYER_BOT][INDEX_OF_COLUMN_2]
                + 2 * counter[PLAYER_BOT][INDEX_OF_COLUMN_3]
                + 3 * counter[PLAYER_BOT][INDEX_OF_COLUMN_4]
                + 2 * counter[PLAYER_BOT][INDEX_OF_COLUMN_5]
                + counter[PLAYER_BOT][INDEX_OF_COLUMN_6]
                - (counter[PLAYER_HUMAN][INDEX_OF_COLUMN_2]
                + 2 * counter[PLAYER_HUMAN][INDEX_OF_COLUMN_3]
                + 3 * counter[PLAYER_HUMAN][INDEX_OF_COLUMN_4]
                + 2 * counter[PLAYER_HUMAN][INDEX_OF_COLUMN_5]
                + counter[PLAYER_HUMAN][INDEX_OF_COLUMN_6]);
    }

    private int getP() {
        int h2 = getNumberOfGroups(GROUP_SIZE_2, PLAYER_HUMAN);
        int h3 = getNumberOfGroups(GROUP_SIZE_3, PLAYER_HUMAN);
        int h4 = getNumberOfGroups(GROUP_SIZE_4, PLAYER_HUMAN);
        int m2 = getNumberOfGroups(GROUP_SIZE_2, PLAYER_BOT);
        int m3 = getNumberOfGroups(GROUP_SIZE_3, PLAYER_BOT);
        int m4 = getNumberOfGroups(GROUP_SIZE_4, PLAYER_BOT);
        return BASIC_VALUE + m2 + MULTIPLIER_FOR_TRIPLE * m3 + WIN_BOT * m4 - h2
                - MULTIPLIER_FOR_TRIPLE * h3 - WIN_HUMAN * h4;
    }

    private int[][] numberOfChipsPerCol() {
        int[][] counter = new int[2][COLS];
        /*
         * Iterating through each column for counting the placed chips. Starts
         * at the bottom of each column so no tile was found it can break.
         */
        for (int col = 0; col < COLS; col++) {
            for (int row = ROWS - 1; row >= 0; row--) {
                Player currentField = getSlot(row, col);
                if (currentField == Player.HUMAN) {
                    counter[PLAYER_HUMAN][col]++;
                } else if (currentField == Player.BOT) {
                    counter[PLAYER_BOT][col]++;
                } else {
                    break;
                }
            }
        }
        return counter;
    }

    /**
     * Gets all groups of given size and player.
     *
     * @param groupSize The size of searched groups.
     * @param player The player the searched groups belong to 0 stands for human
     *               and 1 stands for bot.
     * @return The number of groups fitting the given criteria.
     */
    private int getNumberOfGroups(int groupSize, int player) {
        assert groupSize > 1 && groupSize <= CONNECT
                && (player == 1 || player == 0);
        int[][] vertGroups = this.getStraightGroups(false);
        int[][] horGroups = this.getStraightGroups(true);
        int[][] ascGroups = this.getDiagonalGroups(true);
        int[][] descGroups = this.getDiagonalGroups(false);
        int i = groupSize - 1;
        return vertGroups[player][i] + horGroups[player][i]
                + ascGroups[player][i] + descGroups[player][i];
    }
}
