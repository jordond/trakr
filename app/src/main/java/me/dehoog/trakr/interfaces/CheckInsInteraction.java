package me.dehoog.trakr.interfaces;

import me.dehoog.trakr.models.Purchase;

/**
 * Author:  jordon
 * Created: November, 14, 2014
 * 2:41 PM
 */
public interface CheckInsInteraction {
    public void onCheckInsInteraction();
    public void onShowViewer(Purchase purchase);
}
