/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *

 *******************************************************************************/
package org.eclipse.dltk.evaluation.types;

import org.eclipse.dltk.ti.types.IEvaluatedType;

public class ErrorDefinedType implements IEvaluatedType {

	public static final IEvaluatedType INSTANCE = new ErrorDefinedType();

	private ErrorDefinedType() {
	}

	public String getTypeName() {
		return "error defined"; //$NON-NLS-1$
	}

	public boolean subtypeOf(IEvaluatedType type) {
		// TODO Auto-generated method stub
		return false;
	}
}
