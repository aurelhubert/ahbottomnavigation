package com.aurelhubert.ahbottomnavigation.notification;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;

/**
 * @author repitch
 */
public final class AHNotificationHelper {

    private AHNotificationHelper() {
        // empty
    }

    /**
     * Get text color for given notification. If color is not set (0), returns default value.
     *
     * @param notification     AHNotification, non null
     * @param defaultTextColor int default text color for all notifications
     * @return
     */
    public static int getTextColor(@NonNull AHNotification notification, @ColorInt int defaultTextColor) {
        int textColor = notification.getTextColor();
        return textColor == 0 ? defaultTextColor : textColor;
    }

    /**
     * Get background color for given notification. If color is not set (0), returns default value.
     *
     * @param notification           AHNotification, non null
     * @param defaultBackgroundColor int default background color for all notifications
     * @return
     */
    public static int getBackgroundColor(@NonNull AHNotification notification, @ColorInt int defaultBackgroundColor) {
        int backgroundColor = notification.getBackgroundColor();
        return backgroundColor == 0 ? defaultBackgroundColor : backgroundColor;
    }

    /**
     * Get background drawable for given notification. If drawable is not set (0), returns default value.
     *
     * @param notification           AHNotification, non null
     * @param defaultBackgroundResId int default background resource for all notifications
     * @return
     */
    public static Drawable getBackgroundDrawable(@NonNull Context context, @NonNull AHNotification notification, @DrawableRes int defaultBackgroundResId) {
        int backgroundResId = notification.getBackgroundResId();
        return backgroundResId != 0 ?
                ContextCompat.getDrawable(context, backgroundResId) :
                    defaultBackgroundResId != 0 ? ContextCompat.getDrawable(context, defaultBackgroundResId) : null;
    }
}
