/******************************************************************************************
 * nub
 * Copyright (c) 2019 Universidad Nacional de Colombia, https://visualcomputing.github.io/
 * @author Jean Pierre Charalambos, https://github.com/VisualComputing
 *
 * All rights reserved. A 2D or 3D scene graph library providing eye, input and timing
 * handling to a third party (real or non-real time) renderer. Released under the terms
 * of the GPL v3.0 which is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************************/

package nub.processing;

import nub.timing.Taskable;

/**
 * Parallel task based on java.util.Timer and java.util.TimerTask.
 */
public abstract class ParallelTask implements Taskable {
  java.util.Timer _timer;
  java.util.TimerTask _timerTask;
  boolean _once;
  boolean _active;
  long _period;

  @Override
  public void run(long period) {
    setPeriod(period);
    run();
  }

  @Override
  public void run() {
    stop();
    _timer = new java.util.Timer();
    _timerTask = new java.util.TimerTask() {
      public void run() {
        execute();
      }
    };
    if (_once)
      _timer.schedule(_timerTask, _period);
    else
      _timer.scheduleAtFixedRate(_timerTask, 0, _period);
    _active = true;
  }

  @Override
  public void stop() {
    if (_timer != null) {
      _timer.cancel();
      _timer.purge();
    }
    _active = false;
  }

  @Override
  public boolean isActive() {
    return _timer != null && _active;
  }

  @Override
  public long period() {
    return _period;
  }

  @Override
  public void setPeriod(long period) {
    _period = period;
  }

  @Override
  public void toggleRecurrence() {
    _once = !_once;
  }

  @Override
  public boolean isRecurrent() {
    return !_once;
  }
}