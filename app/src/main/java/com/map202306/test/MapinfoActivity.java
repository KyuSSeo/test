package com.map202306.test;

import com.map202306.test.R;

import static java.lang.Math.round;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.WindowMetrics;
import android.widget.Button;
import android.widget.TextView;

public class MapinfoActivity extends Activity {

    private int width;
    private int height;
    private TextView getMapInfoName;
    private TextView getMapInfoAddr;
    private TextView getMapInfoMan_so;
    private TextView getMapInfoMan_dae;
    private TextView getMapInfoMan2_so;
    private TextView getMapInfoMan2_dae;
    private TextView getMapInfoWoman;
    private TextView getMapInfoWoman2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_info);

        //돌아가기 버튼
        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // MainActivity로 돌아가는 인텐트 생성
                Intent intent = new Intent(MapinfoActivity.this, MainActivity.class);
                startActivity(intent);
                finish(); // 현재 액티비티를 종료하여 뒤로 가기 버튼으로 돌아가기
            }
        });

        getMapInfoName = findViewById(R.id.map_info_name2); //화장실명
        getMapInfoAddr = findViewById(R.id.map_info_address); //주소
        getMapInfoMan_so = findViewById(R.id.map_info_manso1); //남-소
        getMapInfoMan_dae = findViewById(R.id.map_info_mandae2); //남-대
        getMapInfoMan2_so = findViewById(R.id.map_info_man2so2); //남-소(장)
        getMapInfoMan2_dae = findViewById(R.id.map_info_man2dae2); //남-대(장)
        getMapInfoWoman = findViewById(R.id.map_info_woman22); //여-대
        getMapInfoWoman2 = findViewById(R.id.map_info_woman23); //여-대(장)

        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        String addr = intent.getStringExtra("address");
        int man_so = intent.getIntExtra("man_so", 0);
        int man_dae = intent.getIntExtra("man_dae", 0);
        int man2_so = intent.getIntExtra("man2_so", 0);
        int man2_dae = intent.getIntExtra("man2_dae", 0);
        int woman = intent.getIntExtra("woman", 0);
        int woman2 = intent.getIntExtra("woman2", 0);

        getMapInfoName.setText(name);
        getMapInfoAddr.setText(addr);
        getMapInfoMan_so.setText(String.valueOf(man_so));
        getMapInfoMan_dae.setText(String.valueOf(man_dae));
        getMapInfoMan2_so.setText(String.valueOf(man2_so));
        getMapInfoMan2_dae.setText(String.valueOf(man2_dae));
        getMapInfoWoman.setText(String.valueOf(woman));
        getMapInfoWoman2.setText(String.valueOf(woman2));

        initLayout();
    }

    private void initLayout() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowMetrics windowMetrics = getWindowManager().getCurrentWindowMetrics();
            width = windowMetrics.getBounds().width();
            height = windowMetrics.getBounds().height();
        } else {
            Display display = getWindowManager().getDefaultDisplay();
            DisplayMetrics displayMetrics = new DisplayMetrics();
            display.getRealMetrics(displayMetrics);
            width = displayMetrics.widthPixels;
            height = displayMetrics.heightPixels;
        }
        getWindow().setLayout((int) round(width * 0.9), (int) round(height * 0.9));
        getWindow().setGravity(Gravity.BOTTOM);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }
}
