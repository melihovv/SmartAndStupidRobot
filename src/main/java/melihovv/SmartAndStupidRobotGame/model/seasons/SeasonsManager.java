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

package melihovv.SmartAndStupidRobotGame.model.seasons;

import melihovv.SmartAndStupidRobotGame.model.field.Field;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.EventObject;
import java.util.List;

/**
 * The <code>Seasons</code> class defines the seasons manager.
 */
public class SeasonsManager implements ActionListener {

    // Seasons.
    private final List<Season> _seasons;
    // Index of active season.
    private int _activeSeasonIndex;
    // List of the listeners.
    private final List<SeasonsListener> _listenerList;
    // Seasons event.
    private final SeasonsEvent _event;
    // Timer.
    private final Timer _timer;
    // Game field.
    private Field _field;

    /**
     * Constructs seasons manager.
     */
    public SeasonsManager(Field field) {
        _field = field;
        _seasons = new ArrayList<>();
        _listenerList = new ArrayList<>();
        _event = new SeasonsEvent(this);
        _activeSeasonIndex = 0;
        _timer = new Timer(5000, this);
    }

    /**
     * Adds <code>season</code> to seasons.
     *
     * @param season Season.
     */
    public void addSeason(Season season) {
        _seasons.add(season);
    }

    /**
     * Runs endless loop with season changing.
     */
    public void start() {
        _timer.start();
    }

    /**
     * Stops season changing.
     */
    public void stop() {
        _timer.stop();
        _activeSeasonIndex = 0;
    }

    /**
     * Returns active season.
     *
     * @return Active season.
     */
    public Season activeSeason() {
        return _seasons.size() != 0 ? _seasons.get(_activeSeasonIndex) : null;
    }


    ////////////////////////////////////////////////////////////////////////////
    // Events.
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Adds listener <code>l</code> to the list of listeners.
     *
     * @param l The seasons listener.
     */
    public void addListener(SeasonsListener l) {
        _listenerList.add(l);
    }

    /**
     * Removes listener <code>l</code> from the list of listeners.
     *
     * @param l The seasons listener.
     */
    public void removeListener(SeasonsListener l) {
        _listenerList.remove(l);
    }

    /**
     * Removes all the listeners.
     */
    public void clearListeners() {
        _listenerList.clear();
    }

    /**
     * Notifies all the listers that the season is changed.
     */
    private void fireSeasonChanged() {
        for (Object listener : _listenerList) {
            ((SeasonsListener) listener).seasonIsChanged(_event);
        }
    }

    /**
     * This method is invoked each 20 seconds.
     *
     * @param e Action event.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (_seasons.size() == 0) {
            return;
        }

        Season prevSeason = _seasons.get(_activeSeasonIndex);
        prevSeason.cleanInfluence(_field);

        if (++_activeSeasonIndex >= _seasons.size()) {
            _activeSeasonIndex = 0;
        }

        Season curSeason = _seasons.get(_activeSeasonIndex);
        curSeason.influence(_field);

        _event.setName(curSeason.name());
        _event.setDownfall(curSeason.downfall());
        fireSeasonChanged();
    }

    /**
     * Seasons event.
     */
    public static class SeasonsEvent extends EventObject {

        // Season name.
        private String _name;
        // Downfall.
        private String _downfall;

        /**
         * Constructs a prototypical Event.
         *
         * @param source The object on which the Event initially occurred.
         * @throws IllegalArgumentException if source is null.
         */
        public SeasonsEvent(Object source) {
            super(source);
        }

        /**
         * Returns season name.
         *
         * @return Season name.
         */
        public String name() {
            return _name;
        }

        /**
         * Sets season name to <code>name</code>.
         *
         * @param name Season name to set.
         */
        public void setName(String name) {
            _name = name;
        }

        /**
         * Returns downfall.
         *
         * @return Downfall.
         */
        public String downfall() {
            return _downfall;
        }

        /**
         * Sets downfall to <code>downfall</code>.
         *
         * @param downfall Downfall.
         */
        public void setDownfall(String downfall) {
            _downfall = downfall;
        }

    }

    /**
     * Seasons listener.
     */
    public interface SeasonsListener extends EventListener {

        /**
         * This method is invoked when season is changed.
         *
         * @param e Seasons event.
         */
        void seasonIsChanged(SeasonsEvent e);
    }
}
