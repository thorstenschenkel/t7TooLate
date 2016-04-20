package de.t7soft.android.t7toolate;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class SettingsActivity extends Activity {

	private EditText editTextOnTime;
	private EditText editTextSlight;
	private EditText editTextLate;
	private EditText editTextExtrem;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.settings);

		editTextOnTime = (EditText) findViewById(R.id.editTextOnTime);
		editTextSlight = (EditText) findViewById(R.id.editTextSlight);
		editTextLate = (EditText) findViewById(R.id.editTextLate);
		editTextExtrem = (EditText) findViewById(R.id.editTextExtrem);

	}

	@Override
	protected void onResume() {
		super.onResume();
		editTextOnTime.setText(Integer.toString(getPrefOnTime(this)));
		editTextSlight.setText(Integer.toString(getPrefSlight(this)));
		editTextLate.setText(Integer.toString(getPrefLate(this)));
		editTextExtrem.setText(Integer.toString(getPrefLate(this)));
	}

	public void onOk(final View view) {
		// TODO
	}

	private static int getPrefOnTime(final Context context) {
		final SharedPreferences settings = context.getSharedPreferences(MainActivity.PREFS_NAME, 0);
		return settings.getInt(MainActivity.PREF_ON_TIME, MainActivity.PREF_ON_TIME_DEFAULT);
	}

	private static int getPrefSlight(final Context context) {
		final SharedPreferences settings = context.getSharedPreferences(MainActivity.PREFS_NAME, 0);
		return settings.getInt(MainActivity.PREF_SLIGHT, MainActivity.PREF_SLIGHT_DEFAULT);
	}

	private static int getPrefLate(final Context context) {
		final SharedPreferences settings = context.getSharedPreferences(MainActivity.PREFS_NAME, 0);
		return settings.getInt(MainActivity.PREF_LATE, MainActivity.PREF_LATE_DEFAULT);
	}

}
