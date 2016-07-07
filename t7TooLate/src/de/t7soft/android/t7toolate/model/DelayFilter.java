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
package de.t7soft.android.t7toolate.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import de.t7soft.android.t7toolate.MainActivity;
import de.t7soft.android.t7toolate.database.ToLateDatabaseAdapter;
import de.t7soft.android.t7toolate.utils.CaptureUtils;

public class DelayFilter extends AbstractFilter {

	private final Context context;
	private boolean onTime;
	private boolean slight;
	private boolean late;
	private boolean extreme;

	public DelayFilter(final Context context) {
		super("DelayFilter");
		this.context = context;
	}

	public boolean isOnTime() {
		return onTime;
	}

	public void setOnTime(final boolean onTime) {
		this.onTime = onTime;
	}

	public boolean isSlight() {
		return slight;
	}

	public void setSlight(final boolean slight) {
		this.slight = slight;
	}

	public boolean isLate() {
		return late;
	}

	public void setLate(final boolean late) {
		this.late = late;
	}

	public boolean isExtreme() {
		return extreme;
	}

	public void setExterme(final boolean extreme) {
		this.extreme = extreme;
	}

	@Override
	public boolean filter(final Cursor cursor) {

		if (!isActive()) {
			return true;
		}

		final Capture capture = ToLateDatabaseAdapter.createCapture(cursor);

		final int delay = CaptureUtils.getDelayMinutes(capture);
		final int onTime = getPrefOnTime();
		final int slight = getPrefSlight();
		final int late = getPrefLate();

		if (isOnTime()) {
			if (delay < onTime) {
				return true;
			}
		}

		if (isSlight()) {
			if ((delay >= onTime) && (delay < slight)) {
				return true;
			}
		}

		if (isLate()) {
			if ((delay >= slight) && (delay < late)) {
				return true;
			}
		}

		if (isExtreme()) {
			if (delay >= late) {
				return true;
			}
		}

		return false;
	}

	private int getPrefOnTime() {
		final SharedPreferences settings = context.getSharedPreferences(MainActivity.PREFS_NAME, 0);
		return settings.getInt(MainActivity.PREF_ON_TIME, MainActivity.PREF_ON_TIME_DEFAULT);
	}

	private int getPrefSlight() {
		final SharedPreferences settings = context.getSharedPreferences(MainActivity.PREFS_NAME, 0);
		return settings.getInt(MainActivity.PREF_SLIGHT, MainActivity.PREF_SLIGHT_DEFAULT);
	}

	private int getPrefLate() {
		final SharedPreferences settings = context.getSharedPreferences(MainActivity.PREFS_NAME, 0);
		return settings.getInt(MainActivity.PREF_LATE, MainActivity.PREF_LATE_DEFAULT);
	}

}
