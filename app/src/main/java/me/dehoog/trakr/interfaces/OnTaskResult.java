package me.dehoog.trakr.interfaces;

/**
 * Author:  jordon
 * Created: November, 13, 2014
 * 1:14 PM
 */
public interface OnTaskResult {

    void onTaskCompleted(String action, boolean success);
    void onTaskCancelled(String action);

}
