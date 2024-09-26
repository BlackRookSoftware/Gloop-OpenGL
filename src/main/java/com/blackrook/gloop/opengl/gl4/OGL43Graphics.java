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

import static org.lwjgl.opengl.GL43.*;


/**
 * OpenGL 4.3 Graphics Implementation.
 * @author Matthew Tropiano
 */
public class OGL43Graphics extends OGL42Graphics
{
	protected class Info43 extends Info42
	{
		protected Info43()
		{
			super();
			// TODO: Finish.
		}
	}
	
	public OGL43Graphics(Options options, boolean core)
	{
		super(options, core);
	}

	@Override
	public OGLVersion getVersion()
	{
		return OGLVersion.GL43;
	}

	@Override
	protected Info createInfo()
	{
		return new Info43();
	}
	
	// TODO: Finish.

}
