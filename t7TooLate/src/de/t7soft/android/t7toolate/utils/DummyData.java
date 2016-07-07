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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import de.t7soft.android.t7toolate.model.Capture;
import de.t7soft.android.t7toolate.model.Connection;
import de.t7soft.android.t7toolate.model.ConnectionTypes;

public class DummyData {

	private static final boolean CREATE_DUMMY_DATA = false;
	private static final java.text.DateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm");

	public Capture capture(final Capture capture) {

		if (CREATE_DUMMY_DATA) {
			final Capture dummy = new Capture();
			dummy.setCaptureDateTime(captureTime(capture.getCaptureDateTime()));
			dummy.setCanceled(capture.isCanceled());
			dummy.setConnection(connection(capture.getConnection()));
			return dummy;
		}
		return capture;

	}

	public Connection connection(final Connection connection) {

		if (CREATE_DUMMY_DATA) {
			final Connection dummy = new Connection();
			dummy.setConnectionType(ConnectionTypes.TRAIN);
			dummy.setName(connectionName(connection.getName()));
			dummy.setStartStation(connectionStation(connection.getStartStation()));
			dummy.setEndStation(connectionStation(connection.getEndStation()));
			dummy.setStartTime(connectionTime(connection.getStartTime()));
			dummy.setEndTime(connectionTime(connection.getEndTime()));
			return dummy;
		}
		return connection;

	}

	public Date captureTime(final Date time) {
		return connectionTime(time);
	}

	public Date connectionTime(final Date time) {
		if (CREATE_DUMMY_DATA) {
			final String timeStrg = TIME_FORMAT.format(time);
			final Calendar calendar = Calendar.getInstance();
			calendar.setTime(time);
			if (timeStrg.startsWith("0")) {
				calendar.add(Calendar.MINUTE, 5);
			} else {
				calendar.add(Calendar.MINUTE, -7);
			}
			return calendar.getTime();
		}
		return time;
	};

	public String connectionName(final String name) {
		if (CREATE_DUMMY_DATA) {
			if (name.contains("4554")) {
				return "RE 4711";
			}
			if (name.contains("4556")) {
				return "RE 4713";
			}
			if (name.contains("4573")) {
				return "RE 0814";
			}
			if (name.contains("4575")) {
				return "RE 0816";
			}
			if (name.contains("4577")) {
				return "RE 08148";
			}
		}
		return name;
	};

	public String connectionStation(final String station) {
		if (CREATE_DUMMY_DATA) {
			if (station.startsWith("Lampertheim")) {
				return "Gründorf";
			}
			if (station.startsWith("Mannheim")) {
				return "Ahornheim";
			}
			if (station.startsWith("Frankfurt")) {
				return "Musterstadt Hbf";
			}
		}
		return station;
	};

}
