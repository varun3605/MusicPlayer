package com.hvg_group.mpadvnce.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hvg_group.mpadvnce.R;
import com.hvg_group.mpadvnce.helper.Song;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by Varun on 28-10-2017.
 */

public class SongAdapter extends ArrayAdapter<Song>
{

    String finalTimerString;
    String secondsString;
    TextView txtsngnme;
    TextView txtartstnme;
    TextView duratn;

    public SongAdapter(Context context, ArrayList<Song> songs)
    {
        super(context,0,songs);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
    {
        View view = convertView;
        if(view==null)
        {
            view = LayoutInflater.from(getContext()).inflate(R.layout.list_sngcstm,parent,false);
        }

        Song song = getItem(position);

        txtsngnme = view.findViewById(R.id.txtvw_sngnme);
        txtartstnme = view.findViewById(R.id.txtvw_artist);
        duratn = view.findViewById(R.id.txtvw_drn);

        txtsngnme.setText(song.getTitle());
        txtartstnme.setText(song.getArtist());
        int duration = song.getDuration();
        int hours =  (duration / (1000 * 60 * 60));
        int minutes =  (duration % (1000 * 60 * 60)) / (1000 * 60);
        int seconds =  ((duration % (1000 * 60 * 60)) % (1000 * 60) / 1000);
        // Add hours if there
        if (hours > 0) {
            finalTimerString = hours + ":";
        }
        if (seconds < 10) {
            secondsString = "0" + seconds;
        } else {
            secondsString = "" + seconds;
        }

        finalTimerString = minutes + ":" + secondsString;
        duratn.setText(finalTimerString);

        return view;
    }
}
