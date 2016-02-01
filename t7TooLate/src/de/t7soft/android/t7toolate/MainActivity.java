package de.t7soft.android.t7toolate;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
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

}
