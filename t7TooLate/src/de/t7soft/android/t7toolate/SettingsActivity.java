package de.t7soft.android.t7toolate;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

public class SettingsActivity extends Activity {

	private EditText editTextOnTime;
	private EditText editTextSlight;
	private EditText editTextLate;
	private EditText editTextExtrem;
	private CheckBox checkBoxQuickQuide;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.settings);

		editTextOnTime = (EditText) findViewById(R.id.editTextOnTime);
		editTextSlight = (EditText) findViewById(R.id.editTextSlight);
		editTextLate = (EditText) findViewById(R.id.editTextLate);
		editTextExtrem = (EditText) findViewById(R.id.editTextExtrem);
		editTextLate.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(final CharSequence s, final int start, final int before, final int count) {
				// nothing to do
			}

			@Override
			public void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) {
				// nothing to do
			}

			@Override
			public void afterTextChanged(final Editable s) {
				editTextExtrem.setText(editTextLate.getText().toString());
			}
		});

		checkBoxQuickQuide = (CheckBox) findViewById(R.id.checkBoxQuickQuide);
		checkBoxQuickQuide.setChecked(false);

	}

	@Override
	protected void onResume() {
		super.onResume();
		final Settings settings = readSettings();
		editTextOnTime.setText(Integer.toString(settings.getOneTime()));
		editTextSlight.setText(Integer.toString(settings.getSlight()));
		editTextLate.setText(Integer.toString(settings.getLate()));
		editTextExtrem.setText(Integer.toString(settings.getLate()));
	}

	private Settings readSettings() {
		final int oneTime = getPrefOnTime(this);
		final int slight = getPrefSlight(this);
		final int late = getPrefLate(this);
		return new Settings(oneTime, slight, late);
	}

	public void onOk(final View view) {

		int oneTime = -1;
		int slight = -1;
		int late = -1;
		try {
			oneTime = Integer.parseInt(editTextOnTime.getText().toString());
		} catch (final NumberFormatException e) {
			// value is empty or not a number
		}
		try {
			slight = Integer.parseInt(editTextSlight.getText().toString());
		} catch (final NumberFormatException e) {
			// value is empty or not a number
		}
		try {
			late = Integer.parseInt(editTextLate.getText().toString());
		} catch (final NumberFormatException e) {
			// value is empty or not a number
		}
		final Settings settings = new Settings(oneTime, slight, late);
		settings.setShowQuickGuide(checkBoxQuickQuide.isChecked());

		if (!validate(settings)) {
			return;
		}

		if (save(settings, view.getContext())) {
			finish();
		}
	}

	private boolean validate(final Settings settings) {

		boolean ok = true;

		if (settings.getOneTime() < 0) {
			editTextOnTime.setError(getString(R.string.settings_error_no_value));
			ok = false;
		}
		if (settings.getSlight() < 0) {
			editTextSlight.setError(getString(R.string.settings_error_no_value));
			ok = false;
		} else if (settings.getOneTime() > settings.getSlight()) {
			editTextSlight.setError(getString(R.string.settings_error_slight));
			ok = false;
		}
		if (settings.getLate() < 0) {
			editTextLate.setError(getString(R.string.settings_error_no_value));
			ok = false;
		} else if (settings.getSlight() > settings.getLate()) {
			editTextSlight.setError(getString(R.string.settings_error_late));
			ok = false;
		}

		return ok;

	}

	private boolean save(final Settings settings, final Context context) {
		final SharedPreferences preferences = context.getSharedPreferences(MainActivity.PREFS_NAME, 0);
		final SharedPreferences.Editor editor = preferences.edit();
		editor.putInt(MainActivity.PREF_ON_TIME, settings.oneTime);
		editor.putInt(MainActivity.PREF_SLIGHT, settings.slight);
		editor.putInt(MainActivity.PREF_LATE, settings.late);
		editor.putBoolean(MainActivity.PREF_SHOW_QUICK_GUIDE, settings.isShowQuickGuide());
		editor.commit();
		return true;
	}

	private static int getPrefOnTime(final Context context) {
		final SharedPreferences preferences = context.getSharedPreferences(MainActivity.PREFS_NAME, 0);
		return preferences.getInt(MainActivity.PREF_ON_TIME, MainActivity.PREF_ON_TIME_DEFAULT);
	}

	private static int getPrefSlight(final Context context) {
		final SharedPreferences preferences = context.getSharedPreferences(MainActivity.PREFS_NAME, 0);
		return preferences.getInt(MainActivity.PREF_SLIGHT, MainActivity.PREF_SLIGHT_DEFAULT);
	}

	private static int getPrefLate(final Context context) {
		final SharedPreferences preferences = context.getSharedPreferences(MainActivity.PREFS_NAME, 0);
		return preferences.getInt(MainActivity.PREF_LATE, MainActivity.PREF_LATE_DEFAULT);
	}

	private class Settings {

		private final int oneTime;
		private final int slight;
		private final int late;
		private boolean showQuickGuide;

		public Settings(final int oneTime, final int slight, final int late) {
			this.oneTime = oneTime;
			this.slight = slight;
			this.late = late;
			this.showQuickGuide = false;
		}

		public int getOneTime() {
			return oneTime;
		}

		public int getSlight() {
			return slight;
		}

		public int getLate() {
			return late;
		}

		public boolean isShowQuickGuide() {
			return showQuickGuide;
		}

		public void setShowQuickGuide(final boolean showQuickGuide) {
			this.showQuickGuide = showQuickGuide;
		}

	}

}
