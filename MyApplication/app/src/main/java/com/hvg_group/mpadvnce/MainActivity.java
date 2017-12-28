package com.hvg_group.mpadvnce;

import android.Manifest;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.MediaController;

import com.hvg_group.mpadvnce.adapter.SongAdapter;
import com.hvg_group.mpadvnce.helper.Song;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity implements MediaController.MediaPlayerControl

{

    private MscCntrlr cntrlr;
    ArrayList<Song> songList = new ArrayList<>();
    PLService service;
    boolean musicbound = false;
    boolean paused = false, plybckPaused = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},101);

        ContentResolver musicResolver = getContentResolver();
        Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Uri musicUri2 = android.provider.MediaStore.Audio.Media.INTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);
        getsonglist(musicCursor);
        Cursor musicCursor2 = musicResolver.query(musicUri2, null, null, null, null);
        getsonglist(musicCursor2);
        for (int i = 0; i < songList.size(); i++)
            //Log.i("App", songList.get(i).getTitle());
            Log.i("App", songList.get(i).getArtist() + " " + songList.get(i).getTitle() + " " + songList.get(i).getId() + " " + songList.get(i).getDuration() + " " + songList.get(i).getIsmusic());
        Collections.sort(songList, new Comparator<Song>() {
            @Override
            public int compare(Song song, Song t1) {
                return song.getTitle().compareTo(t1.getTitle());
            }
        });
        ListView listView = (ListView)findViewById(R.id.listview);
        SongAdapter adapter = new SongAdapter(MainActivity.this,songList);
        listView.setAdapter(adapter);
        setCntrlr();
    }



    @Override
    protected void onStart() {
        super.onStart();

           Intent songIntent = new Intent(this,PLService.class);
        Log.i("App","In on Start");
            bindService(songIntent,connection, Context.BIND_AUTO_CREATE);
        Log.i("App","Going to bind service");
           //startService(songIntent);

    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            PLService.MusicBinder binder = (PLService.MusicBinder)iBinder;
            service = binder.getService();
            service.setSongArrayList(songList);
            Log.i("App","Successfully Bound to the Service");
            musicbound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.i("App","Not Bound to the Service");
            musicbound = false;
        }
    };

   public void SelectedSong(View view)
    {
        //if(service!=null) {
            Log.i("App", view.getTag().toString());
            service.setSong(Integer.parseInt(view.getTag().toString()));
            service.playIt();
            if(plybckPaused)
            {
                setCntrlr();
                plybckPaused = false;
            }

            cntrlr.show(0);
       // }
    }
    void getsonglist(Cursor musicCursor) {

        if (musicCursor != null && musicCursor.moveToFirst()) {
            //get columns
            int titleColumn = musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Media._ID);
            int artistColumn = musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Media.ARTIST);
            int durCol = musicCursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
            int ismsccol = musicCursor.getColumnIndex(MediaStore.Audio.Media.IS_MUSIC);
            //add songs to list
            do {
                long thisId = musicCursor.getLong(idColumn);
                String thisTitle = musicCursor.getString(titleColumn);
                String thisArtist = musicCursor.getString(artistColumn);
                int thisdurtn = musicCursor.getInt(durCol);
                int ismsc = musicCursor.getInt(ismsccol);
                if(ismsc!=0 && thisdurtn!=0)
                songList.add(new Song(thisId, thisTitle, thisArtist, thisdurtn, ismsc));
            }
            while (musicCursor.moveToNext());
        }
            /*while(musicCursor.moveToNext())
            {
                String contactData = "";
            for(int i=0;i<musicCursor.getColumnCount();i++)
                contactData += musicCursor.getColumnName(i) + ": " + musicCursor.getString(i) + " ";
                Log.i("App",contactData);
       }*/
    }

    @Override
    protected void onDestroy() {
       if(musicbound)
           unbindService(connection);
        service = null;
        super.onDestroy();
    }

    private void setCntrlr()
    {
        Log.i("App","Here at first");
        cntrlr = new MscCntrlr(this);
        cntrlr.setPrevNextListeners(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                playNext();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                playPrev();
            }
        });
        cntrlr.setMediaPlayer(this);
        cntrlr.setAnchorView(findViewById(R.id.listview));
        cntrlr.setEnabled(true);
     }

     private void playNext()
     {
         service.playNext();
         if(plybckPaused)
         {
             setCntrlr();
             plybckPaused=false;
         }
         cntrlr.show(0);
     }

     private void playPrev()
     {
         service.playPrev();
         if(plybckPaused)
         {
             setCntrlr();
             plybckPaused = false;
         }
         cntrlr.show(0);
     }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public void start()
    {

        service.Go();
    }

    @Override
    public void pause() {
       plybckPaused = true;
        service.Playerpse();
    }

    @Override
    public int getDuration() {
        if(service!=null && musicbound && service.isPng())
            return service.getSongdur();
        else
            return 0;
    }

    @Override
    public int getCurrentPosition() {
        if(service!=null && musicbound && service.isPng())
            return service.getSongpos();
        else
            return 0;
    }

    @Override
    public void seekTo(int i) {
        service.Seek(i);
    }

    @Override
    public boolean isPlaying() {
        if(service!=null && musicbound)
            return service.isPng();
        else
            return false;
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return  true;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(paused)
        {
            setCntrlr();
            paused = false;
        }
    }

    @Override
    protected void onStop() {
       cntrlr.hide();
       super.onStop();
    }
}