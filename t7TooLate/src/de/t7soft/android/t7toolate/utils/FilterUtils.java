package de.t7soft.android.t7toolate.utils;

import java.util.Date;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import de.t7soft.android.t7toolate.MainActivity;
import de.t7soft.android.t7toolate.model.AbstractFilter;
import de.t7soft.android.t7toolate.model.DelayFilter;
import de.t7soft.android.t7toolate.model.PeriodFilter;

public class FilterUtils {

	private static final String PREF_PERIOD_ACTIVE = "periodActive";
	private static final String PREF_PERIOD_FROM = "periodFrom";
	private static final String PREF_PERIOD_TO = "periodTo";
	private static final String PREF_DELAY_ACTIVE = "delayActive";

	private FilterUtils() {
		// utility class
	}

	public static void storePeriodFilter(final Context context, final PeriodFilter filter) {

		final SharedPreferences preferences = context.getSharedPreferences(MainActivity.PREFS_NAME, 0);
		final Editor prefEdit = preferences.edit();
		prefEdit.putBoolean(PREF_PERIOD_ACTIVE, filter.isActive());
		if (filter.getFrom() != null) {
			prefEdit.putLong(PREF_PERIOD_FROM, filter.getFrom().getTime());
		} else {
			prefEdit.putLong(PREF_PERIOD_FROM, Long.MAX_VALUE);
		}
		if (filter.getTo() != null) {
			prefEdit.putLong(PREF_PERIOD_TO, filter.getTo().getTime());
		} else {
			prefEdit.putLong(PREF_PERIOD_TO, Long.MAX_VALUE);
		}
		prefEdit.commit();

	}

	public static PeriodFilter createPeriodFilter(final Context context) {

		final SharedPreferences preferences = context.getSharedPreferences(MainActivity.PREFS_NAME, 0);
		final PeriodFilter filter = new PeriodFilter();
		filter.setActive(preferences.getBoolean(PREF_PERIOD_ACTIVE, false));
		long time = preferences.getLong(PREF_PERIOD_FROM, Long.MAX_VALUE);
		if (time == Long.MAX_VALUE) {
			filter.setFrom(null);
		} else {
			filter.setFrom(new Date(time));
		}
		time = preferences.getLong(PREF_PERIOD_TO, Long.MAX_VALUE);
		if (time == Long.MAX_VALUE) {
			filter.setTo(null);
		} else {
			filter.setTo(new Date(time));
		}
		return filter;

	}

	public static void storeDelayFilter(final Context context, final DelayFilter filter) {

		final SharedPreferences preferences = context.getSharedPreferences(MainActivity.PREFS_NAME, 0);
		final Editor prefEdit = preferences.edit();
		prefEdit.putBoolean(PREF_DELAY_ACTIVE, filter.isActive());
		prefEdit.commit();

	}

	public static DelayFilter createDelayFilter(final Context context) {

		final SharedPreferences preferences = context.getSharedPreferences(MainActivity.PREFS_NAME, 0);
		final DelayFilter filter = new DelayFilter();
		filter.setActive(preferences.getBoolean(PREF_DELAY_ACTIVE, false));
		return filter;

	}

	public static boolean isOneFilterActive(final Context context) {

		AbstractFilter filter = createPeriodFilter(context);
		if (filter.isActive()) {
			return true;
		}
		filter = createDelayFilter(context);
		if (filter.isActive()) {
			return true;
		}

		return false;

	}

}