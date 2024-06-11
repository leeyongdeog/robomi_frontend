package com.robomi.robomifront;

import android.app.Application;
import android.content.res.AssetManager;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.opencv.android.OpenCVLoader;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MyApplication extends Application {
    private CascadeClassifier faceCascade;
    private String cascadePath;
    private List<ManagerData> managerList;

    @Override
    public void onCreate() {
        super.onCreate();

        if(OpenCVLoader.initDebug()){
            loadHaarCascade();
            loadManagerList();
            Log.d("myLog", "OpenCV Load Success!!");
        }
        else{
            Log.d("myLog", "OpenCV Load Failed!!");
        }
    }

    private void loadHaarCascade(){
        try {
            // assets 폴더에서 haarcascade 파일 읽기
            AssetManager assetManager = getAssets();
            InputStream is = assetManager.open("haarcascade_frontalface_default.xml");

            // 임시 파일로 복사
            File cascadeDir = getDir("cascade", MODE_PRIVATE);
            File cascadeFile = new File(cascadeDir, "haarcascade_frontalface_default.xml");

            if (!cascadeFile.exists()) {
                FileOutputStream os = new FileOutputStream(cascadeFile);
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = is.read(buffer)) != -1) {
                    os.write(buffer, 0, bytesRead);
                }
                is.close();
                os.close();
            }

            // CascadeClassifier 초기화
            faceCascade = new CascadeClassifier(cascadeFile.getAbsolutePath());
            cascadePath = cascadeFile.getAbsolutePath();

            Log.d("myLog", "OpenCV Haar-Cascade Load Success!!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void loadManagerList(){
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

                Log.d("myLog", "DB Manager List Load Success!!");
            }
        });
    }

    public CascadeClassifier getFaceCascade(){
        return faceCascade;
    }
    public String getCascadePath(){
        return cascadePath;
    }
    public List<ManagerData> getManagerList(){
        return managerList;
    }
}
