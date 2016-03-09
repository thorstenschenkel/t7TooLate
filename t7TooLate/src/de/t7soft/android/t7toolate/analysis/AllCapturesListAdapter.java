package de.t7soft.android.t7toolate.analysis;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import de.t7soft.android.t7toolate.R;
import de.t7soft.android.t7toolate.model.Capture;
import de.t7soft.android.t7toolate.model.Connection;
import de.t7soft.android.t7toolate.model.ConnectionTypes;

public class AllCapturesListAdapter extends BaseAdapter {

	private static final java.text.DateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm");
	private static final DateFormat DATE_TIME_FORMAT = new SimpleDateFormat("dd.MM.yyyy HH:mm");

	private final List<Capture> listItems;
	private final LayoutInflater inflater;

	public AllCapturesListAdapter(final Context context, final List<Capture> listItems) {
		this.listItems = listItems;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return listItems.size();
	}

	@Override
	public Capture getItem(final int position) {
		return listItems.get(position);
	}

	@Override
	public long getItemId(final int position) {
		return position;
	}

	@Override
	public View getView(final int position, final View convertView, final ViewGroup parent) {

		View rowView = convertView;
		if (rowView == null) {
			rowView = inflater.inflate(R.layout.capture_row, null);
		}
		final Capture capture = getItem(position);
		updateRow(rowView, capture);

		return rowView;
	}

	private void updateRow(final View rowView, final Capture capture) {

		final Connection connection = capture.getConnection();

		final TextView textViewName = (TextView) rowView.findViewById(R.id.textViewRowCaptureName);
		textViewName.setText(connection.getName());
		final int drawableResId = ConnectionTypes.ICON_IDS[connection.getConnectionType()];
		textViewName.setCompoundDrawablesWithIntrinsicBounds(drawableResId, 0, 0, 0);

		final TextView textViewStartStation = (TextView) rowView.findViewById(R.id.textViewRowCaptureStartStation);
		textViewStartStation.setText(connection.getStartStation());

		final TextView textViewStartTime = (TextView) rowView.findViewById(R.id.textViewRowCaptureStartTime);
		String timeStrg = TIME_FORMAT.format(connection.getStartTime());
		textViewStartTime.setText(timeStrg);

		final TextView textViewEndStation = (TextView) rowView.findViewById(R.id.textViewRowCaptureEndStation);
		textViewEndStation.setText(connection.getEndStation());

		final TextView textViewEndTime = (TextView) rowView.findViewById(R.id.textViewRowCaptureEndTime);
		timeStrg = TIME_FORMAT.format(connection.getEndTime());
		textViewEndTime.setText(timeStrg);

		final TextView textViewDateTime = (TextView) rowView.findViewById(R.id.textViewRowCaptureDateTime);
		final String dateTimeStrg = DATE_TIME_FORMAT.format(capture.getCaptureDateTime());
		textViewDateTime.setText(dateTimeStrg);

		final TextView textViewDelay = (TextView) rowView.findViewById(R.id.textViewRowCaptureDelay);
		String delayText = "";
		final long captrueSeconds = getSeconds(capture.getCaptureDateTime());
		final long endSeconds = getSeconds(connection.getEndTime());
		final long diff = captrueSeconds - endSeconds;
		final long minutes = diff / 60;

		if (minutes > 0) {
			delayText += " +";
			delayText += minutes;
		} else if (minutes < 0) {
			delayText += minutes;
		} else {
			delayText += minutes;
		}
		delayText += " min"; // TODO
		textViewDelay.setText(delayText);

	}

	private long getSeconds(final Date date) {

		final Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);

		long seconds = calendar.get(Calendar.SECOND);
		seconds += calendar.get(Calendar.MINUTE) * 60;
		seconds += calendar.get(Calendar.HOUR_OF_DAY) * 60 * 60;

		return seconds;

	}

}
