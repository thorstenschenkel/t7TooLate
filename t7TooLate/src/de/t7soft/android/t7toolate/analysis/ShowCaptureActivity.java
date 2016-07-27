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
package de.t7soft.android.t7toolate.analysis;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import de.t7soft.android.t7toolate.IDialogResultTarget;
import de.t7soft.android.t7toolate.R;
import de.t7soft.android.t7toolate.database.ToLateDatabaseAdapter;
import de.t7soft.android.t7toolate.model.Capture;
import de.t7soft.android.t7toolate.model.ConnectionTypes;
import de.t7soft.android.t7toolate.utils.CaptureUtils;

public class ShowCaptureActivity extends Activity implements IDialogResultTarget {

	public static final String CAPTURE_ID = "captureId";
	@SuppressLint("SimpleDateFormat")
	private static final java.text.DateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm");
	@SuppressLint("SimpleDateFormat")
	private static final DateFormat DATE_TIME_FORMAT = new SimpleDateFormat("EEE, dd.MM.yyyy HH:mm");
	private Capture capture;
	private TextView nameValueTextView;
	private ImageView connectionTypeImageView;
	private TextView startStationValueTextView;
	private TextView startTimeValueTextView;
	private TextView endStationValueTextView;
	private TextView endTimeValueTextView;
	private EditText editTextCaptureCommentValue;
	private DeleteCaptureDialog deleteDialog;
	private ToLateDatabaseAdapter dbAdapter;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_capture);

		if (dbAdapter == null) {
			dbAdapter = new ToLateDatabaseAdapter(this);
		}

		deleteDialog = new DeleteCaptureDialog(this, this);

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
			case R.id.action_delete:
				deleteDialog.show(capture);
				return true;
		}
		return super.onOptionsItemSelected(item);
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
		if (capture.isCanceled()) {
			textViewDelay.setText(R.string.canceled);
			textViewDelay.setTypeface(null, Typeface.NORMAL);
		}

		final View viewTopBorder = findViewById(R.id.viewTopBorder);
		viewTopBorder.setBackgroundColor(getResources().getColor(borderColor));

		editTextCaptureCommentValue = (EditText) findViewById(R.id.editTextCaptureCommentValue);
		editTextCaptureCommentValue.setText(capture.getComment());

		final Button okButton = (Button) findViewById(R.id.buttonCaptureOk);
		okButton.setOnClickListener(new android.view.View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				onOk(v);
			}
		});

	}

	private void onOk(final View view) {
		final String comment = editTextCaptureCommentValue.getText().toString();
		if ((capture != null) && !capture.getComment().equals(comment)) {
			capture.setComment(comment);
			dbAdapter.updateCapture(capture);
			Toast.makeText(this, R.string.capture_comment_saved, Toast.LENGTH_SHORT).show();
		}
		finish();
	}

	@Override
	public void onDialogResult(final int resultCode) {
		if (resultCode == IDialogResultTarget.DELETED) {
			finish();
		}
	}

}
