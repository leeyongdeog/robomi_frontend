package com.robomi.robomifront;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MsgActivity extends AppCompatActivity {
    private LinearLayout imgLayout, listLayout;
    private Button btnVideo, btnBack;
    private ImageView imgView;
    private TextView txtDate;
    private LinearLayout linearScroll;
    private static ArrayList<MessageData> captureList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.msg_activity);
        setTitle("MESSAGE");

        imgLayout = (LinearLayout) findViewById(R.id.imgLayout);
        listLayout = (LinearLayout) findViewById(R.id.listLayout);

        btnVideo = (Button) findViewById(R.id.gotoVideo);
        imgView = (ImageView) findViewById(R.id.msgImg);
        txtDate = (TextView) findViewById(R.id.imgDateText);
        linearScroll = (LinearLayout) findViewById(R.id.linearScroll);
        btnBack = (Button) findViewById(R.id.msg_gotoMenu);

        imgLayout.setVisibility(View.INVISIBLE);
        changeLayoutWeight(imgLayout, 0);
        changeLayoutWeight(listLayout, 9);

        //----------------------------------
        // DB에서 메세지 목록 가져오기 최신 20개씩
        callCaptureList();
        //----------------------------------

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MsgActivity.this, MenuActivity.class);
                startActivity(intent);
                finish();
            }
        });
        btnVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MsgActivity.this, VideoActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(MsgActivity.this, MenuActivity.class);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void changeLayoutWeight(View view, float weight){
        LinearLayout.LayoutParams param = (LinearLayout.LayoutParams) view.getLayoutParams();
        param.weight = weight;
        view.setLayoutParams(param);
    }

    private void MakeList(ArrayList<MessageData> datas){
        for(MessageData data : datas){
            LinearLayout row = new LinearLayout(this);
            LinearLayout.LayoutParams rowParam = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    130
            );
            rowParam.setMargins(8,8,8,8);
            row.setLayoutParams(rowParam);

            ImageView icon = new ImageView(this);
            LinearLayout.LayoutParams iconParam = new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.MATCH_PARENT,
                    2
            );
            icon.setLayoutParams(iconParam);
            icon.setImageResource(R.drawable.check_icon);
            icon.setPadding(30,30,30,30);

            ImageView thumb = new ImageView(this);
            thumb.setLayoutParams(iconParam);
            String imgPath = data.getImgPath();

            Glide.with(this).load(imgPath).into(thumb);

            TextView date = new TextView(this);
            LinearLayout.LayoutParams textParam = new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.MATCH_PARENT,
                    6
            );
            date.setLayoutParams(textParam);
            date.setText(data.getUpdateDate());


            row.addView(icon);
            row.addView(thumb);
            row.addView(date);

            linearScroll.addView(row);

            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    imgLayout.setVisibility(View.VISIBLE);
                    changeLayoutWeight(imgLayout, 4);
                    changeLayoutWeight(listLayout, 5);
                    String path = data.getImgPath();
                    GlideUrl glideUrl = new GlideUrl(path, new LazyHeaders.Builder()
                            .addHeader("Authorization", "your-auth-token")
                            .build());
                    Glide.with(MsgActivity.this).load(glideUrl).into(imgView);
                    txtDate.setText(data.getUpdateDate());
                }
            });
        }
    }

    private void callCaptureList(){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(BuildConfig.SERVER_URL + "api/capture/warningCaptures")
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
                Type itemListType = new TypeToken<List<MessageData>>(){}.getType();
                captureList = gson.fromJson(responseBody, itemListType);

                for(MessageData data : captureList){
                    System.out.println("seq: "+data.getSeq());
                    System.out.println("name: "+data.getName());
                    System.out.println("type: "+data.getStatus());
                    System.out.println("imgPath: "+data.getImgPath());
                    System.out.println("updateDate: "+data.getUpdateDate());
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        MakeList(captureList);
                    }
                });
            }
        });
    }
}
