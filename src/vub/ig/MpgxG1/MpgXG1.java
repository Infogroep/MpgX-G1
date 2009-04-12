package vub.ig.MpgxG1;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;

public class MpgXG1 extends Activity {

	public static String server = "http://mpgx.rave.org:6668";
	public static String previousVolume = "0";
	
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.main);
		Button tmpButton;
		tmpButton = (Button)this.findViewById(R.id.ButtonShuffle);
		tmpButton.setOnClickListener(new Buttons().ButtonShuffle());
		tmpButton = (Button)this.findViewById(R.id.ButtonPause);
		tmpButton.setOnClickListener(new Buttons().ButtonPause());
		tmpButton = (Button)this.findViewById(R.id.ButtonNext);
		tmpButton.setOnClickListener(new Buttons().ButtonNext());
		tmpButton = (Button)this.findViewById(R.id.ButtonLowerVolume);
		tmpButton.setOnClickListener(new Buttons().ButtonLowerVolume());
		tmpButton = (Button)this.findViewById(R.id.ButtonMute);
		tmpButton.setOnClickListener(new Buttons().ButtonMute());
		tmpButton = (Button)this.findViewById(R.id.ButtonIncreaseVolume);
		tmpButton.setOnClickListener(new Buttons().ButtonIncreaseVolume());
	}
}