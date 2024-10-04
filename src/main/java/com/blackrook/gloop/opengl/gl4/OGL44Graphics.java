/*******************************************************************************
 * Copyright (c) 2021-2024 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at 
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gloop.opengl.gl4;

import com.blackrook.gloop.opengl.OGLVersion;
import com.blackrook.gloop.opengl.OGLSystem.Options;

import static org.lwjgl.opengl.GL44.*;


/**
 * OpenGL 4.4 Graphics Implementation.
 * @author Matthew Tropiano
 */
public class OGL44Graphics extends OGL43Graphics
{
	protected class Info44 extends Info43
	{
		protected Info44()
		{
			super();
			this.maxVertexAttribStride = getInt(GL_MAX_VERTEX_ATTRIB_STRIDE);
		}
	}
	
	public OGL44Graphics(Options options, boolean core)
	{
		super(options, core);
	}

	@Override
	public OGLVersion getVersion()
	{
		return OGLVersion.GL44;
	}

	@Override
	protected Info createInfo()
	{
		return new Info44();
	}
	
	// TODO: Finish.

}
