package connect4.model;

/**
 * Describes an Element of the recursion tree for getting the next optimal move
 * for the bot, depending on the set level. Can simulate both human and bot
 * moves. Tries to get a high value child when the bot moves in the
 * children games (best move for the bot) and a low value child when the human
 * moves in the children games (small number of points for bot equals to good
 * move for human).
 */
public class Node {

    private final Node parent;
    private final Node[] children;
    private final int level;
    private final Game game;
    private int valueOfBoard;
    private int chosenCol;

    /**
     * Constructor sets the parent-node (which is needed in
     * {@link #setChildren()}), level for knowing how deep it has to go and the
     * Game which should be evaluated (basis case, level = 0) or up to the width
     * of the board amount of moves are going to be simulated on it.
     *
     * @param parent The node who has this node as child.
     * @param level The amount of recursive steps need to be done.
     * @param game The current game.
     */
    public Node(Node parent, int level, Game game) {
            this.parent = parent;
            this.level = level;
            this.game = game;
            children = new Node[Board.COLS];
            setChildren();
    }

    /**
     * Manages the recursive creation of the needed children and getting the
     * best values depending on the current player. That is why a distinction is
     * made when it comes to initializing the {@code bestValue}. The column,
     * which creates the most desired board will be saved in {@code chosenCol}.
     * If the level is <= 0 the breaking case was reached because the recursion
     * reached the needed depth and that is why it can just set the
     * {@code valueOfBoard} and do not create any other children.
     */
    private void setChildren() {
        int currentValue = game.evaluateGame(isFirstMove());
        Player currentPlayer = game.getCurrentPlayer();
        boolean isHumanMove = currentPlayer == Player.HUMAN;
        int bestValue = isHumanMove ? Integer.MAX_VALUE : Integer.MIN_VALUE;
        if (level <= 0) {
            valueOfBoard = currentValue;
        } else {
            for (int i = 0; i < Board.COLS; i++) {
                Game cloneForValidation = (Game) game.clone();
                /*
                 * + 1 because in the method universalMove and Validate-class
                 * need a column between 1 - 7
                 */
                if (Validate.colIsNotFull(cloneForValidation, i + 1)) {
                    children[i] = createChild(i + 1);
                    if (children[i] != null) {
                        int valueOfChild = children[i].valueOfBoard;
                        if ((valueOfChild < bestValue && isHumanMove)
                                || (valueOfChild > bestValue && !isHumanMove)) {
                            bestValue = valueOfChild;
                            chosenCol = i;
                        }
                    }
                }
            }
            valueOfBoard = parent != null ? currentValue + bestValue
                    : bestValue;
        }
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
        return this.level == game.getLevel() - 1;
    }

    private Node createChild(int col) {
        assert Validate.colIsValid(col);
        Game boardAfterMove = null;
        if (Validate.moveIsValid(game, col)) {
            boardAfterMove = (Game) game.universalMove(col);
        }
        /*
         * The level value needs to be decreased because it is the heart of the
         * recursion.
         */
        int levelForMethod = level - 1;
        Node node = null;
        if (boardAfterMove != null) {
            node = new Node(this, levelForMethod, boardAfterMove);
        }
        return node;
    }

    /**
     * Getter for the most beneficial column chosen by the bot.
     *
     * @return The {@code chosenCol} for this {@code game}.
     */
    public int getChosenCol() {
        return chosenCol + 1;
    }

    /**
     * Getter for the representative value of the board.
     *
     * @return The value of the current {@code game}.
     */
    public int getValueOfBoard() {
        return valueOfBoard;
    }
}
