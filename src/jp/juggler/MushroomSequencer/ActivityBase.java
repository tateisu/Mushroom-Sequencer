package jp.juggler.MushroomSequencer;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.widget.Toast;

public abstract class ActivityBase extends Activity {
	static final String TAG = "MushSequencer";
	Handler ui_handler;
	ActivityBase self = this;
	LayoutInflater inflater;
	ContentResolver cr;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ui_handler = new Handler();
		inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
		cr = getContentResolver();
	}
	
    void show_toast(final int length,final int resid){
    	ui_handler.post(new Runnable() {
			@Override
			public void run() {
				if(isFinishing())return;
				Toast.makeText(self,resid,length).show();
			}
		});
    }
    void show_toast(final int length,final String text){
    	ui_handler.post(new Runnable() {
			@Override
			public void run() {
				if(isFinishing())return;
				Toast.makeText(self,text,length).show();
			}
		});
    }
    
	void report_ex(Throwable ex){
		show_toast(Toast.LENGTH_LONG,String.format("%s %s",ex.getClass().getSimpleName(),ex.getMessage()));
		ex.printStackTrace();
	}
	
    SharedPreferences pref(){
    	return PreferenceManager.getDefaultSharedPreferences(self);
    }
	
}
