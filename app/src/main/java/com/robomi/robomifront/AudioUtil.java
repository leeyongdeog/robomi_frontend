package com.robomi.robomifront;

import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import android.util.Base64;

import okhttp3.WebSocket;
import okio.BufferedSource;
import okio.ByteString;
import okio.Okio;

public class AudioUtil {
    private static final String TAG = "AUDIO_UTIL";

    private static final int SAMPLE_RATE = 44100;
    private static final int CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO;
    private static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;

    private static final int RECORDING_STATE = MediaRecorder.AudioSource.MIC;

    private static AudioRecord audioRecord;
    private static boolean isRecording = false;

    private static int DESIRED_CHUNK_SIZE = 5 * 1024;

    public static void gatheringAudio(AppCompatActivity activity, WebSocket socket) throws Exception {

        if (socket != null) {
            isRecording = true;
            startRecording(activity, socket);

        }
    }

    public static void stopGatheringAudio() throws Exception {
        isRecording = false;
        if (audioRecord != null) {
            audioRecord.stop();
            audioRecord.release();
            audioRecord = null;
        }
    }

//    ------------------------------------------------------------------------------------------------- 마이크입력 테스트
    private static void startRecording(AppCompatActivity activity, WebSocket socket) throws Exception {
        if (ContextCompat.checkSelfPermission(activity, android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{android.Manifest.permission.RECORD_AUDIO}, 1);
        }

        int bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT);
        audioRecord = new AudioRecord(RECORDING_STATE, SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT, bufferSize);
        audioRecord.startRecording();

        new Thread(new Runnable() {
            byte[] buffer = new byte[bufferSize];
            List<byte[]> audioChunks = new ArrayList<>();
            int bytesRead;
            int totalBytesRead = 0;
            @Override
            public void run() {
                while (isRecording) {
                    bytesRead = audioRecord.read(buffer, 0, buffer.length);
                    if (bytesRead > 0) {
                        totalBytesRead += bytesRead;
                        if (totalBytesRead >= DESIRED_CHUNK_SIZE) {
                            try {
                                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                                for (byte[] chunk : audioChunks) {
                                    outputStream.write(chunk);
                                }
                                byte[] combinedData = outputStream.toByteArray();
                                outputStream.close();
                                socket.send(ByteString.of(combinedData));
                            } catch (Exception e) {
                                Log.e(TAG, "Error Sending audio data: " + e.getMessage());
                            }
                            totalBytesRead = 0;
                            audioChunks.clear();
                        }
                        else{
                            audioChunks.add(buffer);
                        }
                    }
                }
            }
        }).start();
    }




//----------------------------------------------------------------------------------- mp3 파일 전송 테스트
//    private static void startRecording(AppCompatActivity activity, WebSocket socket) throws Exception {
//
//        if (ContextCompat.checkSelfPermission(activity, android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(activity, new String[]{android.Manifest.permission.RECORD_AUDIO}, 1);
//        }
//
//        int bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT);
//        audioRecord = new AudioRecord(RECORDING_STATE, SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT, bufferSize);
//        audioRecord.startRecording();
//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                Context appContext = activity.getApplicationContext();
//                File filesDir = appContext.getFilesDir();
//                String filePath = filesDir + "/sound.mp3";
//                File mp3File = new File(filePath);
//                BufferedSource source = null;
//                try {
//                    source = Okio.buffer(Okio.source(mp3File));
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                }
//                while (isRecording) {
//                    byte[] buffer = new byte[1024];
//                    int bytesRead = 0;
//                    try {
//                        bytesRead = source.read(buffer);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                        break;
//                    }
//                    if (bytesRead == -1) {
//                        break; // End of file
//                    }
//
//                    ByteString byteString = ByteString.of(buffer, 0, bytesRead);
//                    socket.send(byteString);
//                }
//            }
//        }).start();
//    }
//----------------------------------------------------------------------------------------------------------

}

