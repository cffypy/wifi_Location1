package com.example.peiyuyu.wifi_location;

import android.content.Context;
import android.content.DialogInterface;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static android.os.Environment.getRootDirectory;

public class Main2Activity extends AppCompatActivity {
private TextView Long;
private TextView Lat;
private Map<String,String> map_sum;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Long=findViewById(R.id.Long);
        Lat=findViewById(R.id.Lat);
        Button button=findViewById(R.id.extract);
        Button button1=findViewById(R.id.san);
        final WifiManager wf= (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(wf.isWifiEnabled()){
                    wf.startScan();
                    String wifiinfo="";
                    Toast.makeText(Main2Activity.this,"正在扫描....",Toast.LENGTH_SHORT).show();
                    List<ScanResult> scanResults=wf.getScanResults();
                    wifiinfo+="总共扫描到"+scanResults.size()+"个wifi;";
                    Toast.makeText(Main2Activity.this,wifiinfo,Toast.LENGTH_SHORT).show();
                    map_sum =new HashMap<>();
                    // record=new ArrayList<>();
                    for(ScanResult sc:scanResults){
                        map_sum.put(sc.BSSID, String.valueOf(Math.abs(sc.level)));
                        //record.add(map);
                    }
                }
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //String str="";
/*                File file = new File(Environment.getRootDirectory().getAbsolutePath()+"/LOC/wifi.json" );
                String path = file.getPath();*/
                //if(file.exists()) {
                    try {
                        Map<Object,Map<Object,Double>> min_error=new HashMap<>();
                        Map<Object,Map<Object,Double>> error_cpet=new HashMap<>();
                        JSONArray ja=new JSONArray(getJson("wifi.json"));
                        List<Map<Object,Object>> record=new ArrayList<>();
//                        Map<Object,Double>map1=new HashMap<>();
//                        map1.put(next,new Double(1000));
                        for(int i=0;i<ja.length();i++) {
                            JSONObject jsonObject = ja.getJSONObject(i);
                            //List< Map<Object,Double>>list=new ArrayList<>();
                            Map<Object,Object>map=new HashMap<>();
                            map.put("Point NO",jsonObject.get("Point NO"));
                            map.put("PosLon",jsonObject.get("PosLon"));
                            map.put("PosLat",jsonObject.get("PosLat"));
                            JSONArray jsonArray = jsonObject.getJSONArray("FP info");
                            //List<Map<Object,Double>>error=new ArrayList<>();
                            Map<Object,Double>map_error=new HashMap<>();
                            out:for(Iterator k=map_sum.keySet().iterator();k.hasNext();) {
                                Boolean is_find =false;
                                Object next = k.next();
                                for (int j = 0; j < jsonArray.length(); j++) {
                                    JSONObject jsonObject_mac = jsonArray.getJSONObject(j);
                                    if(jsonObject_mac.get("MAC").equals(next)) {
                                        double meanRssi = jsonObject_mac.getDouble("meanRssi");
                                        double level = Double.parseDouble(map_sum.get(next));
/*                                        Object mac = jsonObject_mac.get("MAC");
                                        Double d = new Double(Math.abs(level - meanRssi));*/
                                        map_error.put(jsonObject_mac.get("MAC"),Math.abs(level - meanRssi));
                                         //minus = Math.abs(level - meanRssi);
                                        //error.add(map_error);//将每一指纹点与本次扫描的wifi强度做差,key值选择mac地址
                                        is_find=true;
                                        continue out;
                                    }
                                }
                                if(!is_find){
                                    map_error.put(next, new Double(10000));
                                   // error.add(map_error);
                                }
                            }
//                            min_error.put(jsonObject.get("Point NO"),map1);
                            error_cpet.put(jsonObject.get("Point NO"),map_error);
                            map.put("error",map_error);
/*                            if(minus<min){
                                double a=min;
                                min=minus;
                                minus=a;
                            }*/
                           map.put("minerr_num",0);
                           record.add(map);
                        }
                        //转换存储方式......三要素：点号、mac地址、信号强度差
                        Map<Object/*mac地址*/,Map<Object/*点号*/,Double/*信号强度差*/>>map_tran =new HashMap<>();
                        for(Iterator k=map_sum.keySet().iterator();k.hasNext();){
                            Object next = k.next();//传过来的mac地址
                            Map<Object/*点号*/,Double/*信号强度差*/>map1=new HashMap<>();
                            out: for(Iterator q=error_cpet.keySet().iterator();q.hasNext();){
                                Object next1 = q.next();//点号
                                for(Iterator l=error_cpet.get(next1).keySet().iterator();l.hasNext();){
                                    Object next2 = l.next();//自己的mac地址
                                    if(next.equals(next2)){
                                        map1.put(next1,error_cpet.get(next1).get(next2));
                                        continue out;
                                    }
                                }
                            }
                            map_tran.put(next,map1);
                        }
                        //进行比较放在list里面
                        /**
                         * 进行比较12
                         */
                        List list=new ArrayList();
                        for(Iterator t=map_tran.keySet().iterator();t.hasNext();){
                            Object next = t.next();//重新存储后的mac地址
                            double min=1000;
                            Object object=null;
                            for(Iterator k=map_tran.get(next).keySet().iterator();k.hasNext();){
                                Object next1 = k.next();//点号
                                if(map_tran.get(next).get(next1)<min){
                                    min=map_tran.get(next).get(next1);
/*                                    Map<Object*//*点号*//*,Double*//*信号强度差*//*>map2=new HashMap<>();
                                    map2.put(next1,map_tran.get(next).get(next1));
                                    object=map2;*/
                                    object=next1;
                                }
                            }
                            list.add(object);//将信号强度最接近的点号传给list
//                            min_error.put(next,(Map<Object,Double>)object);
                        }
                        //开始投票
                        out:for(Object obj:list){
                            if(obj!=null) {
                                for (Map<Object, Object> list1 : record) {
                                    if (list1.get("Point NO").equals(obj)) {
                                        list1.put("minerr_num", ((Integer) list1.get("minerr_num") + 1));
                                        continue out;
                                    }
                                }
                            }
                            else {
                                continue out;
                            }
                        }
                        //寻找最高票的位置
                        int max=0;
                        Object str_Loc=null;
                        for(Map<Object,Object>map_result:record){
                                if( (Integer)map_result.get("minerr_num")>max){
                                    max=(Integer)map_result.get("minerr_num");
                                    str_Loc=  map_result.get("Point NO");
                                }
                        }
                        for(Map<Object,Object> ignored :record){
                            if(ignored.get("Point NO").equals(str_Loc)){
                                Long.setText( ignored.get("PosLon").toString());
                                Lat.setText( ignored.get("PosLat").toString());
                                break;
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                //  }
/*                else{
                    AlertDialog alertDialog=new AlertDialog.Builder(Main2Activity.this).create();
                    alertDialog.setTitle("提醒");
                    alertDialog.setMessage("请导入文件");
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    alertDialog.show();
                }*/

            }
        });
    }
    public String getJson(String is) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            BufferedReader bf = new BufferedReader(new InputStreamReader(getApplicationContext().getAssets().open(is)));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }
}
