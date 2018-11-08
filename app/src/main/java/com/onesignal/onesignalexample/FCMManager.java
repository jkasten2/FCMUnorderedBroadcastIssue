package com.onesignal.onesignalexample;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

class FCMManager {

   private String fcmToken;

   private void registerForFCMToken(final Runnable callback) {
      if (fcmToken != null)
         callback.run();
      FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(instanceIdResult -> {
         fcmToken = instanceIdResult.getToken();
         Log.i("Token", "FCM token is: " + fcmToken);
         callback.run();
      });
   }

   void sendOneSignalPushToSelf() {
      registerForFCMToken(() ->
         new Thread(() -> {
            try {
               sendSelfTest(fcmToken, "onesignal_payload");
            } catch (JSONException e) {
               e.printStackTrace();
            }
         }).start());
   }

   void sendOne3rdPartyPushToSelf() {
      registerForFCMToken(() ->
         new Thread(() -> {
            try {
               sendSelfTest(fcmToken, "other_payload");
            } catch (JSONException e) {
               e.printStackTrace();
            }
         }).start());
   }

   private static final int HTTP_TIMEOUT = 30_000;

   // WARNING: Just to make this example easier to reproduce.
   // This would NEVER be included in a real app.
   private static final String FCM_SERVER_KEY = "FCM_SERVER_KEY_HERE";

   private static void sendSelfTest(String token, String payload_key) throws JSONException {
      JSONObject payload = new JSONObject();
      payload.put("to", token);
      JSONObject data = new JSONObject();
      data.put(payload_key, true);
      payload.put("data", data);

      HttpURLConnection con = null;

      try {
         con = (HttpURLConnection)new URL("https://fcm.googleapis.com/fcm/send").openConnection();
         con.setUseCaches(false);
         con.setDoOutput(true);
         con.setConnectTimeout(HTTP_TIMEOUT);
         con.setReadTimeout(HTTP_TIMEOUT);

         con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
         con.setRequestProperty("Authorization", "key=" + FCM_SERVER_KEY);
         con.setRequestMethod("POST");

         byte[] sendBytes = payload.toString().getBytes("UTF-8");
         con.setFixedLengthStreamingMode(sendBytes.length);

         OutputStream outputStream = con.getOutputStream();
         outputStream.write(sendBytes);

         int httpResponse = con.getResponseCode();

         if (httpResponse == HttpURLConnection.HTTP_OK) {
            Scanner scanner = new Scanner(con.getInputStream(), "UTF-8");
            String json = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
            scanner.close();
            Log.i("FCM_SEND", "RECEIVED JSON: " + json);
         }
         else
            Log.w("FCM_SEND", "ERROR sending test push");
      } catch (Throwable t) {
         Log.w("FCM_SEND", "Error thrown from network stack. ", t);
      }
      finally {
         if (con != null)
            con.disconnect();
      }
   }
}
