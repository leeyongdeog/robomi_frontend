package com.robomi.robomifront;

import static androidx.core.content.ContextCompat.getSystemService;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.text.SimpleDateFormat;
import java.util.Locale;

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

    public void connect(String url) {
        ws_url = url;
        client = new OkHttpClient.Builder()
                .readTimeout(3, TimeUnit.SECONDS)
                .build();
        Request request = new Request.Builder().url(ws_url).build();
        ws = client.newWebSocket(request, this);
    }

    public void disconnect() {
        if (ws != null) {
            ws.close(1000, "ws disconnect...");
        }
        if (client != null) {
            client.dispatcher().executorService().shutdown();
        }
    }

    public void setMessageListener(WebsocketMessageListener listener) {
        this.msgListener = listener;
    }

    @Override
    public void onOpen(@NonNull WebSocket webSocket, @NonNull Response response) {
        super.onOpen(webSocket, response);
        System.out.println("Android -> JAVA Websocket connect");
//        createNotificationChannel();
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
        System.out.println("Message" + " Websocket Connect Failed" + t + response);
        super.onFailure(webSocket, t, response);
    }

    @Override
    public void onMessage(@NonNull WebSocket webSocket, @NonNull String text) {
        Log.d("myLog", "handler received: " + text);

        try {
            JSONObject message = new JSONObject(text);
            if (message.has("alert")) {
                String alertJson = message.getString("alert"); // 문자열로 먼저 가져오기
                JSONObject alert = new JSONObject(alertJson); // 문자열을 JSON 객체로 변환

                String name = alert.getString("name");
                String status = alert.getString("status");
                long time = alert.getLong("time");

                // 푸시 알람 생성 및 표시
                createNotification(name, status, time);
            }
        } catch (JSONException e) {
            Log.e("myLog", "Parsing Error", e);
        }

        super.onMessage(webSocket, text);
    }

    private void createNotification(String name, String status, long time) {
        Context context = MyApplication.getInstance().getApplicationContext(); // MyApplication은 애플리케이션 클래스의 예시입니다.


        // 알림 채널 생성 (Android O 이상 필요)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "ALERT_CHANNEL_ID", "Alert Notifications",
                    NotificationManager.IMPORTANCE_HIGH);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        String alarm_title;
        String alarm_context;
        if("반입물품위반".equals(status)){
            alarm_title = ": 반입금지물품 알림";
            alarm_context = "경고: ";
        }
        else{
            alarm_title = ": 전시물 상태 알림";
            alarm_context = "상태 이상: ";
        }
        Date date = new Date(time);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.getDefault());
        String timeStr = sdf.format(date);

        // 알림 빌더 생성
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "ALERT_CHANNEL_ID")
                .setSmallIcon(R.drawable.icon)
                .setContentTitle(name + alarm_title)
                .setContentText(alarm_context + status + " " + timeStr)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        if (notificationManager.areNotificationsEnabled()) {
            // 알림을 생성할 수 있는 상태
            notificationManager.notify(1, builder.build());
        } else {
            Log.d("myLog", "push alarm permission error");
        }
        Log.d("myLog", "Creating notification for: " + name + " with status: " + status);
    }
}
