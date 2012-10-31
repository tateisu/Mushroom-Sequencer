package jp.juggler.MushroomSequencer;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

public class MyEditText extends EditText{
	
	public MyEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public MyEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MyEditText(Context context) {
		super(context);
	}

	///////////////////////
	
	public static interface SelectionChangeListener{
		public void onSelectionChanged(int start,int end);
	}
	public SelectionChangeListener selection_change_listener;
	@Override
	protected void onSelectionChanged(int selStart, int selEnd) {
		super.onSelectionChanged(selStart, selEnd);
		if( selection_change_listener != null ) selection_change_listener.onSelectionChanged(selStart, selEnd);
	}

	

}
