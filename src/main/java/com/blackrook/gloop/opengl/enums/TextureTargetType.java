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
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL31;

import com.blackrook.gloop.opengl.OGLVersion;
import com.blackrook.gloop.opengl.OGLVersioned;

/**
 * Texture target types.
 * @author Matthew Tropiano
 */
public enum TextureTargetType implements OGLVersioned
{
	TEXTURE_1D        (OGLVersion.GL11, 1, GL11.GL_TEXTURE_1D),
	TEXTURE_2D        (OGLVersion.GL11, 2, GL11.GL_TEXTURE_2D),
	TEXTURE_3D        (OGLVersion.GL12, 3, GL12.GL_TEXTURE_3D),
	TEXTURE_CUBE      (OGLVersion.GL13, 2, GL13.GL_TEXTURE_CUBE_MAP),
	TEXTURE_1D_ARRAY  (OGLVersion.GL30, 2, GL30.GL_TEXTURE_1D_ARRAY),
	TEXTURE_2D_ARRAY  (OGLVersion.GL30, 3, GL30.GL_TEXTURE_2D_ARRAY),
	TEXTURE_RECTANGLE (OGLVersion.GL31, 2, GL31.GL_TEXTURE_RECTANGLE);
	
	private final OGLVersion version;
	private int dimensionality;
	public final int glValue;

	private TextureTargetType(OGLVersion version, int dimensionality, int glValue) 
	{
		this.version = version;
		this.dimensionality = dimensionality;
		this.glValue = glValue;
	}
	
	/**
	 * @return how many dimensions the texture is in storage.
	 */
	public int getDimensionality()
	{
		return dimensionality;
	}
	
	@Override
	public OGLVersion getVersion()
	{
		return version;
	}
	
	@Override
	public boolean isCore()
	{
		return true;
	}
	
}

