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
package de.t7soft.android.t7toolate;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.GregorianCalendar;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

public class TopExceptionHandler implements UncaughtExceptionHandler {

	private static final String FILE_NAME = "stack.trace";

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
		report += "DATE: " + GregorianCalendar.getInstance().getTime().toString() + "\n";
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
			final FileOutputStream fos = app.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
			fos.write(report.getBytes());
			fos.close();
		} catch (final IOException ioe) {
			ioe.printStackTrace();
			Log.e(this.getClass().getSimpleName(), "Can't write stack trace file!", ioe);
		}

		defaultExceptionHandler.uncaughtException(thread, ex);
	}

	public void logStackTrace() {

		try {
			final FileInputStream fis = app.openFileInput(FILE_NAME);
			final BufferedReader br = new BufferedReader(new InputStreamReader(fis));
			String line;
			while ((line = br.readLine()) != null) {
				Log.e(FILE_NAME, line);
			}
			br.close();
			fis.close();
		} catch (final FileNotFoundException fnfe) {
			Log.d(this.getClass().getSimpleName(), "No stack trace file.");
		} catch (final IOException ioe) {
			ioe.printStackTrace();
			Log.e(this.getClass().getSimpleName(), "Can't read stack trace file!", ioe);
		}

	}
}
