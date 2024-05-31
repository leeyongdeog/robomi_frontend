package com.robomi.robomifront;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.util.concurrent.ExecutionException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Multipart;
import retrofit2.http.Tag;

public class RegistActivity extends AppCompatActivity {
    PreviewView regCam;
    Button btnReg, confirmRegist;
    EditText regName;
    private ActivityResultLauncher<String> reqPermissionLauncher;
    private ImageCapture imageCapture;


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
                captureAndUpload();
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

    private void captureAndUpload(){
        if(imageCapture == null){
            Toast.makeText(getApplicationContext(), "카메라 오류.", Toast.LENGTH_SHORT).show();
            return;
        }

        File photoFile = new File(getExternalFilesDir(null), "capture_image.jpg");

        ImageCapture.OutputFileOptions outOptions = new ImageCapture.OutputFileOptions.Builder(photoFile).build();

        imageCapture.takePicture(outOptions,ContextCompat.getMainExecutor(RegistActivity.this),
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(ImageCapture.OutputFileResults outputFileResults) {
                        // insert your code here.
                        Log.d("onImageSavedgetSavedUri", String.valueOf(outputFileResults.getSavedUri()));
                        Toast.makeText(getApplicationContext(), "찍혔어요",Toast.LENGTH_LONG).show();
                        uploadImage(photoFile);
                    }
                    @Override
                    public void onError(ImageCaptureException error) {
                        // insert your code here.
                        Log.d(" Camerror", String.valueOf(error));
                        Toast.makeText(getApplicationContext(), "에러났어요",Toast.LENGTH_LONG).show();

                    }
                }
        );


//        imageCapture.takePicture(outOptions, ContextCompat.getMainExecutor(this),
//                new ImageCapture.OnImageSavedCallback() {
//                    @Override
//                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
//                        uploadImage(photoFile);
//                    }
//
//                    @Override
//                    public void onError(@NonNull ImageCaptureException exception) {
//                        Toast.makeText(getApplicationContext(), "캡쳐 실패.", Toast.LENGTH_SHORT).show();
//                    }
//                });
    }

    private void uploadImage(File photoFile){
        String name = regName.getText().toString();

        if(name.isEmpty()){
            Toast.makeText(getApplicationContext(), "이름을 입력하세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!photoFile.exists() || !photoFile.canRead()) {
            Toast.makeText(getApplicationContext(), "유효하지 않은 파일입니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        OkHttpClient client = new OkHttpClient.Builder().build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.123.10:8080/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RegistInterface service = retrofit.create(RegistInterface.class);

        RequestBody reqFile = RequestBody.create(photoFile, MediaType.parse("image/*"));
        MultipartBody.Part body = MultipartBody.Part.createFormData("img", photoFile.getName(), reqFile);
        RequestBody reqName = RequestBody.create(name, MediaType.parse("text/plain"));

        Call<Void> call = service.addManager(body, reqName);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful()){
                    Toast.makeText(getApplicationContext(), "등록했습니다.", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getApplicationContext(), "등록 실패.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("myLog", "요청 실패: " + t.getMessage());
                Toast.makeText(getApplicationContext(), "등록 실패.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void startCamera(){
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try{
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                // Preview use case
                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(regCam.getSurfaceProvider());

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
}
