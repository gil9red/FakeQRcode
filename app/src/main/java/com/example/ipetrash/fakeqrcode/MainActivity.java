package com.example.ipetrash.fakeqrcode;

import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.common.HybridBinarizer;

import java.io.IOException;


public class MainActivity extends ActionBarActivity implements TextureView.SurfaceTextureListener {
    private TextView qrcodeText;
    private TextureView mTextureView;
    private Camera mCamera;

    private boolean mFoundQRcode = false;

    private final String TAG = "fakeqrcode";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextureView = (TextureView) findViewById(R.id.textureView);
        mTextureView.setSurfaceTextureListener(this);

        qrcodeText = (TextView) findViewById(R.id.qrcodeText);

        Button buttonQuit = (Button) findViewById(R.id.buttonQuit);
        buttonQuit.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                }
        );
    }

    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        mCamera = Camera.open();

        try {
            if (this.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
                mCamera.setDisplayOrientation(90);
            } else {
                mCamera.setDisplayOrientation(0);
            }

            mCamera.setPreviewTexture(surface);
            mCamera.startPreview();
        } catch (IOException ioe) {
            // Something bad happened
        }
    }

    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        // Ignored, Camera does all the work for us
    }

    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        mCamera.stopPreview();
        mCamera.release();
        return true;
    }

    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        // Invoked every time there's a new Camera preview frame

        Log.d(TAG, "GET FRAME");

        if (mFoundQRcode == false) {
            Bitmap b = mTextureView.getBitmap();

            String text = decode(b);
            Log.d(TAG, text != null ? String.format("decode text = '%s'", text) : "text is null");

//                    // Показываем кнопку при удачном разборе qr-кода
//                    buttonToClipboard.setVisibility(text != null ? View.VISIBLE : View.GONE);

            if (text != null) {
                mCamera.stopPreview();
                mFoundQRcode = true;
            }

            text = text != null ? text : "ERROR: Failed to read the QR code";
            qrcodeText.setText(text);
        }

//        ImageView imgView = (ImageView) findViewById(R.id.imageView);
//        imgView.setImageBitmap(mTextureView.getBitmap());
    }

    public String decode(Bitmap image) {
        Log.d(TAG, "decode start");

        int width = image.getWidth(), height = image.getHeight();
        int[] pixels = new int[width * height];
        image.getPixels(pixels, 0, width, 0, 0, width, height);
        Log.d(TAG, "pixels=" + pixels);

        RGBLuminanceSource source = new RGBLuminanceSource(width, height, pixels);
        BinaryBitmap bBitmap = new BinaryBitmap(new HybridBinarizer(source));
        MultiFormatReader reader = new MultiFormatReader();

        try {
            String result = reader.decode(bBitmap).getText();
            Log.d(TAG, "result=" + result);

            return result;

        } catch (NotFoundException e) {
            Log.e(TAG, "decode exception", e);
            return null;
        } finally {
            Log.d(TAG, "decode end");
        }
    }
}
