package jp.juggler.MushroomSequencer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class Receiver1 extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		SharedPreferences pref = Util.pref(context);
		Util.set_notification(context 
			,pref.getBoolean(Const.KEY_NOTIFICATION_ENABLED, false)
			,pref.getBoolean(Const.KEY_NOTIFICATION_WHITETEXT, false)
		);
	}
}
