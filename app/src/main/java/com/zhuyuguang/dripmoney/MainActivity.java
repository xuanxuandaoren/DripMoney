package com.zhuyuguang.dripmoney;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.zhuyuguang.dripmoney.view.MoneyView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private MoneyView moneyview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        moneyview = (MoneyView) findViewById(R.id.moneyview);
        moneyview.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        moneyview.start();
    }
}
