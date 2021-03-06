package com.wilsonow.paymentgateway;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.onesignal.OSNotification;
import com.onesignal.OSNotificationAction;
import com.onesignal.OSNotificationOpenResult;
import com.onesignal.OneSignal;

import org.json.JSONObject;

public class OneSignalNRHandler extends Application implements OneSignal.NotificationReceivedHandler, OneSignal.NotificationOpenedHandler {

    private static final String TAG ="OneSignalHandler";
    private Context context;

    public OneSignalNRHandler(Context context) {
        // Constructor
        this.context = context;
        //this.context = OneSignalNRHandler.this;
    }

    @Override
    public void notificationReceived(OSNotification notification) {
        // Fires when a OneSignal notification is displayed to the user, or
        // when it is received if it is a background / silent notification.

        JSONObject data = notification.payload.additionalData;
        String type, region;

        Log.i(TAG, "1 title: " + notification.payload.title);
        Log.i(TAG, "1 body: " + notification.payload.body);

        if ( data != null )
            Log.i(TAG, "2 data: " + data.toString());

        if (data != null) {
            type = data.optString("TYPE", null);
            region = data.optString("REGION", null);

            if (type != null && type.equals("FIRMWARE")) {
                Log.i("OneSignalExample", "type: " + type);
                Log.i("OneSignalExample", "region: " + region);

                // The following can be used to open an Activity of your choice.
                // Replace - getApplicationContext() - with any Android Context.
                Intent intent = new Intent(context, PromotionActivity.class);
                intent.putExtra("REGION", region);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        }
    }

    @Override
    public void notificationOpened(OSNotificationOpenResult result) {
        // Use to process a OneSignal notification the user just tapped on.

        OSNotificationAction.ActionType actionType = result.action.type;
        JSONObject data = result.notification.payload.additionalData;
        String type, region;

        Log.i(TAG, "2 title: " + result.notification.payload.title);
        Log.i(TAG, "2 body: " + result.notification.payload.body);

        if ( data != null )
            Log.i(TAG, "2 data: " + data.toString());

        if (data != null) {
            type = data.optString("TYPE", null);
            region = data.optString("REGION", null);

            if (type != null && type.equals("FIRMWARE")) {
                Log.i("OneSignalExample", "type: " + type);
                Log.i("OneSignalExample", "region: " + region);

                // The following can be used to open an Activity of your choice.
                // Replace - getApplicationContext() - with any Android Context.
                Intent intent = new Intent(context, PromotionActivity.class);
                intent.putExtra("REGION", region);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        }

        if (actionType == OSNotificationAction.ActionType.ActionTaken)
            Log.i("OneSignalExample", "Button pressed with id: " + result.action.actionID);

        // Add the following to your AndroidManifest.xml to prevent the launching of your main Activity
        //   if you are calling startActivity above.
         /*
            <application ...>
              <meta-data android:name="com.onesignal.NotificationOpened.DEFAULT" android:value="DISABLE" />
            </application>
         */
    }
}
