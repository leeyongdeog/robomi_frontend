package com.robomi.robomifront;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class VideoActivity extends AppCompatActivity {
    TextureView streamingView;
    Button gotoMenuBtn, sendSoundBtn;
    private TextureView.SurfaceTextureListener surfaceTextureListener;
    private boolean isSendSound = false;
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_acivity);
        setTitle("REAL-TIME CAMERA");

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
                if(isSendSound) {
                    sendSoundBtn.setText("음성송출 시작");
                    isSendSound = false;
                }
                else{
                    sendSoundBtn.setText("음성송출 종료");
                    isSendSound = true;
                }
            }
        });

        streamingView = (TextureView) findViewById(R.id.video_streamingView);

        surfaceTextureListener = new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surfaceTexture, int i, int i1) {
                // Backend에서 수신받은 frame을 그리는곳
                // 백그라운드 스레드에서 영상 프레임을 받아오는 작업을 수행한다고 가정
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    while (true) {
//                        // 영상 프레임을 받아오는 작업
//                        Bitmap frame = receiveFrameFromServer();
//
//                        // TextureView의 SurfaceTexture에 프레임을 그림
//                        if (frame != null) {
//                            Canvas canvas = streamingView.lockCanvas();
//                            canvas.drawBitmap(frame, 0, 0, null);
//                            streamingView.unlockCanvasAndPost(canvas);
//                        }
//
//                        // 잠시 멈춤 (예: 프레임 속도에 따라 조절)
//                        try {
//                            Thread.sleep(100); // 100ms 대기
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//            }).start();
            }

            @Override
            public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surfaceTexture, int i, int i1) {

            }

            @Override
            public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surfaceTexture) {
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surfaceTexture) {

            }
        };
    }

//    private Bitmap receiveFrameFromServer(){
//        return BitmapFactory.decodeResource(getResources(), R.drawable.sample_frame);
//    }


}
