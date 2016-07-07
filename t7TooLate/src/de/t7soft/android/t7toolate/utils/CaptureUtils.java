/**
 * Copyright (c) 2016 Thorsten Schenkel (t7soft.de)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR
 * IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * 
 * @author Thorsten Schenkel (t7soft.de)
 * 
 */
package de.t7soft.android.t7toolate.utils;

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

	public static int getDelayMinutes(final Capture capture) {

		if (capture.isCanceled()) {
			return Integer.MAX_VALUE;
		}

		final Connection connection = capture.getConnection();
		final long captrueSeconds = getSeconds(capture.getCaptureDateTime());
		final long endSeconds = getSeconds(connection.getEndTime());
		final long diff = captrueSeconds - endSeconds;
		final long minutes = diff / 60;
		return (int) minutes;
	}

	public static int fillTextViewDelay(final Capture capture, final TextView textViewDelay) {
		final int minutes = getDelayMinutes(capture);
		return fillTextViewDelay(minutes, textViewDelay);
	}

	public static int fillTextViewDelay(final int minutes, final TextView textViewDelay) {

		final Context context = textViewDelay.getContext();

		String delayText = "";
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
