package com.robomi.robomifront;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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
import okio.ByteString;

public class VideoActivity extends AppCompatActivity {
    TextureView streamingView;
    Button gotoMenuBtn, sendSoundBtn;
    private TextureView.SurfaceTextureListener surfaceTextureListener;
    private boolean isSendSound = false;


    private static final String wsVideoPath = "ws://192.168.123.122:8080/video";
    private static final String wsAudioPath = "ws://192.168.123.122:8080/audio";
//    private static final String wsVideoPath = "ws://54.180.210.34:8080/video";
//    private static final String wsAudioPath = "ws://54.180.210.34:8080/audio";

    private OkHttpClient videoClient;
    private OkHttpClient audioClient;
    private WebSocket videoSocket;
    private WebSocket audioSocket;

    private Bitmap currentFrame;

    TextView caminfo_txt;
    int currentCameraIndex = 0;
    int cameraCount = 0;

    LinearLayout camsel_layout;

    AudioUtil audioUtil;

    private final class VideoSocketListener extends WebSocketListener{
        @Override
        public void onOpen(WebSocket webSocket, Response response){
            runOnUiThread(() -> {
                Log.d("myLog","Android -> JAVA Websocket connect");
                callStreamingVideo();
            });
        }
        @Override
        public void onMessage(WebSocket webSocket, String msg){
            Log.d("myLog","onmessage");
            runOnUiThread(() -> {
                try{
                    JSONObject jsonObject = new JSONObject(msg);
                    currentCameraIndex = jsonObject.getInt("cameraIndex");
                    String frameStr = jsonObject.getString("frameData");
                    byte[] decodeByte = Base64.decode(frameStr, Base64.NO_WRAP);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(decodeByte, 0, decodeByte.length);
                    Log.d("myLog","decodeByte: " + decodeByte);
                    Log.d("myLog","bitmap: " + bitmap);
                    if(bitmap != null){
                        runOnUiThread(() -> {
                            currentFrame = bitmap;
                            drawFrame();
                        });
                    }
                    else{
                        Log.d("myLog","error - decodeByte: " + decodeByte);
                        Log.d("myLog","error - bitmap: " + bitmap);
                    }
                }catch (Exception e){

                }

            });
        }

        @Override
        public void onClosing(WebSocket webSocket, int code, String reason){
            runOnUiThread(() -> {
                System.out.println("Websocket closing reason: " + reason);
            });
        }
        @Override
        public void onFailure(WebSocket webSocket, Throwable t, Response response){
            runOnUiThread(() -> {
                System.out.println("Websocket Connect Failed"+t+response);
            });
        }
    }
    private final class AudioSocketListener extends WebSocketListener{
        @Override
        public void onOpen(WebSocket webSocket, Response response){
            runOnUiThread(() -> {
                System.out.println("Audio Websocket connect");
            });
        }
        @Override
        public void onMessage(WebSocket webSocket, String msg){
            runOnUiThread(() -> {
                try{

                }catch (Exception e){

                }

            });
        }
        @Override
        public void onMessage(WebSocket webSocket, ByteString bytes){
            byte[] byteArray = bytes.toByteArray();
            runOnUiThread(() -> {
                try{

                }catch (Exception e){

                }

            });
        }
        @Override
        public void onClosing(WebSocket webSocket, int code, String reason){
            runOnUiThread(() -> {
                System.out.println("Audio Websocket closing reason: " + reason);
            });
        }
        @Override
        public void onFailure(WebSocket webSocket, Throwable t, Response response){
            runOnUiThread(() -> {
                System.out.println("Audio Websocket Connect Failed"+t+response);
            });
        }
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_acivity);
        setTitle("REAL-TIME CAMERA");



        videoClient = new OkHttpClient.Builder()
                .readTimeout(3, TimeUnit.SECONDS)
                .build();

        audioClient = new OkHttpClient.Builder()
                .readTimeout(3, TimeUnit.SECONDS)
                .build();

        Request request = new Request.Builder().url(wsVideoPath).build();
        VideoSocketListener listener = new VideoSocketListener();
        videoSocket = videoClient.newWebSocket(request, listener);

        Request audio_request = new Request.Builder().url(wsAudioPath).build();
        AudioSocketListener audio_listener = new AudioSocketListener();
        audioSocket = audioClient.newWebSocket(audio_request, audio_listener);

        audioUtil = new AudioUtil();

        gotoMenuBtn = (Button) findViewById(R.id.video_gotoMenuBtn);
        gotoMenuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(VideoActivity.this, MenuActivity.class);
                startActivity(intent);
                finish();
            }
        });

        sendSoundBtn = (Button) findViewById(R.id.video_sendSoundBtn);
        sendSoundBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isSendSound) {
                    sendSoundBtn.setText("음성송출 종료");
                    isSendSound = true;
                    callStreamingAudio(); // 음성받기
                    gatheringAudio(); // 음성보내기
                }
                else{
                    sendSoundBtn.setText("음성송출 시작");
                    isSendSound = false;
                    callStopStreamingAudio();
                    stopGatheringAudio();
                }
            }
        });

        streamingView = (TextureView) findViewById(R.id.video_streamingView);
        caminfo_txt = (TextView) findViewById(R.id.video_cam_txt);
        camsel_layout = (LinearLayout) findViewById(R.id.video_camsel_layout);
        getCameraCount();
    }

    private void getCameraCount(){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(BuildConfig.SERVER_URL + "api/streaming/getCameraCount")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                // 실패시 처리
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                }
                String responseBody = response.body().string();
                cameraCount = Integer.parseInt(responseBody.trim());

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < cameraCount; ++i) {
                            final int idx = i;
                            Button btn = new Button(VideoActivity.this);
                            btn.setText("카메라 " + (idx + 1));
                            btn.setLayoutParams(new LinearLayout.LayoutParams(
                                    0,
                                    LinearLayout.LayoutParams.WRAP_CONTENT,
                                    1.0f
                            ));
                            btn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    currentCameraIndex = idx;
                                }
                            });
                            camsel_layout.addView(btn);
                        }
                        caminfo_txt.setText("총 카메라 갯수: " + cameraCount + " / 현재 카메라: "+currentCameraIndex);
                    }
                });
            }
        });
    }
    private void callStreamingVideo(){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(BuildConfig.SERVER_URL + "api/streaming/startVideoWebsocket")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                // 실패시 처리
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                }
                // 응답 처리
            }
        });
    }
    private void callStreamingAudio(){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(BuildConfig.SERVER_URL + "api/streaming/startAudioWebsocket")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                // 실패시 처리
                System.out.println("Audio Streaming Start Call FAILED: "+ e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                }
                // 응답 처리
                System.out.println("Audio Streaming Start Call SUCCESS");
            }
        });
    }
    private void callStopStreamingAudio(){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(BuildConfig.SERVER_URL + "api/streaming/stopAudioWebsocket")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                // 실패시 처리
                System.out.println("Audio Streaming Stop Call FAILED: "+ e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                }
                // 응답 처리
                System.out.println("Audio Streaming Stop Call SUCCESS");
            }
        });
    }

    private void gatheringAudio(){
        try{
            audioUtil.gatheringAudio(VideoActivity.this, audioSocket);
        } catch(Exception e){
            Log.e("AUDIO_UTIL", "Error Audio Util");
        }

    }
    private void stopGatheringAudio(){
        try{
            audioUtil.stopGatheringAudio();
        } catch (Exception e){
            Log.e("AUDIO_UTIL", "Error Audio Util stop");
        }
    }
    private void drawFrame(){
        if(currentFrame != null && streamingView.isAvailable()){
            Canvas canvas = streamingView.lockCanvas();
            if(canvas != null){
                int canvas_w = canvas.getWidth();
                int canvas_h = canvas.getHeight();
                int img_w = currentFrame.getWidth();
                int img_h = currentFrame.getHeight();
                float scale_x = (float) canvas_w / img_w;
                float dx = (canvas_w - img_w * scale_x) /2;
                float dy = (canvas_h - img_h * scale_x) /2;
                Matrix mat = new Matrix();
                mat.postScale(scale_x, scale_x);
                mat.postTranslate(dx, dy);

                canvas.drawBitmap(currentFrame, mat, null);
                streamingView.unlockCanvasAndPost(canvas);
            }
        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        if(videoSocket != null){
            videoSocket.close(1000, "Destroy Video Websocket");
        }
        if(audioSocket != null){
            stopGatheringAudio();
            audioSocket.close(1000, "Destroy Audio Websocket");
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(VideoActivity.this, MenuActivity.class);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
