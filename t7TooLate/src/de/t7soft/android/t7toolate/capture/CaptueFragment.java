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
package de.t7soft.android.t7toolate.capture;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.NumberPicker.OnValueChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import de.t7soft.android.t7toolate.ITabFragment;
import de.t7soft.android.t7toolate.MainActivity;
import de.t7soft.android.t7toolate.R;
import de.t7soft.android.t7toolate.database.ToLateDatabaseAdapter;
import de.t7soft.android.t7toolate.model.Capture;
import de.t7soft.android.t7toolate.model.Connection;
import de.t7soft.android.t7toolate.utils.CaptureUtils;
import de.t7soft.android.t7toolate.utils.DummyData;

public class CaptueFragment extends Fragment implements ITabFragment {

	protected static final java.text.DateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm");

	private ConnectionPicker numberPickerConnection;
	private TextView textViewPlanedEndValue;
	private TextView textViewCurrentValue;
	private TextView textViewDelayValue;
	private List<Connection> connections;
	private Handler currentHandler;
	private Runnable currentRunnable;
	private Button buttonCanceled;
	private Button buttonCapture;

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {

		final View captureView = inflater.inflate(R.layout.capture, container, false);

		textViewPlanedEndValue = (TextView) captureView.findViewById(R.id.textViewPlanedEndValue);
		textViewCurrentValue = (TextView) captureView.findViewById(R.id.textViewCurrentValue);
		textViewDelayValue = (TextView) captureView.findViewById(R.id.textViewDelayValue);

		buttonCanceled = (Button) captureView.findViewById(R.id.buttonCanceled);
		buttonCanceled.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				onCanceled(v);
			}
		});
		buttonCapture = (Button) captureView.findViewById(R.id.buttonCapture);
		buttonCapture.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				onCapture(v);
			}
		});

		numberPickerConnection = (ConnectionPicker) captureView.findViewById(R.id.numberPickerConnection);
		numberPickerConnection.setOnValueChangedListener(new OnValueChangeListener() {
			@Override
			public void onValueChange(final NumberPicker picker, final int oldVal, final int newVal) {
				updatePlanedEnd(newVal);
				updateCurrent();
			}

		});

		return captureView;
	}

	private void updatePlanedEnd(final int index) {

		String connectionHtml;
		try {
			final Connection selectedConnection = connections.get(index);
			final Connection connection = new DummyData().connection(selectedConnection);
			connectionHtml = getString(R.string.capture_planed_end_value);
			final String endTimeStrg = TIME_FORMAT.format(connection.getEndTime());
			connectionHtml = MessageFormat.format(connectionHtml, endTimeStrg, connection.getEndStation());
		} catch (final Exception e) {
			connectionHtml = getString(R.string.capture_no_connection);
		}

		final Spanned connectionText = Html.fromHtml(connectionHtml);
		textViewPlanedEndValue.setText(connectionText);

	}

	private long getSeconds(final Date date) {

		final Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);

		long seconds = calendar.get(Calendar.SECOND);
		seconds += calendar.get(Calendar.MINUTE) * 60;
		seconds += calendar.get(Calendar.HOUR_OF_DAY) * 60 * 60;

		return seconds;

	}

	private void updateCurrent() {

		final Date now = Calendar.getInstance().getTime();
		final String nowTimeStrg = TIME_FORMAT.format(now);

		String currentHtml = "<![CDATA[<i>--:--<i>]]>";
		if (isAdded()) {
			try {
				currentHtml = getString(R.string.capture_current_value);
				currentHtml = MessageFormat.format(currentHtml, nowTimeStrg);
			} catch (final Exception e) {
				currentHtml = getString(R.string.capture_no_current);
			}
		}
		final Spanned currentText = Html.fromHtml(currentHtml);
		if (textViewCurrentValue == null) {
			Log.e(this.getClass().getSimpleName(), "can't update current, textViewCurrentValue is null");
			return;
		}
		textViewCurrentValue.setText(currentText);

		if (numberPickerConnection == null) {
			Log.e(this.getClass().getSimpleName(), "can't update current, numberPickerConnection is null");
			return;
		}
		final int index = numberPickerConnection.getValue();
		long minutes = 0;
		if (index < connections.size()) {
			final Connection nextConnection = connections.get(index);
			final Connection connection = new DummyData().connection(nextConnection);
			final long endSeconds = getSeconds(connection.getEndTime());
			final long diff = getSeconds(now) - endSeconds;
			minutes = diff / 60;
		}
		if (minutes > 0) {
			CaptureUtils.fillTextViewDelay((int) minutes, textViewDelayValue);
		} else {
			CaptureUtils.fillTextViewDelay((int) minutes, textViewDelayValue);
			textViewDelayValue.setText("");
		}

	}

	private void updatePickerSelection() {

		if (connections == null) {
			return;
		}

		final Date now = Calendar.getInstance().getTime();
		final long nowSeconds = getSeconds(now);
		long minDiff = Long.MAX_VALUE;
		int minIndex = -1;

		for (int i = 0; i < connections.size(); i++) {
			final Connection nextConnection = connections.get(i);
			final Connection connection = new DummyData().connection(nextConnection);
			final long endSeconds = getSeconds(connection.getEndTime());
			final long diff = nowSeconds - endSeconds;
			if (Math.abs(diff) < minDiff) {
				minDiff = Math.abs(diff);
				minIndex = i;
			}
		}

		if (minIndex >= 0) {
			setPickerValueInternal(minIndex, false);
		}

	}

	private void setPickerValueInternal(final int value, final boolean updateAllways) {
		if (value != numberPickerConnection.getValue()) {
			numberPickerConnection.setValueInternal(value, false);
			updatePlanedEnd(value);
			updateCurrent();
		}
	}

	private void updatePicker() {

		numberPickerConnection.setWrapSelectorWheel(false);
		numberPickerConnection.setMinValue(0);

		connections = getDbAdapter().getAllConnections();
		if ((connections != null) && (connections.size() > 0)) {

			Collections.sort(connections, new Comparator<Connection>() {
				@Override
				public int compare(final Connection connection1, final Connection connection2) {
					final long sec1 = getSeconds(connection1.getEndTime());
					final long sec2 = getSeconds(connection2.getEndTime());
					if (sec1 > sec2) {
						return 1;
					}
					if (sec1 < sec2) {
						return -1;
					}
					return 0;
				}
			});

			final List<String> connectionNames = new LinkedList<String>();
			for (final Connection nextConnection : connections) {
				final Connection connection = new DummyData().connection(nextConnection);
				connectionNames.add(connection.getName());
			}
			final String[] connectionNamesArray = connectionNames.toArray(new String[connectionNames.size()]);
			numberPickerConnection.setDisplayedValues(connectionNamesArray);
			numberPickerConnection.setConnections(connections);
			numberPickerConnection.setMaxValue(connections.size() - 1);
			setPickerValueInternal(0, false);
			updatePlanedEnd(0);
			updateCurrent();
		} else {
			// TODO
			updateCurrent(); // ?
		}

	}

	@Override
	public CharSequence getTabTitle() {
		return getResources().getString(R.string.capture_tab_tilte);
	}

	@Override
	public void onResume() {
		stopUpdates();
		updatePicker();
		super.onResume();
		startUpdates();
		updatePickerSelection();
		updateButtons();
	}

	private void updateButtons() {
		final boolean enabled = (connections != null) && (connections.size() > 0);
		buttonCanceled.setEnabled(enabled);
		buttonCapture.setEnabled(enabled);
	}

	@Override
	public void setUserVisibleHint(final boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if (isVisibleToUser) {
			updatePickerSelection();
		}
		Log.d(this.getClass().getSimpleName(), "Fragment is visible: " + isVisibleToUser);
	}

	public ToLateDatabaseAdapter getDbAdapter() {
		return ((MainActivity) getActivity()).getDbAdapter();
	}

	public void startUpdates() {
		if (currentHandler == null) {
			currentHandler = new Handler();
			currentRunnable = new Runnable() {
				@Override
				public void run() {
					updateCurrent();
					currentHandler.postDelayed(currentRunnable, 1000);
				}

			};
		}
		currentHandler.postDelayed(currentRunnable, 1000);
	}

	public void stopUpdates() {
		if (currentHandler != null) {
			currentHandler.removeCallbacks(currentRunnable);
		}
	}

	public void onCapture(final View view) {

		final Capture capture = createCapture();
		if (capture != null) {
			getDbAdapter().insertCapture(capture);
			Toast.makeText(getActivity(), R.string.capture_saved, Toast.LENGTH_SHORT).show();
		}

	}

	public void onCanceled(final View view) {

		final Capture capture = createCapture();
		if (capture != null) {
			capture.setCanceled(true);
			getDbAdapter().insertCapture(capture);
			Toast.makeText(getActivity(), R.string.capture_saved, Toast.LENGTH_SHORT).show();
		}

	}

	private Capture createCapture() {

		Connection selectedConnection = null;
		final int selIndex = numberPickerConnection.getValue();
		connections = getDbAdapter().getAllConnections();
		if ((connections != null) && (connections.size() > 0) && (selIndex < connections.size())) {
			selectedConnection = connections.get(selIndex);
		}

		if (selectedConnection != null) {
			try {
				final Capture capture = new Capture();
				capture.setConnection(selectedConnection.clone());
				return capture;
			} catch (final CloneNotSupportedException e) {
				e.printStackTrace();
			}
		}

		return null;

	}

}
