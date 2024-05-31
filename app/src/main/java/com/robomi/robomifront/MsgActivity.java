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

import java.util.ArrayList;

public class MsgActivity extends AppCompatActivity {
    private LinearLayout imgLayout, listLayout;
    private Button btnVideo;
    private ImageView imgView;
    private TextView txtDate;
    private LinearLayout linearScroll;

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

        imgLayout.setVisibility(View.INVISIBLE);
        changeLayoutWeight(imgLayout, 0);
        changeLayoutWeight(listLayout, 9);

        //----------------------------------
        // DB에서 메세지 목록 가져오기 최신 20개씩
        //----------------------------------

        ArrayList<String> test = new ArrayList<>();
        test.add("1");test.add("1");test.add("1");test.add("1");test.add("1");
        MakeList(test);
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

    private void MakeList(ArrayList<String> datas){
        for(int i=0; i<10; ++i){
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
            String imgPath = "https://picsum.photos/50/50";
            Glide.with(this).load(imgPath).into(thumb);

            TextView date = new TextView(this);
            LinearLayout.LayoutParams textParam = new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.MATCH_PARENT,
                    6
            );
            date.setLayoutParams(textParam);
            date.setText("0000/00/00 00:00:00");


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
                    String path = "https://picsum.photos/300/300";
                    Glide.with(MsgActivity.this).load(path).into(imgView);
                    txtDate.setText("2024/00/00 00:00:00");
                }
            });
        }
    }
}
