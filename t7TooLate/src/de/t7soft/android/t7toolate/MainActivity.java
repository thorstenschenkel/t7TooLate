package de.t7soft.android.t7toolate;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
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
import android.webkit.WebView;
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

	private static final int[] TAB_ICON_IDS = {
			R.drawable.ic_time, R.drawable.ic_train, R.drawable.ic_analysis
	};
	private ToLateDatabaseAdapter dbAdapter;
	private CaptueFragment captueFragment;
	private ConnectionsFragment connectionsFragment;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {

		if (dbAdapter == null) {
			dbAdapter = new ToLateDatabaseAdapter(this);
		}

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
		setupViewPager(viewPager);

		final ActionBar actionBar = getActionBar();
		setupActionBar(actionBar, viewPager);

	}

	@Override
	protected void onStart() {
		super.onStart();
		dbAdapter.open();
	}

	@Override
	protected void onStop() {
		super.onStop();
		dbAdapter.close();
	}

	public void onCapture(final View view) {
		captueFragment.onCapture(view);
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
			// tab.setText(TAB_TILTE_IDS[i]);
			tab.setIcon(TAB_ICON_IDS[i]);
			// tab.setCustomView(R.layout.tab_layout);
			// final View tabView = tab.getCustomView();
			// final TextView textView = (TextView) tabView.findViewById(R.id.tabTitleTextView);
			// textView.setText(TAB_TILTE_IDS[i]);
			// final ImageView imageView = (ImageView) tabView.findViewById(R.id.tabIconImageView);
			// imageView.setImageResource(TAB_ICON_IDS[i]);
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
			case R.id.action_about:
				showAboutDlg();
				return true;
		}
		return super.onOptionsItemSelected(item);
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

}
