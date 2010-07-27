/*******************************************************************************
 * Copyright (c) 2009 xored software, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package org.eclipse.dltk.ui.documentation;

import java.io.IOException;
import java.io.Reader;
import java.net.URL;

/**
 * Value object to return script documentation. All implementations should
 * extend {@link AbstractDocumentationResponse}
 * 
 * @since 2.0
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface IDocumentationResponse {

	/**
	 * Returns the object this documentation applies to
	 * 
	 * @return
	 */
	Object getObject();

	/**
	 * Returns the URL of the documentation source if applicable or
	 * <code>null</code>
	 * 
	 * @return
	 * @throws IOException
	 */
	URL getURL() throws IOException;

	/**
	 * Return the Reader to read the documentation. Every call will return new
	 * reader object.
	 * 
	 * @return
	 * @throws IOException
	 */
	Reader getReader() throws IOException;
}
