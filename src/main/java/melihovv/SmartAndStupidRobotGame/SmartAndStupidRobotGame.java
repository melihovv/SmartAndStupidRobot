/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Alexander Melihov
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package melihovv.SmartAndStupidRobotGame;

import melihovv.SmartAndStupidRobotGame.model.Model;
import melihovv.SmartAndStupidRobotGame.model.field.*;
import melihovv.SmartAndStupidRobotGame.model.field.position.CellPosition;
import melihovv.SmartAndStupidRobotGame.model.navigation.Direction;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;
import java.util.logging.Logger;

/**
 * The <code>SmartAndStupidRobotGame</code> defines the main class of game.
 */
public class SmartAndStupidRobotGame extends JFrame {

    private JMenuBar _menuBar;
    private JLabel _infoLabel;
    private final View _view;

    /**
     * Constructs <code>SmartAndStupidRobotGame</code>.
     */
    public SmartAndStupidRobotGame() {
        _view = new View();

        final JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(_view);
        wrapper.add(createStatusBar(), BorderLayout.SOUTH);

        createMenu();
        super.setJMenuBar(_menuBar);

        super.setTitle("Smart and stupid robot game");
        super.setContentPane(wrapper);
        super.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        super.pack();
        super.setLocationRelativeTo(null);
        super.setResizable(false);
        super.setVisible(true);
    }

    private JPanel createStatusBar() {
        final JPanel panel = new JPanel();
        panel.setBorder(new MatteBorder(1, 0, 0, 0, Color.GRAY));
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

        _infoLabel = new JLabel("");
        _infoLabel.setHorizontalAlignment(SwingConstants.LEFT);
        panel.add(_infoLabel);

        final int HEIGHT = 20;
        panel.setPreferredSize(new Dimension(super.getWidth(), HEIGHT));

        return panel;
    }

    private void createMenu() {
        _menuBar = new JMenuBar();
        final JMenu menu = new JMenu("File");
        final String menuItems[] = new String[]{"New", "Exit"};

        for (String menuItem : menuItems) {
            final JMenuItem item = new JMenuItem(menuItem);
            item.setActionCommand(menuItem.toLowerCase());
            item.addActionListener(new MenuItemsListener());
            menu.add(item);
        }

        menu.insertSeparator(1);
        _menuBar.add(menu);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SmartAndStupidRobotGame::new);
    }

    /**
     * The <code>View</code> defines view of <code>Model</code> class.
     */
    public class View extends JPanel implements KeyListener {

        private final Model _model;
        private boolean _isGameStarted;
        final Logger log = Logger.getLogger(View.class.getName());

        private static final int CELL_SIZE = 30;
        private static final int FONT_HEIGHT = 15;

        private final Color BACKGROUND_COLOR = new Color(175, 255, 175);
        private final Color GRID_COLOR = Color.GREEN;
        private final Color FONT_COLOR = Color.RED;
        private final Color MIRE_COLOR = new Color(139, 69, 19);
        private final Color WALL_COLOR = Color.BLACK;

        private final int _width;
        private final int _height;
        private int _offsetX;
        private int _offsetY;

        /**
         * Constructs view.
         */
        public View() {
            _model = new Model();
            _isGameStarted = false;

            _width = CELL_SIZE * _model.field().width();
            _height = CELL_SIZE * _model.field().height();
            setPreferredSize(new Dimension(_width, _height));
            setMaximumSize(new Dimension(_width, _height));
            setMinimumSize(new Dimension(_width, _height));
            setFocusable(true);

            addKeyListener(this);
        }

        public void start() {
            _isGameStarted = true;
            _model.start();
            _model.smartRobot().addListener(new SmartRobotListener());
            _model.stupidRobot().addListener(new StupidRobotListener());
            super.repaint();
        }

        @Override
        public void paintComponent(Graphics g) {
            g.setColor(BACKGROUND_COLOR);
            g.fillRect(0, 0, super.getWidth(), getHeight());

            _offsetX = Math.abs(super.getWidth() - _width) / 2;
            _offsetY = Math.abs(super.getHeight() - _height) / 2;

            drawGrid(g);

            if (_isGameStarted) {
                drawWalls(g, _model.field().objects(Wall.class));
                drawMires(g, _model.field().objects(Mire.class));
                drawTarget(g, _model.target());
                drawSmartRobot(g, _model.smartRobot());
                drawStupidRobot(g, _model.stupidRobot());
            }
        }

        private void drawGrid(Graphics g) {
            Color preserved = g.getColor();
            g.setColor(GRID_COLOR);

            final int height = super.getHeight();
            final int width = super.getWidth();

            for (int i = 1; i <= _model.field().width() + 1; ++i) {
                int x = _offsetX + CELL_SIZE * (i - 1);
                g.drawLine(x, 0, x, height);

                int y = _offsetY + CELL_SIZE * (i - 1);
                g.drawLine(0, y, width, y);
            }

            g.setColor(preserved);
        }

        private void drawTarget(Graphics g, Model.Target target) {
            Color preserved = g.getColor();
            g.setColor(FONT_COLOR);

            Point ltc = leftTopCorner(target.pos());
            g.drawString(
                    "T",
                    ltc.x + CELL_SIZE / 3,
                    ltc.y + CELL_SIZE / 5 + FONT_HEIGHT
            );

            g.setColor(preserved);
        }

        private void drawSmartRobot(Graphics g, SmartRobot smartRobot) {
            Color preserved = g.getColor();
            g.setColor(FONT_COLOR);

            Point ltc = leftTopCorner(smartRobot.pos());
            g.drawString(
                    "Sm",
                    ltc.x + CELL_SIZE / 5,
                    ltc.y + CELL_SIZE / 5 + FONT_HEIGHT
            );

            g.setColor(preserved);
        }

        private void drawStupidRobot(Graphics g, StupidRobot stupidRobot) {
            Color preserved = g.getColor();
            g.setColor(FONT_COLOR);

            Point ltc = leftTopCorner(stupidRobot.pos());
            g.drawString(
                    "St",
                    ltc.x + CELL_SIZE / 3,
                    ltc.y + CELL_SIZE / 5 + FONT_HEIGHT
            );

            g.setColor(preserved);
        }

        private void drawMires(Graphics g, List<FieldObject> mires) {
            Color preserved = g.getColor();
            g.setColor(MIRE_COLOR);

            for (FieldObject mire : mires) {
                Point ltc = leftTopCorner(((Mire) mire).pos());
                g.fillRect(ltc.x + 1, ltc.y + 1, CELL_SIZE - 1, CELL_SIZE - 1);
            }

            g.setColor(preserved);
        }

        private void drawWalls(Graphics g, List<FieldObject> walls) {
            Color preserved = g.getColor();
            g.setColor(WALL_COLOR);

            for (FieldObject wall : walls) {
                Point ltc = leftTopCorner(((Wall) wall).pos().cellPos());
                Direction dir = ((Wall) wall).pos().direct();

                if (dir.equals(Direction.north())) {
                    g.drawLine(
                            ltc.x + 1,
                            ltc.y,
                            ltc.x + CELL_SIZE - 1,
                            ltc.y
                    );
                } else if (dir.equals(Direction.south())) {
                    g.drawLine(
                            ltc.x + 1,
                            ltc.y + CELL_SIZE,
                            ltc.x + CELL_SIZE - 1,
                            ltc.y + CELL_SIZE
                    );
                } else if (dir.equals(Direction.west())) {
                    g.drawLine(
                            ltc.x,
                            ltc.y + 1,
                            ltc.x,
                            ltc.y + CELL_SIZE - 1
                    );
                } else if (dir.equals(Direction.east())) {
                    g.drawLine(
                            ltc.x + CELL_SIZE,
                            ltc.y + 1,
                            ltc.x + CELL_SIZE,
                            ltc.y + CELL_SIZE - 1
                    );
                } else {
                    throw new IllegalArgumentException(
                            "Direction must be north, south, west or east");
                }
            }

            g.setColor(preserved);
        }

        /**
         * Returns left top corner coordinates of the cell.
         *
         * @param c Position of cell.
         * @return Left top corner coordinates of the cell.
         */
        private Point leftTopCorner(CellPosition c) {
            return new Point(
                    _offsetX + CELL_SIZE * ((int) c.pos().getX() - 1),
                    _offsetY + CELL_SIZE * ((int) c.pos().getY() - 1)
            );
        }

        @Override
        public void keyTyped(KeyEvent e) {
        }

        @Override
        public void keyPressed(KeyEvent e) {
            if (!_isGameStarted) {
                return;
            }

            Direction dir = null;
            if (e.getKeyCode() == KeyEvent.VK_UP) {
                dir = Direction.north();
            } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                dir = Direction.south();
            } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                dir = Direction.west();
            } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                dir = Direction.east();
            }

            if (dir != null) {
                _model.makeMove(dir);
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
        }

        /**
         * Smart robot listener.
         */
        private class SmartRobotListener
                implements SmartRobot.SmartRobotActionListener {

            @Override
            public void smartRobotMadeMove(SmartRobot.SmartRobotActionEvent e) {
                log.fine("Smart robot made move");
                repaint();
            }
        }

        /**
         * Stupid robot listener.
         */
        private class StupidRobotListener
                implements StupidRobot.StupidRobotActionListener {

            @Override
            public void stupidRobotMadeMove(StupidRobot.StupidRobotActionEvent e) {
                log.fine("Stupid robot made move");
                repaint();
            }

            @Override
            public void smartRobotIsCaught(StupidRobot.StupidRobotActionEvent e) {
            }
        }
    }

    private class MenuItemsListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();
            if ("exit".equals(command)) {
                System.exit(0);
            }
            if ("new".equals(command)) {
                _view.start();
            }
        }
    }
}
