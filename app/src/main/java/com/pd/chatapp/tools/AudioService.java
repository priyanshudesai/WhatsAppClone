package com.pd.chatapp.tools;

import android.content.Context;
import android.media.MediaPlayer;

import java.io.IOException;

public class AudioService {
    private Context context;
    private MediaPlayer tmpMediaPlayer;
    private boolean b=false;
    private String a="";

    public AudioService(Context context) {
        this.context = context;

    }

    public void playAudioFromUrl(String url, final OnPlayCallBack onPlayCallBack){
        if (tmpMediaPlayer!=null && b){
            tmpMediaPlayer.pause();
            b=false;
            a="pause";
        }else if(!b && a.equals("pause")){
            tmpMediaPlayer.start();
            a="";
        }
        else {

            MediaPlayer mediaPlayer = new MediaPlayer();
            try {

                mediaPlayer.setDataSource(url);
                mediaPlayer.prepare();
                mediaPlayer.start();

                tmpMediaPlayer = mediaPlayer;
                b=true;
            } catch (IOException e) {
                e.printStackTrace();
            }

            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    onPlayCallBack.onFinished();
                    b=false;
                    a="";
                }
            });
        }
    }

    public interface OnPlayCallBack{
        void onFinished();
    }
}
