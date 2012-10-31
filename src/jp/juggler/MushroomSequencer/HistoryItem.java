package jp.juggler.MushroomSequencer;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

public class HistoryItem {
	public static final String COL_ID = BaseColumns._ID;
	public static final String COL_TEXT = "text_";
	public static final String COL_WHEN = "when_";

	public long id =-1;
	public String text;
	public long when;
	
	static final DataProvider.TableMeta meta = DataProvider.history;
	
	void save(ContentResolver cr){
		ContentValues values = new ContentValues();
		values.put(COL_TEXT,text);
		values.put(COL_WHEN,when);
		if( id == -1 ){
			cr.insert(meta.uri, values);
		}else{
			cr.update(meta.getItemUri(id),values,null,null);
		}
	}
	
	public static HistoryItem load(ContentResolver cr,long id){
		Cursor c = cr.query(Uri.withAppendedPath(meta.uri,"/"+id),null,null,null,null);
		if(c !=null){
			try{
				if(c.moveToNext() ){
					HistoryItem item = new HistoryItem();
					item.id = c.getLong(c.getColumnIndex(COL_ID));
					item.text = c.getString(c.getColumnIndex(COL_TEXT));
					item.when = c.getLong(c.getColumnIndex(COL_WHEN));
					return item;
				}
			}finally{
				c.close();
			}
		}
		return null;
	}
}
