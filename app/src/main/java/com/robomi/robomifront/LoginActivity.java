package com.robomi.robomifront;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
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
import com.google.gson.reflect.TypeToken;
import com.google.gson.Gson;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.ExecutionException;
import org.opencv.android.OpenCVLoader;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import com.robomi.robomifront.ManagerData;

public class LoginActivity extends AppCompatActivity {
    private PreviewView prevView;
    private ActivityResultLauncher<String> reqPermissionLauncher;
    private static final int timeAttack = 20000;
    private Handler timeHandler;
    private static List<ManagerData> managerList;
    private ImageCapture imageCapture;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        setTitle("LOGIN");

        //----------------------------------------
        // Backend에 관리자 리스트 요청후 저장.
        callManagerList();
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
                // Preview use case
                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(prevView.getSurfaceProvider());

                // ImageCapture use case
                imageCapture = new ImageCapture.Builder().build();

                // Select back camera as a default
                CameraSelector cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA;

                // Unbind use cases before rebinding
                cameraProvider.unbindAll();

                // Bind use cases to camera
                cameraProvider.bindToLifecycle((LifecycleOwner) this, cameraSelector, preview, imageCapture);
            }
            catch (ExecutionException | InterruptedException e){
                Toast.makeText(getApplicationContext(), "Start camera error", Toast.LENGTH_SHORT).show();
            }
        }, ContextCompat.getMainExecutor(this));
    }


    private static void callManagerList(){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(BuildConfig.SERVER_URL + "api/manager/allManagers")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                }
                String responseBody = response.body().string();
                Gson gson = new Gson();
                Type itemListType = new TypeToken<List<ManagerData>>(){}.getType();
                managerList = gson.fromJson(responseBody, itemListType);

                for(ManagerData data : managerList){
                    System.out.println("seq: "+data.getSeq());
                    System.out.println("name: "+data.getName());
                    System.out.println("type: "+data.getType());
                    System.out.println("imgPath: "+data.getImgPath());
                    System.out.println("createDate: "+data.getCreateDate());
                    System.out.println("updateDate: "+data.getUpdateDate());
                }
            }
        });
    }
}
//test
