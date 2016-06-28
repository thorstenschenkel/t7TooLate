package com.github.amlcurran.showcaseview;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import de.t7soft.android.t7toolate.R;

public class MaterialShowcaseDrawer implements ShowcaseDrawer {

	private final float radius;
	private final Paint basicPaint;
	private final Paint eraserPaint;
	private int backgroundColor;

	public MaterialShowcaseDrawer(final Resources resources) {
		this.radius = resources.getDimension(R.dimen.showcase_radius_material);
		this.eraserPaint = new Paint();
		this.eraserPaint.setColor(0xFFFFFF);
		this.eraserPaint.setAlpha(0);
		this.eraserPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.MULTIPLY));
		this.eraserPaint.setAntiAlias(true);
		this.basicPaint = new Paint();
	}

	@Override
	public void setShowcaseColour(final int color) {
		// no-op
	}

	@Override
	public void drawShowcase(final Bitmap buffer, final float x, final float y, final float scaleMultiplier) {
		final Canvas bufferCanvas = new Canvas(buffer);
		bufferCanvas.drawCircle(x, y, radius, eraserPaint);
	}

	@Override
	public int getShowcaseWidth() {
		return (int) (radius * 2);
	}

	@Override
	public int getShowcaseHeight() {
		return (int) (radius * 2);
	}

	@Override
	public float getBlockedRadius() {
		return radius;
	}

	@Override
	public void setBackgroundColour(final int backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	@Override
	public void erase(final Bitmap bitmapBuffer) {
		bitmapBuffer.eraseColor(backgroundColor);
	}

	@Override
	public void drawToCanvas(final Canvas canvas, final Bitmap bitmapBuffer) {
		canvas.drawBitmap(bitmapBuffer, 0, 0, basicPaint);
	}
}
