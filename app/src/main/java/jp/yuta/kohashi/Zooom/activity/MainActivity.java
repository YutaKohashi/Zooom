package jp.yuta.kohashi.Zooom.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import jp.yuta.kohashi.Zooom.service.MainService;
import jp.yuta.kohashi.Zooom.object.ShotApplication;

public class MainActivity extends AppCompatActivity {
    private String TAG = "Service";
    private int result = 0;
    private Intent intent = null;
    private int REQUEST_MEDIA_PROJECTION = 1;
    private MediaProjectionManager mMediaProjectionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);

        mMediaProjectionManager = (MediaProjectionManager)getApplication().getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        //startActivityForResult(mMediaProjectionManager.createScreenCaptureIntent(), REQUEST_MEDIA_PROJECTION);
        //MainService.mMediaProjectionManager1 = mMediaProjectionManager;
        //((ShotApplication)getApplication()).setMediaProjectionManager(mMediaProjectionManager);
        //Log.i(TAG, "start screen capture intent");

        //finish();

        //Intent intent = new Intent(getApplicationContext(), MainService.class);
        //startService(intent);

        startIntent();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void startIntent(){
        if(intent != null && result != 0){
            Log.i(TAG, "user agree the application to capture screen");
            //MainService.mResultCode = resultCode;
            //MainService.mResultData = data;
            ((ShotApplication)getApplication()).setResult(result);
            ((ShotApplication)getApplication()).setIntent(intent);

            //サービスを開始する
            Intent intent = new Intent(getApplicationContext(), MainService.class);
            startService(intent);
            Log.i(TAG, "start service MainService");
        }else{
            startActivityForResult(mMediaProjectionManager.createScreenCaptureIntent(), REQUEST_MEDIA_PROJECTION);
            //MainService.mMediaProjectionManager1 = mMediaProjectionManager;
            ((ShotApplication)getApplication()).setMediaProjectionManager(mMediaProjectionManager);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_MEDIA_PROJECTION) {
            if (resultCode != Activity.RESULT_OK) {
                return;
            }else if(data != null && resultCode != 0){
                Log.i(TAG, "user agree the application to capture screen");
                //MainService.mResultCode = resultCode;
                //MainService.mResultData = data;
                result = resultCode;
                intent = data;
                ((ShotApplication)getApplication()).setResult(resultCode);
                ((ShotApplication)getApplication()).setIntent(data);
                Intent intent = new Intent(getApplicationContext(), MainService.class);
                startService(intent);
                Log.i(TAG, "start service MainService");

                finish();
            }
        }
    }
}
