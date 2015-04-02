package Mines;

import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Stroke;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Random;

public class Board extends JPanel {

    private final Font NUMBER_FONT = new Font("Arial", Font.BOLD, 14);
    private final int CELL_SIZE = 15;
    private final int SHADE_WIDTH = 2;

    private final int COVER_FOR_CELL = 10;
    private final int MARK_FOR_CELL = 10;
    private final int EMPTY_CELL = 0;
    private final int MINE_CELL = 9;
    private final int COVERED_MINE_CELL = MINE_CELL + COVER_FOR_CELL;
    private final int MARKED_MINE_CELL = COVERED_MINE_CELL + MARK_FOR_CELL;

    private final int DRAW_MINE = 9;
    private final int DRAW_COVER = 10;
    private final int DRAW_MARK = 11;
    private final int DRAW_WRONG_MARK = 12;

    private final int N_MINES = 40;
    private final int N_ROWS = 16;
    private final int N_COLS = 16;

    private int[] MineBoard;
    private boolean GameOn;
    private int mines_left;
    private String[] CoversForCells;

    private int all_cells;
    private JLabel statusbar;


    public Board(JLabel statusbar) {

        this.statusbar = statusbar;

        CoversForCells = new String[13];

        for (int i = 0; i < 9; i++) {
            CoversForCells[i] = Integer.toString(i);
        }
        CoversForCells[9] = "B";
        CoversForCells[10] = "*";
        CoversForCells[11] = "F";
        CoversForCells[12] = "X";

        setDoubleBuffered(true);

        addMouseListener(new MinesAdapter());
        newGame();
    }


    private void newGame() {

        Random random;
        int current_col;

        int i = 0;
        int position = 0;
        int cell = 0;

        random = new Random();
        GameOn = true;
        mines_left = N_MINES;

        all_cells = N_ROWS * N_COLS;
        MineBoard = new int[all_cells];
        
        for (i = 0; i < all_cells; i++)
            MineBoard[i] = COVER_FOR_CELL;

        statusbar.setText("Mines left:" + Integer.toString(mines_left));

        i = 0;
        while (i < N_MINES) {

            position = (int) (all_cells * random.nextDouble());

            if ((position < all_cells) &&
                (MineBoard[position] != COVERED_MINE_CELL)) {

                current_col = position % N_COLS;
                MineBoard[position] = COVERED_MINE_CELL;
                i++;

                int[] cells = {position - 1 - N_COLS, position - 1, position - 1 + N_COLS,
                        position - N_COLS, position + N_COLS,
                        position + 1 - N_COLS, position + 1, position + 1 + N_COLS};

                int iterStart = 0;
                int iterLength = cells.length;

                if (current_col == 0) {iterStart = 3;}
                else if (current_col == (N_COLS - 1)) iterLength -=3;

                for (int j = iterStart; j < iterLength; j++) {

                    if (cells[j] >= 0 && cells[j] < all_cells && MineBoard[cells[j]] != COVERED_MINE_CELL)
                            MineBoard[cells[j]]++;

                }

            }
        }
    }


    public void find_empty_cells(int position) {

        int current_col = position % N_COLS;

        int[] cells = {position - 1 - N_COLS, position - 1, position - 1 + N_COLS,
                position - N_COLS, position + N_COLS,
                position + 1 - N_COLS, position + 1, position + 1 + N_COLS};

        int iterStart = 0;
        int iterLength = cells.length;

        if (current_col == 0) {iterStart = 3;}
        else if (current_col == (N_COLS - 1)) iterLength -=3;

        for (int j = iterStart; j < iterLength; j++) {

            if (cells[j] >= 0 && cells[j] < all_cells)
            if (MineBoard[cells[j]] > MINE_CELL) {
                MineBoard[cells[j]] -= COVER_FOR_CELL;
                if (MineBoard[cells[j]] == EMPTY_CELL)
                    find_empty_cells(cells[j]);
            }
        }
    }

    @Override
    public void paintComponent(Graphics g) {

        int cell = 0;
        int uncover = 0;

        for (int i = 0; i < N_ROWS; i++) {
            for (int j = 0; j < N_COLS; j++) {

                cell = MineBoard[(i * N_COLS) + j];

                if (!GameOn) {
                    if (cell == COVERED_MINE_CELL) {
                        cell = DRAW_MINE;
                    } else if (cell == MARKED_MINE_CELL) {
                        cell = DRAW_MARK;
                    } else if (cell > COVERED_MINE_CELL) {
                        cell = DRAW_WRONG_MARK;
                    } else if (cell > MINE_CELL) {
                        cell = DRAW_COVER;
                    }


                } else {
                    if (cell > COVERED_MINE_CELL)
                        cell = DRAW_MARK;
                    else if (cell > MINE_CELL) {
                        cell = DRAW_COVER;
                        uncover++;
                    }
                }

//                g.drawImage(img[cell], (j * CELL_SIZE),
//                    (i * CELL_SIZE), this);
                drawTile(new Color(211, 216, 219), true, (j * CELL_SIZE), (i * CELL_SIZE), g);

                if(cell == DRAW_COVER)
                    drawTile(new Color(166, 189, 241), false, (j * CELL_SIZE), (i * CELL_SIZE), g);
                else if(cell == 0)
                    drawTile(new Color(211, 216, 219), true, (j * CELL_SIZE), (i * CELL_SIZE), g);
                else if(cell == 9)
                    drawMine((j * CELL_SIZE), (i * CELL_SIZE), g);
                else if(cell == DRAW_MARK)
                    drawMark((j * CELL_SIZE), (i * CELL_SIZE), g);
                else{
                    Color[] colors = {Color.BLUE,Color.GREEN,Color.RED,Color.DARK_GRAY,Color.magenta,Color.ORANGE,Color.CYAN,Color.BLACK};
                    g.setFont(NUMBER_FONT);
                    if (cell == DRAW_MARK) g.setColor(Color.WHITE);
                    else if (cell == DRAW_WRONG_MARK) g.setColor(Color.RED);
                    else if (cell < 9) g.setColor(colors[cell-1]);

                    g.drawString(CoversForCells[cell], (j * CELL_SIZE) + CELL_SIZE / 3,
                            (i * CELL_SIZE) + CELL_SIZE-2);
                }

            }
        }

        if (uncover == 0 && GameOn) {
            GameOn = false;
            statusbar.setText("You won");
        } else if (!GameOn)            statusbar.setText("You lose");
    }

    /**
     * Draws a tile onto the board.
     * @param base The base color of tile.
     * @param x The column.
     * @param y The row.
     * @param g The graphics object.
     */
    private void drawTile(Color base, boolean opened, int x, int y, Graphics g) {

		/*
		 * Fill the entire tile with the base color.
		 */
        g.setColor(base);
        g.fillRect(x, y, CELL_SIZE, CELL_SIZE);

        if(opened) {

            //creates a copy of the Graphics instance
            Graphics2D g2d = (Graphics2D) g.create();

            g2d.setColor(Color.BLACK);

            Stroke dashed = new BasicStroke(0.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL, 0, new float[]{1}, 0);
            g2d.setStroke(dashed);
            g2d.drawRect(x, y, CELL_SIZE, CELL_SIZE);

            //gets rid of the copy
            g2d.dispose();

        } else {
            Color light = base.brighter();
            Color dark = base.darker();

		/*
		 * Fill the bottom and right edges of the tile with the dark shading color.
		 */
            g.setColor(dark);
            g.fillRect(x, y + CELL_SIZE - SHADE_WIDTH, CELL_SIZE, SHADE_WIDTH);
            g.fillRect(x + CELL_SIZE - SHADE_WIDTH, y, SHADE_WIDTH, CELL_SIZE);

		/*
		 * Fill the top and left edges with the light shading. We draw a single line
		 * for each row or column rather than a rectangle so that we can draw a nice
		 * looking diagonal where the light and dark shading meet.
		 */
            g.setColor(light);
            for (int i = 0; i < SHADE_WIDTH; i++) {
                g.drawLine(x, y + i, x + CELL_SIZE - i - 1, y + i);
                g.drawLine(x + i, y, x + i, y + CELL_SIZE - i - 1);
            }
        }
    }

    /**
     * Draws a mine onto the board.
     * @param x The column.
     * @param y The row.
     * @param g The graphics object.
     */
    private void drawMine(int x, int y, Graphics g) {

        Color base = Color.black;

		/*
		 * Fill the entire tile with the base color.
		 */
        g.setColor(base);
        g.fillOval(x+1, y+1, CELL_SIZE-2, CELL_SIZE-2);

    }

    /**
     * Draws a mine onto the board.
     * @param x The column.
     * @param y The row.
     * @param g The graphics object.
     */
    private void drawMark(int x, int y, Graphics g) {

        g.setColor(Color.black);
        g.fillRect(x + CELL_SIZE / 2, y + 3, 2, CELL_SIZE - 5);
        g.fillRect(x + CELL_SIZE / 2 - 2, y + CELL_SIZE - 3, 5, 1);

        int[] coordsx = {x + CELL_SIZE/2 + 1, x + CELL_SIZE/2 - 5, x + CELL_SIZE/2 + 1};
        int[] coordsy = {y + 2, y + 5, y + 9};
        Polygon p = new Polygon(coordsx, coordsy, 3);

        g.setColor(Color.RED);
        g.fillPolygon(p);
    }

    class MinesAdapter extends MouseAdapter {
        
        @Override
        public void mousePressed(MouseEvent e) {

            int x = e.getX();
            int y = e.getY();

            int cCol = x / CELL_SIZE;
            int cRow = y / CELL_SIZE;

            boolean rep = false;


            if (!GameOn) {
                newGame();
                repaint();
            }


            if ((x < N_COLS * CELL_SIZE) && (y < N_ROWS * CELL_SIZE)) {

                if (e.getButton() == MouseEvent.BUTTON3) {

                    if (MineBoard[(cRow * N_COLS) + cCol] > MINE_CELL) {
                        rep = true;

                        if (MineBoard[(cRow * N_COLS) + cCol] <= COVERED_MINE_CELL) {
                            if (mines_left > 0) {
                                MineBoard[(cRow * N_COLS) + cCol] += MARK_FOR_CELL;
                                mines_left--;
                                statusbar.setText(Integer.toString(mines_left));
                            }
                            if (mines_left == 0)
                                statusbar.setText("No marks left");
                        } else {

                            MineBoard[(cRow * N_COLS) + cCol] -= MARK_FOR_CELL;
                            mines_left++;
                            statusbar.setText(Integer.toString(mines_left));
                        }
                    }

                } else {

                    if (MineBoard[(cRow * N_COLS) + cCol] == COVERED_MINE_CELL)
                        GameOn = false;

                    if (MineBoard[(cRow * N_COLS) + cCol] > COVERED_MINE_CELL) {
                        return;
                    }

                    if ((MineBoard[(cRow * N_COLS) + cCol] > MINE_CELL) &&
                        (MineBoard[(cRow * N_COLS) + cCol] < MARKED_MINE_CELL)) {

                        MineBoard[(cRow * N_COLS) + cCol] -= COVER_FOR_CELL;
                        rep = true;

                        if (MineBoard[(cRow * N_COLS) + cCol] == MINE_CELL)
                            GameOn = false;
                        if (MineBoard[(cRow * N_COLS) + cCol] == EMPTY_CELL)
                            find_empty_cells((cRow * N_COLS) + cCol);
                    }
                }

                if (rep)
                    repaint();

            }
        }
    }
}