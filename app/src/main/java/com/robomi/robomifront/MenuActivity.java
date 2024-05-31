package com.robomi.robomifront;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MenuActivity extends AppCompatActivity {
    Button btnMsg, btnVideo, btnLocation, btnRegist, btnLogout, btnObject;

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
}
