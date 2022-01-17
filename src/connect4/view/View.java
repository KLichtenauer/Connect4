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

import static java.lang.Thread.sleep;

public class View {

    // TODO: 16.01.2022 Quitbutton
    // TODO: 16.01.2022 Thread
    // TODO: 16.01.2022 Das mit dem verschieben vom fenster regeln

    // foundation frame
    private JFrame frame;

    private final static Color BACKGROUND = Color.BLUE;
    private final static Color HUMAN_CHIP = Color.YELLOW;
    private final static Color BOT_CHIP = Color.RED;
    private final static Color NOBODY_CHIP = Color.WHITE;


    private boolean isFirstPlayerHuman = true;
    private Board game = new Game(true, 4);
    private static final int DEFAULT_LEVEL = 4;
    private static int setLevel = DEFAULT_LEVEL;
    JPanel board;

    Slot.moveListener.MachineThread machineThread;
    Board boardAfterMove;

    /**
     * Constructs the GUI-Programm by calling {@link #initComponents()}.
     */
    public View() {
        initComponents();
    }

    /**
     *
     */
    private void initComponents() {
        frame = new JFrame();
        // container for game board and option panel
        Container container = frame.getContentPane();
        container.setLayout(new BorderLayout());
        container.setVisible(true);
        // panel, containing the board
        board = new BoardPanel();
        container.add(board, BorderLayout.CENTER);
        // panel, containing all 
        JPanel settings = new SettingsPanel();
        container.add(settings, BorderLayout.SOUTH);
        frame.pack();
        frame.setTitle("connect4");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
    }

    /**
     * {@code BoardPenal} gets the gridlayout and thus sets the foundation of
     * the game board. By filling it with {@code Slot}'s, the board is fully
     * initialized.
     */
    private class BoardPanel extends JPanel {


        private BoardPanel() {
            initBoardPanel();
        }

        private void initBoardPanel() {
            setPreferredSize(new Dimension(720, 720));
            setLayout(new GridLayout(Board.ROWS, Board.COLS));
            setVisible(true);
            setBackground(BACKGROUND);
            fillingBoard();
            repaint();
        }

        private void fillingBoard() {
            for (int row = 0; row < Board.ROWS; row++) {
                for (int col = 0; col < Board.COLS; col++) {
                    add(new Slot(row, col));
                }
            }
        }
    }

    private class Slot extends JPanel {
        private final int row;
        private final int col;

        String invalidMove = "Invalid move";
        String gameOver = "Game over";

        private Slot( int row, int col) {
            this.row = row;
            this.col = col;
            addMouseListener(new moveListener());
            setBackground(BACKGROUND);
        }

        private class moveListener extends MouseAdapter{
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
                            invalidMove);
                } else {
                    Board gameAfterMove = game.move(col + 1);
                    if (gameAfterMove == null) {
                        popup("Invalid move, please chose"
                                        + " again.",
                                invalidMove);
                    } else if (gameAfterMove.isGameOver()) {
                        game = gameAfterMove;
                        if (game.getWinner() == Player.HUMAN) {
                            popup("Congratulations, you won!",
                                    gameOver);
                        } else {
                            popup("Tie", gameOver);
                        }
                    } else {
                        game = gameAfterMove;
                        regulateMachineMove();

                        //game = gameAfterMove.machineMove();
                        //if (game.isGameOver()) {
                            //    if (game.getWinner() == Player.BOT) {
                                //        popup("The bot won, good luck"
                                //                + " next time.", gameOver);
                                //    } else {
                                //        popup("Tie", gameOver);
                                //    }
                            //}
                    }
                    board.repaint();
                }
            }
                /*
                machineThread = new Thread() {
                    boolean threadIsRunning = true;

                    public void exit() {
                        threadIsRunning = false;
                    }

                    @Override
                    public void run() {
                        while (threadIsRunning) {
                            String invalidMove = "Invalid move";
                            String gameOver = "Game over";
                            if (game.isGameOver()) {
                                popup("Invalid move, the game is already"
                                                + " over.",
                                        invalidMove);
                            } else {
                                Board gameAfterMove = game.move(col + 1);
                                if (gameAfterMove == null) {
                                    popup("Invalid move, please chose"
                                                    + " again.",
                                            invalidMove);
                                } else if (gameAfterMove.isGameOver()) {
                                    game = gameAfterMove;
                                    if (game.getWinner() == Player.HUMAN) {
                                        popup("Congratulations, you won!",
                                                gameOver);
                                    } else {
                                        popup("Tie", gameOver);
                                    }
                                } else {
                                    //game = gameAfterMove;
                                    //game = regulateMachineMove();
                                    game = gameAfterMove.machineMove();
                                    if (game.isGameOver()) {
                                        if (game.getWinner() == Player.BOT) {
                                            popup("The bot won, good luck"
                                                    + " next time.", gameOver);
                                        } else {
                                            popup("Tie", gameOver);
                                        }
                                    }
                                }
                                board.repaint();
                            }
                        }
                        try {
                            sleep(1000);
                        } catch (InterruptedException e) {
                            threadIsRunning = false;
                        }
                    }
                };
                machineThread.start();
                */


            private Board regulateMachineMove() {
                // TODO: 16.01.2022 Thread muss global gespeichert werden, dass
                //  man ihn mit quit beenden kann
                machineThread = new MachineThread();
                Thread thread = new Thread(machineThread);
                //thread.start();
                machineThread.start();
                Board board = boardAfterMove;
                return boardAfterMove;
            }

            class MachineThread extends Thread {
                boolean threadIsRunning = true;
                Thread thread;


                MachineThread() {
                //    thread = new Thread(this);
                //    machineThread = this;
                //    thread.start();
                }

                @Override
                public void run() {
                    while (threadIsRunning) {
                        game = game.machineMove();
                        if (game.isGameOver()) {
                            if (game.getWinner() == Player.BOT) {
                                popup("The bot won, good luck"
                                        + " next time.", gameOver);
                            } else {
                                popup("Tie", gameOver);
                            }
                        }
                        try {
                            Thread.sleep(1);

                        } catch (InterruptedException e) {
                            exit();
                        }
                        exit();
                        board.repaint();
                    }

                }

                public void exit() {
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
                JOptionPane.showConfirmDialog(frame, message, title,
                        JOptionPane.DEFAULT_OPTION);
            }
        }

        /**
         * Paints the board by iterating through the field and filling it up
         * with circles. The color of the circles depends on who owns the
         * observed tile.
         *
         * @param graphics
         */
        @Override
        public void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D g2D = (Graphics2D) graphics;
            RenderingHints renderingHints = new RenderingHints(
                                            RenderingHints.KEY_ANTIALIASING,
                                            RenderingHints.VALUE_ANTIALIAS_ON);
            renderingHints.put(RenderingHints.KEY_RENDERING,
                               RenderingHints.VALUE_RENDER_QUALITY);
            g2D.setRenderingHints(renderingHints);
            g2D.setPaint(getColor(row, col));
            int diameter =  (int) (getWidth() * 0.94);
            g2D.fillOval(0,0, diameter, diameter);
            List<Coordinates2D> coords =
                    (List<Coordinates2D>) View.this.game.getWitness();
            if (coords != null) {
                Coordinates2D c = new Coordinates2D(row, col);
                if (coords.contains(c)) {
                    g2D.setPaint(Color.BLACK);
                    int diameterOfWitness = diameter / 2;
                    int coordinateOfWitness = this.getWidth() / 4;
                    g2D.fillOval(coordinateOfWitness, coordinateOfWitness,
                            diameterOfWitness, diameterOfWitness);
                }
            }
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
            Player player = View.this.game.getSlot(adaptedRow, adaptedColumn);
            return switch (player) {
                case HUMAN -> HUMAN_CHIP;
                case BOT -> BOT_CHIP;
                case NOBODY -> NOBODY_CHIP;
            };
        }
    }

    /**
     * Responsible for the segment containing the settings.
     */
    private class SettingsPanel extends JPanel {

        private SettingsPanel() {
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
                if (machineThread != null) {
                    machineThread.exit();
                }
                game = new Game(isFirstPlayerHuman, setLevel);
                if (!isFirstPlayerHuman) {
                    game = game.machineMove();
                }
                board.repaint();
            }
        }

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
             * @param newLevel The level to be set.
             */
            private void setLevel(int newLevel) {
                if (game == null) {
                    game = new Game(true, DEFAULT_LEVEL);
                }
                setLevel = newLevel;
                game.setLevel(setLevel);
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
                game = new Game(!isFirstPlayerHuman, setLevel);
                isFirstPlayerHuman = !isFirstPlayerHuman;
                if (!isFirstPlayerHuman) {
                    game = game.machineMove();
                }
                board.repaint();
            }
        }

        private class QuitGame extends JButton implements ActionListener {

            /**
             *
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
                machineThread.exit();
                frame.dispose();
            }
        }
    }
}








