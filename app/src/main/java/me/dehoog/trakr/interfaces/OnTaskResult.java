package me.dehoog.trakr.interfaces;

import android.os.Bundle;

/**
 * Author:  jordon
 * Created: November, 13, 2014
 * 1:14 PM
 */
public interface OnTaskResult {

    void onTaskCompleted(Bundle bundle);
    void onTaskCancelled(String action);

}
