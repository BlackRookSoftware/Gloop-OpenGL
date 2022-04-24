/*******************************************************************************
 * Copyright (c) 2021-2022 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at 
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gloop.opengl.gl3;

import com.blackrook.gloop.opengl.OGLVersion;

/**
 * OpenGL 3.1 Graphics Implementation.
 * @author Matthew Tropiano
 */
public class OGL31Graphics extends OGL30Graphics
{
	public OGL31Graphics(boolean core)
	{
		super(core);
	}

	@Override
	public OGLVersion getVersion()
	{
		return OGLVersion.GL31;
	}

}
