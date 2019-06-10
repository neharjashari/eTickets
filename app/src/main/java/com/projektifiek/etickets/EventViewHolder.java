package com.projektifiek.etickets;

import android.view.View;
import android.widget.TextView;

public class EventViewHolder {

    TextView tvTitle, tvAuthor, tvDateCreated, tvContent, tvPrice;

    public EventViewHolder(View view)
    {
        tvTitle = view.findViewById(R.id.tvTitle);
        tvAuthor = view.findViewById(R.id.tvAuthor);
        tvDateCreated = view.findViewById(R.id.tvDateCreated);
//        tvContent = view.findViewById(R.id.tvContent);
        tvPrice = view.findViewById(R.id.tvPrice);
    }

    public TextView getTvTitle() {
        return tvTitle;
    }

    public TextView getTvAuthor() {
        return tvAuthor;
    }

    public TextView getTvDateCreated() {
        return tvDateCreated;
    }

    public TextView getTvContent() {
        return tvContent;
    }

    public TextView getTvPrice() {
        return tvPrice;
    }
}
