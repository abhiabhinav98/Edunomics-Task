package com.edunomics.task;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

public class MainActivity extends AppCompatActivity {

    EditText height;
    SeekBar seekBar;
    TextView textView;
    double coeff_of_restitution;
    Button button;
    //double x=0.0,y=0.0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        height = (EditText) findViewById(R.id.height);
        textView = (TextView)findViewById(R.id.coeffvalue);

        button = (Button)findViewById(R.id.plot);

        //seeking the coefficient of restitution:
        seekBar = (SeekBar) findViewById(R.id.coefficient);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                {
                    coeff_of_restitution = (double) i / 100;
                    textView.setText("Coefficient of Restitution: " + coeff_of_restitution);
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    double initHeight = Double.parseDouble(height.getText().toString());

                    double g = 9.8; //gravity
                    //y=initHeight;

                    //inital velocity of ball dropping
                    double v_n = g;

                    double t_total = total_time_to_rest(initHeight, g, coeff_of_restitution);
                    double t1 = firstfall(g, initHeight);

                    int bounce = 1; //, counter = 0;
                    //double dist_total = total_distance_travelled(initHeight, coeff_of_restitution);
                    //double height_n = height_after_n_bounce(g, vn);
                    //first fall
                    double first_fall = flight_time(coeff_of_restitution, g, v_n, bounce);

                    GraphView graph = (GraphView) findViewById(R.id.graph);
                    LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>();

                    graph.addSeries(series);
                    //series.appendData(new DataPoint(0, initHeight), true, 500);


                    int points = 2;
                    series.appendData(new DataPoint(0, initHeight), true, 500);
                    series.appendData(new DataPoint(t1, 0), true, 500);

                    //double height = height_after_n_bounce(g, v_n); //initHeight*(coeff_of_restitution*coeff_of_restitution);
                    //v_n = takeoff_velocity(g, t1);
                    //double time = flight_time(coeff_of_restitution, g, v_n, bounce);


                    double velocity = takeoff_velocity(g, first_fall);
                    double height = height_after_n_bounce(g, velocity);
                    //bounce++;
                    double time = flight_time(coeff_of_restitution, g, velocity, bounce);
                    double t2 = time;
                    while(height>0 && time <= t_total)
                    {
                        points++;
                        t2 += time;
                        series.appendData(new DataPoint(t2,height),true,points);
                        t2 += time;
                        points++;
                        bounce++;
                        series.appendData(new DataPoint(t2,0),true,points);

                        time = flight_time(coeff_of_restitution, g, velocity, bounce);
                        velocity = takeoff_velocity(g, time);
                        height = height_after_n_bounce(g, velocity);


                    }
                    Toast.makeText(MainActivity.this, "Number of bounces are:  "+ bounce, Toast.LENGTH_SHORT).show();




                    /*for (int i = 0; i < 100; i++) {

                        if (x <= first_fall/2 )
                            x = x + 0.5;
                            y = Math.abs(Math.cos(x));
                        series.appendData(new DataPoint(x, y), true, 100);
                    }
                    double velocity = takeoff_velocity(g, first_fall);
                    double h_n = height_after_n_bounce(g, velocity);
                    bounce++;
                    double flight_time = flight_time(coeff_of_restitution, g, velocity, bounce);

                        while(x <= t_total){
                            bounce++;
                            for (int i = 0; i < 100; i++) {

                            if (x <= flight_time && y <= h_n)
                                x = x + 0.5;
                            y = Math.abs(Math.sin(x));
                            series.appendData(new DataPoint(x, y), true, 100);
                        }
                        flight_time = flight_time(coeff_of_restitution, g, velocity, bounce);
                        velocity = takeoff_velocity(g, flight_time);
                        h_n = height_after_n_bounce(g, velocity);
                    }*/




                    //customizing the graph
                    series.setDrawBackground(true);
                    series.setDrawDataPoints(true);
                    graph.getViewport().setScrollable(true);
                    graph.getViewport().setScrollableY(true);
                    graph.getViewport().setScalable(true);
                    graph.getViewport().setScalableY(true);
                    //series.setDataPointsRadius(15);

                    graph.getViewport().setXAxisBoundsManual(true);
                    graph.getViewport().setMinX(0);
                    graph.getViewport().setMaxX(t_total + 5);
                    graph.getViewport().setYAxisBoundsManual(true);
                    graph.getViewport().setMinY(0);
                    graph.getViewport().setMaxY(initHeight + 10);

                }catch (Exception e){
                    Toast.makeText(MainActivity.this, "failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }



    double total_time_to_rest(double initHeight, double g, double coeff_of_restitution){
        double ans=0.0;
        try {
            ans = ((1 + coeff_of_restitution) / (1 - coeff_of_restitution)) * (Math.sqrt(2 * initHeight / g));
        }catch (ArithmeticException e){
            Toast.makeText(MainActivity.this, "failed", Toast.LENGTH_SHORT).show();
        }
        return ans;
    }

    double total_distance_travelled(double initHeight, double coeff_of_restitution){
        double loop = 0.0;
        for(int i=1; i<=Double.POSITIVE_INFINITY; i++)
        {
            loop += Math.pow(coeff_of_restitution,2*i);
        }
        return (1 + 2*loop)*initHeight;
    }

    double height_after_n_bounce(double g, double v_n){
        return Math.pow(v_n, 2)/(2*g);
    }

    double flight_time(double coeff_of_restitution, double g, double velocity, int bounce){
        return Math.pow(coeff_of_restitution, bounce)*((2*velocity)/g);
    }

    double takeoff_velocity(double g, double tn){
        return g*(tn/2);
    }

    double firstfall(double g, double initHeight){
        return Math.sqrt((2*initHeight)/g);
    }

}
