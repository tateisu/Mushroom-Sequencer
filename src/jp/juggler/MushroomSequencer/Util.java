package jp.juggler.MushroomSequencer;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;

public class Util {

	public static SharedPreferences pref(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context);
	}

	static final int NOTIFICATION_ITEM_ID = 456;
	
	public static boolean  set_notification(Context context,boolean onoff ,boolean whitetext ) {
	//	SharedPreferences pref = Util.pref(context);
	//	boolean onoff = pref.getBoolean("notification_enabled",false);
	//	boolean whitetext = pref.getBoolean("notification_whitetext", false);
		boolean no_icon = true;
		
		NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		nm.cancelAll ();
		if(!onoff) return true;
		
		Intent activity_intent = new Intent(context,ActMain.class);
		activity_intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY);
		
		PendingIntent pending_intent = PendingIntent.getActivity(context, 0,activity_intent,Intent.FLAG_ACTIVITY_NEW_TASK);
		
		Notification n = new Notification();
		n.icon = ( no_icon ? R.drawable.empty : R.drawable.icon );
		n.when = ( Build.VERSION.SDK_INT >= 9 ? -Long.MAX_VALUE : Long.MAX_VALUE);
		n.setLatestEventInfo(context,context.getString(R.string.app_name),context.getString(R.string.notification_expand_desc),pending_intent);
		n.flags |= Notification.FLAG_NO_CLEAR ;
		n.flags |= Notification.FLAG_ONGOING_EVENT ;
		if( whitetext ){
			n.contentView  = new RemoteViews(context.getPackageName(),R.layout.notification_expand_white );
		}else{
			n.contentView  = new RemoteViews(context.getPackageName(),R.layout.notification_expand );
		}
		nm.notify(NOTIFICATION_ITEM_ID,n);
		return true;
	}

	public static void set_boot_receiver(Context context, boolean enabled) {
		PackageManager pm = context.getPackageManager();
		ComponentName name = new ComponentName(context, Receiver1.class);
		// すでに設定されているなら何もしない
		int state = pm.getComponentEnabledSetting(name);
		if(  enabled && state == PackageManager.COMPONENT_ENABLED_STATE_ENABLED ) return;
		if( !enabled && state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED ) return;
		// コンポーネントの有効/無効を設定する
		pm.setComponentEnabledSetting(
			name
			,(enabled ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED : PackageManager.COMPONENT_ENABLED_STATE_DISABLED)
			,PackageManager.DONT_KILL_APP
		);
	}
	
}
