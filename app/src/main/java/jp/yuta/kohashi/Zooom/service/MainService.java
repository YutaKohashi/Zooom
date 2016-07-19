package jp.yuta.kohashi.Zooom.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import com.anprosit.android.promise.Callback;
import com.anprosit.android.promise.NextTask;
import com.anprosit.android.promise.Promise;
import com.anprosit.android.promise.Task;

import java.nio.ByteBuffer;

import jp.yuta.kohashi.Zooom.R;
import jp.yuta.kohashi.Zooom.object.ShotApplication;
import jp.yuta.kohashi.Zooom.activity.MainActivity;

public class MainService extends Service {

    public static int ID_NOTIFICATION = 2016;

    private static final int VIEW_SIZE = 200;
    private static final int PX = 70;

    //*******************
    //サービス内で共有する
    static Bitmap shareBitmap = null;
    //*******************

    private RelativeLayout mFloatLayout;
    private WindowManager.LayoutParams wmParams = null;
    WindowManager.LayoutParams params;
    private WindowManager mWindowManager;
    private LayoutInflater inflater;
    private ImageView imageView;
    private ImageButton imageButton;
    private SeekBar seekbar;
    private int mSeekBarProgress;

    private static final String TAG = "MainActivity";

    private MediaProjection mMediaProjection;
    private VirtualDisplay mVirtualDisplay;

    public static int mResultCode = 0;
    public static Intent mResultData;
    public static MediaProjectionManager mMediaProjectionManager1;

    long lastPressTime;
    boolean mHasDoubleClicked = false;

    private WindowManager mWindowManager1 = null;
    private int windowWidth = 0;
    private int windowHeight = 0;
    private ImageReader mImageReader;
    private DisplayMetrics metrics;
    private int mScreenDensity = 0;

    private float downX;
    private float upX;
    private float downY;
    private float upY;

    /*********************************/
     private int initialX;
     private int initialY;
     private float initialTouchX;
     private float initialTouchY;
    /*********************************/
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("メインサービス","onCreate");
        createFloatView();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int i = super.onStartCommand(intent, flags, startId);

        Log.d("メインサービス","onStartCommand");
        createVirtualEnvironment();
        return i;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createFloatView() {

        inflater = LayoutInflater.from(getApplication());
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mFloatLayout = (RelativeLayout) inflater.inflate(R.layout.float_layout, null);
        imageView = (ImageView) mFloatLayout.findViewById(R.id.float_id);

        imageButton=(ImageButton)mFloatLayout.findViewById(R.id.button_image_update);
        seekbar = (SeekBar)mFloatLayout.findViewById(R.id.seekBar);
        seekbar.setMax(VIEW_SIZE);
        seekbar.setProgress(VIEW_SIZE);
        mSeekBarProgress = VIEW_SIZE;

        mFloatLayout.measure(View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED), View.MeasureSpec
                .makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        //View設置時の座標を指定
        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.x = VIEW_SIZE;
        params.y = VIEW_SIZE;

        mWindowManager.addView(mFloatLayout, params);

        mFloatLayout.setOnTouchListener(new View.OnTouchListener() {
            private WindowManager.LayoutParams paramsF = params;
            private float initialTouchX1;
            private float initialTouchY1;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                        long pressTime = System.currentTimeMillis();

                        // ダブルクリックの時
                        if (pressTime - lastPressTime <= 300) {
                            //Notificationを表示
                            createNotification(getApplication(), "クリックして虫眼鏡を表示する", "", "クリックして虫眼鏡を表示する");
                            //サービスを停止する
                            MainService.this.stopSelf();
                            mHasDoubleClicked = true;
                        } else {
                            //ダブルクリックでないとき
                            mHasDoubleClicked = false;
//                            Toast.makeText(getApplicationContext(),"クリック",Toast.LENGTH_SHORT).show();
                        }
                        lastPressTime = pressTime;

                        // 前の座標を取得
                        initialX = paramsF.x;
                        initialY = paramsF.y;
                        // タッチしている位置取得
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        downX = initialTouchX;
                        Log.d("zahyou@@@", String.valueOf(downX));
                        downY = initialTouchY;
                        Log.d("zahyou@@@", String.valueOf(downY));
                        break;
                    case MotionEvent.ACTION_UP:

                        initialTouchX1 = event.getRawX();
                        initialTouchY1 = event.getRawY();

                        upX = initialTouchX1;
                        Log.d("zahyou@@@", String.valueOf(upX));
                        upY = initialTouchY1;
                        Log.d("zahyou@@@", String.valueOf(upY));

                        if (downX == upX && downY == upY) {

                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        paramsF.x = initialX + (int) (event.getRawX() - initialTouchX);
                        paramsF.y = initialY + (int) (event.getRawY() - initialTouchY);

//                        shareBitmap = Bitmap.createBitmap(shareBitmap,paramsF.x,paramsF.y,200,200,null,true);

                        try {
                            //imageViewに画像をセット
                            /**
                             * 1.
                             * 2.トリミングを開始する座標X
                             * 3.トリミングを開始する座標Y
                             * 4.トリミング後の画像の幅 (px)
                             * 5.トリミング後の画像の高さ (px)
                             * 6.
                             * 7.
                             */
//                            int  px = (int)convertDp2Px(PX,getApplicationContext());
                            int  px = (int)convertDp2Px(mSeekBarProgress,getApplicationContext());
                            int fixPx = (int)convertDp2Px(VIEW_SIZE / 2,getApplicationContext());

                            Bitmap temp = Bitmap.createBitmap(shareBitmap, paramsF.x + fixPx -px/2, paramsF.y + fixPx -px/2, px, px, null, true);
                            imageView.setImageBitmap(temp);

                        } catch (Exception e) {

                        }
                        mWindowManager.updateViewLayout(mFloatLayout, params);
                        break;
                }
                return false;
            }
        });



        //更新ボタンのClickイベント
        imageButton.setOnClickListener(new View.OnClickListener() {
            private WindowManager.LayoutParams paramsF = params;
            @Override
            public void onClick(View view) {
                //スクリーンショットを取る
                int x = (int)paramsF.x;
                int y = (int)paramsF.y;
                startVirtualDispCapture(x,y);

            }
        });


        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            private WindowManager.LayoutParams paramsF = params;
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

                if(i == 0){
                    i = i + 1;
                }

                mSeekBarProgress = i;
                int  px = (int)convertDp2Px(mSeekBarProgress,getApplicationContext());
                int fixPx = (int)convertDp2Px(VIEW_SIZE / 2,getApplicationContext());

                Bitmap temp = Bitmap.createBitmap(shareBitmap, paramsF.x + fixPx -px/2, paramsF.y + fixPx -px/2, px, px, null, true);
                imageView.setImageBitmap(temp);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        Log.i(TAG, "created the float sphere view");
    }

    private void createVirtualEnvironment() {
        mMediaProjectionManager1 = (MediaProjectionManager) getApplication().getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        mWindowManager1 = (WindowManager) getApplication().getSystemService(Context.WINDOW_SERVICE);
        windowWidth = mWindowManager1.getDefaultDisplay().getWidth();
        windowHeight = mWindowManager1.getDefaultDisplay().getHeight();
        metrics = new DisplayMetrics();
        mWindowManager1.getDefaultDisplay().getMetrics(metrics);
        mScreenDensity = metrics.densityDpi;
        mImageReader = ImageReader.newInstance(windowWidth, windowHeight, 0x1, 2); //ImageFormat.RGB_565

        Log.i(TAG, "prepared the virtual environment");
    }

    public void startVirtual() {
        if (mMediaProjection != null) {
            Log.i(TAG, "want to display virtual");
            virtualDisplay();
        } else {
            Log.i(TAG, "start screen capture intent");
            Log.i(TAG, "want to build mediaprojection and display virtual");
            setUpMediaProjection();
            virtualDisplay();
        }
    }

    public void setUpMediaProjection() {
        mResultData = ((ShotApplication) getApplication()).getIntent();
        mResultCode = ((ShotApplication) getApplication()).getResult();
        mMediaProjectionManager1 = ((ShotApplication) getApplication()).getMediaProjectionManager();
        mMediaProjection = mMediaProjectionManager1.getMediaProjection(mResultCode, mResultData);
        Log.i(TAG, "mMediaProjection defined");
    }

    private void virtualDisplay() {
        mVirtualDisplay = mMediaProjection.createVirtualDisplay("screen-mirror",
                windowWidth, windowHeight, mScreenDensity, DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                mImageReader.getSurface(), null, null);
        Log.i(TAG, "virtual displayed");
    }

    private void startCapture() {
        Image image = mImageReader.acquireLatestImage();

        int width = image.getWidth();
        int height = image.getHeight();
        final Image.Plane[] planes = image.getPlanes();
        final ByteBuffer buffer = planes[0].getBuffer();
        int pixelStride = planes[0].getPixelStride();
        int rowStride = planes[0].getRowStride();
        int rowPadding = rowStride - pixelStride * width;
        Bitmap bitmap = Bitmap.createBitmap(width + rowPadding / pixelStride, height, Bitmap.Config.ARGB_8888);
        bitmap.copyPixelsFromBuffer(buffer);
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height);

        shareBitmap = bitmap;
        image.close();

        Log.i(TAG, "image data captured");
    }

    private void tearDownMediaProjection() {
        if (mMediaProjection != null) {
            mMediaProjection.stop();
            mMediaProjection = null;
        }
        Log.i(TAG, "mMediaProjection undefined");
    }

    private void stopVirtual() {
        if (mVirtualDisplay == null) {
            return;
        }
        mVirtualDisplay.release();
        mVirtualDisplay = null;
        Log.i(TAG, "virtual display stopped");
    }

    @Override
    public void onDestroy() {
        // to remove mFloatLayout from windowManager
        super.onDestroy();
        if (mFloatLayout != null) {
            mWindowManager.removeView(mFloatLayout);
        }
        tearDownMediaProjection();
        Log.i(TAG, "application destroy");
    }


    //通知表示
    public void createNotification(Context context, String msg, String msgText, String msgAlert) {

        // タップされた時にMainActivityを起動する
        PendingIntent notificIntent = PendingIntent.getActivity(context, 0,
                new Intent(context, MainActivity.class), 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(msg)
                        .setTicker(msgAlert)
                        .setContentText(msgText);

        mBuilder.setContentIntent(notificIntent);
        mBuilder.setDefaults(Notification.DEFAULT_SOUND);
        mBuilder.setAutoCancel(true);
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // ノティフィケーションを起動する
        mNotificationManager.notify(ID_NOTIFICATION, mBuilder.build());

    }

    public void startVirtualDispCapture(int x, int y) {
        final int mXzahyou = x;
        final int mYzahyou = y;

        Promise.with(this, String.class).then(new Task<String, String>() {
            @Override
            public void run(String s, NextTask<String> nextTask) {
                mFloatLayout.setVisibility(View.INVISIBLE);
                nextTask.run(null);

            }

        }).thenOnAsyncThread(new Task<String, String>() {
            @Override
            public void run(String result, NextTask<String> nextTask) {

                try {
                    Thread.sleep(1600);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                nextTask.run(null);

            }

        }).thenOnMainThread(new Task<String, String>() {
            @Override
            public void run(String result, NextTask<String> nextTask) {

                startVirtual();
                nextTask.run(null);

            }

        }).thenOnMainThread(new Task<String, String>() {
            @Override
            public void run(String result, NextTask<String> nextTask) {

                startCapture();
                nextTask.run(null);

            }

        }).setCallback(new Callback<String>() {
            @Override
            public void onSuccess(String s) {
                mFloatLayout.setVisibility(View.VISIBLE);
                ImageView imageView = (ImageView)mFloatLayout.findViewById(R.id.float_id);

//                int  px = (int)convertDp2Px(PX,getApplicationContext());
                int  px = (int)convertDp2Px(mSeekBarProgress,getApplicationContext());
                int fixPx = (int)convertDp2Px(VIEW_SIZE/2,getApplicationContext());
                Bitmap temp2 = null;
                if(shareBitmap != null){
                    temp2 = Bitmap.createBitmap(shareBitmap,mXzahyou + fixPx -px/2, mYzahyou + fixPx -px/2, px, px, null, true);
                }else{
                    Log.d("シェアビットマップ","NULL");
                }
                imageView.setImageBitmap(temp2);
            }

            @Override
            public void onFailure(Bundle bundle, Exception e) {
                mFloatLayout.setVisibility(View.VISIBLE);
            }

        }).create().execute(null);
    }

    /**
     * pixelからdpへの変換
     * @param px
     * @param context
     * @return float dp
     */
    private static float convertPx2Dp(int px, Context context){
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return px / metrics.density;
    }

    public static float convertDp2Px(float dp, Context context){
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return dp * metrics.density;
    }
}