package com.example.geoappmidlle;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;


public class Map extends AppCompatActivity {
    private ImageView img;
    private ImageView stop;
    private TextView IN;
    private Button plot;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        img=(ImageView)findViewById(R.id.bmw_img);
        stop=(ImageView)findViewById(R.id.stop);
        IN=(TextView)findViewById(R.id.in);
        plot=(Button)findViewById(R.id.plot);
        final MediaPlayer soundbmw=MediaPlayer.create(this,R.raw.bmw_music);

        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startplayMusic(soundbmw);
            }
        });
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopPlayMusic(soundbmw);
            }
        });


        plot.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String []puncte=IN.getText().toString().trim().split(" ");

                        float [][] coords=new float[puncte.length/2][2];
                        int tt=0;

                        for(int i=0;i<puncte.length/2;i++) {
                                coords[i][0] = Float.parseFloat(puncte[tt]);
                                coords[i][1] = Float.parseFloat(puncte[tt+1]);
                                tt+=2;
                        }

                        LineGraphSeries<DataPoint>series;
                        double x=-10,y,t=0;
                        GraphView graphView=(GraphView)findViewById(R.id.histogram_view);
                        series=new LineGraphSeries<>();
                        for(int i=0;i<100;i++){
                            t=t+0.01;
                            x=t*t;
                            y=function(t,coords)[1];
                            series.appendData(new DataPoint(x,y),true,1000);
                        }
                        graphView.addSeries(series);
                    }
                }
        );
    }

    private void stopPlayMusic(MediaPlayer sound) {
        sound.pause();
    }
    public void startplayMusic(MediaPlayer sound){
        sound.start();
    }
    public double[] function(double t,float [][] coords){
        double [] point=new double[2];
        int n=coords.length;
        for (int i=0;i<n;i++){
            point[0]+=Combinari(i,n)*putere(t,i)*putere(1-t,n-i)*coords[i][0];
            point[1]+=Combinari(i,n)*putere(t,i)*putere(1-t,n-i)*coords[i][1];
        }
        return point;
    }
    private int Combinari(int k,int n){
        return factorial(n)/(factorial(k)*factorial(n-k));
    }
    private int factorial(int n){
        if (n==0) return 1; else return n*factorial(n-1);
    }
    private double putere(double x,int p){
        double rez=1;
        for (int i=0;i<p;i++){
            rez*=x;
        }
        return rez;
    }
}
