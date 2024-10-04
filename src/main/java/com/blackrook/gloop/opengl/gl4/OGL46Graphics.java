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

import static org.lwjgl.opengl.GL46.*;


/**
 * OpenGL 4.6 Graphics Implementation.
 * @author Matthew Tropiano
 */
public class OGL46Graphics extends OGL45Graphics
{
	protected class Info46 extends Info45
	{
		protected Info46()
		{
			super();
			this.maxTextureMaxAnisotropy = getFloat(GL_MAX_TEXTURE_MAX_ANISOTROPY);
		}
	}
	
	public OGL46Graphics(Options options, boolean core)
	{
		super(options, core);
	}

	@Override
	public OGLVersion getVersion()
	{
		return OGLVersion.GL46;
	}

	@Override
	protected Info createInfo()
	{
		return new Info46();
	}
	
	// TODO: Finish.

}
