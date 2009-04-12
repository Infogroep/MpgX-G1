package vub.ig.MpgxG1;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import vub.ig.MpgxG1.HTTP_Request;

public final class Buttons {

	public class ButtonShuffle implements Button.OnClickListener {
		public void onClick(View v) {
			HTTP_Request.post("shuffle", "");
		}
	}

	public class ButtonPause implements Button.OnClickListener {
		public void onClick(View v) {
			HTTP_Request.post("pause", "");
		}
	}

	public class ButtonNext implements Button.OnClickListener {
		public void onClick(View v) {	
			HTTP_Request.post("skip", "");
		}
	}

	public class ButtonLowerVolume implements Button.OnClickListener {
		public void onClick(View v) {	
			HTTP_Request.post("set_volume", "-5");
		}
	}

	public class ButtonMute implements Button.OnClickListener {
		public void onClick(View v) {
			String tmp = HTTP_Request.get("get_volume");
			HTTP_Request.post("set_volume", MpgXG1.previousVolume);
			MpgXG1.previousVolume = tmp;
		}		
	}

	public class ButtonIncreaseVolume implements Button.OnClickListener {
		public void onClick(View v) {	
			HTTP_Request.post("set_volume", "+5");
		}
	}

	public OnClickListener ButtonShuffle() {
		return new ButtonShuffle();
	}

	public OnClickListener ButtonPause() {
		return new ButtonPause();
	}

	public OnClickListener ButtonNext() {
		return new ButtonNext();
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

}
