package com.projektifiek.etickets;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

public class EventAdapter extends BaseAdapter {

    Context c;
    public List<Event> data = new ArrayList<>();

    public EventAdapter(Context _c)
    {
        this.c = _c;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return data.get(i).getId();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        EventViewHolder viewHolder;
        if(view==null)
        {
            view = LayoutInflater.from(c)
                    .inflate(R.layout.event_row_layout,
                            viewGroup,false);
            viewHolder = new EventViewHolder(view);
            view.setTag(viewHolder);
        }
        else
        {
            viewHolder = (EventViewHolder) view.getTag();
        }

        viewHolder.getTvTitle().setText(data.get(i).getTitle());
        viewHolder.getTvAuthor().setText("Author: " + data.get(i).getAuthor());
        viewHolder.getTvDateCreated().setText("Date Created: " + data.get(i).getDateCreated());
//        viewHolder.getTvContent().setText(data.get(i).getContent());
//        viewHolder.getTvPhotoUrl().setText(data.get(i).getPhotoURL());

        Animation anim = AnimationUtils
                .loadAnimation(c,android.R.anim.slide_in_left);
        viewHolder.getTvTitle().setAnimation(anim);
        return view;
    }

}
