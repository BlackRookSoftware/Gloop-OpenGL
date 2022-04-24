/*******************************************************************************
 * Copyright (c) 2021-2022 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at 
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gloop.opengl;

/**
 * Describes an object that can only be used with specific versions of OpenGL.
 * @author Matthew Tropiano
 */
public interface OGLVersioned
{
	/**
	 * @return the version that this object or value is available for (and higher).
	 */
	OGLVersion getVersion();
	
	/**
	 * @return true if this object is considered part of core spec, false otherwise.
	 */
	boolean isCore();
	
}
