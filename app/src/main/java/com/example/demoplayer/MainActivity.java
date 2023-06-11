package com.example.demoplayer;

import static com.example.demoplayer.R.drawable.baseline_pause_24;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import android.Manifest;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 1;
    private List<String> musicFiles;
    private int currentSongIndex = 0;
    private TextView txtSongTitle;
    FloatingActionButton btnPlayMusic,btnNextMusic,btnPreviousMusic;
    private MediaPlayer mediaPlayer;
    int counter=0;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnPlayMusic = findViewById(R.id.btn_play);
        btnNextMusic = findViewById(R.id.btn_next);
        btnPreviousMusic = findViewById(R.id.btn_previous);
        txtSongTitle = findViewById(R.id.txt_song_title);
        btnPlayMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                counter= counter+1;
                if (counter==1){
                    btnPlayMusic.setImageResource(R.drawable.baseline_play_arrow_24);
                    play();

                }
                if (counter==2){
                    btnPlayMusic.setImageResource(baseline_pause_24);
                    pause();
                    counter=0;
                }


            }
        });

        btnNextMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playNext();
            }
        });

        btnPreviousMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playPreviousSong();
            }
        });

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        } else {
            loadMusic();
        }

    }

    private void loadMusic() {
        musicFiles = new ArrayList<>();

        String[] projection = {MediaStore.Audio.Media.DATA};
        String selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0";
        Cursor cursor = getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                null,
                null
        );

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String filePath = cursor.getString(0);
                musicFiles.add(filePath);
            }
            cursor.close();
        }
    }



    private void play() {
        if (musicFiles.isEmpty()) {
            Toast.makeText(this, "No music files found", Toast.LENGTH_SHORT).show();
            return;
        }

        String currentSong = musicFiles.get(currentSongIndex);
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioAttributes(new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build());

        try {
            mediaPlayer.setDataSource(currentSong);
            mediaPlayer.prepare();
            mediaPlayer.start();
            txtSongTitle.setText("Now Playing: " + currentSong);
        } catch (IOException e) {
            Toast.makeText(this, "Failed to play the song", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
    private void pause() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            Toast.makeText(this, "Song Paused", Toast.LENGTH_SHORT).show();
        }
    }
    private void playNext() {
        if (musicFiles.isEmpty()) {
            Toast.makeText(this, "No music files found", Toast.LENGTH_SHORT).show();
            return;
        }

        currentSongIndex++;
        if (currentSongIndex >= musicFiles.size()) {
            currentSongIndex = 0; // Wrap around to the first song
        }
        if (counter==1) {
            play();
        }
        if (counter==2){
            pause();
        }
    }

    private void playPreviousSong() {
        if (musicFiles.isEmpty()) {
            Toast.makeText(this, "No music files found", Toast.LENGTH_SHORT).show();
            return;
        }

        currentSongIndex--;
        if (currentSongIndex < 0) {
            currentSongIndex = musicFiles.size() - 1; // Wrap around to the last song
        }

        if (counter==1) {
            play();
        }
        if (counter==2){
            pause();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadMusic();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

}