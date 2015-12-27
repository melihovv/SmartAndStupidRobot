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

package melihovv.SmartAndStupidRobotGame.view;

import melihovv.SmartAndStupidRobotGame.model.FieldObject;
import melihovv.SmartAndStupidRobotGame.model.Mire;
import melihovv.SmartAndStupidRobotGame.model.Model;
import melihovv.SmartAndStupidRobotGame.model.SmartRobot;
import melihovv.SmartAndStupidRobotGame.model.Wall;
import melihovv.SmartAndStupidRobotGame.model.navigation.CellPosition;
import melihovv.SmartAndStupidRobotGame.model.navigation.Direction;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;
import java.util.logging.Logger;

/**
 * The <code>View</code> defines representation of <code>Model</code> class.
 */
public class View extends JPanel implements KeyListener {

    private Model _model;
    static final Logger log = Logger.getLogger(View.class.getName());

    private static final int CELL_SIZE = 30;
    private static final int FONT_HEIGHT = 15;

    private static final Color BACKGROUND_COLOR = new Color(175, 255, 175);
    private static final Color GRID_COLOR = Color.GREEN;
    private static final Color FONT_COLOR = Color.RED;
    private static final Color MIRE_COLOR = new Color(139, 69, 19);
    private static final Color WALL_COLOR = Color.BLACK;

    private int _width;
    private int _height;
    private int _offsetX;
    private int _offsetY;

    public View(Model model) {
        _model = model;

        _width = CELL_SIZE * _model.field().width();
        _height = CELL_SIZE * _model.field().height();
        setPreferredSize(new Dimension(_width, _height));
        setMaximumSize(new Dimension(_width, _height));
        setMinimumSize(new Dimension(_width, _height));

        _model.smartRobot().addListener(new SmartRobotObserver());
        addKeyListener(this);

        setFocusable(true);
    }

    @Override
    public void paintComponent(Graphics g) {
        g.setColor(BACKGROUND_COLOR);
        g.fillRect(0, 0, getWidth(), getHeight());

        _offsetX = Math.abs(getWidth() - _width) / 2;
        _offsetY = Math.abs(getHeight() - _height) / 2;

        drawGrid(g);
        drawWalls(g, _model.field().objects(Wall.class));
        drawMires(g, _model.field().objects(Mire.class));
        drawTarget(g, _model.target());
        drawSmartRobot(g, _model.smartRobot());
        // TODO drawStupidRobot
    }

    private void drawGrid(Graphics g) {
        Color preserved = g.getColor();
        g.setColor(GRID_COLOR);

        final int height = getHeight();
        final int width = getWidth();

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
        if (e.getKeyCode() == KeyEvent.VK_UP) {
            _model.smartRobot().makeMove(Direction.north());
        } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            _model.smartRobot().makeMove(Direction.south());
        } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            _model.smartRobot().makeMove(Direction.west());
        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            _model.smartRobot().makeMove(Direction.east());
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    private class SmartRobotObserver
            implements SmartRobot.SmartRobotActionListener {

        @Override
        public void smartRobotMadeMove(SmartRobot.SmartRobotActionEvent e) {
            log.info("Smart robot made move");
            repaint();
        }
    }
}
