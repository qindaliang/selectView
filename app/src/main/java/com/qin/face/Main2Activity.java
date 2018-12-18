package com.qin.face;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.qin.face.widget.SelectView;

public class Main2Activity extends AppCompatActivity {

    private SelectView mSelectview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        mSelectview = findViewById(R.id.selectview);
        TextView tv_click = findViewById(R.id.tv_click);
        TextView tv_drag = findViewById(R.id.tv_drag);
        mSelectview.setOnDragListener(fraction -> tv_drag.setText("拖拽比例："+String.valueOf(fraction)));
        mSelectview.setOnclickListener(fraction -> tv_click.setText("点击比例："+String.valueOf(fraction)));
    }
}
