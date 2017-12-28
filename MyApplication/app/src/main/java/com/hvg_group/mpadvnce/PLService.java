package com.hvg_group.mpadvnce;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentUris;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.util.Log;

import com.hvg_group.mpadvnce.helper.Song;

import java.io.IOException;
import java.util.ArrayList;

public class PLService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener{

    private String songTitle = "";
    private static final int NOTIFY_ID = 1;
    MediaPlayer player;
    ArrayList<Song> songArrayList;
   private int Songpos;
    private final IBinder mscBind = new MusicBinder();

    public class MusicBinder extends Binder
    {
        PLService getService()
        {
            return PLService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i("App","Entered in the Service");
        return mscBind;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        Songpos = 0;
        player = new MediaPlayer();
        MpInit();
        Log.i("App","Inside Oncreate");
    }

    public void MpInit()
    {
        Log.i("App","Initializing Player");
        player.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.setOnPreparedListener(this);
        player.setOnErrorListener(this);
        player.setOnCompletionListener(this);
    }

    void setSongArrayList(ArrayList<Song> songArrayList1)
    {
        songArrayList = songArrayList1;
    }


    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {

        if(player.getCurrentPosition()>0)
        {
            mediaPlayer.reset();
            playNext();
        }
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        mediaPlayer.reset();
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer)
    {
        mediaPlayer.start();
        Intent intentntfctn = new Intent(this, MainActivity.class);
        intentntfctn.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,intentntfctn,PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_play_arrow_black_24dp)
                .setTicker(songTitle)
                .setOngoing(true)
                .setContentTitle("Playing")
                .setContentText(songTitle);
        Notification notification = builder.build();

        startForeground(NOTIFY_ID,notification);

    }

    @Override
    public boolean onUnbind(Intent intent) {
        player.stop();
        player.release();
        return false;
    }

    public void setSong(int songindex)
    {
        Log.i("App","Songpos" + songindex);
        Songpos = songindex;
    }
    public void playIt()
    {
        player.reset();
        Song CurrSong = songArrayList.get(Songpos);
        songTitle = CurrSong.getTitle();
        long ID = CurrSong.getId();
        Uri trackUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,ID);
        try
        {
            player.setDataSource(getApplicationContext(),trackUri);
        }
        catch (Exception e)
        {
            Log.i("App","Error setting Data Source");
        }
        try
        {
            player.prepareAsync();
        }
        catch (Exception e)
        {
            Log.i("App","Exception in PLService playIt method");
        }
    }

    public int getSongpos()
    {
        return player.getCurrentPosition();
    }

    public int getSongdur()
    {
        //Log.i("App","" + player.getDuration());
        return player.getDuration();
    }

    public boolean isPng()
    {
        return player.isPlaying();
    }

    public void playPrev()
    {
        Songpos--;
        if(Songpos<0)
            Songpos=songArrayList.size()-1;
        playIt();
    }

    public void playNext()
    {
        Songpos++;
        if(Songpos>=songArrayList.size())
            Songpos=0;
        playIt();
    }
    public void Playerpse()
    {
        player.pause();
    }

    public void Seek(int posn)
    {
        player.seekTo(posn);
    }

    public void Go()
    {
        player.start();
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
    }
}
