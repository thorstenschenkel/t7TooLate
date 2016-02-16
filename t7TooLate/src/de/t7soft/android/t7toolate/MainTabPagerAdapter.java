package de.t7soft.android.t7toolate;

import java.util.ArrayList;
import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class MainTabPagerAdapter extends FragmentStatePagerAdapter {

	private final List<Fragment> fragmentList = new ArrayList<Fragment>();

	public MainTabPagerAdapter(final FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(final int position) {
		return fragmentList.get(position);
	}

	@Override
	public int getCount() {
		return fragmentList.size();
	}

	@Override
	public CharSequence getPageTitle(final int position) {
		return ((ITabFragment) fragmentList.get(position)).getTabTitle();
	}

	public void addFrag(final Fragment fragment) {
		fragmentList.add(fragment);
	}

}
