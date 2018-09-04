package com.demo.tinkerdemo;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.tinker.lib.tinker.TinkerInstaller;

import org.w3c.dom.Text;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    private Button btnTinker;
    private TextView tvInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnTinker = findViewById(R.id.btn_tinker);
        tvInfo = findViewById(R.id.tv_info);
        btnTinker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvInfo.setText("这是修补后apk");
                File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/patch_signed_7zip.apk");
                if (file.exists()) {
                    TinkerInstaller.onReceiveUpgradePatch(MainActivity.this, Environment.getExternalStorageDirectory().getAbsolutePath() + "/patch_signed_7zip.apk");
                } else {
                    Toast.makeText(MainActivity.this, "未发现差异包", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
