package vub.ig.MpgX;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Map;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class SongListAdapter extends BaseAdapter {

	/** holds all the Song data */
	private Hashtable<String, Map<String, String>> _data = new Hashtable<String, Map<String, String>>();

	/** holds the currently selected position */
	private int _selectedIndex;
	private Activity _context;

	//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
	// constructor
	//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX

	/**
	 * create the model-view object that will control the listview
	 *
	 * @param context  activity that creates this thing
	 * @param listview bind to this listview
	 */
	public SongListAdapter(final Activity context, ListView listview) {

		// save the activity/context ref
		_context = context;

		// bind this model (and cell renderer) to the listview
		listview.setAdapter(this);
	}

	public void clearElements() {
		_data.clear();
	}
	
	@SuppressWarnings("unchecked")
	public void clearUnmarked(String magic_key) {
		for (String k : ((Hashtable<String, Map<String, String>>) (_data.clone())).keySet()) {
			if (!_data.get(k).containsKey(magic_key)) {
				_data.remove(k);
			}
		}
	}

	public void addElement(String key, Map<String, String> m) {
		_data.put(key, m);
	}

	public Map<String, String> getElement(String key) {
		return _data.get(key);
	}

	public void putElement(String key, Map<String, String> m) {
		_data.put(key, m);
	}

	public void removeElement(String key) {
		_data.remove(key);
	}

	//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
	// implement ListAdapter - how big is the underlying list data, and how to iterate it...
	// the underlying data is a Map of Maps, so this really reflects the keyset of the enclosing Map...
	//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
	/** returns all the items in the {@link #_data} table */
	public int getCount() {
		return _data.size();
	}

	/** returns the key for the table, not the value (which is another table) */
	@SuppressWarnings("unchecked")
	public Object getItem(int i) {
		Object[] array =_data.keySet().toArray();
		Arrays.sort(array, new SongComparator());
		Object retval = array[i];
		return retval;
	}

	/** returns the unique id for the given index, which is just the index */
	public long getItemId(int i) {
		return i;
	}

	/**
	 * called when item in listview is selected... fires a model changed event...
	 *
	 * @param index index of item selected in listview. if -1 then it's unselected.
	 */
	public void setSelected(int index) {
		if (index == -1) {
			// unselected
		} else {
			// selected index...
		}
		_selectedIndex = index;
		// notify the model that the data has changed, need to update the view
		notifyDataSetChanged();
	}

	@Override public View getView(int index, View cellRenderer, ViewGroup parent) {
		CellRendererView cellRendererView = null;
		if (cellRenderer == null) {
			// create the cell renderer
			cellRendererView = new CellRendererView();
		} else {
			cellRendererView = (CellRendererView) cellRenderer;
		}
		// update the cell renderer, and handle selection state
		cellRendererView.display(index, _selectedIndex == index);
		return cellRendererView;
	}


	private class CellRendererView extends TableLayout {
		// ui stuff
		private TextView _lblSong;

		public CellRendererView() {
			super(_context);
			_createUI();
		}

		/** create the ui components */
		private void _createUI() {

			// make the 2nd col growable/wrappable
			setColumnShrinkable(1, true);
			setColumnStretchable(1, true);

			// set the padding
			setPadding(10, 10, 10, 10);

			// single row that holds icon/flag & name
			TableRow row = new TableRow(_context);
			LayoutUtils.Layout.WidthFill_HeightWrap.applyTableLayoutParams(row);

			// fill the first row with: icon/flag, name
			{
				_lblSong = new TextView(_context);
				LayoutUtils.Layout.WidthWrap_HeightWrap.applyTableRowParams(_lblSong);
				_lblSong.setPadding(10, 10, 10, 10);

				//_lblIcon = AppUtils.createImageView(_context, -1, -1, -1);
				//LayoutUtils.Layout.WidthWrap_HeightWrap.applyTableRowParams(_lblIcon);
				//_lblIcon.setPadding(10, 10, 10, 10);

				//row.addView(_lblIcon);
				row.addView(_lblSong);
			}

			/*
			// create the 2nd row with: description
			{
				_lblDescription = new TextView(_context);
				LayoutUtils.Layout.WidthFill_HeightWrap.applyTableLayoutParams(_lblDescription);
				_lblDescription.setPadding(10, 10, 10, 10);
			}
			 */

			// add the rows to the table
			addView(row);
		}

		/** update the views with the data corresponding to selection index */
		public void display(int index, boolean selected) {

			String id = getItem(index).toString();
			Map<String, String> song = _data.get(id);
			String artist = song.get(":artist");
			String title = song.get(":title");
			if ((artist == null) || (title == null)) {
				if ((artist == null) && (title == null))
					_lblSong.setText("");	
				else if (title == null)
					_lblSong.setText(artist);
				else
					_lblSong.setText(title);	
			} else {
				_lblSong.setText(artist +  " - " + title);				
			}

			//String icon = weatherForZip.get("icon");
			//int iconId = ResourceUtils.getResourceIdForDrawable(_context, "com.developerlife", "w" + icon);
			//String humidity = weatherForZip.get("humidity");
			//_lblDescription.setText("Humidity: " + humidity + " %");
			/*
			if (selected) {
				_lblDescription.setVisibility(View.VISIBLE);
			}
			else {
				_lblDescription.setVisibility(View.GONE);
			}
			 */
		}
	}
	
	@SuppressWarnings("unchecked")
	private class SongComparator implements Comparator {
		@Override
		public int compare(Object object1, Object object2) {
			Integer i = Integer.parseInt((String) object1);
			return i.compareTo(Integer.parseInt((String) object2));
		}
		
	}
}
