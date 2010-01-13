/*******************************************************************************
 * Copyright (c) 2010 xored software, Inc.  
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html  
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package org.eclipse.dltk.ui.wizards;

import org.eclipse.swt.widgets.Composite;

/**
 * @since 2.0
 */
public interface ISourceModuleWizardMode {

	String getId();

	String getName();

	void createControl(Composite parent, int columns);

	/**
	 * Notifies this template if it was enabled
	 * 
	 * @param enabled
	 */
	void setEnabled(boolean enabled);

}
