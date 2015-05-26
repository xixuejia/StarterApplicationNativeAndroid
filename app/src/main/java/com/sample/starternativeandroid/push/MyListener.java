/*
*
    COPYRIGHT LICENSE: This information contains sample code provided in source code form. You may copy, modify, and distribute
    these sample programs in any form without payment to IBM® for the purposes of developing, using, marketing or distributing
    application programs conforming to the application programming interface for the operating platform for which the sample code is written.
    Notwithstanding anything to the contrary, IBM PROVIDES THE SAMPLE SOURCE CODE ON AN "AS IS" BASIS AND IBM DISCLAIMS ALL WARRANTIES,
    EXPRESS OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, ANY IMPLIED WARRANTIES OR CONDITIONS OF MERCHANTABILITY, SATISFACTORY QUALITY,
    FITNESS FOR A PARTICULAR PURPOSE, TITLE, AND ANY WARRANTY OR CONDITION OF NON-INFRINGEMENT. IBM SHALL NOT BE LIABLE FOR ANY DIRECT,
    INDIRECT, INCIDENTAL, SPECIAL OR CONSEQUENTIAL DAMAGES ARISING OUT OF THE USE OR OPERATION OF THE SAMPLE SOURCE CODE.
    IBM HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS OR MODIFICATIONS TO THE SAMPLE SOURCE CODE.

*/
package com.sample.starternativeandroid.push;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.sample.starternativeandroid.R;
import com.worklight.wlclient.api.WLClient;
import com.worklight.wlclient.api.WLEventSourceListener;
import com.worklight.wlclient.api.WLFailResponse;
import com.worklight.wlclient.api.WLOnReadyToSubscribeListener;
import com.worklight.wlclient.api.WLResponse;
import com.worklight.wlclient.api.WLResponseListener;

/**
 * This class handles all callback functions for various actions such as
 * a. onReadyToSubscribe
 * b. callback from connect
 * c. callback from subscribe
 * d. callback from unsubscribe
 * e. callback when a notification is received
 */
public class MyListener implements WLOnReadyToSubscribeListener, WLResponseListener, WLEventSourceListener {

    /* The mode defines what action the MyListener object will do */
    public static final int MODE_CONNECT = 0;
    public static final int MODE_SUBSCRIBE = 1;
    public static final int MODE_UNSUBSCRIBE = 2;

    private int mode;

    public MyListener(int mode) {
        this.mode = mode;
    }

    /* This function is called when the registration with GCM is successful.
     * We are now ready to subscribe and unsubscribe
     */
    @Override
    public void onReadyToSubscribe() {

		/* Register the event source callback for the alias of myAndroid. 
         * This must be performed before we can subscribe or unsubscribe on an alias
		 */
        WLClient.getInstance().getPush().registerEventSourceCallback("myAndroid", "PushAdapter", "PushEventSource", this);

        AndroidNativePush.updateTextView("Ready to subscribe");
        AndroidNativePush.enableSubscribeButtons();
    }

    /* onFailure - Update the UI with the error message
     *
     */
    @Override
    public void onFailure(WLFailResponse arg0) {
        switch (mode) {
            case MODE_CONNECT:
                AndroidNativePush.updateTextView("Unable to connect : " + arg0.getErrorMsg());
                break;

            case MODE_SUBSCRIBE:
                AndroidNativePush.updateTextView("Failure to subscribe : " + arg0.getErrorMsg());
                break;

            case MODE_UNSUBSCRIBE:
                AndroidNativePush.updateTextView("Failure to unsubscribe : " + arg0.getErrorMsg());
                break;

        }
    }


    @Override
    public void onSuccess(WLResponse arg0) {
        switch (mode) {
            case MODE_CONNECT:
                AndroidNativePush.updateTextView("Connected successfully ");
                break;

            case MODE_SUBSCRIBE:
                AndroidNativePush.updateTextView("Subscribed successfully to push notifications");
                break;

            case MODE_UNSUBSCRIBE:
                AndroidNativePush.updateTextView("Unsubscribed successfully from push notifications");

                break;

        }
    }

    /* Update the UI with the notification received */
    @Override
    public void onReceive(String arg0, String arg1) {
        AndroidNativePush.updateTextView("Notification received  " + arg0);
        NotificationManager mNotificationManager = (NotificationManager) AndroidNativePush._this.getSystemService(Context.NOTIFICATION_SERVICE);
        int icon = R.drawable.push;
        CharSequence tickerText = arg0;
        long when = System.currentTimeMillis();
        Notification.Builder nb = new Notification.Builder(AndroidNativePush._this);
        nb.setWhen(when).setTicker(tickerText).setSmallIcon(icon);
        Notification notification = nb.build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        Context uiContext = AndroidNativePush._this.getApplicationContext();
        Intent notificationIntent = new Intent(uiContext, AndroidNativePush.class);
        PendingIntent pi = PendingIntent.getActivity(uiContext, 0, notificationIntent, 0);
        notification.setLatestEventInfo(uiContext, "Demo", arg1, pi);
        mNotificationManager.notify(1, notification);
    }

}
