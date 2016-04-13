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
import android.view.ViewGroup;
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

public class CaptueFragment extends Fragment implements ITabFragment {

	protected static final java.text.DateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm");

	private ConnectionPicker numberPickerConnection;
	private TextView textViewPlanedEndValue;
	private TextView textViewCurrentValue;
	private TextView textViewDelayValue;
	private List<Connection> connections;
	private Handler currentHandler;
	private Runnable currentRunnable;

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {

		final View captureView = inflater.inflate(R.layout.capture, container, false);

		textViewPlanedEndValue = (TextView) captureView.findViewById(R.id.textViewPlanedEndValue);
		textViewCurrentValue = (TextView) captureView.findViewById(R.id.textViewCurrentValue);
		textViewDelayValue = (TextView) captureView.findViewById(R.id.textViewDelayValue);

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
			final Connection connection = connections.get(index);
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

		String currentHtml;
		try {
			currentHtml = getString(R.string.capture_current_value);
			currentHtml = MessageFormat.format(currentHtml, nowTimeStrg);
		} catch (final Exception e) {
			currentHtml = getString(R.string.capture_no_current);
		}
		final Spanned currentText = Html.fromHtml(currentHtml);
		textViewCurrentValue.setText(currentText);

		final int index = numberPickerConnection.getValue();
		final long nowSeconds = getSeconds(now);

		final Connection connection = connections.get(index);
		final long endSeconds = getSeconds(connection.getEndTime());
		final long diff = nowSeconds - endSeconds;
		final long minutes = diff / 60;
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
			final Connection connection = connections.get(i);
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

			numberPickerConnection.setMaxValue(connections.size() - 1);
			final List<String> connectionNames = new LinkedList<String>();
			for (final Connection connection : connections) {
				connectionNames.add(connection.getName());
			}
			final String[] connectionNamesArray = connectionNames.toArray(new String[connectionNames.size()]);
			numberPickerConnection.setDisplayedValues(connectionNamesArray);
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

		if (currentHandler == null) {
			currentHandler = new Handler();
			currentRunnable = new Runnable() {
				@Override
				public void run() {
					updateCurrent();
					currentHandler.postDelayed(currentRunnable, 10000);
				}

			};
		}

		startUpdates();
		updatePickerSelection();

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
		currentHandler.postDelayed(currentRunnable, 10000);
	}

	public void stopUpdates() {
		if (currentHandler != null) {
			currentHandler.removeCallbacks(currentRunnable);
		}
	}

	public void onCapture(final View view) {

		Toast.makeText(getActivity(), "debug: #onCapture", Toast.LENGTH_SHORT).show();
		Connection selectedConnection = null;
		final int selIndex = numberPickerConnection.getValue();
		connections = getDbAdapter().getAllConnections();
		if ((connections != null) && (connections.size() > 0) && (selIndex < connections.size())) {
			selectedConnection = connections.get(selIndex);
		}
		if (selectedConnection != null) {
			final Capture capture = new Capture();
			try {
				capture.setConnection(selectedConnection.clone());
				getDbAdapter().insertCapture(capture);
				Toast.makeText(getActivity(), R.string.capture_saved, Toast.LENGTH_SHORT).show();
			} catch (final CloneNotSupportedException e) {
				e.printStackTrace();
			}
		}

	}

}
