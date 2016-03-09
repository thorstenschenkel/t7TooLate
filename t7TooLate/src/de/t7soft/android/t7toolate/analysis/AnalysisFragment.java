package de.t7soft.android.t7toolate.analysis;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import de.t7soft.android.t7toolate.ITabFragment;
import de.t7soft.android.t7toolate.R;

public class AnalysisFragment extends Fragment implements ITabFragment {

	private View analysisView;
	private FragmentManager fragmentManager;

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {

		analysisView = inflater.inflate(R.layout.analysis, container, false);

		fragmentManager = getFragmentManager();
		final FragmentTransaction transaction = fragmentManager.beginTransaction();
		transaction.add(R.id.fragment, new AllListFragment());
		transaction.commit();

		final Spinner spinnerAnalysisType = (Spinner) analysisView.findViewById(R.id.spinnerAnalysisType);
		final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
				R.array.analysis_types_array, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerAnalysisType.setAdapter(adapter);
		spinnerAnalysisType.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(final AdapterView<?> parent, final View view, final int position, final long id) {

				final FragmentTransaction transaction = fragmentManager.beginTransaction();
				Fragment newFragment;
				switch (position) {
					case 0:
						newFragment = new AllListFragment();
						break;
					case 1:
						newFragment = new WeekFragment();
						break;
					case 2:
						newFragment = new MonthFragment();
						break;
					default:
						newFragment = new AllListFragment();
						break;
				}
				transaction.replace(R.id.fragment, newFragment);
				transaction.addToBackStack(null);
				transaction.commit();

			}

			@Override
			public void onNothingSelected(final AdapterView<?> parent) {
			}
		});
		return analysisView;
	}

	@Override
	public CharSequence getTabTitle() {
		return getResources().getString(R.string.analysis_tab_tilte);
	}

}
