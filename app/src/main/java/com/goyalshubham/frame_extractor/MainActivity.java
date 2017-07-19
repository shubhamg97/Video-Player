/*
 * Created by Shubham Goyal on 5/30/17 11:06 AM
 * Copyright (c) 2017. All rights reserved.
 *
 * File last modified on 7/14/17 10:53 AM
 */

package com.goyalshubham.frame_extractor;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.goyalshubham.frame_extractor.R.id.button1;

public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    Button buttonCapture1;
    Handler handler;
    ImageView dominantColor;
    MediaController myMediaController;
    MediaMetadataRetriever mmr;
    Runnable runningCode;
    Snackbar snackbar;
    String viewSource;
    VideoView myVideoView;

    // Global variables for all types of color for the color palette
    int vibrantColor;
    int lightVibrantColor;
    int darkVibrantColor;
    int mutedColor;
    int lightMutedColor;
    int darkMutedColor;

    private GridView grid;
    private static final int EXTERNAL_STORAGE_PERMISSION_CONSTANT = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mmr = new MediaMetadataRetriever();
        myMediaController = new MediaController(this);

        buttonCapture1 = (Button) findViewById(button1);
        dominantColor = (ImageView) findViewById(R.id.dominantColor);
        grid = (GridView) findViewById(R.id.gridView);
        myVideoView = (VideoView) findViewById(R.id.videoView);

        buttonCapture1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // Opens a pop up to request permission to read external storage for Android M & up if not already granted
                if (ActivityCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_PERMISSION_CONSTANT);
                    } else {
                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_PERMISSION_CONSTANT);
                    }
                } else {
                    // Takes the text inputted in the screen and sets it as the file source
                    EditText uriInput = (EditText) findViewById(R.id.fileName);
                    String userInput = uriInput.getText().toString();
                    viewSource = Environment.getExternalStorageDirectory() + "/" + Environment.DIRECTORY_DOWNLOADS
                            + "/" + userInput/* + ".mp4"*/;

                    // Minimizes the soft keyboard when the "Enter" button for URI is pressed
                    InputMethodManager inputMgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMgr.hideSoftInputFromWindow(uriInput.getWindowToken(), 0);

                    // Sets the source for the instance of the MediaMetaDataRetriever
                    mmr.setDataSource(viewSource);

                    myVideoView.setVideoURI(Uri.parse(viewSource));
                    myVideoView.setMediaController(myMediaController); //Displays the media controller
                    myVideoView.setOnCompletionListener(myVideoViewCompletionListener);
                    myVideoView.setOnErrorListener(myVideoViewErrorListener);
                    myVideoView.requestFocus();
                    myVideoView.start(); //Starts it immediately on entering filename

                    handler = new Handler();
                    runningCode = new Runnable() {
                        @Override
                        public void run() {
                            int currentPosition = myVideoView.getCurrentPosition();
                            Bitmap bmFrame = mmr.getFrameAtTime(currentPosition * 1000,
                                    MediaMetadataRetriever.OPTION_PREVIOUS_SYNC); //units in microsecond
                            if (bmFrame != null) {
                                extractColor(bmFrame);
                            }
                            // Runs the above process every half a second
                            handler.postDelayed(runningCode, 500);
                        }
                    };
                    // Uses the handler created above to periodically call doTask to refresh colors
                    handler.post(runningCode);
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case EXTERNAL_STORAGE_PERMISSION_CONSTANT: {
                // If the array returned says that permission wasn't granted, then display snackbar claiming problem.
                if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    snackbar = Snackbar.make(findViewById(R.id.relativeLayout),
                            "Unfortunately, this app won't work without the storage permission.",
                            Snackbar.LENGTH_INDEFINITE).setAction("DISMISS", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            snackbar.dismiss(); // Dismisses the snackbar on tapping "DISMISS"
                        }
                    });
                    snackbar.show(); // Displays the snackbar
                }
            }
        }
    }

    private void extractColor(final Bitmap bmp) {
        // Retrieves Palette Colors here
        Palette.from(bmp).generate(new Palette.PaletteAsyncListener() {
            public void onGenerated(Palette palette) {
                int defaultVal = 0xFFFFFF;
                dominantColor.setBackgroundColor(getDominantColor(bmp));
                vibrantColor = palette.getVibrantColor(defaultVal);
                lightVibrantColor = palette.getLightVibrantColor(defaultVal);
                darkVibrantColor = palette.getDarkVibrantColor(defaultVal);
                mutedColor = palette.getMutedColor(defaultVal);
                lightMutedColor = palette.getLightMutedColor(defaultVal);
                darkMutedColor = palette.getDarkMutedColor(defaultVal);
            }
        });

        // Creates arrays of color type and their variables to populate GridView
        String name[] = {"Vibrant", "Light Vibrant", "Dark Vibrant", "Muted", "Light Muted", "Dark Muted"};
        int val[] = {vibrantColor, lightVibrantColor, darkVibrantColor, mutedColor, lightMutedColor, darkMutedColor};

        // Sets up an instance of GridAdapter to send the above arrays to the class to be populated
        GridAdapter adapter = new GridAdapter(MainActivity.this, name, val);
        grid.setAdapter(adapter);
    }

    // Retrieves the actual dominant color in the frame
    public static int getDominantColor(Bitmap bitmap) {
        List<Palette.Swatch> swatchesTemp = Palette.from(bitmap).generate().getSwatches();
        List<Palette.Swatch> swatches = new ArrayList<Palette.Swatch>(swatchesTemp);
        Collections.sort(swatches, new Comparator<Palette.Swatch>() {
            @Override
            public int compare(Palette.Swatch swatch1, Palette.Swatch swatch2) {
                return swatch2.getPopulation() - swatch1.getPopulation();
            }
        });
        return swatches.size() > 0 ? swatches.get(0).getRgb() : 0xfff;
    }

    MediaPlayer.OnCompletionListener myVideoViewCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer arg0) {
            Snackbar.make(findViewById(R.id.relativeLayout), "End of Video", Snackbar.LENGTH_LONG).show();
        }
    };

    MediaPlayer.OnErrorListener myVideoViewErrorListener = new MediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            Snackbar.make(findViewById(R.id.relativeLayout), "ERROR!!!", Snackbar.LENGTH_LONG).show();
            return true;
        }
    };

    @Override
    protected void onPause() {
        myVideoView.pause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        myVideoView.stopPlayback();
        mmr.release();
    }
}