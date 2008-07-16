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
package org.eclipse.dltk.validators.internal.core;

import org.eclipse.dltk.compiler.problem.CategorizedProblem;
import org.eclipse.dltk.compiler.problem.DefaultProblem;
import org.eclipse.dltk.compiler.problem.IProblem;

public class TaskInfo extends CategorizedProblem {

	private final String message;
	private final int charStart;
	private final int lineNumber;
	private final int charEnd;
	private final int priority;

	/**
	 * @param message
	 * @param lineNumber
	 * @param charEnd
	 * @param charStart
	 */
	public TaskInfo(String message, int lineNumber, int priority,
			int charEnd, int charStart) {
		this.charEnd = charEnd;
		this.charStart = charStart;
		this.lineNumber = lineNumber;
		this.message = message;
		this.priority = priority;
	}

	public int getCategoryID() {
		return 0;
	}

	public String getMarkerType() {
		return DefaultProblem.MARKER_TYPE_TASK;
	}

	public String[] getArguments() {
		return null;
	}

	public int getID() {
		return IProblem.Task;
	}

	public String getMessage() {
		return message;
	}

	public String getOriginatingFileName() {
		return null;
	}

	public int getSourceEnd() {
		return charEnd;
	}

	public int getSourceLineNumber() {
		return lineNumber;
	}

	public int getSourceStart() {
		return charStart;
	}

	public int getPriority() {
		return priority;
	}

	public boolean isError() {
		return false;
	}

	public boolean isWarning() {
		return false;
	}

	public void setSourceEnd(int sourceEnd) {
		// unsupported
	}

	public void setSourceLineNumber(int lineNumber) {
		// unsupported
	}

	public void setSourceStart(int sourceStart) {
		// unsupported
	}

}
