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
