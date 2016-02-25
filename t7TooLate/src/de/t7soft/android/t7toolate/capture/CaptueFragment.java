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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;
import android.widget.NumberPicker.OnValueChangeListener;
import android.widget.TextView;
import de.t7soft.android.t7toolate.ITabFragment;
import de.t7soft.android.t7toolate.MainActivity;
import de.t7soft.android.t7toolate.R;
import de.t7soft.android.t7toolate.database.ToLateDatabaseAdapter;
import de.t7soft.android.t7toolate.model.Connection;

public class CaptueFragment extends Fragment implements ITabFragment {

	protected static final java.text.DateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm");

	private NumberPicker numberPickerConnection;
	private TextView textViewPlanedEndValue;
	private TextView textViewCurrentValue;
	private List<Connection> connections;
	private Handler currentHandler;
	private Runnable currentRunnable;

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		final View captureView = inflater.inflate(R.layout.capture, container, false);

		textViewPlanedEndValue = (TextView) captureView.findViewById(R.id.textViewPlanedEndValue);
		textViewCurrentValue = (TextView) captureView.findViewById(R.id.textViewCurrentValue);

		numberPickerConnection = (NumberPicker) captureView.findViewById(R.id.numberPickerConnection);
		numberPickerConnection.setOnValueChangedListener(new OnValueChangeListener() {
			@Override
			public void onValueChange(final NumberPicker picker, final int oldVal, final int newVal) {
				updatePlanedEnd(newVal);
				updateCurrent();
			}

		});

		updatePicker();

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

		final int index = numberPickerConnection.getValue();
		final Date now = Calendar.getInstance().getTime();
		final String nowTimeStrg = TIME_FORMAT.format(now);
		final long nowSeconds = getSeconds(now);

		String currentHtml;
		try {
			final Connection connection = connections.get(index);
			final long endSeconds = getSeconds(connection.getEndTime());
			final long diff = nowSeconds - endSeconds;
			final long minutes = diff / 60;
			if (minutes > 0) {
				currentHtml = getString(R.string.capture_current_late_value);
				currentHtml = MessageFormat.format(currentHtml, nowTimeStrg, minutes);
			} else {
				currentHtml = getString(R.string.capture_current_value);
				currentHtml = MessageFormat.format(currentHtml, nowTimeStrg);
			}
		} catch (final Exception e) {
			currentHtml = getString(R.string.capture_current_value);
			currentHtml = MessageFormat.format(currentHtml, nowTimeStrg);
		}

		final Spanned connectionText = Html.fromHtml(currentHtml);
		textViewCurrentValue.setText(connectionText);

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
			numberPickerConnection.setValue(0);
			updatePlanedEnd(0);
		} else {
			// TODO
		}
		updateCurrent();

	}

	@Override
	public CharSequence getTabTitle() {
		return getResources().getString(R.string.capture_tab_tilte);
	}

	@Override
	public void onResume() {
		updatePicker();
		super.onResume();

		if (currentHandler == null) {
			currentHandler = new Handler();
		}
		if (currentRunnable == null) {
			currentRunnable = new Runnable() {
				@Override
				public void run() {
					updateCurrent();
					currentHandler.postDelayed(currentRunnable, 10000);
				}

			};
		}
		currentHandler.postDelayed(currentRunnable, 10000);
	}

	@Override
	public void onPause() {
		super.onPause();
		currentHandler.removeCallbacks(currentRunnable);
	}

	public ToLateDatabaseAdapter getDbAdapter() {
		return ((MainActivity) getActivity()).getDbAdapter();
	}

}
