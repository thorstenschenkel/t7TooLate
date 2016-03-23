package de.t7soft.android.t7toolate;

import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;

import android.app.Activity;
import android.content.Context;

public class TopExceptionHandler implements UncaughtExceptionHandler {

	private final Thread.UncaughtExceptionHandler defaultExceptionHandler;

	private Activity app = null;

	public TopExceptionHandler(final Activity app) {
		this.defaultExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
		this.app = app;
	}

	@Override
	public void uncaughtException(final Thread thread, final Throwable ex) {

		StackTraceElement[] arr = ex.getStackTrace();
		String report = ex.toString() + "\n\n";
		report += "--------- Stack trace ---------\n\n";
		for (final StackTraceElement stackTraceElement : arr) {
			report += "    " + stackTraceElement.toString() + "\n";
		}
		report += "-------------------------------\n\n";

		// If the exception was thrown in a background thread inside
		// AsyncTask, then the actual exception can be found with getCause
		report += "--------- Cause ---------\n\n";
		final Throwable cause = ex.getCause();
		if (cause != null) {
			report += cause.toString() + "\n\n";
			arr = cause.getStackTrace();
			for (final StackTraceElement stackTraceElement : arr) {
				report += "    " + stackTraceElement.toString() + "\n";
			}
		}
		report += "-------------------------------\n\n";

		try {
			final FileOutputStream trace = app.openFileOutput("stack.trace", Context.MODE_PRIVATE);
			trace.write(report.getBytes());
			trace.close();
		} catch (final IOException ioe) {
			// ...
		}

		defaultExceptionHandler.uncaughtException(thread, ex);
	}

}
