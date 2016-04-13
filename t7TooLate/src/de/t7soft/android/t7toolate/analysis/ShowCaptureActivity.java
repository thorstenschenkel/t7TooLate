package de.t7soft.android.t7toolate.analysis;

import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import de.t7soft.android.t7toolate.R;
import de.t7soft.android.t7toolate.database.ToLateDatabaseAdapter;
import de.t7soft.android.t7toolate.model.Capture;
import de.t7soft.android.t7toolate.model.ConnectionTypes;
import de.t7soft.android.t7toolate.utils.CaptureUtils;

public class ShowCaptureActivity extends Activity {

	public static final String CAPTURE_ID = "captureId";
	private static final java.text.DateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm");
	private static final DateFormat DATE_TIME_FORMAT = new SimpleDateFormat("EEE, dd.MM.yyyy HH:mm");
	private Capture capture;
	private TextView nameValueTextView;
	private ImageView connectionTypeImageView;
	private TextView startStationValueTextView;
	private TextView startTimeValueTextView;
	private TextView endStationValueTextView;
	private TextView endTimeValueTextView;
	private TextView commentValueTextView;
	private ToLateDatabaseAdapter dbAdapter;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_capture);

		if (dbAdapter == null) {
			dbAdapter = new ToLateDatabaseAdapter(this);
		}

	}

	@Override
	protected void onResume() {
		dbAdapter.open();
		capture = createCaptue();
		initActivity();
		super.onResume();
	}

	@Override
	protected void onPause() {
		dbAdapter.close();
		super.onPause();
	}

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		getMenuInflater().inflate(R.menu.show_capture, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		switch (item.getItemId()) {
		// case R.id.action_edit:
		// editConnection();
		// return true;
			case R.id.action_delete:
				deleteCapture();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	// private void editConnection() {
	// final Intent intent = new Intent(this, EditConnectionActivity.class);
	// intent.putExtra(EditConnectionActivity.CONNECTION_ID, capture.getId());
	// startActivity(intent);
	// }

	private void deleteCapture() {
		String deleteMessage = getString(R.string.capture_delete_msg_text);
		final String dateTimeStrg = DATE_TIME_FORMAT.format(capture.getCaptureDateTime());
		deleteMessage = MessageFormat.format(deleteMessage, dateTimeStrg);
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(deleteMessage) //
				// .setIcon(android.R.drawable.stat_notify_error)
				.setTitle(R.string.capture_delete_msg_title);
		builder.setPositiveButton(getString(R.string.yes), new OnClickListener() {
			@Override
			public void onClick(final DialogInterface dialog, final int which) {
				dbAdapter.deleteCapture(capture);
				dialog.dismiss();
				finish();
			}
		});
		builder.setNegativeButton(getString(R.string.no), new OnClickListener() {
			@Override
			public void onClick(final DialogInterface dialog, final int which) {
				dialog.dismiss();
			}
		});
		builder.create();
		builder.show();
	}

	protected Capture createCaptue() {
		final String captureId = getIntent().getExtras().getString(CAPTURE_ID);
		return dbAdapter.getCapture(captureId);
	}

	private void initActivity() {

		nameValueTextView = (TextView) findViewById(R.id.textViewConnectionNameValue);
		nameValueTextView.setText(capture.getConnection().getName());

		connectionTypeImageView = (ImageView) findViewById(R.id.imageViewConnectionType);
		connectionTypeImageView.setImageResource(ConnectionTypes.ICON_IDS[capture.getConnection().getConnectionType()]);

		startStationValueTextView = (TextView) findViewById(R.id.textViewConnectionStartStationValue);
		startStationValueTextView.setText(capture.getConnection().getStartStation());

		startTimeValueTextView = (TextView) findViewById(R.id.textViewConnectionStartTimeValue);
		startTimeValueTextView.setText(TIME_FORMAT.format(capture.getConnection().getStartTime()));

		endStationValueTextView = (TextView) findViewById(R.id.textViewConnectionEndStationValue);
		endStationValueTextView.setText(capture.getConnection().getEndStation());

		endTimeValueTextView = (TextView) findViewById(R.id.textViewConnectionEndTimeValue);
		endTimeValueTextView.setText(TIME_FORMAT.format(capture.getConnection().getEndTime()));

		final TextView textViewDateTime = (TextView) findViewById(R.id.textViewCaptureDateTime);
		final String dateTimeStrg = DATE_TIME_FORMAT.format(capture.getCaptureDateTime());
		textViewDateTime.setText(dateTimeStrg);

		final TextView textViewDelay = (TextView) findViewById(R.id.textViewCaptureDelay);
		final int borderColor = CaptureUtils.fillTextViewDelay(capture, textViewDelay);

		final View viewTopBorder = findViewById(R.id.viewTopBorder);
		viewTopBorder.setBackgroundColor(getResources().getColor(borderColor));

		// TODO comment

	}

	public void onCancel(final View view) {
		finish();
	}

}
