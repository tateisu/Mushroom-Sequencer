package jp.juggler.MushroomSequencer;




import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.ClipboardManager;
import android.text.format.DateUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ActHistory extends ActivityBase {
	
	ListView listview;
	MyCursorAdapter adapter;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_list);

		
		setResult(RESULT_CANCELED);
		
		
		listview = (ListView)findViewById(R.id.list);
		adapter = new MyCursorAdapter(this,cr.query(DataProvider.history.uri,null,null,null,HistoryItem.COL_WHEN+" desc"),true);
		listview.setAdapter(adapter);
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,long arg3) {
				long id = adapter.getItemId(position);
				final HistoryItem item = HistoryItem.load(cr,id);
				new AlertDialog.Builder(self)
				.setItems(getResources().getStringArray(R.array.history_context_menu), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch(which){
						case 0: return_text(item.text); break;
						case 1: copy_text( item.text ); break;
						case 2: remove_history( item ); break;
						}
					}
				})
				.setCancelable(true)
				.show();
			}
			
		});
	}
	
	///////////////////////////////////////////////


	void return_text(String text){
		Intent intent = new Intent();
		intent.putExtra("text",text);
		setResult(RESULT_OK,intent);
		finish();
	}

	void remove_history(HistoryItem item){
		cr.delete(HistoryItem.meta.getItemUri(item.id),null,null);
	}
	
	void copy_text(String text){
		ClipboardManager cm = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
		cm.setText(text);
		Toast.makeText(self,R.string.copy_to_clipboard,Toast.LENGTH_SHORT).show();
	}
	
	///////////////////////////////////////////////
	static class ViewHolder{
		TextView tvTime;
		TextView tvText;
	}
	class MyCursorAdapter extends CursorAdapter{
		int colidx_id;
		int colidx_text;
		int colidx_when;
		
		
		public MyCursorAdapter(Context context, Cursor c, boolean autoRequery) {
			super(context, c, autoRequery);
			 colidx_id = c.getColumnIndex(HistoryItem.COL_ID);
			 colidx_text = c.getColumnIndex(HistoryItem.COL_TEXT);
			 colidx_when = c.getColumnIndex(HistoryItem.COL_WHEN);
		}
		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			View view = inflater.inflate(R.layout.lv_history,null);
			ViewHolder holder = new ViewHolder();
			view.setTag(holder);
			holder.tvTime = (TextView)view.findViewById(R.id.tvTime);
			holder.tvText = (TextView)view.findViewById(R.id.tvText);
			return view;
		}
		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			ViewHolder holder = (ViewHolder)view.getTag();
			
			holder.tvTime.setText(
					DateUtils.formatDateTime(self,cursor.getLong(colidx_when),DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_24HOUR)
			);
			holder.tvText.setText( cursor.getString(colidx_text) );

		}
	}

	///////////////////////////////////
	
}
