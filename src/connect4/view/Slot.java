package connect4.view;

import connect4.model.Board;
import connect4.model.Player;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import static java.awt.RenderingHints.KEY_ANTIALIASING;
import static java.awt.RenderingHints.KEY_RENDERING;
import static java.awt.RenderingHints.VALUE_ANTIALIAS_ON;
import static java.awt.RenderingHints.VALUE_RENDER_QUALITY;

/**
 * Slots are the main component in the board. They represent each tile and
 * perform the move in the corresponding clicked column.
 */
public class Slot extends JPanel {

    /**
     * Putting colors in attributes for better readability and having a fix
     * place for every color.
     */
    private static final Color HUMAN_CHIP = Color.YELLOW;
    private static final Color BOT_CHIP = Color.RED;
    private static final Color NOBODY_CHIP = Color.WHITE;
    private static final Color WITNESS = Color.DARK_GRAY;
    private static final Color CIRCLE = Color.BLACK;
    private static final double FACTOR_FOR_WITNESS_LOCATION = 0.25;
    private static final double FACTOR_FOR_CHIP_SIZE = 0.94;
    private final int col;
    private Player ownerOfTile;
    private boolean isWitness;

    /**
     * Returns the column the slot lies in.
     *
     * @return The column of {@code this} slot.
     */
    public int getCol() {
        return col;
    }

    /**
     * Creates a {@code Slot} at the given coordinate of the board.
     *
     * @param row The row of the slot to create.
     * @param col The column of the slot to create.
     */
    Slot(int row, int col) {
        assert (row >= 0) && (row < Board.ROWS) && (col >= 0)
                && (col < Board.COLS);
        this.col = col;
        ownerOfTile = Player.NOBODY;
        setBackground(Color.BLUE);
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
        if (graphics == null) {
            throw new IllegalArgumentException("Given value is null!");
        }
        super.paintComponent(graphics);
        Graphics2D g2D = (Graphics2D) graphics;
        RenderingHints renderingHints = new RenderingHints(KEY_ANTIALIASING,
                VALUE_ANTIALIAS_ON);
        renderingHints.put(KEY_RENDERING, VALUE_RENDER_QUALITY);
        g2D.setRenderingHints(renderingHints);
        g2D.setPaint(getColor());
        g2D.fillOval(0, 0, getAdjustedWidth(), getAdjustedHeight());
        g2D.setPaint(CIRCLE);
        g2D.drawOval(0, 0, getAdjustedWidth(), getAdjustedHeight());
        paintWitness(g2D);
    }

    /**
     * This method is responsible for painting the witness.
     *
     * @param g2D The graphics to paint.
     */
    private void paintWitness(Graphics2D g2D) {

         /*
          * By starting a new game the isWitness attribute is still true. By
          * checking if the game got restarted (which means each tile contains
          * no player
          */
        checkIfStillWitness();
        if (isWitness) {
            g2D.setPaint(WITNESS);
            int diameterOfWitness = getAdjustedWidth() / 2;
            int heightOfWitness = getAdjustedHeight() / 2;
            int xOfWitness = (int) (getWidth() * FACTOR_FOR_WITNESS_LOCATION);
            int yOfWitness = (int) (getHeight() * FACTOR_FOR_WITNESS_LOCATION);
            g2D.fillOval(xOfWitness, yOfWitness, diameterOfWitness,
                    heightOfWitness);
        }
    }

    /**
     * Sets {@code isWitness} to false if the owner of the tile is not a player.
     */
    private void checkIfStillWitness() {
        if (ownerOfTile == Player.NOBODY) {
            isWitness = false;
        }
    }

    /**
     * Reduces the width of the tiles to paint by 6% for a better looking.
     *
     * @return The reduced width.
     */
    private int getAdjustedWidth() {
        return (int) (getWidth() * FACTOR_FOR_CHIP_SIZE);
    }

    /**
     * Reduces the height of the tiles to paint by 6% for a better looking.
     *
     * @return The reduced height.
     */
    private int getAdjustedHeight() {
        return (int) (getHeight() * FACTOR_FOR_CHIP_SIZE);
    }

    /**
     * Returns the color representing the tile at the location of the given
     * row and column.
     *
     * @return The color-representation of the searched tile.
     */
    private Color getColor() {
        return switch (ownerOfTile) {
            case HUMAN -> HUMAN_CHIP;
            case BOT -> BOT_CHIP;
            case NOBODY -> NOBODY_CHIP;
        };
    }

    /**
     * Sets the new owner of the tile if it got changed by a move.
     *
     * @param player The new owner of {@code this} slot.
     */
    void setPlayer(Player player) {
        ownerOfTile = player;
        repaint();
    }

    /**
     * Sets the {@code isWitness} true, so that a mark is being paint, that
     * {@code this} slot is a witness.
     */
    void setWitness() {
        isWitness = true;
        repaint();
    }
}
