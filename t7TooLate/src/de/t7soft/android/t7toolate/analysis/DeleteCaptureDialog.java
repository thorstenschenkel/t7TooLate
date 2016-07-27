package de.t7soft.android.t7toolate.analysis;

import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import de.t7soft.android.t7toolate.IDialogResultTarget;
import de.t7soft.android.t7toolate.R;
import de.t7soft.android.t7toolate.database.ToLateDatabaseAdapter;
import de.t7soft.android.t7toolate.model.Capture;

public class DeleteCaptureDialog {

	@SuppressLint("SimpleDateFormat")
	private static final DateFormat DATE_TIME_FORMAT = new SimpleDateFormat("EEE, dd.MM.yyyy HH:mm");

	private final Context context;
	private ToLateDatabaseAdapter dbAdapter;
	private final IDialogResultTarget target;

	public DeleteCaptureDialog(final Context context, final IDialogResultTarget traget) {
		this.context = context;
		this.target = traget;
		if (dbAdapter == null) {
			dbAdapter = new ToLateDatabaseAdapter(context);
		}
	}

	public void show(final Capture capture) {
		String deleteMessage = context.getString(R.string.capture_delete_msg_text);
		String dateTimeStrg;
		if (capture.isCanceled()) {
			dateTimeStrg = context.getString(R.string.analysis_delay_canceled);
		} else {
			dateTimeStrg = DATE_TIME_FORMAT.format(capture.getCaptureDateTime());
		}
		deleteMessage = MessageFormat.format(deleteMessage, dateTimeStrg);
		final AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage(deleteMessage) //
				// .setIcon(android.R.drawable.stat_notify_error)
				.setTitle(R.string.capture_delete_msg_title);
		builder.setPositiveButton(context.getString(R.string.yes), new OnClickListener() {
			@Override
			public void onClick(final DialogInterface dialog, final int which) {
				dbAdapter.deleteCapture(capture);
				dialog.dismiss();
				passBack(IDialogResultTarget.DELETED);
			}
		});
		builder.setNegativeButton(context.getString(R.string.no), new OnClickListener() {
			@Override
			public void onClick(final DialogInterface dialog, final int which) {
				dialog.dismiss();
				passBack(IDialogResultTarget.CANCELED);
			}
		});
		builder.create();
		builder.show();
	}

	private void passBack(final int code) {
		if (target != null) {
			target.onDialogResult(code);
		}
	}

}
