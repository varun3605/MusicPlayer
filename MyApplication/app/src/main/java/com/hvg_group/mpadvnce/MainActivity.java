package com.hvg_group.mpadvnce;

import android.Manifest;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.hvg_group.mpadvnce.adapter.SongAdapter;
import com.hvg_group.mpadvnce.helper.Song;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
{

    ArrayList<Song> songList = new ArrayList<>();

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
            //Log.i("App",songList.get(i).getTitle());
            Log.i("App", songList.get(i).getArtist() + " " + songList.get(i).getTitle() + " " + songList.get(i).getId() + " " + songList.get(i).getDuration() + " " + songList.get(i).getIsmusic());
        ListView listView = (ListView)findViewById(R.id.listview);
        SongAdapter adapter = new SongAdapter(MainActivity.this,songList);
        listView.setAdapter(adapter);
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
                if(ismsc!=0)
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

}