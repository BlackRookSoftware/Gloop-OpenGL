/*******************************************************************************
 * Copyright (c) 2021 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at 
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gloop.opengl.gl1;

import java.nio.FloatBuffer;

import org.lwjgl.system.MemoryStack;

import com.blackrook.gloop.opengl.OGLVersion;
import com.blackrook.gloop.opengl.enums.TextureMagFilter;
import com.blackrook.gloop.opengl.enums.TextureMinFilter;
import com.blackrook.gloop.opengl.enums.TextureTargetType;

import static org.lwjgl.opengl.GL14.*;

/**
 * OpenGL 1.4 Graphics Implementation.
 * @author Matthew Tropiano
 */
public class OGL14Graphics extends OGL13Graphics
{
	public OGL14Graphics(boolean core)
	{
		super(core);
	}

	@Override
	public OGLVersion getVersion()
	{
		return OGLVersion.GL14;
	}
	
	/**
	 * Sets the maximum size for the diameter of Point geometry when
	 * it is attenuated by point distance from the "camera".
	 * @param size the minimum size.
	 */
	public void setPointAttenuationMaximum(float size)
	{
		glPointParameterf(GL_POINT_SIZE_MAX, size);
	}

	/**
	 * Sets the minimum size for the diameter of Point geometry when
	 * it is attenuated by point distance from the "camera".
	 * @param size the minimum size.
	 */
	public void setPointAttenuationMinimum(float size)
	{
		glPointParameterf(GL_POINT_SIZE_MIN, size);
	}

	/**
	 * Sets the attenuation formula to use when changing the sizes
	 * of points based on their location in space.
	 * @param constant the formula constant coefficient.
	 * @param linear the formula linear coefficient.
	 * @param quadratic the formula quadratic coefficient.
	 */
	public void setPointAttenuationFormula(float constant, float linear, float quadratic)
	{
		try (MemoryStack stack = MemoryStack.stackPush())
		{
			FloatBuffer fbuf = stack.mallocFloat(3);
			fbuf.put(0, constant);
			fbuf.put(1, linear);
			fbuf.put(2, quadratic);
			glPointParameterfv(GL_POINT_DISTANCE_ATTENUATION, fbuf);
		}
	}

	/**
	 * Sets the Level Of Detail bias for automatic texture mipmapping.
	 * @param bias the bias value.
	 */
	public void setTextureLODBias(float bias)
	{
		checkNonCore();
		glTexEnvf(GL_TEXTURE_FILTER_CONTROL, GL_TEXTURE_LOD_BIAS, bias);
	}

	/**
	 * Sets the filtering for the current texture bound to the specified target.
	 * @param target the texture target.
	 * @param minFilter the minification filter.
	 * @param magFilter the magnification filter.
	 * @param genMipmaps if this generates mipmaps automatically.
	 */
	public void setTextureFiltering(TextureTargetType target, TextureMinFilter minFilter, TextureMagFilter magFilter, boolean genMipmaps)
	{
		setTextureFiltering(target, minFilter, magFilter);
		glTexParameteri(target.glValue, GL_GENERATE_MIPMAP, toGLBool(genMipmaps));
	}

	/**
	 * Sets the filtering for the current texture bound to the specified target.
	 * @param target the texture target.
	 * @param minFilter the minification filter.
	 * @param magFilter the magnification filter.
	 * @param anisotropy the anisotropic filtering (2.0 or greater to enable, 1.0 or less is "off").
	 * @param genMipmaps if this generates mipmaps automatically.
	 */
	public void setTextureFiltering(TextureTargetType target, TextureMinFilter minFilter, TextureMagFilter magFilter, float anisotropy, boolean genMipmaps)
	{
		setTextureFiltering(target, minFilter, magFilter, anisotropy);
		glTexParameteri(target.glValue, GL_GENERATE_MIPMAP, toGLBool(genMipmaps));
	}

}
