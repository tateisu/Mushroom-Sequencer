package jp.juggler.MushroomSequencer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.ClipboardManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ActMain extends Activity implements View.OnClickListener{
	static final String TAG="Mushroom Sequencer";
	static final int REQ_HISTORY = 1;
	static final int REQ_MUSH = 2;
	
	ActMain self = this;
	
	MyEditText etMushText;
	ListView listview;
	InputMethodManager imm;
	LayoutInflater inflater;
	Handler ui_handler;
	CheckBox cbAppend;
	CheckBox cbSelection;

	static final String MUSHROOM_ACTION = "com.adamrocker.android.simeji.ACTION_INTERCEPT";
	static final String MUSHROOM_CATEGORY = "com.adamrocker.android.simeji.REPLACE";
	static final String MUSHROOM_REPLACE_KEY = "replace_key";
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN); 
        setContentView(R.layout.main);
        setTitle(R.string.name_app);
        ui_handler = new Handler();
        inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
        imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        cbAppend = (CheckBox)findViewById(R.id.cbAppend);
        cbSelection = (CheckBox)findViewById(R.id.cbSelection);
        listview = (ListView)findViewById(R.id.list);
        etMushText = (MyEditText)findViewById(R.id.etMushText);

        init_list();

        
        setResult(RESULT_CANCELED);
        Intent intent = getIntent();
        String text = intent.getStringExtra(MUSHROOM_REPLACE_KEY);
        if( text != null ){
    		etMushText.setText( text );
        }else{
        	text = intent.getStringExtra(Intent.EXTRA_TEXT);
        	if(text!=null) etMushText.setText( text );
        }
        
        etMushText.setOnFocusChangeListener(new View.OnFocusChangeListener(){
    		@Override
    		public void onFocusChange(View v, boolean flag){
    			if(flag == false) imm.hideSoftInputFromWindow(v.getWindowToken(),0);
    		}
    	});
        
        etMushText.selection_change_listener = new MyEditText.SelectionChangeListener() {
			@Override
			public void onSelectionChanged(int start, int end) {
				Log.d(TAG,"onSelectionChanged "+start+","+end);
				cbSelection.setEnabled( start < end );
			}
		};
		
        findViewById(R.id.btnKeyboard).setOnClickListener(this);
        findViewById(R.id.btnComplete).setOnClickListener(this);
        findViewById(R.id.btnEditMenu).setOnClickListener(this);
        		

		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,long arg3) {
				AppItem item  = adapter.getItem(position);
				try{
					Intent intent = new Intent(MUSHROOM_ACTION);
					intent.addCategory(MUSHROOM_CATEGORY);
					intent.setComponent(new ComponentName(item.pkg,item.act));
					//
					int sel_start = etMushText.getSelectionStart();
					int sel_end = etMushText.getSelectionEnd();
					boolean has_selection = (sel_start != -1 && sel_end != -1 && sel_start < sel_end);
					Log.d(TAG,"onClick selection="+sel_start+","+sel_end);
					if( has_selection && cbSelection.isChecked() ){
						// 選択範囲があって、範囲モードなら、そのテキストをマッシュルームに送る
						intent.putExtra(MUSHROOM_REPLACE_KEY, etMushText.getText().toString().substring(sel_start, sel_end) );
					}else{
						// そうでなければ、テキスト全てをマッシュルームに送る
						intent.putExtra(MUSHROOM_REPLACE_KEY, etMushText.getText().toString() );
					}
					//
					startActivityForResult(intent, REQ_MUSH);
					
				}catch(Throwable ex){
					Toast.makeText(self,ex.getMessage(),Toast.LENGTH_LONG).show();
				}
			}
		});
		
		/////////////////
        etMushText.requestFocus();
        imm.restartInput(etMushText);
		imm.hideSoftInputFromInputMethod(etMushText.getWindowToken(),0);

    }
    
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d(TAG,"onActivityResult requestCode="+requestCode+",result="+resultCode+",detail="+data);
		if( resultCode == RESULT_OK ){ 
			if( requestCode == REQ_MUSH ){ 
				String text = data.getStringExtra(MUSHROOM_REPLACE_KEY);
				Log.d(TAG,"onActivityResult result text="+text);
				replace_text(text);
				return;
			}else if( requestCode == REQ_HISTORY ){
				String text = data.getStringExtra("text");
				replace_text(text);
				return;
			}
		}
	}

	void replace_text(String text){
		if(text!=null && text.length() > 0){
			// 現在の選択範囲
			String old_text = etMushText.getText().toString();
			int sel_start = etMushText.getSelectionStart();
			int sel_end = etMushText.getSelectionEnd();
			boolean has_selection = (sel_start != -1 && sel_end != -1 && sel_start < sel_end);

			if( has_selection && cbSelection.isChecked() ){
				// 選択範囲の古いテキスト
				String old_selection_text = old_text.substring(sel_start, sel_end);
				if( text.equals(old_selection_text) ){
					// テキストに変化がない。
					// いくつかのマッシュは「変更なし」を示すためにRESULT_OKで同じテキストを返してくる
					return;
				}
				StringBuffer sb = new StringBuffer();
				sb.append( old_text.substring(0,sel_start));
				// 追記モードなら、選択範囲内部の末尾に追記する
				if( cbAppend.isChecked() ) sb.append( old_text.substring(sel_start,sel_end));
				sb.append( text );
				int sel_end_new = sb.length();
				sb.append( old_text.substring(sel_end));
				//
				etMushText.setText(sb.toString());
				etMushText.setSelection(sel_start,sel_end_new);
			}else{
				String old_selection_text = old_text;
				if( text.equals(old_selection_text) ){
					// テキストに変化がない。
					// いくつかのマッシュは「変更なし」を示すためにRESULT_OKで同じテキストを返してくる
					return;
				}
				StringBuffer sb = new StringBuffer();
				if( cbAppend.isChecked() ) sb.append( old_text);
				sb.append( text );
				//
				etMushText.setText(sb.toString());
				etMushText.setSelection(text.length());
			}
		}
	}
    
    
    ////////////////////////////////////////////////////////////////////////





	static class AppItem{
		Drawable drawable;
		String label;
		String pkg;
		String act;
	}
	
	ArrayList<AppItem> item_list = new ArrayList<AppItem>();
	AppAdapter adapter;

	
	void init_list(){
		adapter = new AppAdapter();
		listview.setAdapter(adapter);

		final PackageManager pm = getPackageManager();
		new AsyncTask<Void,Void,ArrayList<AppItem>> (){
			@Override
			protected ArrayList<AppItem> doInBackground(Void... params) {
				Intent intent = new Intent(MUSHROOM_ACTION, null);
				intent.addCategory(MUSHROOM_CATEGORY);
				 ArrayList<AppItem> result = new ArrayList<AppItem>();
				 String my_name = self.getClass().getName();
				for( ResolveInfo info : pm.queryIntentActivities( intent, 0) ){
					if(isFinishing()) break;
					if( my_name.equals(info.activityInfo.name ) ) continue;
					if( "jp.pud.android.mushrelay".equals(info.activityInfo.packageName ) ) continue;
					AppItem item = new AppItem();
					item.pkg   = info.activityInfo.packageName;
					item.act   = info.activityInfo.name;
					item.label = info.loadLabel(pm).toString();
					item.drawable = info.loadIcon(pm);
					result.add(item);
				}
				Collections.sort(result,new Comparator<AppItem>() {
					@Override
					public int compare(AppItem object1, AppItem object2) {
						return String.CASE_INSENSITIVE_ORDER.compare( object1.label, object2.label );
					}
				});
				return result;
			}

			@Override
			protected void onPostExecute(ArrayList<AppItem> result) {
				if(isFinishing()) return;
				if( result != null ){
					adapter.setNotifyOnChange(false);
					for(AppItem item : result ){
						adapter.add(item);
					}
					adapter.setNotifyOnChange(true);
					adapter.notifyDataSetChanged();
				}
			}
			
		}.execute();
		
	}
	
	static class ViewHolder{
		ImageView icon;
		TextView  label;
	}

    class AppAdapter extends ArrayAdapter<AppItem>{
    	AppAdapter(){
    		super(self,0);
    	}

		@Override
		public View getView(int position, View view, ViewGroup parent) {
			ViewHolder holder;
			if( view == null ){
				view = inflater.inflate(R.layout.lv_app,null);
				view.setTag(holder = new ViewHolder() );
				holder.icon = (ImageView)view.findViewById(R.id.icon);
				holder.label = (TextView)view.findViewById(R.id.label);
			}else{
				holder = (ViewHolder)view.getTag();
			}
			AppItem item = getItem(position);
			holder.icon.setImageDrawable(item.drawable);
			holder.label.setText( item.label );
			return view;
		}
    	
    }
    
    ///////////////////////////////////////////
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.xml.option_menu, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    default:
	        return super.onOptionsItemSelected(item);
	    case R.id.menu_history:
	    	startActivityForResult(new Intent(self,ActHistory.class),REQ_HISTORY);
	    	return true;
	    case R.id.menu_setting:
	    	ui_handler.post(new Runnable() {
				@Override
				public void run() {
					if(isFinishing())return;
					startActivity(new Intent(self,ActPref.class));
				}
			});
			return true;
	    }
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.btnKeyboard:
			imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
			break;
		case R.id.btnEditMenu:
			if( etMushText != null ) etMushText.performLongClick();
			break;
		case R.id.btnComplete:
			complete();
			break;
		}
	}
	
	void complete(){
		imm.hideSoftInputFromInputMethod(etMushText.getWindowToken(),0);
		if( etMushText != null ){
			String text = etMushText.getText().toString();
			//
			HistoryItem item = new HistoryItem();
			item.text = text;
			item.when = System.currentTimeMillis();
			item.save( getContentResolver() );
			//
			if( MUSHROOM_ACTION.equals( getIntent().getAction() ) ){
				Intent data = new Intent();
				data.putExtra(MUSHROOM_REPLACE_KEY,text );
				setResult(RESULT_OK, data);
			}else{
				ClipboardManager cm = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
				cm.setText(text);
				Toast.makeText(self,R.string.copy_to_clipboard,Toast.LENGTH_SHORT).show();
			}
			finish();				
		}
	}
}