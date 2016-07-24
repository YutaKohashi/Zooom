package jp.yuta.kohashi.Zooom.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import jp.yuta.kohashi.Zooom.R;
import jp.yuta.kohashi.Zooom.service.MainService;
import jp.yuta.kohashi.Zooom.object.ShotApplication;

public class MainActivity extends AppCompatActivity {
    private String TAG = "Service";
    private int result = 0;
    private Intent intent = null;
    private int REQUEST_MEDIA_PROJECTION = 1;
    private MediaProjectionManager mMediaProjectionManager;

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        FrameLayout frameLayout = (FrameLayout)findViewById(R.id.layout_frame);
        int width = frameLayout.getWidth();
        int height = frameLayout.getHeight();

        // ウィンドウマネージャのインスタンス取得
//        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
//        // ディスプレイのインスタンス生成
//        Display disp = wm.getDefaultDisplay();
//
//        Rect rectgle = new Rect();
//        getWindow().getDecorView().getWindowVisibleDisplayFrame(rectgle);
        Display display = getWindowManager().getDefaultDisplay();
        Point p = new Point();
        display.getSize(p);
        Log.d("メインアクティビティ","p.y：" + String.valueOf(p.y));
        Log.d("メインアクティビティ","height：" + String.valueOf(height));
        int StatusBarHeight = p.y-height;

//        int statusBarHeight = getStatusBarHeight(MainActivity.this);

        Log.d("メインアクティビティ","ステータスバーの高さ：" + String.valueOf(StatusBarHeight));

        ((ShotApplication)getApplication()).setHeight(height);
        ((ShotApplication)getApplication()).setWidth(width);
        ((ShotApplication)getApplication()).setStatusBarHeight(StatusBarHeight);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.dummy_layout);

        mMediaProjectionManager = (MediaProjectionManager)getApplication().getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        startIntent();
    }

    private void startIntent(){
        if(intent != null && result != 0){
            Log.i(TAG, "user agree the application to capture screen");
            ((ShotApplication)getApplication()).setResult(result);
            ((ShotApplication)getApplication()).setIntent(intent);

            //サービスを開始する
            Intent intent = new Intent(getApplicationContext(), MainService.class);
            startService(intent);
            Log.i(TAG, "start service MainService");
        }else{
            startActivityForResult(mMediaProjectionManager.createScreenCaptureIntent(), REQUEST_MEDIA_PROJECTION);
            ((ShotApplication)getApplication()).setMediaProjectionManager(mMediaProjectionManager);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //ダイアログの結果を監視
        if (requestCode == REQUEST_MEDIA_PROJECTION) {
            if (resultCode != Activity.RESULT_OK) {
                finish();
                return;
            }else if(data != null && resultCode != 0){
                Log.i(TAG, "user agree the application to capture screen");
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

    /* Return Statusbar size.
    * @param activity Activity
    * @return Statusbar size
    */
    public static int getStatusBarHeight(Activity activity){
        final Rect rect = new Rect();
        Window window = activity.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(rect);
        return rect.top;
    }
}
