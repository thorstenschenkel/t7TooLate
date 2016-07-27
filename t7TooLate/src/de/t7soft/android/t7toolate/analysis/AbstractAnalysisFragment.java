package de.t7soft.android.t7toolate.analysis;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import de.t7soft.android.t7toolate.IDialogResultTarget;
import de.t7soft.android.t7toolate.MainActivity;
import de.t7soft.android.t7toolate.R;
import de.t7soft.android.t7toolate.database.ToLateDatabaseAdapter;
import de.t7soft.android.t7toolate.model.Capture;

public abstract class AbstractAnalysisFragment extends ListFragment implements IDialogResultTarget {

	private static final String LOG_TAG = AbstractAnalysisFragment.class.getSimpleName();
	private DeleteCaptureDialog deleteDialog;

	abstract protected void updateListAdapter();

	@Override
	public void onActivityCreated(final Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		deleteDialog = new DeleteCaptureDialog(getActivity(), this);
	}

	@Override
	public void onListItemClick(final ListView l, final View v, final int position, final long id) {
		final int itemPosition = position - getListView().getHeaderViewsCount();
		if ((itemPosition >= 0) && (itemPosition < getListAdapter().getCount())) {
			final Capture capture = (Capture) getListAdapter().getItem(itemPosition);
			openCapture(capture);
		}
	}

	private void openCapture(final Capture capture) {
		if (capture != null) {
			final Intent intent = new Intent(getActivity(), ShowCaptureActivity.class);
			intent.putExtra(ShowCaptureActivity.CAPTURE_ID, capture.getId());
			startActivity(intent);
		}
	}

	@Override
	public void onCreateContextMenu(final ContextMenu menu, final View v, final ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		if (v == getListView()) {
			final MenuInflater inflater = getActivity().getMenuInflater();
			inflater.inflate(R.menu.captures_context_menu, menu);
		}
	}

	@Override
	public boolean onContextItemSelected(final MenuItem item) {
		final AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		final Capture capture = (Capture) getListAdapter().getItem(info.position);
		if (capture == null) {
			Log.w(LOG_TAG, "No capture at position " + info.position);
			return false;
		}
		switch (item.getItemId()) {
			case R.id.open_capture_item:
				openCapture(capture);
				return true;
			case R.id.delete_capture_item:
				deleteDialog.show(capture);
				return true;
			default:
				return super.onContextItemSelected(item);
		}
	}

	public ToLateDatabaseAdapter getDbAdapter() {
		return ((MainActivity) getActivity()).getDbAdapter();
	}

	@Override
	public void onDialogResult(final int resultCode) {
		if (resultCode == IDialogResultTarget.DELETED) {
			updateListAdapter();
		}
	}

}
