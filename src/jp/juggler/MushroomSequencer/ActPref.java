package jp.juggler.MushroomSequencer;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;

public class ActPref extends PreferenceActivity{
	ActPref self = this;
	Handler ui_handler;
	SharedPreferences pref;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref);
        this.ui_handler = new Handler();
        this.pref = Util.pref(self);
        
        findPreference(Const.KEY_NOTIFICATION_ENABLED).setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object value) {
				final boolean enabled =  read_boolean(value);
				//
				Util.set_notification(self ,enabled ,isNotificationWhiteText() );
				Util.set_boot_receiver( self , enabled );
				//
				return true;
			}
		});

        findPreference(Const.KEY_NOTIFICATION_WHITETEXT).setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object value) {
				final boolean white = read_boolean(value);
				//
				Util.set_notification(self ,isNotificationEnabled() ,white );
				//
				return true;
			}
		});
	}
	
	boolean read_boolean(Object newval){
		return ((Boolean)newval).booleanValue();
	}
	boolean isNotificationEnabled(){
		return pref.getBoolean(Const.KEY_NOTIFICATION_ENABLED, false);
	}
	boolean isNotificationWhiteText(){
		return pref.getBoolean(Const.KEY_NOTIFICATION_WHITETEXT,false);
	}
	
}
