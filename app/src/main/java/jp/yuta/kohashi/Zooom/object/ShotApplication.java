package jp.yuta.kohashi.Zooom.object;

import android.app.Application;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;

/**
 * Created by Yuta on 2016/07/20.
 */

/**
 * アプリケーションクラスを継承しデータ、オブジェクトを共有する
 */
public class ShotApplication extends Application {
    private int result;
    private Intent intent;
    private MediaProjectionManager mMediaProjectionManager;

    private int mWidth;
    private int mHeight;
    private int mStatusBarHeight;

    public int getResult(){
        return result;
    }

    public Intent getIntent(){
        return intent;
    }

    public MediaProjectionManager getMediaProjectionManager(){
        return mMediaProjectionManager;
    }

    public int getHeight(){
        return mHeight;
    }

    public int getWidth(){
        return mWidth;
    }

    public int getStatusBarHeight(){
        return mStatusBarHeight;
    }

    public void setResult(int result1){
        this.result = result1;
    }

    public void setIntent(Intent intent1){
        this.intent = intent1;
    }

    public void setMediaProjectionManager(MediaProjectionManager mMediaProjectionManager){
        this.mMediaProjectionManager = mMediaProjectionManager;
    }

    public void setHeight(int height){
        this.mHeight = height;
    }

    public void setWidth(int width){
        this.mWidth  = width;
    }

    public void setStatusBarHeight(int height){
        this.mStatusBarHeight  = height;
    }
}
