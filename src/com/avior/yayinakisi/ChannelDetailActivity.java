package com.avior.yayinakisi;

import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.avior.yayinakisi.ChannelImageProvider;
import com.avior.yayinakisi.ProgramAdapter;
import com.avior.yayinakisi.R;
import com.avior.yayinakisi.model.Channel;
import com.avior.yayinakisi.model.Program;

public class ChannelDetailActivity extends Activity {
	private static final String APP_TAG = "YayinAkisiApp";
	private ListView programList;
	private List<Program> programs;
	private ProgramAdapter plAdapter;
	
	private final int ABOUT_ITEM = Menu.FIRST+1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Log.i(APP_TAG, "Kanal gorunumune gecildi.");

		setContentView(R.layout.channel);
		
		Channel current = MainActivity.chosen;
		if(current == null){
			Log.e(APP_TAG, "Secili kanala ulasilamadi.");
		} else {
			Log.d(APP_TAG, "Secili kanal: " + current);
			
			ImageView detailLogo = (ImageView) findViewById(R.id.detail_icon);
			detailLogo.setImageResource(ChannelImageProvider.getChannelImageResource(current));

			TextView chName = (TextView) findViewById(R.id.channelName);
			if(chName != null)
				chName.setText(current.getName());
			
			programs = current.getSchedule(); 
			if (programs.size() > 0) {
				plAdapter = new ProgramAdapter(
						this,
						R.layout.program_row,
						programs,
						(LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE));
			}
		}
		
		this.programList = (ListView) findViewById(R.id.programList);
		this.programList.setAdapter(plAdapter);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuItem aboutItem = menu.add(0, ABOUT_ITEM, Menu.NONE, getString(R.string.about));
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case ABOUT_ITEM:
			performAbout(getApplicationContext());
			break;
		default:
			// unhandled.
			break;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	private void performAbout(Context applicationContext) {
		Dialog dialog = new Dialog(this);
		dialog.setContentView(R.layout.about_layout);
		dialog.setTitle(R.string.about);
		TextView authorText = (TextView) dialog.findViewById(R.id.author);
		authorText.setText(getString(R.string.author)+"\n"+getString(R.string.author_mail)+"\n"+getString(R.string.author_site));
		ImageView image = (ImageView) dialog.findViewById(R.id.image);
		image.setImageResource(R.drawable.icon);
		dialog.show();
	}
}