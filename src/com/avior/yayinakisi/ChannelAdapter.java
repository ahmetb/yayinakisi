package com.avior.yayinakisi;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.avior.yayinakisi.R;
import com.avior.yayinakisi.R.id;
import com.avior.yayinakisi.R.layout;
import com.avior.yayinakisi.model.Channel;
import com.avior.yayinakisi.model.Program;

class ChannelAdapter extends ArrayAdapter<Channel> {

        private List<Channel> items;
        private LayoutInflater vi;

        public ChannelAdapter(Context context, int textViewResourceId, List<Channel> items, LayoutInflater layoutInflater) {
                super(context, textViewResourceId, items);
                this.items = items;
                this.vi = layoutInflater;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
                View v = convertView;
                if (v == null) {
                    v = vi.inflate(R.layout.channel_row, null);
                }
                Channel c = items.get(position);
                if (c != null) {
                        TextView channelName = (TextView) v.findViewById(R.id.channel_name);
                        TextView channelCurrent = (TextView) v.findViewById(R.id.channel_current);
                        ImageView channelIcon = (ImageView) v.findViewById(R.id.channel_icon);
						if (channelName != null) 
							channelName.setText(c.getName());
						if (channelCurrent != null){ 
							Program current = c.getCurrentProgram();
							if(current != null){
								channelCurrent.setText(current.getName() +" ("+current.getTime()+")");
							}
						}
						if(channelIcon != null){
							channelIcon.setImageResource(ChannelImageProvider.getChannelImageResource(c));
						}
                }
                return v;
        }
}