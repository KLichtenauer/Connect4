package connect4.view;

import connect4.model.Board;
import connect4.model.Coordinates2D;
import connect4.model.Game;
import connect4.model.Player;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * {@code BoardPenal} gets the gridlayout and thus sets the foundation of
 * the game board. By filling it with {@code Slot}'s, the board is fully
 * initialized. Moves will be managed by repainting the board after each move.
 * The bot-move is running parallel for maintaining the ability to interact with
 * the settings panel while a move gets calculated.
 */
public class BoardPanel extends JPanel {

    private static final String GAME_OVER = "Game over";

    // Attributes needed for the controller classes interaction with the model.
    private static final String INVALID_MOVE = "Invalid move";
    private static final int DEFAULT_LEVEL = 4;
    private static int setLevel = DEFAULT_LEVEL;
    private boolean isFirstPlayerHuman = true;
    private boolean isCurrentPlayerHuman = true;
    private Board game = new Game(true, setLevel);
    private List<Coordinates2D> witness;
    private Slot[][] slots;

    // Attributes responsible for the machine move and the corresponding thread.
    private MachineThread machineThread;
    private boolean threadIsRunning;

    /**
     * Constructs the panel: sets the background, basic settings and body of
     * {@code this} panel.
     */
    BoardPanel() {
        game = new Game(true, DEFAULT_LEVEL);
        slots = new Slot[Board.ROWS][Board.COLS];
        setSize(getPreferredSize());
        setLayout(new GridLayout(Board.ROWS, Board.COLS));
        setVisible(true);
        setBackground(Color.BLUE);
        fillingBoard();
        repaint();
    }

    /**
     * Sets a {@code Slot} in each panel of the board and fills up the
     * {@code slots} array.
     */
    private void fillingBoard() {
        for (int row = 0; row < Board.ROWS; row++) {
            for (int col = 0; col < Board.COLS; col++) {
                Slot slot = new Slot(row, col);
                add(slot);
                slots[row][col] = slot;
                slot.addMouseListener(new MoveListener());
            }
        }
    }

    /**
     * Stops the thread gracefully.
     */
    public void endMachineThread() {
        if (threadIsRunning) {
            machineThread.interrupt();
        }
        threadIsRunning = false;
    }

    private class MoveListener extends MouseAdapter {

        /**
         * Performs the move for the column, the clicked slot lies in.
         * Correctly differentiates and handles multiple error states that
         * can occur by showing a popup with a fitting error message.
         *
         * @param e The event of interest.
         */
        public void mouseClicked(MouseEvent e) {
            if (e == null) {
                throw new IllegalArgumentException("Given value is null!");
            }
            if (game.isGameOver()) {
                popup("Invalid move, the game is already over.",
                        INVALID_MOVE);
            } else {

                /* Differentiating if it is humans turn to move. */
                if (!isCurrentPlayerHuman) {
                    popup("Wait until bot moved!", INVALID_MOVE);
                } else {
                    Board gameAfterMove = game.move(((Slot) e.getComponent())
                            .getCol() + 1);

                    /* Differentiating if move was valid or not. */
                    if (gameAfterMove == null) {
                        popup("Invalid move, please chose again.",
                                INVALID_MOVE);
                    } else {
                        witness = (java.util.List<Coordinates2D>) gameAfterMove
                                .getWitness();
                        smartRepaint(gameAfterMove);

                        /*
                         * Differentiating if the game is over after the
                         * last move or a machine move has to be made.
                         */
                        if (game.isGameOver()) {
                            if (game.getWinner() == Player.HUMAN) {
                                popup("Congratulations, you won!",
                                        GAME_OVER);
                            } else {
                                popup("Tie", GAME_OVER);
                            }
                        } else {
                            controlMachineMove();
                        }
                    }
                }
            }
        }
    }

    /**
     * Only repaints tiles when they got changed in the given
     * {@code gameAfterMove} board or the witnesses have to be painted.
     *
     * @param gameAfterMove The game which should be repainted smartly.
     */
    private void smartRepaint(Board gameAfterMove) {
        for (int row = 0; row < Board.ROWS; row++) {
            for (int col = 0; col < Board.COLS; col++) {
                Slot slotForOperation = slots[row][col];
                if (tileChanged(gameAfterMove, row, col)) {
                    slotForOperation.setPlayer(gameAfterMove.getSlot(row, col));
                }
                if (tileIsWitness(gameAfterMove, row, col)) {
                    slotForOperation.setWitness();
                }
            }
        }
        game = gameAfterMove;
    }

    private boolean tileIsWitness(Board gameAfterMove, int row, int col) {
        boolean tileIsWitness = false;
        witness = (List<Coordinates2D>) gameAfterMove.getWitness();
        Coordinates2D c = new Coordinates2D(row, col);
        if (witness != null) {
            tileIsWitness = witness.contains(c);
        }
        return tileIsWitness;
    }

    private boolean tileChanged(Board gameAfterMove, int row, int col) {
        return !(gameAfterMove.getSlot(row, col) == game.getSlot(row, col));
    }

    /**
     * Regulates the machine moves by creating a new {@code MachineThread}
     * instance and starting it.
     */
    private void controlMachineMove() {
        machineThread = new MachineThread();
        machineThread.start();
    }

    /**
     * Responsible for creating and performing a Thread specific for the machine
     * moves. {@code InterruptedException} gets thrown when the thread should be
     * stopped immediately, e.g. new game or switch button gets pressed.
     */
    private class MachineThread extends Thread {

        /**
         * Performs the machine move for the thread. Gets called when
         * the thread gets started.
         */
        @Override
        public void run() {
            threadIsRunning = true;
            isCurrentPlayerHuman = false;
            Board gameAfterMove;
            try {
                gameAfterMove = game.machineMove();
                smartRepaint(gameAfterMove);
                if (gameAfterMove.isGameOver()) {
                    if (gameAfterMove.getWinner() == Player.BOT) {
                        popup("The bot won, good luck next time.",
                                GAME_OVER);
                    } else {
                        popup("Tie", GAME_OVER);
                    }
                }
            } catch (InterruptedException e) {
                return;
            }
            threadIsRunning = false;
            isCurrentPlayerHuman = true;
        }
    }

    /**
     * Creates a popup with message and title.
     *
     * @param message The message the popup should contain.
     * @param title The title the popup should get.
     */
    private void popup(String message, String title) {
        JOptionPane.showConfirmDialog(this, message, title,
                JOptionPane.DEFAULT_OPTION);
    }

    /**
     * Gets called by the new game button. Creates a new game and repaints the
     * board.
     */
    public void newGame() {
        endMachineThread();
        Board newGame = new Game(isFirstPlayerHuman, setLevel);
        isCurrentPlayerHuman = isFirstPlayerHuman;
        setNewGame(newGame);
    }

    /**
     * Sets the games intern and views current level.
     *
     * @param newLevel The level to be set.
     */
     void setLevel(int newLevel) {
        if (newLevel < 1) {
            throw new IllegalArgumentException("Invalid level given!");
        }
        if (game != null) {
            setLevel = newLevel;
            game.setLevel(setLevel);
        }
    }

    /**
     * Creates a new game with the needed changes of the switch command.
     */
    void switchGame() {
        endMachineThread();
        isFirstPlayerHuman = !isFirstPlayerHuman;
        Board newGame = new Game(isFirstPlayerHuman, setLevel);
        isCurrentPlayerHuman = isFirstPlayerHuman;
        setNewGame(newGame);
    }

    private void setNewGame(Board newGame) {
        if (isFirstPlayerHuman) {
            smartRepaint(newGame);
        } else {
            smartRepaint(new Game(false, setLevel));
            controlMachineMove();
        }
    }
}