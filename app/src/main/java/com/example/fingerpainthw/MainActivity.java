package com.example.fingerpainthw;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.tensorflow.lite.*;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


import com.nex3z.fingerpaintview.FingerPaintView;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        //Buttons for clearing and activating. Text for output
        Button clear = findViewById(R.id.Clearbutton);
        Button solve = findViewById(R.id.button);
        TextView tex = findViewById(R.id.textView);
        //View for user paint input
        FingerPaintView canv = findViewById(R.id.paintview);
        //listener to run machine learning model and update text
        solve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tex.setVisibility(View.VISIBLE);
                tex.setText(doInference(canv));
            }
        });
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tex.setVisibility(View.INVISIBLE);
                canv.clear();
            }
        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.intro), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

    }

    @Override
    public void onClick (View view){

        Paint pen = new Paint();

        pen.setStyle(Paint.Style.STROKE);


    }
    public String doInference(FingerPaintView val)
    {

        Bitmap map = val.exportToBitmap(28, 28);
        //user input to float
        int width = map.getWidth();
        int height = map.getHeight();
        //setting buffer allocation to match tensor capacity
        ByteBuffer buff = ByteBuffer.allocate(4 * width * height);

        map.copyPixelsToBuffer(buff);
        float[][] output = new float[1][10];
        try (Interpreter interpreter = new Interpreter(loadModelFile())){
            interpreter.allocateTensors();
            interpreter.run(buff, output);
            float pred = output[0][0];
            for (int i = 0; i <= 9; i++){
                if (output[0][i] > pred){
                    pred = output[0][i];
                }
            }
            String predi = "Prediction: " + pred;
            return predi;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private MappedByteBuffer loadModelFile() throws IOException
    {

        AssetFileDescriptor assetFileDescriptor =
                this.getAssets().openFd("digit.tflite");
        FileInputStream fileInputStream = new
                FileInputStream(assetFileDescriptor.getFileDescriptor());
        FileChannel fileChannel = fileInputStream.getChannel();
        long startOffset = assetFileDescriptor.getStartOffset();
        long length = assetFileDescriptor.getLength();
        //return the mapped byte buffer
        return fileChannel.map(FileChannel.MapMode.READ_ONLY,startOffset,length);
    }
    private MappedByteBuffer getByteBuffer(String fileName) throws IOException {
        Context ctx = getApplicationContext();
        AssetFileDescriptor fd = this.getAssets().openFd(fileName);
        long startOffset = fd.getStartOffset();
        long declaredLength = fd.getDeclaredLength();

        FileInputStream inputStream = new FileInputStream(fd.getFileDescriptor());
        FileChannel fc = inputStream.getChannel();
        MappedByteBuffer mbb = fc.map(FileChannel.MapMode.READ_ONLY,
                startOffset, declaredLength);
        return mbb;
    }



}