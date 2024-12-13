package com.example.fingerpainthw;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RadioButton;
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
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        Button clear = findViewById(R.id.Clearbutton);
        Button solve = findViewById(R.id.button);


        FingerPaintView canv = findViewById(R.id.paintview);
        solve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doInference(canv);
            }
        });
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                canv.clear();
            }
        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
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
    public float doInference(FingerPaintView val)
    {

        Bitmap map = val.exportToBitmap();


        //user input to float
        int width = map.getWidth();
        int height = map.getHeight();
        ByteBuffer buff = ByteBuffer.allocate(width * height * 32);
        buff.rewind();
        map.copyPixelsToBuffer(buff);

        int[] input = new int[width * height];
        byte[] bytes = buff.array();
        map.getPixels(input, 0, width, 0, 0, width, map.  getHeight());
        //float[] input1 = new float[input.length];
      //  for (int i = 0; i <= 255; i++){
        //    bytes[i] = (buff) input[i];
        //}
        float[][] output = new float[1][32];
        try (Interpreter interpreter = new Interpreter(loadModelFile())){
            int[] shape = interpreter.getInputTensor(0).shape();
            width = shape[1];
            height = shape[2];
            int inputsize = 4 * width * height * 1;
            //Map<String, Object> inputs2 = new HashMap<>();
            //inputs2.put("Inputs", input);
            //HashMap<String, Object> outputs = new HashMap<>();

            interpreter.run(buff, output);
            float[] result = output[1];
            interpreter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return output[1][1];
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