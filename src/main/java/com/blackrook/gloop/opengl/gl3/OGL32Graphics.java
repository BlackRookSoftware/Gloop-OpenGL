/*******************************************************************************
 * Copyright (c) 2021 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at 
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gloop.opengl.gl3;

import com.blackrook.gloop.opengl.OGLVersion;

/**
 * OpenGL 3.2 Graphics Implementation.
 * @author Matthew Tropiano
 */
public class OGL32Graphics extends OGL31Graphics
{
	@Override
	public OGLVersion getVersion()
	{
		return OGLVersion.GL32;
	}

}
