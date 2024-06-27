package com.robomi.robomifront;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.List;
import java.util.concurrent.ExecutionException;
import org.opencv.android.OpenCVLoader;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.FaceRecognizerSF;


public class LoginActivity extends AppCompatActivity {
    private PreviewView prevView;
    private ActivityResultLauncher<String> reqPermissionLauncher;
    private static final int timeAttack = 20000;
    private Handler timeHandler;
    private ImageCapture imageCapture;
    private List<ManagerData> managerDataList;
    private CascadeClassifier faceCascade;
    private String cascadePath;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        setTitle("LOGIN");

        if (!OpenCVLoader.initDebug()) {
            Log.e("OpenCV", "OpenCV initialization failed.");
            Toast.makeText(getApplicationContext(), "오류가 발생했습니다 잠시후에 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            Log.d("OpenCV", "OpenCV initialized successfully.");
        }

        //----------------------------------------
        // Backend에 관리자 리스트 요청후 저장.
        MyApplication app = (MyApplication) getApplication();
        faceCascade = app.getFaceCascade();
        cascadePath = app.getCascadePath();
        managerDataList = app.getManagerList();
        //----------------------------------------

        prevView = (PreviewView) findViewById(R.id.camView);

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

        timeHandler = new Handler();
        timeHandler.postDelayed(() -> {
            Toast.makeText(getApplicationContext(), "인증이 실패했습니다.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }, timeAttack);

        //----------------------------------------
        // haarcascade, face recognition 으로 관리자 리스트와 얼굴대조
        boolean isMatched = true;



        if(isMatched){
            timeHandler.removeCallbacksAndMessages(null);
            Toast.makeText(getApplicationContext(), "안녕하세요 관리자님.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
            startActivity(intent);
            finish();
        } else{
            Toast.makeText(getApplicationContext(), "관리자 목록에 없습니다.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
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

                imageCapture = new ImageCapture.Builder().build();
                CameraSelector cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA;
                cameraProvider.unbindAll();

                cameraProvider.bindToLifecycle((LifecycleOwner) this, cameraSelector, preview, imageCapture);
            }
            catch (ExecutionException | InterruptedException e){
                Toast.makeText(getApplicationContext(), "Start camera error", Toast.LENGTH_SHORT).show();
            }
        }, ContextCompat.getMainExecutor(this));
    }

//    private boolean checkManagerFace(){
//
//    }

}
//test
