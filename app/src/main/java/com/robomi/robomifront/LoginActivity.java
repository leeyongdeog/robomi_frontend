package com.robomi.robomifront;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.util.Log;
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
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.ExecutionException;

public class LoginActivity extends AppCompatActivity {
    private PreviewView prevView;
    private ActivityResultLauncher<String> reqPermissionLauncher;
    private static final int timeAttack = 20000;
    private Handler timeHandler;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        setTitle("LOGIN");

        //----------------------------------------
        // Backend에 관리자 리스트 요청후 저장.
        //----------------------------------------

        prevView = (PreviewView) findViewById(R.id.camView);

        reqPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if(isGranted){
//                        startCamera();
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "Permission error", Toast.LENGTH_SHORT).show();
                    }
        });

        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
//            startCamera();
        }
        else{
            reqPermissionLauncher.launch(android.Manifest.permission.CAMERA);
        }

//        timeHandler = new Handler();
//        timeHandler.postDelayed(() -> {
//            Toast.makeText(getApplicationContext(), "인증이 실패했습니다.", Toast.LENGTH_SHORT).show();
//            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//            startActivity(intent);
//            finish();
//        }, timeAttack);

        //----------------------------------------
        // haarcascade, face recognition 으로 관리자 리스트와 얼굴대조
        boolean isMatched = true;

        if(isMatched){
//            timeHandler.removeCallbacksAndMessages(null);
            Toast.makeText(getApplicationContext(), "안녕하세요 관리자님.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
            startActivity(intent);
//            String currentActivityName = getLocalClassName();
//            Toast.makeText(getApplicationContext(), currentActivityName, Toast.LENGTH_SHORT).show();
            finish();
        } else{
            Toast.makeText(getApplicationContext(), "관리자 목록에 없습니다.", Toast.LENGTH_SHORT).show();
//            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//            startActivity(intent);
//            finish();
        }
        //----------------------------------------


    }

    private void startCamera(){
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try{
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(prevView.getSurfaceProvider());

                CameraSelector camSel = CameraSelector.DEFAULT_FRONT_CAMERA;

                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle(this, camSel, preview);
            }
            catch (ExecutionException | InterruptedException e){
                Toast.makeText(getApplicationContext(), "Start camera error", Toast.LENGTH_SHORT).show();
            }
        }, ContextCompat.getMainExecutor(this));
    }

//    private void startCamera() {
//        final ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);
//        cameraProviderFuture.addListener(() -> {
//            try {
//                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
//
//                if (getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED)) {
//                    bindPreview(cameraProvider);
//                } else {
//                    // 현재 라이프사이클 상태가 유효하지 않으면 로그 출력 또는 예외 처리
//                    Log.e("LoginActivity", "Cannot bind camera. Activity is not in a valid state.");
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }, ContextCompat.getMainExecutor(this));
//    }
//
//    private void bindPreview(ProcessCameraProvider cameraProvider) {
//        Preview preview = new Preview.Builder().build();
//        CameraSelector cameraSelector = new CameraSelector.Builder()
//                .requireLensFacing(CameraSelector.DEFAULT_FRONT_CAMERA)
//                .build();
//
//        preview.setSurfaceProvider(findViewById(R.id.camView).getSurfaceProvider());
//
//        cameraProvider.bindToLifecycle((LifecycleOwner) this, cameraSelector, preview);
//    }
}
//test
