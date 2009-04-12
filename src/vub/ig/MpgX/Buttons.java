package vub.ig.MpgX;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import vub.ig.MpgX_G1;
import vub.ig.HTTP_Request;

public final class Buttons {

	public class ButtonRefresh implements Button.OnClickListener {
		public void onClick(View v) {
			MpgX_G1.refreshPlayList();
		}
	}

	public class ButtonPause implements Button.OnClickListener {
		public void onClick(View v) {
			HTTP_Request.fast_post(MpgX_G1.MpgX_Server, "pause", "");
		}
	}

	public class ButtonNext implements Button.OnClickListener {
		public void onClick(View v) {
			HTTP_Request.fast_post(MpgX_G1.MpgX_Server, "skip", "");
			MpgX_G1.refreshPlayList();
		}
	}

	public class ButtonClear implements Button.OnClickListener {
		public void onClick(View v) {
			HTTP_Request.fast_post(MpgX_G1.MpgX_Server, "flush");
			MpgX_G1.refreshPlayList();
		}
	}

	public class ButtonLowerVolume implements Button.OnClickListener {
		public void onClick(View v) {
			HTTP_Request.fast_post(MpgX_G1.MpgX_Server, "set_volume", "-5");
		}
	}

	public class ButtonMute implements Button.OnClickListener {
		public void onClick(View v) {
			new Thread() {
			    @Override public void run() {
			    	String tmp = HTTP_Request.get(MpgX_G1.MpgX_Server, "get_volume");
			    	HTTP_Request.post(MpgX_G1.MpgX_Server, "set_volume", MpgX_G1.previousVolume);
			    	MpgX_G1.previousVolume = tmp;
			    }
			 }.start();
		}		
	}

	public class ButtonIncreaseVolume implements Button.OnClickListener {
		public void onClick(View v) {	
			HTTP_Request.fast_post(MpgX_G1.MpgX_Server, "set_volume", "+5");
		}
	}

	public class ButtonShuffle implements Button.OnClickListener {
		public void onClick(View v) {
			HTTP_Request.fast_post(MpgX_G1.MpgX_Server, "shuffle", "");
			MpgX_G1.refreshPlayList();
		}
	}
	
	public OnClickListener ButtonRefresh() {
		return new ButtonRefresh();
	}

	public OnClickListener ButtonPause() {
		return new ButtonPause();
	}

	public OnClickListener ButtonNext() {
		return new ButtonNext();
	}

	public OnClickListener ButtonClear() {
		return new ButtonClear();
	}
	
	public OnClickListener ButtonLowerVolume() {
		return new ButtonLowerVolume();
	}

	public OnClickListener ButtonMute() {
		return new ButtonMute();
	}

	public OnClickListener ButtonIncreaseVolume() {
		return new ButtonIncreaseVolume();
	}

	public OnClickListener ButtonShuffle() {
		return new ButtonShuffle();
	}

}
