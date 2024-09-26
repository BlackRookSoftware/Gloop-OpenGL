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

import static org.lwjgl.opengl.GL45.*;


/**
 * OpenGL 4.5 Graphics Implementation.
 * @author Matthew Tropiano
 */
public class OGL45Graphics extends OGL44Graphics
{
	protected class Info45 extends Info44
	{
		protected Info45()
		{
			super();
			// TODO: Finish.
		}
	}
	
	public OGL45Graphics(Options options, boolean core)
	{
		super(options, core);
	}

	@Override
	public OGLVersion getVersion()
	{
		return OGLVersion.GL45;
	}

	@Override
	protected Info createInfo()
	{
		return new Info45();
	}
	
	// TODO: Finish.

}
