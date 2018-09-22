package jp.techacademy.shohei.yamamoto.autoslideshowapp;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Button;
import java.util.Timer;
import java.util.TimerTask;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity{

    Button mBackButton;
    Button mAutoButton;
    Button mNextButton;
    ImageView imageView;
    Cursor cursor = null;
    Timer mTimer;

    double mTimerSec = 0.0;

    private static final int PERMISSIONS_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Handler mHandler = new Handler();

        mBackButton = (Button) findViewById(R.id.back_button);
        mAutoButton = (Button) findViewById(R.id.auto_button);
        mNextButton = (Button) findViewById(R.id.next_button);

        // Android 6.0以降の場合
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //パーミッションの許可状態を確認する
            if (checkCallingOrSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                //許可されている
                getContentsInfo();
            } else {
                //許可されていないので許可ダイアログを表示する
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_CODE);
            }
            //Android5以下の場合
        } else {
            getContentsInfo();
        }
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cursor.moveToNext() == false) {
                    cursor.moveToFirst();
                    int fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
                    Long id = cursor.getLong(fieldIndex);
                    Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

                    ImageView imageView = (ImageView) findViewById(R.id.imageView);
                    imageView.setImageURI(imageUri);
                } else {
                    cursor.moveToNext();
                    int fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
                    Long id = cursor.getLong(fieldIndex);
                    Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

                    ImageView imageView = (ImageView) findViewById(R.id.imageView);
                    imageView.setImageURI(imageUri);
                }
            }
        });

        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cursor.moveToPrevious() == false) {
                    cursor.moveToLast();
                    int fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
                    Long id = cursor.getLong(fieldIndex);
                    Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

                    ImageView imageView = (ImageView) findViewById(R.id.imageView);
                    imageView.setImageURI(imageUri);
                } else {
                    cursor.moveToPrevious();
                    int fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
                    Long id = cursor.getLong(fieldIndex);
                    Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

                    ImageView imageView = (ImageView) findViewById(R.id.imageView);
                    imageView.setImageURI(imageUri);
                }
            }
        });

        mAutoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTimer == null) {
                    mAutoButton.setText("停止");
                    mNextButton.setEnabled(false);
                    mBackButton.setEnabled(false);
                    mTimer = new Timer();
                    mTimerSec = 0.0;
                    mTimer.schedule(new TimerTask() {
                        @Override
                        public  void run(){
                            mTimerSec += 0.1;
                            mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (cursor.moveToNext() == false) {
                                cursor.moveToFirst();
                                int fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
                                Long id = cursor.getLong(fieldIndex);
                                Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

                                ImageView imageView = (ImageView) findViewById(R.id.imageView);
                                imageView.setImageURI(imageUri);
                            } else {
                                cursor.moveToNext();
                                int fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
                                Long id = cursor.getLong(fieldIndex);
                                Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

                                ImageView imageView = (ImageView) findViewById(R.id.imageView);
                                imageView.setImageURI(imageUri);
                            }
                        }
                            });
                        }
                    },2000,2000);
                }else{
                mAutoButton.setText("再生");
                mBackButton.setEnabled(true);
                mNextButton.setEnabled(true);
                mTimer.cancel();
                mTimer = null;
            }
        }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("Android", "許可された");
                }else {
                    Log.d("Android","許可されなかった");
                    Toast.makeText(this,"アプリの起動に許可は必須です",Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
                break;
        }
    }

    private void getContentsInfo() {

        //画像の情報を取得する
        ContentResolver resolver = getContentResolver();
        cursor = resolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, //データの種類
                null, //項目(null=全項目)
                null, //フィルタ条件（null＝フィルタなし）
                null, //フィルタ用パラメータ
                null //ソート（null　ソートなし）
        );

        if (cursor.moveToFirst()) {
            int fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
            Long id = cursor.getLong(fieldIndex);
            Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

            ImageView imageView = (ImageView) findViewById(R.id.imageView);
            imageView.setImageURI(imageUri);
            //cursor.close();
        }
    }
}
