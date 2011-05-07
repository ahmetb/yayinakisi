package com.avior.yayinakisi;

import java.util.List;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.avior.yayinakisi.R;
import com.avior.yayinakisi.R.id;
import com.avior.yayinakisi.R.layout;
import com.avior.yayinakisi.model.Channel;
import com.avior.yayinakisi.model.Program;

class ProgramAdapter extends ArrayAdapter<Program> {

        private List<Program> items;
        private LayoutInflater vi;

        public ProgramAdapter(Context context, int textViewResourceId, List<Program> items, LayoutInflater layoutInflater) {
                super(context, textViewResourceId, items);
                this.items = items;
                this.vi = layoutInflater;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
                View v = convertView;
                if (v == null) {
                    v = vi.inflate(R.layout.program_row, null);
                }
                Program p = items.get(position);
                if (p != null) {
                        TextView programName = (TextView) v.findViewById(R.id.program_name);
                        TextView programTime = (TextView) v.findViewById(R.id.program_time);
                        TextView programCategory = (TextView) v.findViewById(R.id.program_category);
						if (programName != null) 
							programName.setText(p.getName());
						if (programCategory != null){
							String cat = p.getCategory();
							if(cat == null || cat.length()==0)
								programCategory.setVisibility(View.INVISIBLE);
							else{
								programCategory.setText(p.getCategory());
								programCategory.setVisibility(View.VISIBLE);
							}
								
						}
						if (programTime != null){
							programTime.setText(p.getTime());
						}
                }
                return v;
        }
}