package com.robomi.robomifront;

import android.app.Application;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.graphics.Color;
import android.graphics.Matrix;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

//import org.opencv.android.OpenCVLoader;
//import org.opencv.android.Utils;
//import org.opencv.core.Mat;
//import org.opencv.imgproc.Imgproc;
//import org.opencv.objdetect.CascadeClassifier;
//import org.opencv.face.LBPHFaceRecognizer;
//import org.opencv.core.MatOfInt;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MyApplication extends Application implements WebsocketMessageListener{
    private static MyApplication instance;
//    private CascadeClassifier faceCascade;
    private String cascadePath;
//    private LBPHFaceRecognizer faceRecognizer;
    private List<ManagerData> managerList;
    private MessageWebsocketHandler msgWsHandler;
//    private String messageWsUrl = "ws://54.180.210.34:8080/msg";
//    private String messageWsUrl = "ws://192.168.123.122:8080/msg";
    private String messageWsUrl = "ws://192.168.0.223:8080/msg";

    public MessageWebsocketHandler getMsgWsHandler(){
        return msgWsHandler;
    }

    public static MyApplication getInstance() {
        return instance;
    }

    @Override
    public void onMessageReceived(String message) {
        Log.d("myLog", "received: "+message);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        msgWsHandler = new MessageWebsocketHandler();
        msgWsHandler.setMessageListener(this);
        msgWsHandler.connect(messageWsUrl);

        loadManagerList();

//        if(OpenCVLoader.initDebug()){
//            loadHaarCascade();
//            loadManagerList();
//            Log.d("myLog", "OpenCV Load Success!!");
//        }
//        else{
//            Log.d("myLog", "OpenCV Load Failed!!");
//        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        if(msgWsHandler != null) msgWsHandler.disconnect();
    }


    private void loadManagerList(){
        Log.d("myLog", BuildConfig.SERVER_URL + "api/manager/allManagers");
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(BuildConfig.SERVER_URL + "api/manager/allManagers")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("myLog", BuildConfig.SERVER_URL + "api/manager/allManagers - fail");
                Log.d("myLog", e.getMessage());
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
//                augmentAndTrainImages(managerList);
                Log.d("myLog", "DB Manager List Load Success!!");
            }
        });
    }

//    private void loadHaarCascade(){
//        try {
//            // assets 폴더에서 haarcascade 파일 읽기
//            AssetManager assetManager = getAssets();
//            InputStream is = assetManager.open("haarcascade_frontalface_default.xml");
//
//            // 임시 파일로 복사
//            File cascadeDir = getDir("cascade", MODE_PRIVATE);
//            File cascadeFile = new File(cascadeDir, "haarcascade_frontalface_default.xml");
//
//            if (!cascadeFile.exists()) {
//                FileOutputStream os = new FileOutputStream(cascadeFile);
//                byte[] buffer = new byte[4096];
//                int bytesRead;
//                while ((bytesRead = is.read(buffer)) != -1) {
//                    os.write(buffer, 0, bytesRead);
//                }
//                is.close();
//                os.close();
//            }
//
//            // CascadeClassifier 초기화
//            faceCascade = new CascadeClassifier(cascadeFile.getAbsolutePath());
//            cascadePath = cascadeFile.getAbsolutePath();
//            faceRecognizer = LBPHFaceRecognizer.create();
//
//            Log.d("myLog", "OpenCV Haar-Cascade Load Success!!");
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void augmentAndTrainImages(List<ManagerData> managerList) {
//        List<Bitmap> allImages = new ArrayList<>();
//        List<String> allLabels = new ArrayList<>();
//
//        for (ManagerData manager : managerList) {
//            Bitmap originalBitmap = loadImageFromPath(manager.getImgPath());
//
//            if (originalBitmap != null) {
//                // 좌 20도 회전
//                Bitmap leftRotatedBitmap = rotateImage(originalBitmap, -20);
//                saveBitmap(leftRotatedBitmap, manager.getName() + "_left_rotated.jpg");
//                allImages.add(leftRotatedBitmap);
//                allLabels.add(manager.getName());
//
//                // 우 20도 회전
//                Bitmap rightRotatedBitmap = rotateImage(originalBitmap, 20);
//                saveBitmap(rightRotatedBitmap, manager.getName() + "_right_rotated.jpg");
//                allImages.add(rightRotatedBitmap);
//                allLabels.add(manager.getName());
//
//                // 원본 사진
//                saveBitmap(originalBitmap, manager.getName() + "_original.jpg");
//                allImages.add(originalBitmap);
//                allLabels.add(manager.getName());
//
//                // 밝기를 30% 떨어뜨린 사진
//                Bitmap darkenedBitmap = changeBrightness(originalBitmap, -30);
//                saveBitmap(darkenedBitmap, manager.getName() + "_darkened.jpg");
//                allImages.add(darkenedBitmap);
//                allLabels.add(manager.getName());
//
//                // 밝기를 30% 올린 사진
//                Bitmap brightenedBitmap = changeBrightness(originalBitmap, 30);
//                saveBitmap(brightenedBitmap, manager.getName() + "_brightened.jpg");
//                allImages.add(brightenedBitmap);
//                allLabels.add(manager.getName());
//            }
//        }
//
//        // 모든 이미지를 FaceRecognition 모델에 학습시킴
//        trainFaceRecognitionModel(allImages, allLabels);
//    }
//
//    private void trainFaceRecognitionModel(List<Bitmap> images, List<String> labels) {
//        List<Mat> mats = new ArrayList<>();
//        for (Bitmap bitmap : images) {
//            Mat mat = new Mat();
//            Utils.bitmapToMat(bitmap, mat);
//            Imgproc.cvtColor(mat, mat, Imgproc.COLOR_BGR2GRAY);
//            mats.add(mat);
//        }
//
//        // 레이블을 MatOfInt로 변환
//        int[] labelArray = new int[labels.size()];
//        Map<String, Integer> labelMap = new HashMap<>();
//        int labelCounter = 0;
//        for (int i = 0; i < labels.size(); i++) {
//            if (!labelMap.containsKey(labels.get(i))) {
//                labelMap.put(labels.get(i), labelCounter++);
//            }
//            labelArray[i] = labelMap.get(labels.get(i));
//        }
//        MatOfInt labelsMat = new MatOfInt(labelArray);
//
//        // 학습
//        faceRecognizer.train(mats, labelsMat);
//        faceRecognizer.save(getFilesDir() + "/face_recognition_model.xml");
//        Log.d("myLog", "face_recognition train Success!!");
//    }
//
//    private Bitmap loadImageFromPath(String imgPath) {
//        return BitmapFactory.decodeFile(imgPath);
//    }
//
//    private Bitmap rotateImage(Bitmap source, float angle) {
//        Matrix matrix = new Matrix();
//        matrix.postRotate(angle);
//        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
//    }
//
//    private Bitmap changeBrightness(Bitmap bitmap, int value) {
//        Bitmap result = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
//        for (int x = 0; x < bitmap.getWidth(); x++) {
//            for (int y = 0; y < bitmap.getHeight(); y++) {
//                int pixel = bitmap.getPixel(x, y);
//                int red = Color.red(pixel) + value;
//                int green = Color.green(pixel) + value;
//                int blue = Color.blue(pixel) + value;
//                red = Math.max(0, Math.min(255, red));
//                green = Math.max(0, Math.min(255, green));
//                blue = Math.max(0, Math.min(255, blue));
//                result.setPixel(x, y, Color.rgb(red, green, blue));
//            }
//        }
//        return result;
//    }
//
//    private void saveBitmap(Bitmap bitmap, String fileName) {
//        File file = new File(getFilesDir(), fileName);
//        try (FileOutputStream out = new FileOutputStream(file)) {
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    public void InitFaceRecognition(){

    }

//    public CascadeClassifier getFaceCascade(){
//        return faceCascade;
//    }
    public String getCascadePath(){
        return cascadePath;
    }
    public List<ManagerData> getManagerList(){
        return managerList;
    }
}
