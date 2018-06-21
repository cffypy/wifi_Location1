package com.example.peiyuyu.wifi_location;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Main3Activity extends AppCompatActivity implements SensorEventListener{
    private SensorManager sm;
    private double s;//距离
    private double a;//角度
    private double xx ;
    private double yy;
    private double xx1;
    private double yy1;
    private EditText x;
    private EditText y;
    private TextView x1;
    private TextView y1;
    private Button loc;
    private Long l;
    private Message message;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
         sm= (SensorManager) getSystemService(Context.SENSOR_SERVICE);
         x=findViewById(R.id.x);
         y=findViewById(R.id.y);
         x1=findViewById(R.id.x1);
         y1=findViewById(R.id.y1);
         loc=findViewById(R.id.loc);
         loc.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 if(x.getText().equals("")||y.getText().equals("")){
                     AlertDialog alertDialog=new AlertDialog.Builder(Main3Activity.this).create();
                     alertDialog.setTitle("没有初始位置");
                     alertDialog.setMessage("请填写初始位置");
                     alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "确定", new DialogInterface.OnClickListener() {
                         @Override
                         public void onClick(DialogInterface dialogInterface, int i) {

                         }
                     });
                     alertDialog.show();
                 }
                 else{
                      xx=Double.parseDouble(String.valueOf(x.getText()));
                      yy=Double.parseDouble(String.valueOf(y.getText()));
                     message=Message.obtain();
                     handler.sendMessage(message);
                 }
             }
         });
    }
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            // if(msg.what==2){
            x1.setText(String.valueOf(xx1));
            y1.setText(String.valueOf(yy1));
            message=Message.obtain();
            // msg1.what=2;
            handler.sendMessageDelayed(message,1000);
            // }

        }
    };

    @Override
    public void onSensorChanged(SensorEvent event) {
        float[] ac_values=new float[3];
//        float[] mg_values=new float[3];
        if(event.sensor.getType()==Sensor.TYPE_ACCELEROMETER){
            ac_values = event.values.clone();
            Double d=/*(System.currentTimeMillis()-l)/1000.0;*/200/1000.0;
            s=0.5*ac_values[1]*d*d;
//            l=System.currentTimeMillis();
/*            x.setText(String.valueOf(ac_values[0]));
            y.setText(String.valueOf(ac_values[1]));
            z.setText(String.valueOf(ac_values[2]));*/

        }
  /*      else if(event.sensor.getType()==Sensor.TYPE_MAGNETIC_FIELD){
             mg_values = event.values.clone();
        }*/
        else if(event.sensor.getType()==Sensor.TYPE_ORIENTATION){
            float[] values = event.values;
            a=Math.toRadians(values[0]);
        }
/*        float[] R        =new float[9];
        float[] fx_values=new float[3];
        sm.getRotationMatrix(R,null,ac_values,mg_values);
        sm.getOrientation(R,fx_values);*/
//       x.setText(String.valueOf(fx_values[0]));
//        y.setText(String.valueOf(fx_values[1]));
//        z.setText(String.valueOf(fx_values[2]));
        //算位置
        xx1=xx+s*Math.cos(a);
        yy1=xx+s*Math.sin(a);
        xx=xx1;
        yy=yy1;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        sm.registerListener(this,sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),SensorManager.SENSOR_DELAY_NORMAL);
//        sm.registerListener(this,sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),SensorManager.SENSOR_DELAY_NORMAL);
        sm.registerListener(this,sm.getDefaultSensor(Sensor.TYPE_ORIENTATION),SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onStop() {
        sm.unregisterListener(this);
        super.onStop();

    }
}
