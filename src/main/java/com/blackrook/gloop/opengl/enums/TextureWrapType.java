/*******************************************************************************
 * Copyright (c) 2021 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at 
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gloop.opengl.enums;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.blackrook.gloop.opengl.OGLVersion;
import com.blackrook.gloop.opengl.OGLVersioned;

/**
 * Texture wrapping types.
 * @author Matthew Tropiano
 */
public enum TextureWrapType implements OGLVersioned
{
	/** 
	 * Texture coordinates wrap to the other side. 
	 * Edge colors interpolate accordingly. 
	 */
	TILE(OGLVersion.GL11, GL11.GL_REPEAT),
	
	/** 
	 * Texture coordinates clamp to [0,1]. 
	 * Edge colors are interpolated with the border color. 
	 */
	CLAMP(OGLVersion.GL11, GL11.GL_CLAMP),
	
	/** 
	 * Texture coordinates clamp to [0,1]. 
	 * Edge colors are interpolated with the edge texel's color. 
	 */
	CLAMP_TO_EDGE(OGLVersion.GL12, GL12.GL_CLAMP_TO_EDGE);

	private final OGLVersion version;
	public final int glValue;

	private TextureWrapType(OGLVersion version, int glValue) 
	{
		this.version = version;
		this.glValue = glValue;
	}
	
	@Override
	public OGLVersion getVersion()
	{
		return version;
	}
	
}

