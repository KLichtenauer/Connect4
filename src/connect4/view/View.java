package connect4.view;

import connect4.model.Board;
import connect4.model.Coordinates2D;
import connect4.model.Game;
import connect4.model.Player;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.*;
import java.util.List;

import static java.awt.RenderingHints.KEY_ANTIALIASING;
import static java.awt.RenderingHints.KEY_RENDERING;
import static java.awt.RenderingHints.VALUE_ANTIALIAS_ON;
import static java.awt.RenderingHints.VALUE_RENDER_QUALITY;

/**
 * The view is responsible for the creation of the user interaction platform and
 * its management. It is build up by a {@code JFrame} and {@code JPanel} for the
 * basic fundament. The {@code JPanel} gets a border layout. The board will be
 * implemented in the north and the settings bar in the south of the border
 * layout.
 * Moves will be managed by repainting the board after each move. The bot-move
 * is running parallel for maintaining the ability to interact with the settings
 * panel while a move gets calculated.
 */
public class View extends JFrame {

    private final String GAME_OVER = "Game over";

    // Predefined colors for each element
    private final static Color BACKGROUND = Color.BLUE;
    private final static Color HUMAN_CHIP = Color.YELLOW;
    private final static Color BOT_CHIP = Color.RED;
    private final static Color NOBODY_CHIP = Color.WHITE;
    private final static Color WITNESS = Color.DARK_GRAY;
    private final static Color CIRCLE = Color.BLACK;

    // Attributes needed for the controller classes interaction with the model.
    private static final int DEFAULT_LEVEL = 4;
    private static int setLevel = DEFAULT_LEVEL;
    private boolean isFirstPlayerHuman = true;
    private Board game = new Game(true, DEFAULT_LEVEL);
    private JPanel board;

    // Attributes responsible for the machine move and the corresponding thread.
    private MachineThread machineThread;

    /**
     * Constructs the GUI-Programm by calling {@link #initComponents()}.
     */
    public View() {
        initComponents();
    }

    /**
     * Initializes the components of the view.
     */
    private void initComponents() {
        // Container for game board and option panel.
        Container container = getContentPane();
        setPreferredSize(new Dimension(720, 600));
        container.setLayout(new BorderLayout());
        container.setVisible(true);
        // Panel which is containing the board.
        board = new BoardPanel();
        container.add(board, BorderLayout.CENTER);
        // Panel which is containing all components of the settings section.
        JPanel settings = new SettingsPanel();
        container.add(settings, BorderLayout.SOUTH);
        // Standard commands for the frame.
        pack();
        setTitle("connect4");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
        // TODO: 26.01.2022 Wo packt man den windowlistener am schönsten hin?
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                machineThread.endMachineThread();
            }
        });
    }

    /**
     * {@code BoardPenal} gets the gridlayout and thus sets the foundation of
     * the game board. By filling it with {@code Slot}'s, the board is fully
     * initialized.
     */
    private class BoardPanel extends JPanel {

        /**
         * Constructs the panel which contains the game board by calling
         * {@link #initBoardPanel()}.
         */
        private BoardPanel() {
            initBoardPanel();
        }

        /**
         * Sets the background, basic settings and body of {@code this} panel.
         */
        private void initBoardPanel() {
            setSize(getPreferredSize());
            setLayout(new GridLayout(Board.ROWS, Board.COLS));
            setVisible(true);
            setBackground(BACKGROUND);
            fillingBoard();
            repaint();
        }

        /**
         * Sets a {@code Slot} in each panel of the board.
         */
        private void fillingBoard() {
            for (int row = 0; row < Board.ROWS; row++) {
                for (int col = 0; col < Board.COLS; col++) {
                    add(new Slot(row, col));
                }
            }
        }
    }

    /**
     * Slots are the main component in the board. They represent each tile and
     * perform a move in the corresponding clicked column.
     */
    private class Slot extends JPanel {
        private static final double FACTOR_FOR_WITNESS_LOCATION = 0.25;
        private static final double FACTOR_FOR_TILE = 0.94;
        private static final String INVALID_MOVE = "Invalid move";
        private final int row;
        private final int col;

        /**
         * Creates a {@code Slot} at the given coordinate of the board.
         *
         * @param row The row of the slot to create.
         * @param col The column of the slot to create.
         */
        private Slot(int row, int col) {
            this.row = row;
            this.col = col;
            addMouseListener(new MoveListener());
            setBackground(BACKGROUND);
        }

        private class MoveListener extends MouseAdapter {

            /**
             * Checks first if game is over. If so a popup gets generated with
             * the proper error message.
             * Otherwise, the game makes the move and safes
             * the {@code Board} after the move with the given column in a
             * separate attribute. That's because there are two cases where we
             * don't want the bot to move immediately after the human move.
             * First case: the game after move is {@code null}. An invalid move
             * was made and the player can choose again or, second case: the
             * game after the move could be over.
             * If neither of these cases happen the machine move can be called.
             *
             * @param e The event of interest.
             */
            public void mouseClicked(MouseEvent e) {
                if (game.isGameOver()) {
                    popup("Invalid move, the game is already"
                                    + " over.",
                            INVALID_MOVE);
                } else {
                    Board gameAfterMove = game.move(col + 1);
                    /* Differentiating if move was valid or not. */
                    if (gameAfterMove == null) {
                        popup("Invalid move, please chose"
                                        + " again.",
                                INVALID_MOVE);
                    } else {
                        game = gameAfterMove;
                        board.repaint();

                        /*
                         * Differentiating if th game is over after the last
                         * move or a machine move has to be made.
                         */
                        if (game.isGameOver()) {
                            if (game.getWinner() == Player.HUMAN) {
                                popup("Congratulations, you won!",
                                        GAME_OVER);
                            } else {
                                popup("Tie", GAME_OVER);
                            }
                        } else {
                            regulateMachineMove();
                        }
                    }
                }
            }
        }

        /**
         * Paints the board by iterating through the field and filling it up
         * with circles. The color of the circles depends on who owns the
         * observed tile.
         *
         * @param graphics The graphics to paint.
         */
        @Override
        public void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D g2D = (Graphics2D) graphics;
            RenderingHints renderingHints = new RenderingHints(KEY_ANTIALIASING,
                                            VALUE_ANTIALIAS_ON);
            renderingHints.put(KEY_RENDERING, VALUE_RENDER_QUALITY);
            g2D.setRenderingHints(renderingHints);
            g2D.setPaint(getColor(row, col));
            g2D.fillOval(0, 0, getAdjustedWidth(), getAdjustedHeight());
            g2D.setPaint(CIRCLE);
            g2D.drawOval(0, 0, getAdjustedWidth(), getAdjustedHeight());
            paintWitness(g2D);
        }

        /**
         * Is responsible for painting the witness.
         *
         * @param g2D The graphics to paint.
         */
        private void paintWitness(Graphics2D g2D) {
            List<Coordinates2D> coords =
                    (List<Coordinates2D>) game.getWitness();
            if (coords != null) {
                Coordinates2D c = new Coordinates2D(row, col);
                if (coords.contains(c)) {
                    g2D.setPaint(WITNESS);
                    int diameterOfWitness = getAdjustedWidth() / 2;
                    int heightOfWitness = getAdjustedHeight() / 2;
                    int xOfWitness = (int) (getWidth()
                            * FACTOR_FOR_WITNESS_LOCATION);
                    int yOfWitness = (int) (getHeight()
                            * FACTOR_FOR_WITNESS_LOCATION);
                    g2D.fillOval(xOfWitness, yOfWitness, diameterOfWitness,
                            heightOfWitness);
                }
            }
        }

        /**
         * Reduces the width of the tiles to paint by 6% for a better looking.
         *
         * @return The reduced width.
         */
        private int getAdjustedWidth() {
            return (int) (getWidth() * FACTOR_FOR_TILE);
        }

        /**
         * Reduces the height of the tiles to paint by 6% for a better looking.
         *
         * @return The reduced height.
         */
        private int getAdjustedHeight() {
            return (int)  (getHeight() * FACTOR_FOR_TILE);
        }

        /**
         * Returns the color representing the tile at the location of the given
         * row and column.
         *
         * @param adaptedRow The given row in which the observed tile lies.
         * @param adaptedColumn The given column in which the observed tile
         *                      lies.
         * @return The color-representation of the searched tile.
         */
        private Color getColor(int adaptedRow, int adaptedColumn) {
            Player player = game.getSlot(adaptedRow, adaptedColumn);
            return switch (player) {
                case HUMAN -> HUMAN_CHIP;
                case BOT -> BOT_CHIP;
                case NOBODY -> NOBODY_CHIP;
            };
        }
    }

    /**
     * Regulates the machine moves by creating a new {@code MachineThread}
     * instance and starting it.
     */
    private void regulateMachineMove() {
        machineThread = new MachineThread();
        machineThread.start();
    }

    /**
     * Responsible for creating and performing a Thread specific for the machine
     * moves. {@code InterruptedException} gets thrown when the thread should be
     * stopped immediately, e.g. new game or switch button gets pressed.
     */
    private class MachineThread extends Thread {
        boolean threadIsRunning;

        /**
         * Performs the machine move for the thread. Gets called when
         * the thread gets started.
         */
        @Override
        public void run() {
            threadIsRunning = true;
            try {
                game = game.machineMove();
            } catch (InterruptedException e) {
                return;
            }
            if (game.isGameOver()) {
                if (game.getWinner() == Player.BOT) {
                    popup("The bot won, good luck"
                            + " next time.", GAME_OVER);
                } else {
                    popup("Tie", GAME_OVER);
                }
            }
            threadIsRunning = false;
            board.repaint();
            // TODO: 26.01.2022 hier sollte wahrscheinlich die update board methode hin
        }

        /**
         * Stops the thread.
         */
        public void endMachineThread() {
            if (threadIsRunning) {
                this.interrupt();
            }
            threadIsRunning = false;
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
     * Responsible for the segment containing the settings.
     */
    private class SettingsPanel extends JPanel {

        /**
         * Correctly creates the panel by calling {@link #initSettingsPanel()},
         * which then fills and sets up the panel.
         */
        private SettingsPanel() {
            initSettingsPanel();
        }

        /**
         * Initializes the panel.
         */
        private void initSettingsPanel() {
            setBackground(BACKGROUND);
            setLayout(new FlowLayout());
            // Creating the needed components.
            JButton newGame = new NewGame();
            JComboBox<Integer> levelSetter = new LevelSetter();
            JButton switchGame = new SwitchGame();
            JButton quitGame = new QuitGame();
            // Adding the needed components to the JPanel.
            add(newGame);
            add(levelSetter);
            add(switchGame);
            add(quitGame);
        }

        /**
         * The Button for creating a new game.
         */
        private class NewGame extends JButton implements ActionListener {
            /**
             * Creates the {@code JButton} for starting a new game.
             */
            public NewGame() {
                setText("New");
                addActionListener(this);
            }

            /**
             * Gets called when the new game button was pressed. Creates a new
             * game with the already set first player and level. Repaints the
             * board to either an empty or with one bot move if the bot starts.
             *
             * @param e The event of interest.
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                machineThread.endMachineThread();
                game = new Game(isFirstPlayerHuman, setLevel);
                if (!isFirstPlayerHuman) {
                    regulateMachineMove();
                }
                board.repaint();
            }
        }

        /**
         * Creates the {@code JComboBox} for setting the level. It also manages
         * its changes by setting the level of the game.
         */
        private class LevelSetter extends JComboBox<Integer> implements
                ItemListener {
            /**
             * Creates the {@code JComboBox} by setting the values of the
             * {@code LEVEL} array, so that the human can chose the current
             * level. The default level was set to 4. Available levels are
             * 1 - 7.
             */
            public LevelSetter() {
                int[] LEVELS = {1, 2, 3, 4, 5, 6, 7};
                for (int level : LEVELS) {
                    addItem(level);
                    addItemListener(this);
                }
                setSelectedIndex(3);
            }

            /**
             * Notifies all listeners that have registered interest in
             * notification on this event type.
             * Corrects the set level to the new chosen value.
             *
             * @param e the event of interest
             */
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (getSelectedItem() == null) {
                    throw new IllegalArgumentException("Selected level item is"
                            + " null.");
                }
                setLevel((Integer) getSelectedItem());
            }

            /**
             * Sets the games intern and views current level.
             *
             * @param newLevel The level to be set.
             */
            private void setLevel(int newLevel) {
                if (game != null) {
                    setLevel = newLevel;
                    game.setLevel(setLevel);
                }
            }
        }

        private class SwitchGame extends JButton implements ActionListener {
            /**
             * Creates the {@code JButton} by setting the text.
             **/
            public SwitchGame() {
                setText("Switch");
                addActionListener(this);
            }

            /**
             * Gets called when the switch button got pressed. Creates a new
             * game with the opposite first player by negating
             * {@code isFirstPlayerHuman} and the currently set level.
             * Sets the {@code coords} to {@code null} because if the previous
             * game got a witness the old witness will still be saved in the
             * {@code coords} instance.
             * If the first player is not human, it automatically performs a
             * machine move.
             *
             * @param e the action event
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                machineThread.endMachineThread();
                game = new Game(!isFirstPlayerHuman, setLevel);
                isFirstPlayerHuman = !isFirstPlayerHuman;
                if (!isFirstPlayerHuman) {
                    regulateMachineMove();
                }
                board.repaint();

            }
        }

        /**
         * Button is responsible for closing the window and ending any running
         * thread when pressed.
         */
        private class QuitGame extends JButton implements ActionListener {

            /**
             * Creates button for quitting the game.
             */
            public QuitGame() {
                setText("Quit");
                addActionListener(this);
            }

            /**
             * Shuts down running threads and closes the application.
             *
             * @param e The event of interest.
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                machineThread.endMachineThread();
            }
        }
    }

    private void updateBoard(Board newBoard) {
        game = newBoard;
        board.repaint();
    }

    // update methode mit  swing utilites invoke later und dann bekommt spielfeld als attribut
    // TODO: 26.01.2022 Für die close window operation
    // TODO: 26.01.2022 Im frame sollte keine Game instanz sein
}








