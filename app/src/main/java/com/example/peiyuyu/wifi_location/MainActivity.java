package com.example.peiyuyu.wifi_location;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private TextView t;
    //public List<Map<String,String>>record;
    private Map<String,String> map;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        t=findViewById(R.id.text);
        t.setMovementMethod(ScrollingMovementMethod.getInstance());
        final WifiManager wf= (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        Button button=findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    if(wf.isWifiEnabled()){
                         wf.startScan();
                         String wifiinfo="当前扫描到的wifi网络：\n";
                        Toast.makeText(MainActivity.this,"正在扫描....",Toast.LENGTH_SHORT).show();
                        List<ScanResult> scanResults=wf.getScanResults();
                        wifiinfo+="总共扫描到"+scanResults.size()+"个wifi;\n";
                         map =new HashMap<>();
                       // record=new ArrayList<>();
                        for(ScanResult sc:scanResults){
                            wifiinfo+=sc.SSID+"||"+sc.BSSID+"||"+sc.level+"||"+sc.frequency+"\n";
                            map.put(sc.BSSID, String.valueOf(Math.abs(sc.level)));
                            //record.add(map);
                        }
                        t.setText(wifiinfo);
                    }

            }
        });
        Button button3=findViewById(R.id.button3);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str="";
                for(Iterator i=map.keySet().iterator();i.hasNext();){
                    Object key = i.next();
                    str+=key+"||"+map.get(key)+"\n";
                }//提取map里面key值不一样的各项
/*                for(Map<String, String> list:record){
                    str+=list.keySet()+"||"+list.get(list.keySet());
                }*///每个mac地址都不相同
                t.setText(str);
            }
        });
    }
    public void next(View view){
        Intent intent=new Intent(MainActivity.this,Main2Activity.class);
        startActivity(intent);
    }
    public void pdr(View view){
        Intent intent=new Intent(MainActivity.this,Main3Activity.class);
        startActivity(intent);
    }
}
