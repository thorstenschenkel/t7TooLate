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
package de.t7soft.android.t7toolate;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import sheetrock.panda.changelog.ChangeLog;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.RelativeLayout;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.Target;

import de.t7soft.android.t7toolate.MainActivityTarget.PosX;
import de.t7soft.android.t7toolate.analysis.AnalysisFragment;
import de.t7soft.android.t7toolate.capture.CaptueFragment;
import de.t7soft.android.t7toolate.connections.ConnectionsFragment;
import de.t7soft.android.t7toolate.database.ToLateDatabaseAdapter;

/**
 * 
 * http://android-holo-colors.com/
 * 
 * @author tsc
 * 
 */
public class MainActivity extends FragmentActivity {

	public static final String PREFS_NAME = "ToLatePrefsFile";
	public static final String PREF_ON_TIME = "onTime";
	public static final String PREF_SLIGHT = "slight";
	public static final String PREF_LATE = "late";
	public static final String PREF_SHOW_QUICK_GUIDE = "showQuickGuide";
	public static final int PREF_ON_TIME_DEFAULT = 5;
	public static final int PREF_SLIGHT_DEFAULT = 10;
	public static final int PREF_LATE_DEFAULT = 60;

	private static final int[] TAB_ICON_IDS = {
			R.drawable.ic_time, R.drawable.ic_connection_wide, R.drawable.ic_list
	};
	private ToLateDatabaseAdapter dbAdapter;
	private CaptueFragment captueFragment;
	private ConnectionsFragment connectionsFragment;
	private ShowcaseView sv;
	private ViewPager viewPager;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {

		final TopExceptionHandler handler = new TopExceptionHandler(this);
		Thread.setDefaultUncaughtExceptionHandler(handler);
		handler.logStackTrace();

		if (dbAdapter == null) {
			dbAdapter = new ToLateDatabaseAdapter(this);
		}

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		viewPager = (ViewPager) findViewById(R.id.pager);
		setupViewPager(viewPager);

		final ActionBar actionBar = getActionBar();
		setupActionBar(actionBar, viewPager);

	}

	private void setupShowcase() {

		final RelativeLayout.LayoutParams lps = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		lps.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		lps.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		final int margin = ((Number) (getResources().getDisplayMetrics().density * 12)).intValue();
		lps.setMargins(margin, margin, margin, margin);

		final OnClickListener showcaseOnClickListener = new ShowcaseOnClickListener();
		final Target target = new MainActivityTarget(this, viewPager, PosX.CENTER);
		sv = new ShowcaseView.Builder(this).withMaterialShowcase() //
				.useDecorViewAsParent() //
				.setTarget(target) //
				.setContentTitle(R.string.showcase_connections_title) //
				.setContentText(R.string.showcase_connections_message) //
				.setStyle(R.style.ShowcaseView) //
				.replaceEndButton(R.layout.view_custom_button) //
				.setOnClickListener(showcaseOnClickListener) //
				.build();
		sv.setButtonText(getString(R.string.showcase_next));
		sv.setButtonPosition(lps);

	}

	@Override
	protected void onStart() {
		super.onStart();

		final ChangeLog cl = new ChangeLog(this);
		if (cl.firstRun()) {
			cl.getLogDialog().show();
		}

		dbAdapter.open();

		showQuickGuide();

	}

	private void showQuickGuide() {

		final SharedPreferences settings = getSharedPreferences(MainActivity.PREFS_NAME, 0);
		final boolean showQuickGuide = settings.getBoolean(MainActivity.PREF_SHOW_QUICK_GUIDE, true);
		if (showQuickGuide) {
			viewPager.setCurrentItem(1);
			setupShowcase();
			final SharedPreferences.Editor editor = settings.edit();
			editor.putBoolean(MainActivity.PREF_SHOW_QUICK_GUIDE, false);
			editor.commit();
		}

	}

	@Override
	protected void onStop() {
		super.onStop();
		dbAdapter.close();
	}

	public void onCapture(final View view) {
		captueFragment.onCapture(view);
	}

	public void onCanceled(final View view) {
		captueFragment.onCanceled(view);
	}

	public void onAddConnection(final View view) {
		connectionsFragment.onAdd(view);
	}

	private void setupViewPager(final ViewPager viewPager) {

		final MainTabPagerAdapter adapter = new MainTabPagerAdapter(getSupportFragmentManager());
		captueFragment = new CaptueFragment();
		adapter.addFrag(captueFragment);
		connectionsFragment = new ConnectionsFragment();
		adapter.addFrag(connectionsFragment);
		adapter.addFrag(new AnalysisFragment());
		viewPager.setAdapter(adapter);

		viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(final int position) {
				final ActionBar actionBar = getActionBar();
				actionBar.setSelectedNavigationItem(position);
				final Fragment selectedFragment = adapter.getItem(position);
				if (selectedFragment == captueFragment) {
					captueFragment.startUpdates();
				} else {
					captueFragment.stopUpdates();
				}
			}
		});

	}

	private void setupActionBar(final ActionBar actionBar, final ViewPager viewPager) {
		// Enable Tabs on Action Bar
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		final ActionBar.TabListener tabListener = new ActionBar.TabListener() {

			@Override
			public void onTabReselected(final android.app.ActionBar.Tab tab, final FragmentTransaction ft) {
				// nothing todo
			}

			@Override
			public void onTabSelected(final ActionBar.Tab tab, final FragmentTransaction ft) {
				final int position = tab.getPosition();
				viewPager.setCurrentItem(position);
			}

			@Override
			public void onTabUnselected(final android.app.ActionBar.Tab tab, final FragmentTransaction ft) {
				// nothing todo
			}

		};
		// Add New Tab
		for (int i = 0; i < TAB_ICON_IDS.length; i++) {
			final Tab tab = actionBar.newTab();
			tab.setIcon(TAB_ICON_IDS[i]);
			actionBar.addTab(tab.setTabListener(tabListener));
		}
	}

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_settings:
				showSettings();
				return true;
			case R.id.action_change_log:
				showChangeLog();
				return true;
			case R.id.action_about:
				showAboutDlg();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void showSettings() {
		final Intent intent = new Intent(this, SettingsActivity.class);
		startActivity(intent);
	}

	private void showChangeLog() {
		final ChangeLog cl = new ChangeLog(this);
		cl.getFullLogDialog().show();
	}

	private void showAboutDlg() {
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		final String infoText = createInfoText();
		final WebView wv = new WebView(this);
		wv.loadData(infoText, "text/html", "utf-8");
		wv.setBackgroundColor(getResources().getColor(android.R.color.white));
		wv.getSettings().setDefaultTextEncodingName("utf-8");
		builder.setView(wv);
		builder.setIcon(R.drawable.ic_launcher);
		builder.setTitle(R.string.app_name);
		builder.setCancelable(true);
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(final DialogInterface dialog, final int id) {
				dialog.cancel();
			}
		});
		builder.setNegativeButton(null, null);
		final AlertDialog alert = builder.create();
		alert.show();
	}

	private String createInfoText() {
		try {
			final String versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
			final Date buildTime = getBuildTime();
			final String dateString = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.GERMAN).format(buildTime);
			String msgString = getString(R.string.tooLate_info_msg);
			msgString = MessageFormat.format(msgString, versionName, dateString);
			return msgString;
		} catch (final NameNotFoundException e) {
			Log.e(this.getClass().getSimpleName(), "No infos found!", e);
			return "ERROR";
		}
	}

	private Date getBuildTime() {
		try {
			final ApplicationInfo ai = getPackageManager().getApplicationInfo(getPackageName(), 0);
			final ZipFile zf = new ZipFile(ai.sourceDir);
			final ZipEntry ze = zf.getEntry("classes.dex");
			final long time = ze.getTime();
			zf.close();
			return new Date(time);
		} catch (final Exception e) {
			Log.e(this.getClass().getSimpleName(), "No build time!", e);
			return null;
		}
	}

	public ToLateDatabaseAdapter getDbAdapter() {
		return dbAdapter;
	}

	private class ShowcaseOnClickListener implements OnClickListener {

		private int callCount;

		@Override
		public void onClick(final View v) {

			final Context context = v.getContext();

			switch (callCount) {
				case 0:
					viewPager.setCurrentItem(0);
					final Target target2 = new MainActivityTarget(getActivity(v), viewPager, PosX.LEFT);
					sv.setShowcase(target2, true);
					sv.setContentTitle(context.getString(R.string.showcase_capture_title));
					sv.setContentText(context.getString(R.string.showcase_capture_message));
					break;
				case 1:
					viewPager.setCurrentItem(2);
					final Target target3 = new MainActivityTarget(getActivity(v), viewPager, PosX.RIGHT);
					sv.setShowcase(target3, true);
					sv.setContentTitle(context.getString(R.string.showcase_analysis_title));
					sv.setContentText(context.getString(R.string.showcase_analysis_message));
					sv.setButtonText(getString(R.string.showcase_close));
					break;
				case 2:
					viewPager.setCurrentItem(1);
					sv.hide();
					break;
			}

			callCount++;

		}
	}

	private static Activity getActivity(final View view) {
		Context context = view.getContext();
		while (context instanceof ContextWrapper) {
			if (context instanceof Activity) {
				return (Activity) context;
			}
			context = ((ContextWrapper) context).getBaseContext();
		}
		return null;
	}

}
