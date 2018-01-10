package com.harish.eat;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.util.Date;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class result extends AppCompatActivity {

    MediaPlayer player;
    AssetFileDescriptor afd;

    private boolean mIsBound = false;
    private boolean misPlaying = true;
    private MusicService mServ;
    private ServiceConnection Scon = new ServiceConnection() {

        public void onServiceConnected(ComponentName name, IBinder
                binder) {
            mServ = ((MusicService.ServiceBinder) binder).getService();
        }

        public void onServiceDisconnected(ComponentName name) {
            mServ = null;
        }
    };


    void doBindService() {
        bindService(new Intent(this, MusicService.class),
                Scon, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    void doUnbindService() {
        if (mIsBound) {
            unbindService(Scon);
            mIsBound = false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        doBindService();
        Intent music = new Intent();
        music.setClass(this, MusicService.class);
        startService(music);

        Typeface pacmanTypeface = Typeface.createFromAsset(getAssets(), "fonts/pac-font.ttf");
        Typeface arcadeTypeface = Typeface.createFromAsset(getAssets(), "fonts/arcade.ttf");

        TextView gameOverTV = (TextView) findViewById(R.id.gameover);
        gameOverTV.setTypeface(pacmanTypeface);

        TextView scoreTV = (TextView) findViewById(R.id.scorelabel);
        scoreTV.setTypeface(arcadeTypeface);

        TextView highScoreTV = (TextView) findViewById(R.id.highScoreLabel);
        highScoreTV.setTypeface(arcadeTypeface);

        Button tryAgainButton = (Button) findViewById(R.id.tryAgain);
        tryAgainButton.setTypeface(pacmanTypeface);

        TextView scorelabel = (TextView) findViewById(R.id.scorelabel);
        TextView highScoreLabel = (TextView) findViewById(R.id.highScoreLabel);

        ImageView imageView = (ImageView) findViewById(R.id.share_icon);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(result.this, "Capturing Screenshot !", Toast.LENGTH_SHORT).show();
                Bitmap bitmap = takeScreenshot();
                saveBitmap(bitmap);
                shareIt();

            }
        });





        int score = getIntent().getIntExtra("SCORE", 0);
        scorelabel.setText(score + "");

        SharedPreferences settings = getSharedPreferences("GAME_DATA", Context.MODE_PRIVATE);
        int highScore = settings.getInt("HIGH_SCORE", 0);

        if (score > highScore) {
            highScoreLabel.setText("High Score : " + score);

            SharedPreferences.Editor editor = settings.edit();
            editor.putInt("HIGH_SCORE", score);
            editor.commit();
        } else {
            highScoreLabel.setText("High Score : " + highScore);
        }
    }

    public void tryAgain(View view) {
        MusicService mServ = new MusicService();

        Intent music = new Intent();
        music.setClass(this, MusicService.class);
        stopService(music);

        mServ.onDestroy();
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }

    public void onBackPressed() {
        //  super.onBackPressed();
        moveTaskToBack(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (misPlaying) {
            mServ.pauseMusic();
            misPlaying = false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!misPlaying) {
            mServ.resumeMusic();
            misPlaying = true;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MusicService mServ = new MusicService();

        Intent music = new Intent();
        music.setClass(this, MusicService.class);
        stopService(music);

        mServ.onDestroy();
    }
    public Bitmap takeScreenshot() {
        View rootView = findViewById(android.R.id.content).getRootView();
        rootView.setDrawingCacheEnabled(true);
        return rootView.getDrawingCache();
    }
    File imagePath = new File(Environment.getExternalStorageDirectory() + "/scrnshot.png"); ////File imagePath
    private void saveBitmap(Bitmap bitmap) {

        FileOutputStream fos;
        try {
            fos = new FileOutputStream(imagePath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            Log.e("GREC", e.getMessage(), e);
        } catch (IOException e) {
            Log.e("GREC", e.getMessage(), e);
        }
    }

    private void shareIt() {
        Uri uri = Uri.fromFile(imagePath);
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("image/*");
        String shareBody = "My highest score in Bounce with screen shot";
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "My Highscore");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        sharingIntent.putExtra(Intent.EXTRA_STREAM, uri);

        startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }

}
