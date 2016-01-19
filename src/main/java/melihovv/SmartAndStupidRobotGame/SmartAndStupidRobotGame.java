/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Alexander Melihov
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
import melihovv.SmartAndStupidRobotGame.model.Model.ModelEvent;
import melihovv.SmartAndStupidRobotGame.model.field.FieldObject;
import melihovv.SmartAndStupidRobotGame.model.field.SmartRobot;
import melihovv.SmartAndStupidRobotGame.model.field.SmartRobot.SmartRobotActionEvent;
import melihovv.SmartAndStupidRobotGame.model.field.StupidRobot;
import melihovv.SmartAndStupidRobotGame.model.field.StupidRobot.StupidRobotActionEvent;
import melihovv.SmartAndStupidRobotGame.model.field.position.CellPosition;
import melihovv.SmartAndStupidRobotGame.model.field.position.MiddlePosition;
import melihovv.SmartAndStupidRobotGame.model.navigation.Direction;
import melihovv.SmartAndStupidRobotGame.model.seasons.Season;
import melihovv.SmartAndStupidRobotGame.model.seasons.SeasonsManager;
import melihovv.SmartAndStupidRobotGame.model.seasons.SeasonsManager.SeasonsEvent;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * The <code>SmartAndStupidRobotGame</code> defines the main class of game.
 */
public class SmartAndStupidRobotGame extends JFrame {

    // Menu.
    private JMenuBar _menuBar;
    // Info label.
    private JLabel _infoLabel;
    // View of model.
    private final View _view;
    // Logger.
    final Logger _log = Logger.getLogger(View.class.getName());
    // File path with game situation.
    private String _path;

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

    /**
     * Create status bar.
     *
     * @return Status bar.
     */
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

    /**
     * Create menu.
     */
    private void createMenu() {
        _menuBar = new JMenuBar();
        final JMenu menu = new JMenu("Game");
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

    /**
     * Main function.
     *
     * @param args Args.
     */
    public static void main(final String[] args) {
        SwingUtilities.invokeLater(SmartAndStupidRobotGame::new);
    }

    /**
     * The <code>View</code> defines view of <code>Model</code> class.
     */
    private class View extends JPanel implements KeyListener {

        // Model.
        private final Model _model;
        // Is game started?
        private boolean _isGameStarted;

        // Cell size.
        private static final int CELL_SIZE = 30;
        // Font height.
        private static final int FONT_HEIGHT = 15;
        // Constants.
        private Map<String, Integer> _constants =
                new HashMap<String, Integer>() {{
                    put("cell size", CELL_SIZE);
                    put("font size", FONT_HEIGHT);
                }};
        // Colors for field objects draw.
        private Map<String, Color> _colors = new HashMap<>();

        // Width of field.
        private final int _width;
        // Height of field.
        private final int _height;
        // Offset from left and right side of frame.
        private int _offsetX;
        // Offset from top and bottom side of frame.
        private int _offsetY;

        /**
         * Constructs view.
         */
        public View() {
            _model = new Model();
            _model.addListener(new ModelListener());
            _isGameStarted = false;

            _width = CELL_SIZE * _model.field().width();
            _height = CELL_SIZE * _model.field().height();
            setPreferredSize(new Dimension(_width, _height));
            setMaximumSize(new Dimension(_width, _height));
            setMinimumSize(new Dimension(_width, _height));
            setFocusable(true);

            addKeyListener(this);
        }

        /**
         * Starts the game.
         */
        public void start()
                throws IOException, IllegalArgumentException {

            _isGameStarted = true;
            _model.start(_path);
            _model.smartRobot().addListener(new SmartRobotListener());
            _model.stupidRobot().addListener(new StupidRobotListener());
            super.repaint();

            Season activeSeason = _model.seasonsManager().activeSeason();
            String downfall = activeSeason.downfall().length() != 0 ?
                    ", downfall: " + activeSeason.downfall() :
                    "";
            _infoLabel.setText(activeSeason.name() + " is now" + downfall);
        }

        /**
         * Paints view.
         *
         * @param g Graphic context.
         */
        @Override
        public void paintComponent(final Graphics g) {
            if (_isGameStarted) {
                _colors = _model.seasonsManager().activeSeason().colors();
                g.setColor(_colors.get("field"));
                g.fillRect(0, 0, super.getWidth(), getHeight());

                _offsetX = Math.abs(super.getWidth() - _width) / 2;
                _offsetY = Math.abs(super.getHeight() - _height) / 2;

                drawGrid(g);
                final List<FieldObject> objects = _model.field().objects();
                for (FieldObject obj : _model.field().objects()) {
                    if (obj.pos() instanceof CellPosition) {
                        obj.draw(
                                g,
                                leftTopCorner((CellPosition) obj.pos()),
                                _constants,
                                _colors
                        );
                    } else if (obj.pos() instanceof MiddlePosition) {
                        obj.draw(
                                g,
                                leftTopCorner(
                                        ((MiddlePosition) obj.pos()).cellPos()
                                ),
                                _constants,
                                _colors
                        );
                    }
                }
            }
        }

        /**
         * Draws grid.
         *
         * @param g Graphic context.
         */
        private void drawGrid(final Graphics g) {
            Color preserved = g.getColor();
            g.setColor(_colors.get("grid"));

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

        /**
         * Returns left top corner coordinates of the cell.
         *
         * @param c Position of cell.
         * @return Left top corner coordinates of the cell.
         */
        private Point leftTopCorner(final CellPosition c) {
            return new Point(
                    _offsetX + CELL_SIZE * ((int) c.pos().getX() - 1),
                    _offsetY + CELL_SIZE * ((int) c.pos().getY() - 1)
            );
        }

        /**
         * This method is invoked when the key is typed.
         *
         * @param e Key event.
         */
        @Override
        public void keyTyped(final KeyEvent e) {
        }

        /**
         * This method is invoked when the key is pressed.
         *
         * @param e Key event.
         */
        @Override
        public void keyPressed(final KeyEvent e) {
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

        /**
         * This method is invoked when the key is released.
         *
         * @param e Key event.
         */
        @Override
        public void keyReleased(final KeyEvent e) {
        }

        /**
         * Smart robot listener.
         */
        private class SmartRobotListener
                implements SmartRobot.SmartRobotActionListener {

            @Override
            public void smartRobotMadeMove(final SmartRobotActionEvent e) {
                _log.fine("Smart robot made move");
                repaint();
            }
        }

        /**
         * Stupid robot listener.
         */
        private class StupidRobotListener
                implements StupidRobot.StupidRobotActionListener {

            @Override
            public void stupidRobotMadeMove(final StupidRobotActionEvent e) {
                _log.fine("Stupid robot made move");
                repaint();
            }

            @Override
            public void smartRobotIsCaught(final StupidRobotActionEvent e) {
                _infoLabel.setText("Game is over");
            }
        }
    }

    /**
     * Menu listener.
     */
    private class MenuItemsListener implements ActionListener {
        @Override
        public void actionPerformed(final ActionEvent e) {
            String command = e.getActionCommand();
            if ("exit".equals(command)) {
                System.exit(0);
            }

            if ("new".equals(command)) {
                JFileChooser chooser = new JFileChooser();
                chooser.setCurrentDirectory(new File("."));

                if (chooser.showOpenDialog(SmartAndStupidRobotGame.this) ==
                        JFileChooser.APPROVE_OPTION) {

                    try {
                        _path = chooser.getSelectedFile().toString();
                        _view.start();
                        _view._model.seasonsManager().addListener(
                                new SeasonsListener()
                        );
                    } catch (Exception exception) {
                        _view._isGameStarted = false;
                        _infoLabel.setText("");
                        JOptionPane.showMessageDialog(
                                SmartAndStupidRobotGame.this,
                                exception.getMessage() == null ?
                                        "Cannot load specified file" :
                                        exception.getMessage()
                        );
                    }
                }
            }
        }
    }

    /**
     * Seasons listener.
     */
    private class SeasonsListener implements SeasonsManager.SeasonsListener {

        @Override
        public void seasonIsChanged(final SeasonsEvent e) {
            if (_view._model.isGameFinished()) {
                return;
            }

            String downfall = e.downfall().length() != 0 ?
                    ", downfall: " + e.downfall() :
                    "";
            _infoLabel.setText(e.name() + " is now" + downfall);
            repaint();
        }
    }

    /**
     * Model listener.
     */
    private class ModelListener implements Model.ModelListener {

        @Override
        public void gameIsOver(final ModelEvent e) {
            repaint();
            _infoLabel.setText(e.message());
        }
    }
}
