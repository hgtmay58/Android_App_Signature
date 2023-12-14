package edu.takming.myapplication_signatures;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {
    LinearLayout mContent;
    signature mSignature;
    Button mClear, mGetSign;
    public static String tempDir;
    public int count = 1;
    public String current = null;
    private Bitmap mBitmap;
    private Bitmap bitmap;
    ImageView imageiewImageCaptured;
    View mView;
    String ss;
    File signFile = null;
    private Uri mImageCaptureUri;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        //tempDir = Environment.getExternalStorageDirectory() + "/"+ getResources().getString(R.string.external_dir) + "/";
       // tempDir = Environment.getExternalStorageDirectory().getAbsolutePath()+ "/";
       // Log.v("log_tag", tempDir);
        //prepareDirectory();
        //Environment.getExternalStorageDirectory().getAbsolutePath()
       // current = count + ".png";

        //
        mContent = (LinearLayout) findViewById(R.id.linearLayout);
        mSignature = new signature(this, null);
        mSignature.setBackgroundColor(Color.BLUE);
        mContent.addView(mSignature, LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT);
        mClear = (Button) findViewById(R.id.clear);
        mGetSign = (Button) findViewById(R.id.getsign);
        imageiewImageCaptured = (ImageView)findViewById(R.id.imageView);
        mView = mContent;
//
        if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            //
            Log.v("log_tag", "requestPermissions");
            requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},2);
            //
           // return;
        }
        //
        mClear.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.v("log_tag", "Panel Cleared");
                mSignature.clear();
            }
        });
        mGetSign.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //
                Log.v("log_tag", "Panel Saved");


                    mView.setDrawingCacheEnabled(true);
                    try {
                        mSignature.save(mView);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }

        });
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //

        //
    }
    //
    private boolean prepareDirectory() {
        try {
            if (makedirs()) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this,"Could not initiate File System.. Is Sdcard mounted properly",Toast.LENGTH_LONG).show();
            return false;
        }
    }
    private boolean makedirs() {
        File tempdir = new File(tempDir);
        if (!tempdir.exists())
            tempdir.mkdirs();
        if (tempdir.isDirectory()) {
            File[] files = tempdir.listFiles();
            for (File file : files) {
                if (!file.delete()) {
                    System.out.println("Failed to delete " + file);
                }
            }
        }
        return (tempdir.isDirectory());
    }
    //
    public class signature extends View {
        private static final float STROKE_WIDTH = 5f;
        private static final float HALF_STROKE_WIDTH = STROKE_WIDTH / 2;
        private Paint paint = new Paint();
        private Path path = new Path();
        private float lastTouchX;
        private float lastTouchY;
        private final RectF dirtyRect = new RectF();

        public signature(Context context, AttributeSet attrs) {
            super(context, attrs);
            paint.setAntiAlias(true);
            paint.setColor(Color.YELLOW);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeWidth(STROKE_WIDTH);
        }
        public void save(View v) throws IOException {
            Log.v("log_tag", "Width: " + v.getWidth());
            Log.v("log_tag", "Height: " + v.getHeight());
            if (mBitmap == null) {
                mBitmap = Bitmap.createBitmap(v.getWidth(),v.getHeight(), Bitmap.Config.RGB_565);
            }
            Canvas canvas = new Canvas(mBitmap);
            //
            DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
            dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Taipei"));
            String Fname= "SIN_"+dateFormat.format(new Date()); //+".png";
           // String FtoSave = Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+Fname;
            //File myImage = new File(strImage);
            //String FtoSave = tempDir + current;
            //
            File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            Log.d("xxx",storageDir.getAbsolutePath().toString());

            try {
                signFile = File.createTempFile(
                        Fname,  /* prefix */
                        ".png",         /* suffix */
                        storageDir      /* directory */
                );
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            //

            //
            String FtoSave = storageDir.getAbsolutePath().toString()+"/"+Fname+".png";
           // Log.i("File=",FtoSave);
           // File file = new File(FtoSave);
            try {
                FileOutputStream mFileOutStream = new FileOutputStream(signFile);
                v.draw(canvas);
                mBitmap.compress(Bitmap.CompressFormat.PNG, 100, mFileOutStream);

                // Bitmap bmp = intent.getExtras().get("data");
                //              ByteArrayOutputStream stream = new ByteArrayOutputStream();
                //              mBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                //              byte[] val = stream.toByteArray();
                //                String s = new String(val.toString());
                //
                //               ss = Base64.encodeToString(val, Base64.DEFAULT);
                //               System.out.println("String image data" + ss);
                mFileOutStream.flush();
                mFileOutStream.close();
                String url = MediaStore.Images.Media.insertImage(getContentResolver(),mBitmap, Fname+".png", null);
                Log.v("log_tag", "url" + url);
                decodeFile();
                //decodeFile(FtoSave);
            }
            catch (Exception e) {
                Log.v("log_tag", e.toString());
            }
        }
        public void clear() {
            path.reset();
            invalidate();
        }
        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawPath(path, paint);
        }
        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float eventX = event.getX();
            float eventY = event.getY();
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    path.moveTo(eventX, eventY);
                    lastTouchX = eventX;
                    lastTouchY = eventY;
                    return true;
                case MotionEvent.ACTION_MOVE:
                case MotionEvent.ACTION_UP:
                    resetDirtyRect(eventX, eventY);
                    int historySize = event.getHistorySize();
                    for (int i = 0; i < historySize; i++) {
                        float historicalX = event.getHistoricalX(i);
                        float historicalY = event.getHistoricalY(i);
                        expandDirtyRect(historicalX, historicalY);
                        path.lineTo(historicalX, historicalY);
                    }
                    path.lineTo(eventX, eventY);
                    break;
                default:
                    debug("Ignored touch event: " + event.toString());
                    return false;
            }
            invalidate((int) (dirtyRect.left - HALF_STROKE_WIDTH),(int) (dirtyRect.top - HALF_STROKE_WIDTH),(int) (dirtyRect.right + HALF_STROKE_WIDTH),(int) (dirtyRect.bottom + HALF_STROKE_WIDTH));
            lastTouchX = eventX;
            lastTouchY = eventY;
            return true;
        }
        private void debug(String string) {

        }
        private void expandDirtyRect(float historicalX, float historicalY) {
            if (historicalX < dirtyRect.left) {
                dirtyRect.left = historicalX;
            }
            else if (historicalX > dirtyRect.right) {
                dirtyRect.right = historicalX;
            }
            if (historicalY < dirtyRect.top) {
                dirtyRect.top = historicalY;
            }
            else if (historicalY > dirtyRect.bottom) {
                dirtyRect.bottom = historicalY;
            }
        }
        private void resetDirtyRect(float eventX, float eventY) {
            dirtyRect.left = Math.min(lastTouchX, eventX);
            dirtyRect.right = Math.max(lastTouchX, eventX);
            dirtyRect.top = Math.min(lastTouchY, eventY);
            dirtyRect.bottom = Math.max(lastTouchY, eventY);
        }
       //
       public void decodeFile() {


           Bitmap bitmap = null;
           mImageCaptureUri=FileProvider.getUriForFile(this.getContext(),getPackageName(),signFile);
           try {
               bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), mImageCaptureUri);
           } catch (IOException e) {
               e.printStackTrace();
           }
           //
           // The new size we want to scale to
           final int REQUIRED_SIZE = 1024;

           // Find the correct scale value. It should be the power of 2.
           int width_tmp = bitmap.getWidth(), height_tmp = bitmap.getHeight();
           int scale = 1;
           while (true) {
               if (width_tmp < REQUIRED_SIZE && height_tmp < REQUIRED_SIZE)
                   break;
               width_tmp /= 2;
               height_tmp /= 2;
               scale *= 2;
           }

           //
           bitmap=Bitmap.createScaledBitmap(bitmap, width_tmp, height_tmp, false);
           imageiewImageCaptured.setImageBitmap(bitmap);
       }

        //

        public void decodeFile(String filePath) {
            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(filePath, o);

            // The new size we want to scale to
            final int REQUIRED_SIZE = 1024;

            // Find the correct scale value. It should be the power of 2.
            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 1;
            while (true) {
                if (width_tmp < REQUIRED_SIZE && height_tmp < REQUIRED_SIZE)
                    break;
                width_tmp /= 2;
                height_tmp /= 2;
                scale *= 2;
            }

            // Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            bitmap = BitmapFactory.decodeFile(filePath, o2);

            imageiewImageCaptured.setImageBitmap(bitmap);

        }

    }

}