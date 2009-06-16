/*******************************************************************************
 * Copyright (c) 2008 xored software, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package org.eclipse.dltk.ui.formatter.internal;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.dltk.compiler.util.Util;
import org.eclipse.dltk.ui.preferences.IPreferenceDelegate;

public class FormatterDialogPreferences implements IPreferenceDelegate {

	private final Map<String, String> preferences = new HashMap<String, String>();

	public String getString(Object key) {
		final String value = preferences.get(key);
		return value != null ? value : Util.EMPTY_STRING;
	}

	public boolean getBoolean(Object key) {
		return Boolean.valueOf(getString(key)).booleanValue();
	}

	public void setString(Object key, String value) {
		preferences.put((String) key, value);
	}

	public void setBoolean(Object key, boolean value) {
		setString(key, String.valueOf(value));
	}

	/**
	 * @return
	 */
	public Map<String, String> get() {
		return Collections.unmodifiableMap(preferences);
	}

	/**
	 * @param prefs
	 */
	public void set(Map<String, String> prefs) {
		preferences.clear();
		if (prefs != null) {
			preferences.putAll(prefs);
		}
	}

}
