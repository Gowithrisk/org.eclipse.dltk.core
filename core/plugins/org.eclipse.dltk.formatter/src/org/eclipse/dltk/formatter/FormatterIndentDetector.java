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
package org.eclipse.dltk.formatter;

import org.eclipse.jface.text.IRegion;

public class FormatterIndentDetector implements IFormatterWriter {

	private final int offset;
	private boolean indentDetected = false;
	private int level;

	/**
	 * @param offset
	 */
	public FormatterIndentDetector(int offset) {
		this.offset = offset;
	}

	public void addNewLineCallback(IFormatterCallback callback) {
		// empty
	}

	public void excludeRegion(IRegion region) {
		// empty

	}

	public void ensureLineStarted(IFormatterContext context) throws Exception {
		// empty
	}

	public void write(IFormatterContext context, int startOffset, int endOffset)
			throws Exception {
		if (!indentDetected && startOffset >= offset) {
			level = context.getIndent();
			indentDetected = true;
		}
	}

	/*
	 * @see IFormatterWriter#writeText(IFormatterContext, String)
	 */
	public void writeText(IFormatterContext context, String text)
			throws Exception {
		// empty
	}

	/*
	 * @see IFormatterWriter#writeLineBreak(IFormatterContext)
	 */
	public void writeLineBreak(IFormatterContext context) throws Exception {
		// empty
	}

	/**
	 * @return the level
	 */
	public int getLevel() {
		return level;
	}

}
