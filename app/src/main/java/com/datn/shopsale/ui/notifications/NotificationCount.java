package com.datn.shopsale.ui.notifications;

public class NotificationCount {
    static NotificationCount instance;

    public static NotificationCount getInstance() {
        if (instance == null) {
            instance = new NotificationCount();
        }
        return instance;
    }

    public static int count = 0;
}
