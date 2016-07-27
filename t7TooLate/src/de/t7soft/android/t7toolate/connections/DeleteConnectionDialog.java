package de.t7soft.android.t7toolate.connections;

import java.text.MessageFormat;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import de.t7soft.android.t7toolate.IDialogResultTarget;
import de.t7soft.android.t7toolate.R;
import de.t7soft.android.t7toolate.database.ToLateDatabaseAdapter;
import de.t7soft.android.t7toolate.model.Connection;

public class DeleteConnectionDialog {

	private final Context context;
	private ToLateDatabaseAdapter dbAdapter;
	private final IDialogResultTarget target;

	public DeleteConnectionDialog(final Context context, final IDialogResultTarget traget) {
		this.context = context;
		this.target = traget;
		if (dbAdapter == null) {
			dbAdapter = new ToLateDatabaseAdapter(context);
		}
	}

	public void show(final Connection connection) {
		String deleteMessage = context.getString(R.string.connection_delete_msg_text);
		deleteMessage = MessageFormat.format(deleteMessage, connection.getName());
		final AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage(deleteMessage) //
				// .setIcon(android.R.drawable.stat_notify_error)
				.setTitle(R.string.connection_delete_msg_title);
		builder.setPositiveButton(context.getString(R.string.yes), new OnClickListener() {
			@Override
			public void onClick(final DialogInterface dialog, final int which) {
				dbAdapter.deleteConnection(connection);
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
