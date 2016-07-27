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

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import de.t7soft.android.t7toolate.MainActivity;
import de.t7soft.android.t7toolate.R;
import de.t7soft.android.t7toolate.database.ToLateDatabaseAdapter;

public class AllListFragment extends AbstractAnalysisFragment {

	private View allView;
	private AllCapturesCursorAdapter capturesAdapter;

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {

		allView = inflater.inflate(R.layout.fragment_all, container, false);

		capturesAdapter = new AllCapturesCursorAdapter(getActivity(), getCursor());
		setListAdapter(capturesAdapter);

		return allView;
	}

	@Override
	public void onActivityCreated(final Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		registerForContextMenu(getListView());
	}

	private Cursor getCursor() {
		return getDbAdapter().getAllCapturesCursor();
	}

	@Override
	protected void updateListAdapter() {
		capturesAdapter.changeCursor(getCursor());
	}

	@Override
	public void onResume() {
		updateListAdapter();
		super.onResume();
	}

	@Override
	public ToLateDatabaseAdapter getDbAdapter() {
		return ((MainActivity) getActivity()).getDbAdapter();
	}

}
