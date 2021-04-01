/*******************************************************************************
 * Copyright (c) 2021 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at 
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gloop.opengl.enums;

import com.blackrook.gloop.opengl.OGLVersion;
import com.blackrook.gloop.opengl.OGLVersioned;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

/**
 * Color pixel storage format.
 */
public enum ColorFormat implements OGLVersioned
{
	COLOR_INDEX(OGLVersion.GL11, false, GL11.GL_COLOR_INDEX),
	STENCIL_INDEX(OGLVersion.GL11, true, GL11.GL_STENCIL_INDEX),
	DEPTH_COMPONENT(OGLVersion.GL11, true, GL11.GL_DEPTH_COMPONENT),
	RED(OGLVersion.GL11, true, GL11.GL_RED),
	GREEN(OGLVersion.GL11, true, GL11.GL_GREEN),
	BLUE(OGLVersion.GL11, true, GL11.GL_BLUE),
	ALPHA(OGLVersion.GL11, false, GL11.GL_ALPHA),
	RGB(OGLVersion.GL11, true, GL11.GL_RGB),
	RGBA(OGLVersion.GL11, true, GL11.GL_RGBA),
	LUMINANCE(OGLVersion.GL11, false, GL11.GL_LUMINANCE),
	LUMINANCE_ALPHA(OGLVersion.GL11, false, GL11.GL_LUMINANCE_ALPHA),
	
	BGR(OGLVersion.GL12, true, GL12.GL_BGR),
	BGRA(OGLVersion.GL12, true, GL12.GL_BGRA);
	
	private final OGLVersion version;
	private final boolean core;
	public final int glValue;

	private ColorFormat(OGLVersion version, boolean core, int id) 
	{
		this.version = version;
		this.core = core;
		this.glValue = id;
	}
	
	@Override
	public OGLVersion getVersion()
	{
		return version;
	}
	
	@Override
	public boolean isCore()
	{
		return core;
	}

}

