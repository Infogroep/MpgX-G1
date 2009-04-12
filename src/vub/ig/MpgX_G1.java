package vub.ig;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jvyaml.YAML;

import vub.ig.MpgX.Buttons;
import vub.ig.MpgX.SongListAdapter;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView; 
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

public class MpgX_G1 extends Activity {

	public static final String MpgX_Server = "http://mpgx.rave.org:6668";
	public static final int MpgX_REFRESH_SECONDS = 30;
	public static final int MpgX_NR_SONGS = 30;
	public static final int INTERNET_DELAY = 2000;

	public static final int MpgX_LISTVIEW_SHOW_LOADING = 31337;
	public static final int MpgX_LISTVIEW_HIDE_LOADING = 31338;
	public static final int MpgX_LISTVIEW_NOTIFY = 31339;
	public static final int MpgX_LISTVIEW_REFRESH = 31340;
	private static final int MpgX_LISTVIEW_CONTEXT_DEQUEUE = 31341;
	private static final int MpgX_LISTVIEW_CONTEXT_MAGICSHUFFLE = 31342;
	private static final int MpgX_LISTVIEW_EMPTY = 31343;
	private static final int MpgX_LISTVIEW_NO_INTERNET = 31344;

	public static String previousVolume = "0";
	public static Handler myG1ViewUpdateHandler;

	private ListView MpgX_ListView;
	private TextView MpgX_TextSentinel;
	private static SongListAdapter MpgX_SongListAdapter;
	private static Boolean MpgX_PlayListExtraUpdated = false;

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.main);

		// ========
		//   Message Handler
		// ========
		myG1ViewUpdateHandler = new Handler(){
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case MpgX_G1.MpgX_LISTVIEW_HIDE_LOADING:
					MpgX_TextSentinel.setText("");
					break;
				case MpgX_G1.MpgX_LISTVIEW_SHOW_LOADING:
					MpgX_TextSentinel.setText("Loading..");
					break;
				case MpgX_LISTVIEW_NOTIFY:
					MpgX_SongListAdapter.notifyDataSetChanged();
					break;
				case MpgX_LISTVIEW_REFRESH:
					try { Thread.sleep(INTERNET_DELAY);
					} catch (InterruptedException e) { }
					MpgX_RefreshPlayList(true);
					break;
				case MpgX_LISTVIEW_EMPTY:
					MpgX_TextSentinel.setText("Empty playlist");
					break;		
				case MpgX_LISTVIEW_NO_INTERNET:
					MpgX_TextSentinel.setText("Connection failed");
					break;							
				}
				super.handleMessage(msg);
			}
		};
		// ========
		//   MPGX
		// ========
		ImageButton tmpImgButton;
		MpgX_ListView = (ListView)this.findViewById(R.id.ListViewSongs);
		MpgX_TextSentinel = new TextView(this);
		tmpImgButton = (ImageButton)this.findViewById(R.id.ButtonRefresh);
		tmpImgButton.setOnClickListener(new Buttons().ButtonRefresh());
		tmpImgButton = (ImageButton)this.findViewById(R.id.ButtonPause);
		tmpImgButton.setOnClickListener(new Buttons().ButtonPause());
		tmpImgButton = (ImageButton)this.findViewById(R.id.ButtonNext);
		tmpImgButton.setOnClickListener(new Buttons().ButtonNext());
		tmpImgButton = (ImageButton)this.findViewById(R.id.ButtonClear);
		tmpImgButton.setOnClickListener(new Buttons().ButtonClear());
		tmpImgButton = (ImageButton)this.findViewById(R.id.ButtonLowerVolume);
		tmpImgButton.setOnClickListener(new Buttons().ButtonLowerVolume());
		tmpImgButton = (ImageButton)this.findViewById(R.id.ButtonMute);
		tmpImgButton.setOnClickListener(new Buttons().ButtonMute());
		tmpImgButton = (ImageButton)this.findViewById(R.id.ButtonIncreaseVolume);
		tmpImgButton.setOnClickListener(new Buttons().ButtonIncreaseVolume());
		tmpImgButton = (ImageButton)this.findViewById(R.id.ButtonShuffle);
		tmpImgButton.setOnClickListener(new Buttons().ButtonShuffle());
		MpgX_ListView.addHeaderView(MpgX_TextSentinel);
		MpgX_SongListAdapter = MpgX_bindListViewToAdapter(this, MpgX_ListView);
		registerForContextMenu(MpgX_ListView);
		MpgX_SheduleAtFixedRate();
	}

	public static void refreshPlayList() {
		Message m = new Message();
		m.what = MpgX_G1.MpgX_LISTVIEW_REFRESH;
		myG1ViewUpdateHandler.sendMessage(m);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo) {
		menu.add(0, MpgX_LISTVIEW_CONTEXT_MAGICSHUFFLE, 0, "Magic shuffle");
		menu.add(0, MpgX_LISTVIEW_CONTEXT_DEQUEUE, 1, "Dequeue");
	}

	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info;
		try {
			info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		} catch (ClassCastException e) {
			return false;
		}
		try {
			String id = (String) MpgX_SongListAdapter.getItem(info.position - 1);
			Map<String, String> song = (Map<String, String>) MpgX_SongListAdapter.getElement(id);
			switch (item.getItemId()) {
			case MpgX_LISTVIEW_CONTEXT_DEQUEUE:
				HTTP_Request.fast_post(MpgX_G1.MpgX_Server, "dequeue", song.get(":id") + "\n");
				MpgX_TextSentinel.setText("Dequeued");
				refreshPlayList();
				break;
			case MpgX_LISTVIEW_CONTEXT_MAGICSHUFFLE:
				HTTP_Request.fast_post(MpgX_G1.MpgX_Server, "magic_shuffle", song.get(":id") + "\n");
				MpgX_TextSentinel.setText("Magic shuffled!");
				refreshPlayList();
				break;
			}
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	@SuppressWarnings("unchecked") // Ruby is too cool
	private static void MpgX_RefreshPlayList(final boolean notCalledByTimer) {
		final Runnable run = new Runnable() {
			public void run() { 
				try {
					MpgX_PlayListExtraUpdated = notCalledByTimer;
					Message m;
					m= new Message();
					m.what = MpgX_G1.MpgX_LISTVIEW_SHOW_LOADING;
					MpgX_G1.myG1ViewUpdateHandler.sendMessage(m);
					String songs = HTTP_Request.get_body(MpgX_G1.MpgX_Server, "queue", "" + MpgX_NR_SONGS);
					if (songs != null) {
						List<HashMap<String, Object>> songList = (List<HashMap<String, Object>>)YAML.load(songs);
						String magic_key = ":still_in_list_" + new GregorianCalendar().get(Calendar.MILLISECOND);
						if (songList.size() == 0) {
							m = new Message();
							m.what = MpgX_G1.MpgX_LISTVIEW_EMPTY;
							MpgX_G1.myG1ViewUpdateHandler.sendMessage(m);
						} else {
							for (int i = 0; i < songList.size(); i++) {
								HashMap mp = songList.get(i);
								mp.put(magic_key, "true");
								mp.put(":id", ((Long)mp.get(":id")).toString()); // Dynamic Languages FTW!
								MpgX_SongListAdapter.putElement("" + i, mp);
							}
							m = new Message();
							m.what = MpgX_G1.MpgX_LISTVIEW_HIDE_LOADING;
							MpgX_G1.myG1ViewUpdateHandler.sendMessage(m);
						}
						MpgX_SongListAdapter.clearUnmarked(magic_key);
						m= new Message();
						m.what = MpgX_G1.MpgX_LISTVIEW_NOTIFY;
						MpgX_G1.myG1ViewUpdateHandler.sendMessage(m);
					} else {
						MpgX_SongListAdapter.clearElements();
						m = new Message();
						m.what = MpgX_G1.MpgX_LISTVIEW_NO_INTERNET;
						MpgX_G1.myG1ViewUpdateHandler.sendMessage(m);
						m = new Message();
						m.what = MpgX_G1.MpgX_LISTVIEW_NOTIFY;
						MpgX_G1.myG1ViewUpdateHandler.sendMessage(m);
					}
				} catch (Exception e) {
					HashMap<String, String> t  = new HashMap<String, String>();
					ByteArrayOutputStream err = new ByteArrayOutputStream();
					PrintStream r = new PrintStream(err);
					e.printStackTrace(r);
					t.put("artist", err.toString());
					t.put("id", "0");
					MpgX_SongListAdapter.putElement((String) "Dummy", t);
					Message m = new Message();
					m.what = MpgX_G1.MpgX_LISTVIEW_NOTIFY;
					MpgX_G1.myG1ViewUpdateHandler.sendMessage(m);
				}
			}
		};
		Thread t = new Thread(run);
		t.start();
	}

	private static void MpgX_SheduleAtFixedRate() {
		final Runnable run = new Runnable() {
			public void run() { 
				try {
					while(!Thread.currentThread().isInterrupted()){ 
						try {
							if (MpgX_PlayListExtraUpdated) {
								MpgX_PlayListExtraUpdated = false;
							} else {
								MpgX_RefreshPlayList(false);
							}
							Thread.sleep(MpgX_REFRESH_SECONDS * 1000);
						} catch (InterruptedException e) {
							Thread.currentThread().interrupt();
						} 
					}} catch (Exception e) {}
			}
		};
		Thread t = new Thread(run);
		t.start();
	}

	private static SongListAdapter MpgX_bindListViewToAdapter(Activity ctx, ListView listview) {
		final SongListAdapter listModelView = new SongListAdapter(ctx, listview);
		// bind a selection listener to the view
		listview.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@SuppressWarnings("unchecked")
			public void onItemSelected(AdapterView parentView, View childView, int position, long id) {
				listModelView.setSelected(position);
			}
			@SuppressWarnings("unchecked")
			public void onNothingSelected(AdapterView parentView) {
				listModelView.setSelected(-1);
			}
		});
		return listModelView;
	}
}


// Evilness

/*
HashMap<String, String> t  = new HashMap<String, String>();
ByteArrayOutputStream err = new ByteArrayOutputStream();
PrintStream r = new PrintStream(err);
e.printStackTrace(r);
t.put("artist", err.toString());
 */


// Andere
/*
public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo) {
	AdapterView.AdapterContextMenuInfo info;
	try {
		info = (AdapterView.AdapterContextMenuInfo) menuInfo;
	} catch (ClassCastException e) {
		return;
	}
	long id = MpgX_SongListAdapter.getItemId(info.position);
	menu.add(0, MpgX_LISTVIEW_CONTEXT_DEQUEUE, 0, "Dequeue");
	menu.add(0, MpgX_LISTVIEW_CONTEXT_MAGICSHUFFLE, 1, "Magic shuffle");
}
 */