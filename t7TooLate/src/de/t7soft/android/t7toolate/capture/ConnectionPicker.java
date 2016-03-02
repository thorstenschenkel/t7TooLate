package de.t7soft.android.t7toolate.capture;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.NumberPicker;

public class ConnectionPicker extends NumberPicker {

	/**
	 * The text for showing the current value.
	 */
	private EditText pickerInputText;

	public ConnectionPicker(final Context context, final AttributeSet attrs, final int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public ConnectionPicker(final Context context, final AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public ConnectionPicker(final Context context) {
		super(context);
		init();
	}

	private void init() {
		final List<Field> fileds = getAllFields(ConnectionPicker.class);

		try {
			for (final Field field : fileds) {
				if ("mInputText".equals(field.getName())) {
					field.setAccessible(true);
					pickerInputText = (EditText) field.get(this);
					pickerInputText.addTextChangedListener(new TextWatcher() {

						@Override
						public void beforeTextChanged(final CharSequence s, final int start, final int count,
								final int after) {
						}

						@Override
						public void onTextChanged(final CharSequence s, final int start, final int before,
								final int count) {
						}

						@Override
						public void afterTextChanged(final Editable s) {
							// final int drawableResId = ConnectionTypes.ICON_IDS[1];
							// pickerInputText.setCompoundDrawablesWithIntrinsicBounds(drawableResId, 0, 0, 0);
						}
					});
					break;
				}
			}
		} catch (final IllegalAccessException e) {
			e.printStackTrace();
		} catch (final IllegalArgumentException e) {
			e.printStackTrace();
		}

	}

	private List<Field> getAllFields(final Class clazz) {
		final List<Field> fields = new ArrayList<Field>();

		fields.addAll(Arrays.asList(clazz.getDeclaredFields()));

		final Class superClazz = clazz.getSuperclass();
		if (superClazz != null) {
			fields.addAll(getAllFields(superClazz));
		}

		return fields;
	}

	public void setValueInternal(final int current, final boolean notifyChange) {
		try {
			final Method m = getClass().getSuperclass().getDeclaredMethod("setValueInternal", int.class, boolean.class);
			m.setAccessible(true);
			m.invoke(this, current, notifyChange);
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

}
