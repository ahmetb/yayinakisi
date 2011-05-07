package com.avior.yayinakisi;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.avior.yayinakisi.R;
import com.avior.yayinakisi.model.Channel;
import com.avior.yayinakisi.model.Program;
import com.avior.yayinakisi.util.UrlOpener;

public class MainActivity extends Activity implements OnItemClickListener {
	private static final String APP_TAG = "YayinAkisiApp";
	
	private static final String LOCAL_FILE = "akis.json";

	private static final long ALLOWED_DIFF_MINS = 6*60;
	
	private static ListView channelList;
	
	public static Channel chosen;
	
	public static List<Channel> channels;
	private static ProgressDialog loadingDialog;
	private static ChannelAdapter clAdapter;
	private Runnable loadChannels;
	
	public static MainActivity firstThread;
	
	private final int EXIT_APP = Menu.FIRST+1;
	private final int ABOUT_ITEM = Menu.FIRST+2;
	private final int REFRESH_ITEM = Menu.FIRST+3;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(APP_TAG, "Activity starting.");
		setContentView(R.layout.main);
		super.onCreate(savedInstanceState);
		channelList = (ListView) findViewById(R.id.channelList);
		
		firstThread = this;
		
		channels = new ArrayList<Channel>();
		clAdapter = new ChannelAdapter(this, R.layout.channel_row, channels, (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE));
		
		channelList.setAdapter(clAdapter);
		channelList.setOnItemClickListener(firstThread);
		
		refresh(); // exec app
	}
	
	private void refresh(){
		if(!isOfflineValid() && !isConnected()){
			Log.w(APP_TAG, "Baglanti yok.");
			new AlertDialog.Builder(this).setTitle(R.string.error)
					.setMessage(R.string.connection_error).setPositiveButton(R.string.exit, new OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							finish();
						}
					}).show();
		} else {
			final boolean loadOffline = isOfflineValid();
			
			loadChannels = new Runnable() {
				@Override
				public void run() {
					if (!loadOffline) channels = retrieveChannelsFromSource();
					else channels = retrieveChannelsOffline();
					
					if(loadOffline && (channels == null || channels.isEmpty())){
						// local not working.
						channels = retrieveChannelsFromSource();
					}
					
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							clAdapter.clear();
							if(channels != null && channels.size()>0){
								clAdapter.notifyDataSetChanged();
								for(Channel t: channels){
									clAdapter.add(t);
								}
								Log.d(APP_TAG, "Listede "+channels.size()+" eleman var. Guncellendi.");
							}
							loadingDialog.dismiss();
							clAdapter.notifyDataSetChanged();
						}
					});
				}
			};
			
			if (!loadOffline) Log.d(APP_TAG, "Retrieving from internet.");
			else Log.d(APP_TAG, "Retrieving from file.");
			
			Thread thread = new Thread(null, loadChannels, "downloadSchedule");
			thread.start();
			
			loadingDialog = new ProgressDialog(this);
			loadingDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			loadingDialog.setTitle(getString(R.string.loading));
			loadingDialog.setMessage(getString(R.string.please_wait));
			loadingDialog.setCancelable(false);
			loadingDialog.show();
		}
	}
	
	private boolean isConnected() {
		ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = connManager.getActiveNetworkInfo();
		if (info == null) return false;
		else return info.isConnectedOrConnecting();	
	}
	
	public void deleteOfflineFile(){
		File f = getApplicationContext().getFileStreamPath(LOCAL_FILE);
		if (f.exists() && f.isFile()) f.delete();
		Log.d(APP_TAG, "Offline file deleted.");
	}
	
	public boolean isOfflineValid(){
		Log.d(APP_TAG, "Is local file valid?");
		File f = getApplicationContext().getFileStreamPath(LOCAL_FILE);
		
		if (!f.exists()){
			Log.d(APP_TAG, " Local file does not exist.");
			return false;
		}
		
		if (f.length() < 10){
			Log.d(APP_TAG, " Local file is empty, deleting.");
			f.delete();
			return false;
		}
		
		long diffMins = (System.currentTimeMillis()-f.lastModified())/(1000*60);
		Log.d(APP_TAG, "Modification time of local file: "+new Date(f.lastModified()));
		Log.d(APP_TAG, "Now time is: "+new Date(System.currentTimeMillis()));

		Log.d(APP_TAG, "Difference minutes: "+diffMins);
		
		// check whether recent 12 hours (abs h1-h2
		if(diffMins > ALLOWED_DIFF_MINS){
			Log.d(APP_TAG, " 12 hours are expired.");
			return false;
		} else {
			Log.d(APP_TAG, " Offline file is OK.");
			return true;
		}
	}
	
	private List<Channel> retrieveChannelsOffline() {
		try {
			FileInputStream fIn = openFileInput(LOCAL_FILE);
			InputStreamReader isr = new InputStreamReader(fIn);
			StringBuffer sb = new StringBuffer();
			char c = (char) isr.read();
			
			while(c>0 && c < 65535){
				sb.append(c);
				c = (char) isr.read();
			}
			
			String fileContents = sb.toString();
			return parseChannelsFromContent(fileContents);
		} catch (IOException e) {
			return null;
		}
	}
	
	

	private List<Channel> retrieveChannelsFromSource() {
		URL u = null;
		try {
			u = new URL(this.getString(R.string.daily_json_url));
		} catch (MalformedURLException e) {
			// rare case.
		}
		String pageContents = UrlOpener.getContents(u);
		
		// write downloaded content to the local file
		try {
			FileOutputStream fo = openFileOutput(LOCAL_FILE, MODE_WORLD_WRITEABLE);
			OutputStreamWriter osw = new OutputStreamWriter(fo);
			
			if (osw != null){
				osw.write(pageContents);
				osw.flush();
				osw.close();
			} else {
				throw new IOException("Could not initialize OutputStreamWriter.");
			}
		} catch(IOException e){
			Log.e(APP_TAG, "Could not write downloaded contents to the local file!");
			Log.e(APP_TAG, e.toString());
		}
		
		if(loadingDialog != null){
			loadingDialog.cancel();
		}
		
		return parseChannelsFromContent(pageContents);
	}

	private List<Channel> parseChannelsFromContent(String pageContents) {
		List<Channel> retrieved = new ArrayList<Channel>();

		if (pageContents != null) {
			Log
					.d(APP_TAG, pageContents.length()
							+ " karakter sayfa getirildi.");
			try {
				JSONObject jsonMain = (JSONObject) new JSONTokener(pageContents)
						.nextValue();
				JSONArray jsonChs = jsonMain.getJSONArray("channels");

				for (int ci = 0; ci < jsonChs.length(); ci++) {
					JSONObject jsonCh = jsonChs.getJSONObject(ci);
					String channelName = jsonCh.getString("name");
					Integer channelCode = jsonCh.getInt("code");
					Channel c = new Channel(channelName, channelCode);

					JSONArray jsonPrgs = null;
					try {
						jsonPrgs = jsonCh.getJSONArray("programs");

						for (int pi = 0; pi < jsonPrgs.length(); pi++) {
							JSONObject jsonPrg = jsonPrgs.getJSONObject(pi);
							String programName = jsonPrg.getString("name");
							String programTime = jsonPrg.getString("time");
							
							String programCategory = "";
							try{
								programCategory = jsonPrg.getString("category");
							} catch(JSONException e){}
							Program p = new Program(programTime, programName, programCategory);
							c.getSchedule().add(p);
						}
					} catch (JSONException e) {
						Log.w(APP_TAG,
								"Kanal icin Program listesine erisilemedi. Kanal: "
										+ c.getName());
					}
					retrieved.add(c);
				}
			} catch (JSONException e) {
				Log.e(APP_TAG, "JSON hatasi olustu: " + e.getMessage() + " -> "
						+ e.getClass().getName());
				for (StackTraceElement t : e.getStackTrace())
					Log.e(APP_TAG, t.getClassName() + "." + t.getMethodName()
							+ " @ " + t.getLineNumber());
			}
		} else {
			Log.e(APP_TAG, "Kaynaktan icerik alinamadi (bos icerik).");
			Toast.makeText(firstThread, R.string.connection_error, Toast.LENGTH_SHORT)
					.show();
		}
		return retrieved;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int index, long arg3) {
		Channel clicked = channels.get(index);
		chosen = clicked;

		Log.d(APP_TAG, clicked.getName() + " kanalina tiklandi. ("+clicked.getSchedule().size()+" program)");
		Intent i = new Intent(getApplicationContext(),
				ChannelDetailActivity.class);
		startActivity(i);
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuItem refreshItem = menu.add(0, REFRESH_ITEM, Menu.NONE, getString(R.string.refresh));
		MenuItem aboutItem = menu.add(0, ABOUT_ITEM, Menu.NONE, getString(R.string.about));
		MenuItem exitItem = menu.add(0, EXIT_APP, Menu.NONE, getString(R.string.exit));
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {

		switch (item.getItemId()) {
		case EXIT_APP:
			finish();
			break;
		case ABOUT_ITEM:
			performAbout(getApplicationContext());
			break;
		case REFRESH_ITEM:
			deleteOfflineFile();
			refresh();
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