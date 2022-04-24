/*******************************************************************************
 * Copyright (c) 2021-2022 Black Rook Software
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
import com.blackrook.gloop.opengl.exception.GraphicsException;

/**
 * Texture target types.
 * @author Matthew Tropiano
 */
public enum TextureTargetType implements OGLVersioned
{
	TEXTURE_1D        (OGLVersion.GL11, 1, 1, GL11.GL_TEXTURE_1D),
	TEXTURE_2D        (OGLVersion.GL11, 2, 2, GL11.GL_TEXTURE_2D),
	TEXTURE_3D        (OGLVersion.GL12, 3, 3, GL12.GL_TEXTURE_3D),
	TEXTURE_CUBE      (OGLVersion.GL13, 3, 2, GL13.GL_TEXTURE_CUBE_MAP),
	TEXTURE_1D_ARRAY  (OGLVersion.GL30, 1, 2, GL30.GL_TEXTURE_1D_ARRAY),
	TEXTURE_2D_ARRAY  (OGLVersion.GL30, 2, 3, GL30.GL_TEXTURE_2D_ARRAY),
	TEXTURE_RECTANGLE (OGLVersion.GL31, 2, 2, GL31.GL_TEXTURE_RECTANGLE);
	
	private final OGLVersion version;
	private int sampleDimensions;
	private int storageDimensions;
	public final int glValue;

	private TextureTargetType(OGLVersion version, int sampleDimensions, int storageDimensions, int glValue) 
	{
		this.version = version;
		this.sampleDimensions = sampleDimensions;
		this.storageDimensions = storageDimensions;
		this.glValue = glValue;
	}
	
	/**
	 * @return how many dimensions the texture is sampled through.
	 */
	public int getSampleDimensions()
	{
		return sampleDimensions;
	}
	
	/**
	 * @return how many dimensions the texture is in storage.
	 */
	public int getStorageDimensions()
	{
		return storageDimensions;
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
	
	/**
	 * Checks if this target supports the provided amount of dimensions for storage.
	 * @param dimensions the amount of dimensions.
	 * @throws GraphicsException if no match.
	 */
	public void checkSampleDimensions(int dimensions)
	{
		if (sampleDimensions != dimensions)
			throw new GraphicsException("Sampling/Wrapping dimensions for this target must be " + sampleDimensions + ", not " + dimensions);
	}
	
	/**
	 * Checks if this target supports the provided amount of dimensions for storage.
	 * @param dimensions the amount of dimensions.
	 * @throws GraphicsException if no match.
	 */
	public void checkStorageDimensions(int dimensions)
	{
		if (storageDimensions != dimensions)
			throw new GraphicsException("Storage dimensions for this target must be " + storageDimensions + ", not " + dimensions);
	}
	
}

