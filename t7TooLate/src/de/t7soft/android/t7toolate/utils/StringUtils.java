package de.t7soft.android.t7toolate.utils;

public class StringUtils {

	private StringUtils() {
		// utility class
	}

	public static boolean isEmpty(final CharSequence cs) {
		return cs == null || cs.length() == 0;
	}

}
