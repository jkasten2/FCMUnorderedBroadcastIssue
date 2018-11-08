package com.onesignal.onesignalexample;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

// This is an example of some other push SDK that will also receive the FCM broadcast
// It might be coded poorly where it may not handle other payload formats;
//   1. It might display a notification of it's own resulting in double notifications.
//      - It will most likely not display correctly either it is trying to parse a OneSignal payload
//   2. App might crash due to it trying to parse the payload in correctly.
public class OtherSDKReceiverExample extends BroadcastReceiver {

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
      String isOtherSDKPayload = bundle.getString("other_payload");

      if (!"true".equals(isOtherSDKPayload)) {
         Log.e("OtherSDK", "ERROR - Unexpected payload!. This FCM receiver should have NOT fired!");
      }
      else {
         Log.i("OtherSDK", "FCM 'OtherSDK' Message received in the expected payload format");
      }
   }
}
