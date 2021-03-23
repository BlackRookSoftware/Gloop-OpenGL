/*******************************************************************************
 * Copyright (c) 2021 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at 
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gloop.opengl;

import java.util.Set;

/**
 * A common encapsulator for all OpenGL implementations.
 * @author Matthew Tropiano
 */
public interface OGLGraphics
{
	/**
	 * @return the OpenGL version string, according to the native implementation.
	 */
	String getVersion();
	
	/**
	 * @return the OpenGL GLSL version string, according to the native implementation.
	 */
	String getShadingLanguageVersion();

	/**
	 * @return the OpenGL vendor string, according to the native implementation.
	 */
	String getVendor();

	/**
	 * @return the OpenGL renderer string, according to the native implementation.
	 */
	String getRenderer();

	/**
	 * @return the OpenGL extension names as a Set, according to the native implementation.
	 */
	Set<String> getExtensionNames();

}
