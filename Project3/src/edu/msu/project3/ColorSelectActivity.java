package edu.msu.project3;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;

public class ColorSelectActivity extends Activity {

	public static final String COLOR = "COLOR";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_color_select);
	}

	public void selectColor(int color) {
		Intent result = new Intent();
		result.putExtra(COLOR, color);
		setResult(Activity.RESULT_OK, result);
		finish();
    }
}
