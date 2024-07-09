package com.robomi.robomifront;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MenuActivity extends AppCompatActivity {
    Button btnMsg, btnVideo, btnLocation, btnRegist, btnLogout, btnObject, btnRobot;
    boolean isRobotStop;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_activity);
        setTitle("MENU");

        btnMsg = (Button) findViewById(R.id.btnMsg);
        btnVideo = (Button) findViewById(R.id.btnVideo);
        btnLocation = (Button) findViewById(R.id.btnLocation);
        btnRegist = (Button) findViewById(R.id.btnRegist);
        btnLogout = (Button) findViewById(R.id.btnLogout);
        btnObject = (Button) findViewById(R.id.btnObject);
        btnRobot = (Button) findViewById(R.id.btnRobot);
        isRobotStop = false;

        btnRobot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isRobotStop){
                    btnRobot.setText("순찰 시작");
                    isRobotStop = false;
                    sendHttpGetRequest("http://121.143.245.56:5000/stop");
                }
                else{
                    btnRobot.setText("순찰 종료");
                    isRobotStop = true;
                    sendHttpGetRequest("http://121.143.245.56:5000/start");
                }
            }
        });

        btnMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuActivity.this, MsgActivity.class);
                startActivity(intent);
                finish();
            }
        });
        btnVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuActivity.this, VideoActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        btnRegist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuActivity.this, RegistActivity.class);
                startActivity(intent);
                finish();
            }
        });
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MenuActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        btnObject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuActivity.this, ObjectRegistActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void sendHttpGetRequest(final String urlString) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(urlString);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");

                    // 서버 응답 코드 확인 (200은 성공)
                    int responseCode = conn.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        // 요청 성공
                        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        String inputLine;
                        StringBuffer response = new StringBuffer();
                        while ((inputLine = in.readLine()) != null) {
                            response.append(inputLine);
                        }
                        in.close();
                        // 응답 처리 (옵셔널)
                    } else {
                        // 요청 실패 처리
                        Log.e("HttpURLConnection", "GET request failed with response code: " + responseCode);
                    }
                    conn.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
