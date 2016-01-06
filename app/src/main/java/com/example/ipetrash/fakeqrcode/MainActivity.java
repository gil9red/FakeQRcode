package com.example.ipetrash.fakeqrcode;

import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.common.HybridBinarizer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends ActionBarActivity {
    private Camera camera;
    private SurfaceView preview;
    private SurfaceHolder surfaceHolder;
    private TextView qrcodeText;
    private Switch switchSearchQRcode;

    private final String TAG = "helloqrcode";

    Timer timer = new Timer();
    SearchQRCodeTimeTask searchQRCodeTimeTask = new SearchQRCodeTimeTask();

    // Задача для таймера
    private class SearchQRCodeTimeTask extends TimerTask {

//
//        @Override
//        public void run() {
//            // Your logic here...
//
//            // When you need to modify a UI element, do so on the UI thread.
//            // 'getActivity()' is required as this is being ran from a Fragment.
//            getActivity().runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    // This code will always run on the UI thread, therefore is safe to modify UI elements.
//                    myTextBox.setText("my text");
//                }
//            });
//        }
//

        @Override
        public void run() {
            Log.d(TAG, "Search QR Code");

//            Log.d(TAG, "preview="+preview);

            // When you need to modify a UI element, do so on the UI thread.
            // 'getActivity()' is required as this is being ran from a Fragment.
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // This code will always run on the UI thread, therefore is safe to modify UI elements.

                    preview.setDrawingCacheEnabled(true);
                    Bitmap photo = Bitmap.createBitmap(preview.getDrawingCache());

                    // be sure to call the createBitmap that returns a mutable Bitmap
//                    Bitmap photo = Bitmap.createBitmap(preview.getWidth(), preview.getHeight(), Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(photo);
                    preview.draw(canvas);
//                    preview.draw

                    preview.setDrawingCacheEnabled(false);

                    try {
                        java.io.File file = new java.io.File(Environment
                                .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                                + "/image.jpg");

//                        File sdCard = Environment.getDownloadCacheDirectory();
//                        File file = new File(sdCard, "image.jpg");
                        Log.e(TAG, "save = " + photo.compress(Bitmap.CompressFormat.JPEG, 95, new FileOutputStream(file)));
                    } catch (FileNotFoundException e) {
                        Log.e(TAG, e.toString(), e);
                    }

//                    preview.setDrawingCacheEnabled(true);
//                    Bitmap photo = preview.getDrawingCache();

//                    Log.d(TAG, "canvas=" + canvas);
//                    Log.d(TAG, "photo=" + photo);
//                    Log.d(TAG, "photo.w=" + photo.getWidth() + " photo.h=" + photo.getHeight());
//
                    try
                    {
                        String text = decode(photo);

                        qrcodeText.setText(text);
                        qrcodeText.setTextColor(Color.parseColor("#FFFFFF"));

                        // Отключаем кнопку поиска
                        switchSearchQRcode.setChecked(false);
//                        switchSearchQRcode.callOnClick();

                        Log.d(TAG, "QR Code: " + text);
                    }
                    catch (NotFoundException e) {
                        String notFound = "QR code not found.";

                        // Пишем что не нашли, и делаем цвет текста красным
                        qrcodeText.setText(notFound);
                        qrcodeText.setTextColor(Color.parseColor("#FF0000"));
                        Log.d(TAG, notFound);
                    }

                    Log.d(TAG, "5");
                }
            });



//            Log.d(TAG, "1");
//
//            try
//            {
//                String text = decode(photo);
//
//                Log.d(TAG, "2");
//
//                qrcodeText.setText(text);
//                qrcodeText.setTextColor(Color.parseColor("#FFFFFF"));
//
//                Log.d(TAG, "3");
//
//                // Отключаем кнопку поиска
////                switchSearchQRcode.setChecked(false);
//                switchSearchQRcode.callOnClick();
//
//                Log.d(TAG, "4");
//
////                searchQRcode
//
//                Log.d(TAG, "QR Code: " + text);
//            }
//            catch (NotFoundException e) {
//                String notFound = "QR code not found.";
//
//                Log.d(TAG, "1.1");
//                Log.d(TAG, "qrcodeText="+qrcodeText);
//
//                // Пишем что не нашли, и делаем цвет текста красным
//                qrcodeText.setText(notFound);
//                Log.d(TAG, "Color.parseColor");
//                qrcodeText.setTextColor(Color.parseColor("#FF0000"));
//                Log.d(TAG, "1.2");
//                Log.d(TAG, notFound);
//            }
//
//            Log.d(TAG, "5");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        qrcodeText = (TextView) findViewById(R.id.qrcodeText);

        preview = (SurfaceView) findViewById(R.id.surfaceView);
        surfaceHolder = preview.getHolder();
        switchSearchQRcode = (Switch) findViewById(R.id.switchSearchQRcode);

        preview.setZOrderOnTop(true);    // necessary
        surfaceHolder.setFormat(PixelFormat.TRANSPARENT);
    }

    public void searchQRcode(View view) {
        try
        {
            if (switchSearchQRcode.isChecked()) {
                camera = Camera.open();
                camera.setPreviewDisplay(surfaceHolder);

                //ImageView view = (SurfaceView) findViewById(R.id.imageView);
                //view.hol

                if (this.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
                    camera.setDisplayOrientation(90);
                } else {
                    camera.setDisplayOrientation(0);
                }

                // Чтобы включить отображение preview, вызываем
                camera.startPreview();

                // Вызываем каждую секунду без задержки
                timer.schedule(searchQRCodeTimeTask, 0, 1000);

            } else {
                camera.release();
                camera = null;

                timer.cancel();
            }
        }
        catch (Throwable e) {
            Log.e(TAG, e.toString(), e);
        }
    }

    public String decode(Bitmap image) throws NotFoundException {
        int width = image.getWidth(), height = image.getHeight();
        int[] pixels = new int[width * height];
        image.getPixels(pixels, 0, width, 0, 0, width, height);

        RGBLuminanceSource source = new RGBLuminanceSource(width, height, pixels);
        BinaryBitmap bBitmap = new BinaryBitmap(new HybridBinarizer(source));
        Log.e(TAG, "bBitmap="+bBitmap);
        MultiFormatReader reader = new MultiFormatReader();
        return reader.decode(bBitmap).getText();
    }

    public void quit(View view) {
        timer.cancel();

        System.exit(0);
    }
}
