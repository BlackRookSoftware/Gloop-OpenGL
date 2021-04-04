/*******************************************************************************
 * Copyright (c) 2021 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at 
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gloop.opengl.gl3;

import com.blackrook.gloop.opengl.OGLVersion;
import com.blackrook.gloop.opengl.math.MatrixStack;

/**
 * OpenGL 3.2 Graphics Implementation.
 * The implementation of the matrix operations are done using a {@link MatrixStack}.
 * @author Matthew Tropiano
 */
public class OGL32Graphics extends OGL31Graphics
{
	public OGL32Graphics(boolean core)
	{
		super(core);
	}

	@Override
	public OGLVersion getVersion()
	{
		return OGLVersion.GL32;
	}

}
