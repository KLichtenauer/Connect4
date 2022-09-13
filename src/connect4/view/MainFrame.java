package connect4.view;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * The view is responsible for the creation of the user interaction platform and
 * its management. The board will be implemented in the centre and the settings
 * bar in the south of the window.
 */
public class MainFrame extends JFrame {

    private final BoardPanel boardPanel;
    private final static int FRAME_WIDTH = 720;
    private final static int FRAME_HEIGHT = 600;

    /**
     * Constructs the GUI-program by initializing the components.
     */
    public MainFrame() {
        Container container = getContentPane();
        setPreferredSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT));
        container.setLayout(new BorderLayout());
        container.setVisible(true);
        boardPanel = new BoardPanel();
        container.add(boardPanel, BorderLayout.CENTER);
        JPanel settings = new SettingsPanel();
        container.add(settings, BorderLayout.SOUTH);
        setTitle("connect4");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
        addWindowListener(new WindowCloser());
        pack();
    }

    private class WindowCloser extends WindowAdapter {

        /**
         * When the window gets closed the thread should get shut down.
         *
         * @param event The event of interest.
         */
        @Override
        public void windowClosed(WindowEvent event) {
            boardPanel.endMachineThread();
        }
    }
    
    /**
     * Responsible for the segment containing the settings.
     */
    private final class SettingsPanel extends JPanel {

        /**
         * Correctly creates and initializes the panel.
         */
        private SettingsPanel() {
            setBackground(Color.BLUE);
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
        private final class NewGame extends JButton implements ActionListener {

            /**
             * Creates the {@code JButton} for starting a new game.
             */
            private NewGame() {
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
                boardPanel.newGame();
            }
        }

        /**
         * Creates the {@code JComboBox} for setting the level. It also manages
         * its changes by setting the level of the game.
         */
        private final class LevelSetter extends JComboBox<Integer>
                implements ItemListener {

            /**
             * Creates the {@code JComboBox} by setting the values of the
             * {@code LEVEL} array, so that the human can chose the current
             * level. The default level was set to 4. Available levels are
             * 1 - 7.
             */
            private LevelSetter() {
                int indexOfDefaultLevel = 3;
                int[] levels = {1, 2, 3, 4, 5, 6, 7};
                for (int level : levels) {
                    addItem(level);
                    addItemListener(this);
                }
                setSelectedIndex(indexOfDefaultLevel);
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
                boardPanel.setLevel((Integer) getSelectedItem());
            }
        }

        private final class SwitchGame extends JButton
                implements ActionListener {

            /**
             * Creates the {@code JButton} by setting the text.
             */
             private SwitchGame() {
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
                boardPanel.switchGame();
            }
        }

        /**
         * Button is responsible for closing the window and ending any running
         * thread when pressed.
         */
        private final class QuitGame extends JButton implements ActionListener {

            /**
             * Creates button for quitting the game.
             */
            private QuitGame() {
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
            }
        }
    }
}