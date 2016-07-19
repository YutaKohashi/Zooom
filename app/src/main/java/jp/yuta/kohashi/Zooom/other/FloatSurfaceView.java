package jp.yuta.kohashi.Zooom.other;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by Yuta on 2016/07/16.
 */

public class FloatSurfaceView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    private static final String LOG = "FloatSurfaceView:::";
    private SurfaceHolder 	mSurfaceHolder;
    private Thread 		  	thread;
    public Bitmap bitmapImage;

    //コンストラクタ
    public FloatSurfaceView(Context context, Bitmap targetBitImage) {
        super(context);
        this.mSurfaceHolder = this.getHolder();

        //引数にbitmapを入れる
        this.initSurfaceHolder(targetBitImage);
    }

    public FloatSurfaceView(Context context, SurfaceView sv,Bitmap targetBitImage) {
        super(context);
        Log.d(LOG, "MainSurfaceView is constructed(Context, SurfaceView)");
        this.mSurfaceHolder = sv.getHolder();
        this.initSurfaceHolder(targetBitImage);
    }


    @Override
    public void run() {
        Log.d(LOG, "run is fired");
        Canvas c = this.mSurfaceHolder.lockCanvas();
        c.drawBitmap(this.bitmapImage, 0, 0, null);
        this.mSurfaceHolder.unlockCanvasAndPost(c);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        thread = new Thread(this);
        thread.start();

    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        this.mSurfaceHolder.setFormat(PixelFormat.TRANSLUCENT);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        thread = null;

    }

    private void drawOnThread()
    {
        thread = new Thread(this);
        thread.start();
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                drawOnThread();
                //描画
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:

                //1筆書きをクリア
                break;
        }

        return super.onTouchEvent(event);
    }

    //独自クラス
    private void initSurfaceHolder(Bitmap targetImage){
        Log.d(LOG, "initSurfaceHolder method is fired");
        this.mSurfaceHolder.addCallback(this);
        Resources res = this.getContext().getResources();
        //ここでBitmapImageをセットしている
//        TODO: 外部からbitmapimageを取得しここで代入
         bitmapImage = targetImage;
//        bitmapImage = BitmapFactory.decodeResource(res, R.drawable.lena);
        setFocusable(true);
        requestFocus();
    }

}
