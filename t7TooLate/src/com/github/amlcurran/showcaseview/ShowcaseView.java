/*
 * Copyright 2014 Alex Curran
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.amlcurran.showcaseview;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.text.Layout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.github.amlcurran.showcaseview.AnimationFactory.AnimationEndListener;
import com.github.amlcurran.showcaseview.AnimationFactory.AnimationStartListener;
import com.github.amlcurran.showcaseview.targets.Target;

import de.t7soft.android.t7toolate.R;

/**
 * A view which allows you to showcase areas of your app with an explanation.
 */
public class ShowcaseView extends RelativeLayout implements View.OnTouchListener, ShowcaseViewApi {

	private static final int HOLO_BLUE = Color.parseColor("#33B5E5");
	public static final int UNDEFINED = -1;
	public static final int LEFT_OF_SHOWCASE = 0;
	public static final int RIGHT_OF_SHOWCASE = 2;
	public static final int ABOVE_SHOWCASE = 1;
	public static final int BELOW_SHOWCASE = 3;

	@Retention(RetentionPolicy.SOURCE)
	public @interface TextPosition {
	}

	private Button mEndButton;
	private final TextDrawer textDrawer;
	private ShowcaseDrawer showcaseDrawer;
	private final ShowcaseAreaCalculator showcaseAreaCalculator;
	private final AnimationFactory animationFactory;
	private final ShotStateStore shotStateStore;

	// Showcase metrics
	private int showcaseX = -1;
	private int showcaseY = -1;
	private float scaleMultiplier = 1f;

	// Touch items
	private boolean hasCustomClickListener = false;
	private boolean blockTouches = true;
	private boolean hideOnTouch = false;
	private OnShowcaseEventListener mEventListener = OnShowcaseEventListener.NONE;

	private boolean hasAlteredText = false;
	private boolean hasNoTarget = false;
	private boolean shouldCentreText;
	private Bitmap bitmapBuffer;

	// Animation items
	private long fadeInMillis;
	private long fadeOutMillis;
	private boolean isShowing;
	private int backgroundColor;
	private int showcaseColor;
	private boolean blockAllTouches;
	private final int[] positionInWindow = new int[2];

	protected ShowcaseView(final Context context, final boolean newStyle) {
		this(context, null, R.styleable.CustomTheme_showcaseViewStyle, newStyle);
	}

	protected ShowcaseView(final Context context, final AttributeSet attrs, final int defStyle, final boolean newStyle) {
		super(context, attrs, defStyle);

		final ApiUtils apiUtils = new ApiUtils();
		if (apiUtils.isCompatWithHoneycomb()) {
			animationFactory = new AnimatorAnimationFactory();
		} else {
			animationFactory = new NoAnimationFactory();
		}
		showcaseAreaCalculator = new ShowcaseAreaCalculator();
		shotStateStore = new ShotStateStore(context);

		// Get the attributes for the ShowcaseView
		final TypedArray styled = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ShowcaseView,
				R.attr.showcaseViewStyle, R.style.ShowcaseView);

		// Set the default animation times
		fadeInMillis = getResources().getInteger(android.R.integer.config_mediumAnimTime);
		fadeOutMillis = getResources().getInteger(android.R.integer.config_mediumAnimTime);

		mEndButton = (Button) LayoutInflater.from(context).inflate(R.layout.showcase_button, null);
		if (newStyle) {
			showcaseDrawer = new NewShowcaseDrawer(getResources(), context.getTheme());
		} else {
			showcaseDrawer = new StandardShowcaseDrawer(getResources(), context.getTheme());
		}
		textDrawer = new TextDrawer(getResources(), getContext());

		updateStyle(styled, false);

		init();
	}

	private void init() {

		setOnTouchListener(this);

		if (mEndButton.getParent() == null) {
			final int margin = (int) getResources().getDimension(R.dimen.button_margin);
			final RelativeLayout.LayoutParams lps = (LayoutParams) generateDefaultLayoutParams();
			lps.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
			lps.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			lps.setMargins(margin, margin, margin, margin);
			mEndButton.setLayoutParams(lps);
			mEndButton.setText(android.R.string.ok);
			if (!hasCustomClickListener) {
				mEndButton.setOnClickListener(hideOnClickListener);
			}
			addView(mEndButton);
		}

	}

	private boolean hasShot() {
		return shotStateStore.hasShot();
	}

	void setShowcasePosition(final Point point) {
		setShowcasePosition(point.x, point.y);
	}

	void setShowcasePosition(final int x, final int y) {
		if (shotStateStore.hasShot()) {
			return;
		}
		getLocationInWindow(positionInWindow);
		showcaseX = x - positionInWindow[0];
		showcaseY = y - positionInWindow[1];
		// init();
		recalculateText();
		invalidate();
	}

	public void setTarget(final Target target) {
		setShowcase(target, false);
	}

	public void setShowcase(final Target target, final boolean animate) {
		postDelayed(new Runnable() {
			@Override
			public void run() {

				if (!shotStateStore.hasShot()) {

					if (canUpdateBitmap()) {
						updateBitmap();
					}

					final Point targetPoint = target.getPoint();
					if (targetPoint != null) {
						hasNoTarget = false;
						if (animate) {
							animationFactory.animateTargetToPoint(ShowcaseView.this, targetPoint);
						} else {
							setShowcasePosition(targetPoint);
						}
					} else {
						hasNoTarget = true;
						invalidate();
					}

				}
			}
		}, 100);
	}

	private void updateBitmap() {
		if ((bitmapBuffer == null) || haveBoundsChanged()) {
			if (bitmapBuffer != null) {
				bitmapBuffer.recycle();
			}
			bitmapBuffer = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(), Bitmap.Config.ARGB_8888);
		}
	}

	private boolean haveBoundsChanged() {
		return (getMeasuredWidth() != bitmapBuffer.getWidth()) || (getMeasuredHeight() != bitmapBuffer.getHeight());
	}

	public boolean hasShowcaseView() {
		return ((showcaseX != 1000000) && (showcaseY != 1000000)) && !hasNoTarget;
	}

	public void setShowcaseX(final int x) {
		setShowcasePosition(x, getShowcaseY());
	}

	public void setShowcaseY(final int y) {
		setShowcasePosition(getShowcaseX(), y);
	}

	public int getShowcaseX() {
		getLocationInWindow(positionInWindow);
		return showcaseX + positionInWindow[0];
	}

	public int getShowcaseY() {
		getLocationInWindow(positionInWindow);
		return showcaseY + positionInWindow[1];
	}

	/**
	 * Override the standard button click event
	 * 
	 * @param listener
	 *            Listener to listen to on click events
	 */
	public void overrideButtonClick(final OnClickListener listener) {
		if (shotStateStore.hasShot()) {
			return;
		}
		if (mEndButton != null) {
			if (listener != null) {
				mEndButton.setOnClickListener(listener);
			} else {
				mEndButton.setOnClickListener(hideOnClickListener);
			}
		}
		hasCustomClickListener = true;
	}

	public void setOnShowcaseEventListener(final OnShowcaseEventListener listener) {
		if (listener != null) {
			mEventListener = listener;
		} else {
			mEventListener = OnShowcaseEventListener.NONE;
		}
	}

	public void setButtonText(final CharSequence text) {
		if (mEndButton != null) {
			mEndButton.setText(text);
		}
	}

	private void recalculateText() {
		final boolean recalculatedCling = showcaseAreaCalculator.calculateShowcaseRect(showcaseX, showcaseY,
				showcaseDrawer);
		final boolean recalculateText = recalculatedCling || hasAlteredText;
		if (recalculateText) {
			final Rect rect = hasShowcaseView() ? showcaseAreaCalculator.getShowcaseRect() : new Rect();
			textDrawer.calculateTextPosition(getMeasuredWidth(), getMeasuredHeight(), shouldCentreText, rect);
		}
		hasAlteredText = false;
	}

	@SuppressWarnings("NullableProblems")
	@Override
	protected void dispatchDraw(final Canvas canvas) {
		if ((showcaseX < 0) || (showcaseY < 0) || shotStateStore.hasShot() || (bitmapBuffer == null)) {
			super.dispatchDraw(canvas);
			return;
		}

		// Draw background color
		showcaseDrawer.erase(bitmapBuffer);

		// Draw the showcase drawable
		if (!hasNoTarget) {
			showcaseDrawer.drawShowcase(bitmapBuffer, showcaseX, showcaseY, scaleMultiplier);
			showcaseDrawer.drawToCanvas(canvas, bitmapBuffer);
		}

		// Draw the text on the screen, recalculating its position if necessary
		textDrawer.draw(canvas);

		super.dispatchDraw(canvas);

	}

	@Override
	public void hide() {
		// If the type is set to one-shot, store that it has shot
		shotStateStore.storeShot();
		mEventListener.onShowcaseViewHide(this);
		fadeOutShowcase();
	}

	private void clearBitmap() {
		if ((bitmapBuffer != null) && !bitmapBuffer.isRecycled()) {
			bitmapBuffer.recycle();
			bitmapBuffer = null;
		}
	}

	private void fadeOutShowcase() {
		animationFactory.fadeOutView(this, fadeOutMillis, new AnimationEndListener() {
			@Override
			public void onAnimationEnd() {
				setVisibility(View.GONE);
				clearBitmap();
				isShowing = false;
				mEventListener.onShowcaseViewDidHide(ShowcaseView.this);
			}
		});
	}

	@Override
	public void show() {
		isShowing = true;
		if (canUpdateBitmap()) {
			updateBitmap();
		}
		mEventListener.onShowcaseViewShow(this);
		fadeInShowcase();
	}

	private boolean canUpdateBitmap() {
		return (getMeasuredHeight() > 0) && (getMeasuredWidth() > 0);
	}

	private void fadeInShowcase() {
		animationFactory.fadeInView(this, fadeInMillis, new AnimationStartListener() {
			@Override
			public void onAnimationStart() {
				setVisibility(View.VISIBLE);
			}
		});
	}

	@Override
	public boolean onTouch(final View view, final MotionEvent motionEvent) {
		if (blockAllTouches) {
			mEventListener.onShowcaseViewTouchBlocked(motionEvent);
			return true;
		}

		final float xDelta = Math.abs(motionEvent.getRawX() - showcaseX);
		final float yDelta = Math.abs(motionEvent.getRawY() - showcaseY);
		final double distanceFromFocus = Math.sqrt(Math.pow(xDelta, 2) + Math.pow(yDelta, 2));

		if ((MotionEvent.ACTION_UP == motionEvent.getAction()) && hideOnTouch
				&& (distanceFromFocus > showcaseDrawer.getBlockedRadius())) {
			this.hide();
			return true;
		}

		final boolean blocked = blockTouches && (distanceFromFocus > showcaseDrawer.getBlockedRadius());
		if (blocked) {
			mEventListener.onShowcaseViewTouchBlocked(motionEvent);
		}
		return blocked;
	}

	private static void insertShowcaseView(final ShowcaseView showcaseView, final ViewGroup parent,
			final int parentIndex) {
		parent.addView(showcaseView, parentIndex);
		if (!showcaseView.hasShot()) {
			showcaseView.show();
		} else {
			showcaseView.hideImmediate();
		}
	}

	private void hideImmediate() {
		isShowing = false;
		setVisibility(GONE);
	}

	@Override
	public void setContentTitle(final CharSequence title) {
		textDrawer.setContentTitle(title);
		invalidate();
	}

	@Override
	public void setContentText(final CharSequence text) {
		textDrawer.setContentText(text);
		invalidate();
	}

	private void setScaleMultiplier(final float scaleMultiplier) {
		this.scaleMultiplier = scaleMultiplier;
	}

	public void hideButton() {
		mEndButton.setVisibility(GONE);
	}

	public void showButton() {
		mEndButton.setVisibility(VISIBLE);
	}

	/**
	 * Builder class which allows easier creation of {@link ShowcaseView}s. It is recommended that you use this Builder class.
	 */
	public static class Builder {

		private final ShowcaseView showcaseView;
		private final Activity activity;

		private ViewGroup parent;
		private int parentIndex;

		public Builder(final Activity activity) {
			this(activity, false);
		}

		/**
		 * @param useNewStyle
		 *            should use "new style" showcase (see {@link #withNewStyleShowcase()}
		 * @deprecated use {@link #withHoloShowcase()}, {@link #withNewStyleShowcase()}, or {@link #setShowcaseDrawer(ShowcaseDrawer)}
		 */
		@Deprecated
		public Builder(final Activity activity, final boolean useNewStyle) {
			this.activity = activity;
			this.showcaseView = new ShowcaseView(activity, useNewStyle);
			this.showcaseView.setTarget(Target.NONE);
			this.parent = (ViewGroup) activity.findViewById(android.R.id.content);
			this.parentIndex = parent.getChildCount();
		}

		/**
		 * Create the {@link com.github.amlcurran.showcaseview.ShowcaseView} and show it.
		 * 
		 * @return the created ShowcaseView
		 */
		public ShowcaseView build() {
			insertShowcaseView(showcaseView, parent, parentIndex);
			return showcaseView;
		}

		/**
		 * Draw a holo-style showcase. This is the default.<br/>
		 * <img alt="Holo showcase example" src="../../../../../../../../example2.png" />
		 */
		public Builder withHoloShowcase() {
			return setShowcaseDrawer(new StandardShowcaseDrawer(activity.getResources(), activity.getTheme()));
		}

		/**
		 * Draw a new-style showcase.<br/>
		 * <img alt="Holo showcase example" src="../../../../../../../../example.png" />
		 */
		public Builder withNewStyleShowcase() {
			return setShowcaseDrawer(new NewShowcaseDrawer(activity.getResources(), activity.getTheme()));
		}

		/**
		 * Draw a material style showcase. <img alt="Material showcase" src="../../../../../../../../material.png" />
		 */
		public Builder withMaterialShowcase() {
			return setShowcaseDrawer(new MaterialShowcaseDrawer(activity.getResources()));
		}

		/**
		 * Set a custom showcase drawer which will be responsible for measuring and drawing the showcase
		 */
		public Builder setShowcaseDrawer(final ShowcaseDrawer showcaseDrawer) {
			showcaseView.setShowcaseDrawer(showcaseDrawer);
			return this;
		}

		/**
		 * Set the title text shown on the ShowcaseView.
		 */
		public Builder setContentTitle(final int resId) {
			return setContentTitle(activity.getString(resId));
		}

		/**
		 * Set the title text shown on the ShowcaseView.
		 */
		public Builder setContentTitle(final CharSequence title) {
			showcaseView.setContentTitle(title);
			return this;
		}

		/**
		 * Set the descriptive text shown on the ShowcaseView.
		 */
		public Builder setContentText(final int resId) {
			return setContentText(activity.getString(resId));
		}

		/**
		 * Set the descriptive text shown on the ShowcaseView.
		 */
		public Builder setContentText(final CharSequence text) {
			showcaseView.setContentText(text);
			return this;
		}

		/**
		 * Set the target of the showcase.
		 * 
		 * @param target
		 *            a {@link com.github.amlcurran.showcaseview.targets.Target} representing the item to showcase (e.g., a button, or action item).
		 */
		public Builder setTarget(final Target target) {
			showcaseView.setTarget(target);
			return this;
		}

		/**
		 * Set the style of the ShowcaseView. See the sample app for example styles.
		 */
		public Builder setStyle(final int theme) {
			showcaseView.setStyle(theme);
			return this;
		}

		/**
		 * Set a listener which will override the button clicks.
		 * <p/>
		 * Note that you will have to manually hide the ShowcaseView
		 */
		public Builder setOnClickListener(final OnClickListener onClickListener) {
			showcaseView.overrideButtonClick(onClickListener);
			return this;
		}

		/**
		 * Don't make the ShowcaseView block touches on itself. This doesn't block touches in the showcased area.
		 * <p/>
		 * By default, the ShowcaseView does block touches
		 */
		public Builder doNotBlockTouches() {
			showcaseView.setBlocksTouches(false);
			return this;
		}

		/**
		 * Make this ShowcaseView hide when the user touches outside the showcased area. This enables {@link #doNotBlockTouches()} as well.
		 * <p/>
		 * By default, the ShowcaseView doesn't hide on touch.
		 */
		public Builder hideOnTouchOutside() {
			showcaseView.setBlocksTouches(true);
			showcaseView.setHideOnTouchOutside(true);
			return this;
		}

		/**
		 * Set the ShowcaseView to only ever show once.
		 * 
		 * @param shotId
		 *            a unique identifier (<em>across the app</em>) to store whether this ShowcaseView has been shown.
		 */
		public Builder singleShot(final long shotId) {
			showcaseView.setSingleShot(shotId);
			return this;
		}

		public Builder setShowcaseEventListener(final OnShowcaseEventListener showcaseEventListener) {
			showcaseView.setOnShowcaseEventListener(showcaseEventListener);
			return this;
		}

		public Builder setParent(final ViewGroup parent, final int index) {
			this.parent = parent;
			this.parentIndex = index;
			return this;
		}

		/**
		 * Sets the paint that will draw the text as specified by {@link #setContentText(CharSequence)} or {@link #setContentText(int)}. If you're using a TextAppearance (set by {@link #setStyle(int)}, then this {@link TextPaint} will override that
		 * TextAppearance.
		 */
		public Builder setContentTextPaint(final TextPaint textPaint) {
			showcaseView.setContentTextPaint(textPaint);
			return this;
		}

		/**
		 * Sets the paint that will draw the text as specified by {@link #setContentTitle(CharSequence)} or {@link #setContentTitle(int)}. If you're using a TextAppearance (set by {@link #setStyle(int)}, then this {@link TextPaint} will override that
		 * TextAppearance.
		 */
		public Builder setContentTitlePaint(final TextPaint textPaint) {
			showcaseView.setContentTitlePaint(textPaint);
			return this;
		}

		/**
		 * Replace the end button with the one provided. Note that this resets any OnClickListener provided by {@link #setOnClickListener(OnClickListener)}, so call this method before that one.
		 */
		public Builder replaceEndButton(final Button button) {
			showcaseView.setEndButton(button);
			return this;
		}

		/**
		 * Replace the end button with the one provided. Note that this resets any OnClickListener provided by {@link #setOnClickListener(OnClickListener)}, so call this method before that one.
		 */
		public Builder replaceEndButton(final int buttonResourceId) {
			final View view = LayoutInflater.from(activity).inflate(buttonResourceId, showcaseView, false);
			if (!(view instanceof Button)) {
				throw new IllegalArgumentException(
						"Attempted to replace showcase button with a layout which isn't a button");
			}
			return replaceEndButton((Button) view);
		}

		/**
		 * Block any touch made on the ShowcaseView, even inside the showcase
		 */
		public Builder blockAllTouches() {
			showcaseView.setBlockAllTouches(true);
			return this;
		}

		/**
		 * Uses the android decor view to insert a showcase, this is not recommended as then UI elements in showcase view can hide behind the nav bar
		 */
		public Builder useDecorViewAsParent() {
			this.parent = ((ViewGroup) activity.getWindow().getDecorView());
			this.parentIndex = -1;
			return this;
		}
	}

	private void setEndButton(final Button button) {
		final LayoutParams copyParams = (LayoutParams) mEndButton.getLayoutParams();
		mEndButton.setOnClickListener(null);
		removeView(mEndButton);
		mEndButton = button;
		button.setOnClickListener(hideOnClickListener);
		button.setLayoutParams(copyParams);
		addView(button);
	}

	private void setShowcaseDrawer(final ShowcaseDrawer showcaseDrawer) {
		this.showcaseDrawer = showcaseDrawer;
		this.showcaseDrawer.setBackgroundColour(backgroundColor);
		this.showcaseDrawer.setShowcaseColour(showcaseColor);
		hasAlteredText = true;
		invalidate();
	}

	private void setContentTitlePaint(final TextPaint textPaint) {
		this.textDrawer.setTitlePaint(textPaint);
		hasAlteredText = true;
		invalidate();
	}

	private void setContentTextPaint(final TextPaint paint) {
		this.textDrawer.setContentPaint(paint);
		hasAlteredText = true;
		invalidate();
	}

	/**
	 * Set whether the text should be centred in the screen, or left-aligned (which is the default).
	 */
	public void setShouldCentreText(final boolean shouldCentreText) {
		this.shouldCentreText = shouldCentreText;
		hasAlteredText = true;
		invalidate();
	}

	/**
	 * @see com.github.amlcurran.showcaseview.ShowcaseView.Builder#setSingleShot(long)
	 */
	private void setSingleShot(final long shotId) {
		shotStateStore.setSingleShot(shotId);
	}

	/**
	 * Change the position of the ShowcaseView's button from the default bottom-right position.
	 * 
	 * @param layoutParams
	 *            a {@link android.widget.RelativeLayout.LayoutParams} representing the new position of the button
	 */
	@Override
	public void setButtonPosition(final RelativeLayout.LayoutParams layoutParams) {
		mEndButton.setLayoutParams(layoutParams);
	}

	/**
	 * Sets the text alignment of the detail text
	 */
	public void setDetailTextAlignment(final Layout.Alignment textAlignment) {
		textDrawer.setDetailTextAlignment(textAlignment);
		hasAlteredText = true;
		invalidate();
	}

	/**
	 * Sets the text alignment of the title text
	 */
	public void setTitleTextAlignment(final Layout.Alignment textAlignment) {
		textDrawer.setTitleTextAlignment(textAlignment);
		hasAlteredText = true;
		invalidate();
	}

	/**
	 * Set the duration of the fading in and fading out of the ShowcaseView
	 */
	public void setFadeDurations(final long fadeInMillis, final long fadeOutMillis) {
		this.fadeInMillis = fadeInMillis;
		this.fadeOutMillis = fadeOutMillis;
	}

	public void forceTextPosition(@TextPosition final int textPosition) {
		textDrawer.forceTextPosition(textPosition);
		hasAlteredText = true;
		invalidate();
	}

	/**
	 * @see com.github.amlcurran.showcaseview.ShowcaseView.Builder#hideOnTouchOutside()
	 */
	@Override
	public void setHideOnTouchOutside(final boolean hideOnTouch) {
		this.hideOnTouch = hideOnTouch;
	}

	/**
	 * @see com.github.amlcurran.showcaseview.ShowcaseView.Builder#doNotBlockTouches()
	 */
	@Override
	public void setBlocksTouches(final boolean blockTouches) {
		this.blockTouches = blockTouches;
	}

	private void setBlockAllTouches(final boolean blockAllTouches) {
		this.blockAllTouches = blockAllTouches;
	}

	/**
	 * @see com.github.amlcurran.showcaseview.ShowcaseView.Builder#setStyle(int)
	 */
	@Override
	public void setStyle(final int theme) {
		final TypedArray array = getContext().obtainStyledAttributes(theme, R.styleable.ShowcaseView);
		updateStyle(array, true);
	}

	@Override
	public boolean isShowing() {
		return isShowing;
	}

	private void updateStyle(final TypedArray styled, final boolean invalidate) {
		backgroundColor = styled.getColor(R.styleable.ShowcaseView_sv_backgroundColor, Color.argb(128, 80, 80, 80));
		showcaseColor = styled.getColor(R.styleable.ShowcaseView_sv_showcaseColor, HOLO_BLUE);
		String buttonText = styled.getString(R.styleable.ShowcaseView_sv_buttonText);
		if (TextUtils.isEmpty(buttonText)) {
			buttonText = getResources().getString(android.R.string.ok);
		}
		final boolean tintButton = styled.getBoolean(R.styleable.ShowcaseView_sv_tintButtonColor, true);

		final int titleTextAppearance = styled.getResourceId(R.styleable.ShowcaseView_sv_titleTextAppearance,
				R.style.TextAppearance_ShowcaseView_Title);
		final int detailTextAppearance = styled.getResourceId(R.styleable.ShowcaseView_sv_detailTextAppearance,
				R.style.TextAppearance_ShowcaseView_Detail);

		styled.recycle();

		showcaseDrawer.setShowcaseColour(showcaseColor);
		showcaseDrawer.setBackgroundColour(backgroundColor);
		tintButton(showcaseColor, tintButton);
		mEndButton.setText(buttonText);
		textDrawer.setTitleStyling(titleTextAppearance);
		textDrawer.setDetailStyling(detailTextAppearance);
		hasAlteredText = true;

		if (invalidate) {
			invalidate();
		}
	}

	private void tintButton(final int showcaseColor, final boolean tintButton) {
		if (tintButton) {
			mEndButton.getBackground().setColorFilter(showcaseColor, PorterDuff.Mode.MULTIPLY);
		} else {
			mEndButton.getBackground().setColorFilter(HOLO_BLUE, PorterDuff.Mode.MULTIPLY);
		}
	}

	private final OnClickListener hideOnClickListener = new OnClickListener() {
		@Override
		public void onClick(final View v) {
			hide();
		}
	};

}
