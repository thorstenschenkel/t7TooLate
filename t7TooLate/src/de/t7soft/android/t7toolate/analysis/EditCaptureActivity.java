package de.t7soft.android.t7toolate.analysis;

import android.app.Activity;
import android.os.Bundle;
import de.t7soft.android.t7toolate.R;
import de.t7soft.android.t7toolate.database.ToLateDatabaseAdapter;
import de.t7soft.android.t7toolate.model.Capture;

public class EditCaptureActivity extends Activity {

	public static final String CAPTURE_ID = "captureId";

	private ToLateDatabaseAdapter dbAdapter;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_capture);

		if (dbAdapter == null) {
			dbAdapter = new ToLateDatabaseAdapter(this);
		}

	}

	@Override
	protected void onResume() {
		dbAdapter.open();
		createCaptue();
		initActivity();
		super.onResume();
	}

	@Override
	protected void onPause() {
		dbAdapter.close();
		super.onPause();
	}

	private Capture createCaptue() {
		final String captureId = getIntent().getExtras().getString(ShowCaptureActivity.CAPTURE_ID);
		return dbAdapter.getCapture(captureId);
	}

	private void initActivity() {

	}

}
