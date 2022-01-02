package model;

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

    // TODO: 14.12.2021 move kann illegalArugmentException schmeißen, was damit tun?
    // TODO: 17.12.2021 schauen wenn game over ist nichts mehr machen!
    // TODO: 17.12.2021 überprüfen ob mehrere Spiele hintereinadner ausgeführt
    //  werden können bzw die einstellungen gespeichert werden

    /**
     * Manages the recursive creation of the needed children and getting the
     * best values depending on the current player. That is why a distinction is
     * made when it comes to initializing the {@code bestValue}. The column,
     * which creates the most desired board will be saved in {@code chosenCol}.
     * If the level is <= 0 the breaking case was reached because the recursion
     * reached the needed depth and that is why it can just set the
     * {@code valueOfBoard} and do not create any other children.
     */
    void setChildren() {
        int currentValue = game.evaluateGame(parent == null);
        int col = 0;
        if (level <= 0) {
            valueOfBoard = currentValue;
        } else if (game.getCurrentPlayer() == Player.HUMAN) { //human move
            int minValue = Integer.MAX_VALUE;
            for (int i = 0; i < Board.COLS; i++) {
               children[i] = createChild(i + 1);
                int value = children[i].valueOfBoard;
                if (value < minValue) {
                    minValue = value;
                    col = i;
                }
            }
            valueOfBoard = parent != null ? currentValue + minValue : minValue;
        } else { // bot move
            int maxValue = Integer.MIN_VALUE;
            for (int i = 0; i < Board.COLS; i++) {
                children[i] = createChild(i + 1);
                int value = children[i].valueOfBoard;
                if (value > maxValue) {
                    maxValue = value;
                    col = i;
                }
            }
            valueOfBoard = parent != null ? currentValue + maxValue : maxValue;
        }
        chosenCol = col + 1; // + 1 because in the method machineMove
                             // universalMove needs an col between 1 - 7
    }

    void setChildren2() {
        int col = 0;
        int currentValue = game.evaluateGame(parent == null);
        Player currentPlayer = game.getCurrentPlayer();
        boolean isHumanMove = currentPlayer == Player.HUMAN;
        int bestValue = isHumanMove ? Integer.MAX_VALUE : Integer.MIN_VALUE;
        if (level <= 0) {
            valueOfBoard = currentValue;
        } else {
            for (int i = 0; i < Board.COLS; i++) {
                Game cloneForMethod = (Game) game.clone();
                if(Validate.colIsNotFull(cloneForMethod,i + 1)) {
                    children[i] = createChild(i + 1);
                    int valueOfChild = children[i].valueOfBoard;
                    if ((valueOfChild < bestValue && isHumanMove)
                            || (valueOfChild > bestValue && !isHumanMove)) {
                        bestValue = valueOfChild;
                        col = i;
                    }
                }
            }
            valueOfBoard = parent != null ? currentValue + bestValue : bestValue;
        }
        // + 1 because in the method machineMove universalMove needs a column
        // between 1 - 7
        chosenCol = col + 1;
    }

    // TODO: 22.12.2021 getChosenCol wird nicht stimmen!
    void setChildrenChosenColStattCol() {
        int currentValue = game.evaluateGame(parent == null);
        Player currentPlayer = game.getCurrentPlayer();
        boolean isHumanMove = currentPlayer == Player.HUMAN;
        int bestValue = isHumanMove ? Integer.MAX_VALUE : Integer.MIN_VALUE;
        if (level <= 0) {
            valueOfBoard = currentValue;
        } else {
            for (int i = 0; i < Board.COLS; i++) {
                // TODO: 22.12.2021 daniel oder so fragen ob das fürs klassengeheimnis benötigt wird!
                Game cloneForValidation = (Game) game.clone();
                if(Validate.colIsNotFull(cloneForValidation,i + 1)) {
                    children[i] = createChild(i + 1);
                    int valueOfChild = children[i].valueOfBoard;
                    if ((valueOfChild < bestValue && isHumanMove)
                            || (valueOfChild > bestValue && !isHumanMove)) {
                        bestValue = valueOfChild;
                        chosenCol = i;
                    }
                }
            }
            valueOfBoard = parent != null ? currentValue + bestValue : bestValue;
        }
        // + 1 because in the method machineMove universalMove needs a column
        // between 1 - 7
    }

    private Node createChild(int col) {
        Game boardAfterMove = (Game) game.universalMove(col);
        int levelForMethod = level - 1;
        return new Node(this, levelForMethod, boardAfterMove);
    }

    /**
     * Getter for the most beneficial column chosen by the bot.
     *
     * @return The {@code chosenCol} for this {@code game}.
     */
    // TODO: 22.12.2021 wenn die 3. variante von der setCHildren Mehtode
    //  genommen wird msus chosenCol + 1 zurück gegeben werden!
    public int getChosenCol() {
        return chosenCol;
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
