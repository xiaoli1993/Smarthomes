package com.kandaovr.tracking.tracker;

import com.kandaovr.tracking.representation.Quaternion;

/**
 * Created by dongfeng on 2016/6/15.
 */
public interface SensorTrackerListener {
    public void onOrientationChange(Quaternion orientation);
}
