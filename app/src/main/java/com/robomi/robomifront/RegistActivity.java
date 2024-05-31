package com.robomi.robomifront;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.ExecutionException;

public class RegistActivity extends AppCompatActivity {
    PreviewView regCam;
    Button btnReg, confirmRegist;
    EditText regName;
    private ActivityResultLauncher<String> reqPermissionLauncher;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(RegistActivity.this, MenuActivity.class);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.regist_activity);
        setTitle("REGIST");

        btnReg = (Button) findViewById(R.id.btnReg);
        confirmRegist = (Button) findViewById(R.id.confirmRegist);
        regName = (EditText) findViewById(R.id.regName);

        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "찰칵", Toast.LENGTH_SHORT).show();
            }
        });
        confirmRegist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //---------------------------
                // Backend에 캡쳐이미지, 이름 등록요청
                //---------------------------
                Toast.makeText(getApplicationContext(), "등록했습니다.", Toast.LENGTH_SHORT).show();
            }
        });

        regCam = (PreviewView) findViewById(R.id.regCam);

        reqPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if(isGranted){
                        startCamera();
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "Permission error", Toast.LENGTH_SHORT).show();
                    }
                });

        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
            startCamera();
        }
        else{
            reqPermissionLauncher.launch(android.Manifest.permission.CAMERA);
        }
    }

    private void startCamera(){
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try{
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(regCam.getSurfaceProvider());

                CameraSelector camSel = CameraSelector.DEFAULT_FRONT_CAMERA;

                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle(this, camSel, preview);
            }
            catch (ExecutionException | InterruptedException e){
                Toast.makeText(getApplicationContext(), "Start camera error", Toast.LENGTH_SHORT).show();
            }
        }, ContextCompat.getMainExecutor(this));
    }
}
