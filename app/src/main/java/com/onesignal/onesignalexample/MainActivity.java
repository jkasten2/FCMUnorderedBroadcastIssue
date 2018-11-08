package com.onesignal.onesignalexample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

   private FCMManager fcmManager;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);

      fcmManager = new FCMManager();
   }

   public void onSendOneSignalPushClicked(View view) {
      fcmManager.sendOneSignalPushToSelf();
   }

   public void onSendOtherSDKPushClicked(View view) {
      fcmManager.sendOne3rdPartyPushToSelf();
   }
}
