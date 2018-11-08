package com.onesignal.onesignalexample;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

// Not really OneSignal's FCM receiver
// Just an example of how it uses abortBroadcast filter other FCM receivers from
//    getting it's payload
// More Explanation on way this is important in OtherSDKReceiverExample.java
public class OneSignalReceiverExample extends BroadcastReceiver {

   private static final String GCM_RECEIVE_ACTION = "com.google.android.c2dm.intent.RECEIVE";
   private static final String GCM_TYPE = "gcm";
   private static final String MESSAGE_TYPE_EXTRA_KEY = "message_type";

   private static boolean isFCMMessage(Intent intent) {
      if (GCM_RECEIVE_ACTION.equals(intent.getAction())) {
         String messageType = intent.getStringExtra(MESSAGE_TYPE_EXTRA_KEY);
         return (messageType == null || GCM_TYPE.equals(messageType));
      }
      return false;
   }

   private static boolean isTokenUpdate(Intent intent) {
      Bundle bundle = intent.getExtras();
      if (bundle == null || "google.com/iid".equals(bundle.getString("from")))
         return true;
      return false;
   }

   @Override
   public void onReceive(Context context, Intent intent) {
      if (isTokenUpdate(intent))
         return;

      if (!isFCMMessage(intent))
         return;

      Bundle bundle = intent.getExtras();
      String isOneSignalPayload = bundle.getString("onesignal_payload");

      if ("true".equals(isOneSignalPayload)) {
         setResultCode(Activity.RESULT_OK);

         // Prevents other FCM receivers from firing to filter out this payload
         //   since it is already being processed.
         // On Android Pie devices this errors out, since the broadcast is not ordered
         // Not an issue for devices older than Pie even with the same "Google Play services" version
         // Tested with the "Google Play services" app version 12.8.62 & 14.3.67
         abortBroadcast();

         processOneSignalPayload();
         return;
      }

      setResultCode(Activity.RESULT_OK);
   }

   private void processOneSignalPayload() {
      Log.i("OneSignal", "OneSignal payload received from FCM!!!!");
   }
}
