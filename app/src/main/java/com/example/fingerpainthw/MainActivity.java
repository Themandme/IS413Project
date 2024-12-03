package com.example.fingerpainthw;

import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RadioButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.nex3z.fingerpaintview.FingerPaintView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        RadioButton red = findViewById(R.id.Color1);
        RadioButton blue = findViewById(R.id.Color2);
        RadioButton green = findViewById(R.id.Color3);
        Button clear = findViewById(R.id.Clearbutton);

        FingerPaintView canv = findViewById(R.id.paintview);
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                canv.clear();
            }
        });
        red.setOnClickListener(this);
        blue.setOnClickListener(this);
        green.setOnClickListener(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

    }
    @Override
    public void onClick (View view){
        FingerPaintView canv = findViewById(R.id.paintview);
        Paint pen = new Paint();
        pen.setStyle(Paint.Style.STROKE);
        if(view.getId() == R.id.Color1){
            pen.setColor(Color.RED);
            pen.setStrokeWidth(28);
            canv.setPen(pen);
        }
        else if(view.getId() == R.id.Color2){
            pen.setColor(Color.BLUE);
            pen.setStrokeWidth(36);
            canv.setPen(pen);
        }
        else if(view.getId() == R.id.Color3){
            pen.setColor(Color.GREEN);
            pen.setStrokeWidth(48);
            canv.setPen(pen);
        }
    }
}