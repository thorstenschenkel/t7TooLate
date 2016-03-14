package de.t7soft.android.t7toolate.utils.view;

import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.TextView;
import de.t7soft.android.t7toolate.MainActivity;
import de.t7soft.android.t7toolate.R;
import de.t7soft.android.t7toolate.model.Capture;
import de.t7soft.android.t7toolate.model.Connection;

public class CaptureUtils {

	private CaptureUtils() {
		// utility class
	}

	public static int fillTextViewDelay(final Capture capture, final TextView textViewDelay) {

		final Connection connection = capture.getConnection();
		final Context context = textViewDelay.getContext();

		String delayText = "";
		final long captrueSeconds = getSeconds(capture.getCaptureDateTime());
		final long endSeconds = getSeconds(connection.getEndTime());
		final long diff = captrueSeconds - endSeconds;
		final long minutes = diff / 60;

		if (minutes > 0) {
			delayText += " +";
			delayText += minutes;
		} else if (minutes < 0) {
			delayText += minutes;
		} else {
			delayText += minutes;
		}
		delayText += " min";
		textViewDelay.setText(delayText);

		int textColor = android.R.color.primary_text_light;
		int bgColor = android.R.color.transparent;
		int borderColor = textColor;
		if (minutes < getPrefOnTime(context)) {
			textColor = R.color.text_color_on_time;
			borderColor = textColor;
		} else if (minutes < getPrefSlight(context)) {
			textColor = R.color.text_color_slight;
			borderColor = textColor;
		} else if (minutes < getPrefLate(context)) {
			textColor = R.color.text_color_late;
			borderColor = textColor;
		} else {
			textColor = R.color.text_color_extrem;
			bgColor = R.color.bg_color_extrem;
			borderColor = bgColor;
		}
		textViewDelay.setTextColor(context.getResources().getColor(textColor));
		textViewDelay.setBackgroundColor(context.getResources().getColor(bgColor));
		return borderColor;

	}

	private static long getSeconds(final Date date) {

		final Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);

		long seconds = calendar.get(Calendar.SECOND);
		seconds += calendar.get(Calendar.MINUTE) * 60;
		seconds += calendar.get(Calendar.HOUR_OF_DAY) * 60 * 60;

		return seconds;

	}

	private static int getPrefOnTime(final Context context) {
		final SharedPreferences settings = context.getSharedPreferences(MainActivity.PREFS_NAME, 0);
		return settings.getInt(MainActivity.PREF_ON_TIME, 5);
	}

	private static int getPrefSlight(final Context context) {
		final SharedPreferences settings = context.getSharedPreferences(MainActivity.PREFS_NAME, 0);
		return settings.getInt(MainActivity.PREF_SLIGHT, 10);
	}

	private static int getPrefLate(final Context context) {
		final SharedPreferences settings = context.getSharedPreferences(MainActivity.PREFS_NAME, 0);
		return settings.getInt(MainActivity.PREF_LATE, 60);
	}

}
