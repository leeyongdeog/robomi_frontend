package com.robomi.robomifront;

import static androidx.core.content.ContextCompat.getSystemService;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class MessageWebsocketHandler extends WebSocketListener {
    OkHttpClient client;
    private String ws_url = "";
    private WebSocket ws;
    private String message;
    private WebsocketMessageListener msgListener;
    private String PUSH_CHANNEL_ID = "ALERT";

    public void connect(String url){
        ws_url = url;
        client = new OkHttpClient.Builder()
                .readTimeout(3, TimeUnit.SECONDS)
                .build();
        Request request = new Request.Builder().url(ws_url).build();
        ws = client.newWebSocket(request, this);
    }
    public void disconnect(){
        if (ws != null) {
            ws.close(1000, "ws disconnect...");
        }
        if (client != null) {
            client.dispatcher().executorService().shutdown();
        }
    }
    public void setMessageListener(WebsocketMessageListener listener){
        this.msgListener = listener;
    }

    @Override
    public void onOpen(@NonNull WebSocket webSocket, @NonNull Response response) {
        super.onOpen(webSocket, response);
        System.out.println("Android -> JAVA Websocket connect");
    }

    @Override
    public void onClosed(@NonNull WebSocket webSocket, int code, @NonNull String reason) {
        System.out.println("Message" + " Websocket closed reason: " + reason);
        super.onClosed(webSocket, code, reason);
    }

    @Override
    public void onClosing(@NonNull WebSocket webSocket, int code, @NonNull String reason) {
        System.out.println("Message" + " Websocket closing reason: " + reason);
        super.onClosing(webSocket, code, reason);
    }

    @Override
    public void onFailure(@NonNull WebSocket webSocket, @NonNull Throwable t, @Nullable Response response) {
        System.out.println("Message" + " Websocket Connect Failed"+t+response);
        super.onFailure(webSocket, t, response);
    }

    @Override
    public void onMessage(@NonNull WebSocket webSocket, @NonNull String text) {
        Log.d("myLog", "handler received: "+text);

        try{
            JSONObject message = new JSONObject(text);
            if(message.has("allert")){
                String alertJson = message.getString("alert");
                JSONObject alert = new JSONObject(alertJson);

                String name = alert.getString("name");
                String status = alert.getString("status");
                long time = alert.getLong("time");

            }
        }catch(JSONException e){
            Log.e("myLog", "Parsing Error");
        }

        super.onMessage(webSocket, text);
    }

//    private void createNotification(String name, String status, long time){
//        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, PUSH_CHANNEL_ID)
//                .setSmallIcon()
//                .setContentTitle("전시물 상태알림: "+name)
//                .setContentText("상태이상: " + status + " at " + time)
//                .setPriority(NotificationCompat.PRIORITY_HIGH)
//                .setDefaults(Notification.DEFAULT_ALL)
//                .setAutoCancel(true);
//        notificationManager.notify(1, builder.build());
//    }
//
//    private void createNotificationChannel() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationChannel channel = new NotificationChannel(PUSH_CHANNEL_ID, "Alert Notifications",
//                    NotificationManager.IMPORTANCE_HIGH);
//            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//            notificationManager.createNotificationChannel(channel);
//        }
//    }
}
