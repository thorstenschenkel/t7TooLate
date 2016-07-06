package de.t7soft.android.t7toolate.analysis;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;
import de.t7soft.android.t7toolate.R;
import de.t7soft.android.t7toolate.database.ToLateDatabaseAdapter;
import de.t7soft.android.t7toolate.model.Capture;
import de.t7soft.android.t7toolate.model.Connection;
import de.t7soft.android.t7toolate.model.ConnectionTypes;
import de.t7soft.android.t7toolate.utils.CaptureUtils;
import de.t7soft.android.t7toolate.utils.DummyData;
import de.t7soft.android.t7toolate.utils.StringUtils;

public class WeekMonthCapturesCursorAdapter extends CursorAdapter {

	private static final java.text.DateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm");
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("EEE, dd.MM.yyyy");

	private final Context context;

	public WeekMonthCapturesCursorAdapter(final Context context, final Cursor cursor) {
		super(context, cursor, 0);
		this.context = context;
	}

	@Override
	public View newView(final Context context, final Cursor cursor, final ViewGroup parent) {
		return LayoutInflater.from(context).inflate(R.layout.day_capture_row, null);
	}

	@Override
	public void bindView(final View view, final Context context, final Cursor cursor) {
		final Capture rowCapture = ToLateDatabaseAdapter.createCapture(cursor);
		final Capture capture = new DummyData().capture(rowCapture);
		boolean showDay = true;
		if (cursor.move(-1)) {
			final Capture previousCapture = ToLateDatabaseAdapter.createCapture(cursor);
			final Calendar calendar = Calendar.getInstance();
			calendar.setTime(previousCapture.getCaptureDateTime());
			final int previousWeekDay = calendar.get(Calendar.DAY_OF_WEEK);
			calendar.setTime(capture.getCaptureDateTime());
			final int weekDay = calendar.get(Calendar.DAY_OF_WEEK);
			showDay = previousWeekDay != weekDay;
			cursor.move(1);
		}
		updateRow(view, capture, showDay);
	}

	private void updateRow(final View rowView, final Capture capture, final boolean showDay) {

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

		final View viewTopBorder = rowView.findViewById(R.id.viewTopBorder);
		final View dayView = rowView.findViewById(R.id.viewCaptureDay);
		if (showDay) {
			final TextView textViewDate = (TextView) rowView.findViewById(R.id.textViewRowCaptureDate);
			final String dateTimeStrg = DATE_FORMAT.format(capture.getCaptureDateTime());
			textViewDate.setText(dateTimeStrg);
			dayView.setVisibility(View.VISIBLE);
			viewTopBorder.setVisibility(View.VISIBLE);
		} else {
			dayView.setVisibility(View.GONE);
			viewTopBorder.setVisibility(View.GONE);
		}

		final TextView textViewTime = (TextView) rowView.findViewById(R.id.textViewRowCaptureTime);
		timeStrg = TIME_FORMAT.format(capture.getCaptureDateTime());
		textViewTime.setText(timeStrg);

		final TextView textViewDelay = (TextView) rowView.findViewById(R.id.textViewRowCaptureDelay);
		final int borderColor = CaptureUtils.fillTextViewDelay(capture, textViewDelay);
		if (capture.isCanceled()) {
			textViewDelay.setText(R.string.canceled);
			textViewDelay.setTypeface(null, Typeface.NORMAL);
		}

		final TextView textViewRowComment = (TextView) rowView.findViewById(R.id.textViewRowComment);
		if (StringUtils.isEmpty(capture.getComment())) {
			textViewRowComment.setVisibility(View.GONE);
		} else {
			textViewRowComment.setVisibility(View.VISIBLE);
			textViewRowComment.setText(capture.getComment());
		}

		final View viewLeftBorder = rowView.findViewById(R.id.viewLeftBorder);
		viewLeftBorder.setBackgroundColor(rowView.getResources().getColor(borderColor));

	}

	@Override
	public Capture getItem(final int position) {
		final Cursor cursor = (Cursor) super.getItem(position);
		if (cursor == null) {
			return null;
		}
		final Capture capture = ToLateDatabaseAdapter.createCapture(cursor);
		return capture;
	}

	public Context getContext() {
		return context;
	}

}
