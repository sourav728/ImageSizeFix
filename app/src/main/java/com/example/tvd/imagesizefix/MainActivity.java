package com.example.tvd.imagesizefix;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;

public class MainActivity extends Activity {
    private static final int CAPTURE_IMAGE_CLICK_CODE = 10;
    private static File mediaStorageDir;
    private static String timeStamp = "";
    // String timeStamp="";
    private static File mediaFile;
    ImageView imVCature_pic;
    Button btnCapture;
    Uri fileUri;
    // String timeStamp = "";
    // File mediaStorageDir;
    // File mediaFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeControls();


        btnCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Clicked..", Toast.LENGTH_SHORT).show();

                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                StrictMode.setVmPolicy(builder.build());

                final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                startActivityForResult(intent, CAPTURE_IMAGE_CLICK_CODE);

            }
        });
    }

    private void initializeControls() {
        imVCature_pic = (ImageView) findViewById(R.id.image_from_camera);
        btnCapture = (Button) findViewById(R.id.take_image_from_camera);
    }

    private static Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    private static File getOutputMediaFile(int type) {

        mediaStorageDir = new File(Environment.getExternalStorageDirectory(), "Image_Compress" + File.separator + "Normal_images");

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }
        // Create a media file name
        timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_" + timeStamp + ".jpg");
        } else {
            return null;
        }

        return mediaFile;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Toast.makeText(this, "Image saved ", Toast.LENGTH_SHORT).show();
            File sdcard = Environment.getExternalStorageDirectory();
            File directory = new File(sdcard.getAbsolutePath() + "/Image_Compress" + File.separator + "Normal_images");
            File file = new File(directory, "IMG_" + timeStamp + ".jpg");
            try {
                FileInputStream streamIn = new FileInputStream(file);
                Bitmap b = BitmapFactory.decodeStream(streamIn); //This gets the image
                timestampItAndSave(b);
                /****************Below code is for image compression****************/
                /*streamIn.close();
                int origWidth = b.getWidth();
                int origHeight = b.getHeight();

                *//****Required Height && Width fixed here 800*500*****//*

                final int destWidth = 800;//or the width you need
                // final int destHeight = 500;

                if (origWidth > destWidth) {
                    // picture is wider than we want it, we calculate its target height
                    //int destHeight = origHeight/( origWidth / destWidth ) ;
                    int destHeight = 500;
                    // we create an scaled bitmap so it reduces the image, not just trim it
                    Bitmap b2 = Bitmap.createScaledBitmap(b, destWidth, destHeight, false);
                    ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                    // compress to the format you want, JPEG, PNG...
                    // 70 is the 0-100 quality percentage
                    b2.compress(Bitmap.CompressFormat.JPEG, 70, outStream);
                    // we save the file, at least until we have made use of it

                    File f = new File(Environment.getExternalStorageDirectory()
                            + File.separator + "Image_Compress" + File.separator + timeStamp + "compress.jpg");
                    try {
                        f.createNewFile();
                        //write the bytes in file
                        FileOutputStream fo = new FileOutputStream(f);
                        fo.write(outStream.toByteArray());
                        // remember close de FileOutput
                        fo.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                File show = new File(Environment.getExternalStorageDirectory() + File.separator + "Image_Compress" + File.separator + timeStamp + "compress.jpg");
                if (show != null) {
                    Bitmap bitmap2 = BitmapFactory.decodeFile(show.getAbsolutePath());
                    imVCature_pic.setImageBitmap(bitmap2);
                    imVCature_pic.setRotation(90);
                }*/
                /*imVCature_pic.setImageBitmap(b);
                imVCature_pic.setRotation(90);*/
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (resultCode == RESULT_CANCELED) {
            Toast.makeText(this, "Image canceled..", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Can't captured!!", Toast.LENGTH_SHORT).show();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /****************Below code is for adding Watermark************************/
    private void timestampItAndSave(Bitmap bitmap) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        // Bitmap bitmap = BitmapFactory.decodeFile(getOutputMediaFile().getAbsolutePath());

        //        Bitmap src = BitmapFactory.decodeResource(); // the original file is cuty.jpg i added in resources
        Bitmap dest = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateTime = sdf.format(Calendar.getInstance().getTime()); // reading local time in the system

        Canvas cs = new Canvas(dest);
        Paint tPaint = new Paint();
        tPaint.setTextSize(100);
        tPaint.setColor(Color.RED);
        tPaint.setStyle(Paint.Style.FILL);
        cs.drawBitmap(bitmap, 0f, 0f, null);

        /*int xpos = ((cs.getWidth())/2)-2;
        int yPos = (int) ((cs.getHeight() / 2) - ((tPaint.descent() + tPaint.ascent()) / 2)) ;
       */
        float height = tPaint.measureText("yY");
        //cs.drawText(dateTime, 20f, height+15f, tPaint);
        cs.drawText(dateTime, 2000f, 1500f, tPaint);

        try {
            dest.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(new File("/sdcard/timeStampedImage.jpg")));
            File sdcard = Environment.getExternalStorageDirectory();
            File directory = new File(sdcard.getAbsoluteFile() + "/timeStampedImage.jpg");
            FileInputStream watermarkimagestream = new FileInputStream(directory);
            Bitmap watermarkimage = BitmapFactory.decodeStream(watermarkimagestream);
            imVCature_pic.setImageBitmap(watermarkimage);
            // imVCature_pic.setRotation(90);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}