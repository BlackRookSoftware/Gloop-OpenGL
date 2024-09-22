/*******************************************************************************
 * Copyright (c) 2021-2024 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at 
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gloop.opengl.gl3;

import com.blackrook.gloop.opengl.OGLVersion;

import com.blackrook.gloop.opengl.OGLSystem.Options;
import com.blackrook.gloop.opengl.enums.LogicFunc;
import com.blackrook.gloop.opengl.enums.TextureMagFilter;
import com.blackrook.gloop.opengl.enums.TextureMinFilter;
import com.blackrook.gloop.opengl.enums.TextureWrapType;

import java.nio.FloatBuffer;

import org.lwjgl.system.MemoryStack;

import static org.lwjgl.opengl.GL33.*;


/**
 * OpenGL 3.3 Graphics Implementation.
 * @author Matthew Tropiano
 */
public class OGL33Graphics extends OGL32Graphics
{
	public OGL33Graphics(Options options, boolean core)
	{
		super(options, core);
	}

	@Override
	public OGLVersion getVersion()
	{
		return OGLVersion.GL33;
	}

	@Override
	protected void endFrame() 
	{
		// Clean up abandoned objects.
		OGLSampler.destroyUndeleted();
		super.endFrame();
	}
	
	/**
	 * Creates a new sampler object.
	 * @return the new sampler object.
	 */
	public OGLSampler createSampler()
	{
		return new OGLSampler();
	}
	
	/**
	 * Destroys a sampler object.
	 * @param sampler the sampler to destroy.
	 */
	public void destroySampler(OGLSampler sampler) 
	{
		destroyObject(sampler);
		checkError();
	}
	
	/**
	 * Sets the sampler to use for a given texture unit.
	 * @param sampler the sampler to bind.
	 * @param textureUnit the texture unit to bind it to.
	 */
	public void setSampler(OGLSampler sampler, int textureUnit)
	{
		glBindSampler(textureUnit, sampler.getName());
	}

	/**
	 * Sets the S-coordinate wrapping for a sampler.
	 * @param sampler the sampler to set the parameter on.
	 * @param wrapType the new wrapping type.
	 */
	public void setSamplerWrapS(OGLSampler sampler, TextureWrapType wrapType)
	{
		glSamplerParameteri(sampler.getName(), GL_TEXTURE_WRAP_S, wrapType.glValue);
		checkError();
	}
	
	/**
	 * Sets the T-coordinate wrapping for a sampler.
	 * @param sampler the sampler to set the parameter on.
	 * @param wrapType the new wrapping type.
	 */
	public void setSamplerWrapT(OGLSampler sampler, TextureWrapType wrapType)
	{
		glSamplerParameteri(sampler.getName(), GL_TEXTURE_WRAP_T, wrapType.glValue);
		checkError();
	}
	
	/**
	 * Sets the R-coordinate wrapping for a sampler.
	 * @param sampler the sampler to set the parameter on.
	 * @param wrapType the new wrapping type.
	 */
	public void setSamplerWrapR(OGLSampler sampler, TextureWrapType wrapType)
	{
		glSamplerParameteri(sampler.getName(), GL_TEXTURE_WRAP_R, wrapType.glValue);
		checkError();
	}
	
	/**
	 * Sets the texture filtering types for a sampler.
	 * @param sampler the sampler to set the parameters on.
	 * @param minFilter the minification filter to use.
	 * @param magFilter the magnification filter to use.
	 */
	public void setSamplerFiltering(OGLSampler sampler, TextureMinFilter minFilter, TextureMagFilter magFilter)
	{
		glSamplerParameteri(sampler.getName(), GL_TEXTURE_MIN_FILTER, minFilter.glid);
		glSamplerParameteri(sampler.getName(), GL_TEXTURE_MAG_FILTER, magFilter.glid);
	}
	
	/**
	 * Sets the level-of-detail bias on this sampler.
	 * @param sampler the sampler to set the parameters on.
	 * @param minLOD the minimum LOD bias. This value limits the selection of highest resolution mipmap (lowest mipmap level).
	 * @param maxLOD the maximum LOD bias. This value limits the selection of lowest resolution mipmap (highest mipmap level).
	 */
	public void setSamplerLODBias(OGLSampler sampler, float minLOD, float maxLOD)
	{
		glSamplerParameterf(sampler.getName(), GL_TEXTURE_MIN_LOD, minLOD);
		checkError();
		glSamplerParameterf(sampler.getName(), GL_TEXTURE_MAX_LOD, maxLOD);
		checkError();
	}
	
	/**
	 * Sets the texture border color on a sampler object.
	 * @param sampler the sampler to set the parameter on.
	 * @param red the red component value (0 to 1).
	 * @param green the green component value (0 to 1).
	 * @param blue the blue component value (0 to 1).
	 * @param alpha the alpha component value (0 to 1).
	 */
	public void setSamplerTextureBorderColor(OGLSampler sampler, float red, float green, float blue, float alpha)
	{
		try (MemoryStack stack = MemoryStack.stackPush())
		{
			FloatBuffer fbuf = stack.mallocFloat(4);
			fbuf.put(0, red);
			fbuf.put(1, green);
			fbuf.put(2, blue);
			fbuf.put(3, alpha);
			glSamplerParameterfv(sampler.getName(), GL_TEXTURE_BORDER_COLOR, fbuf);
			checkError();
		}
	}
	
	/**
	 * Sets the texture compare mode on this sampler.
	 * This is for a texture that is a depth-component texture.
	 * @param sampler the sampler to set the parameter on.
	 * @param enabled if true, enable depth comparison on this sampler.
	 */
	public void setSamplerTextureCompareMode(OGLSampler sampler, boolean enabled)
	{
		glSamplerParameteri(sampler.getName(), GL_TEXTURE_COMPARE_MODE, enabled ? GL_COMPARE_REF_TO_TEXTURE : GL_NONE);
	}
	
	/**
	 * Sets the texture compare function on this sampler.
	 * This is for a texture that is a depth-component texture.
	 * @param sampler the sampler to set the parameter on.
	 * @param func if true, enable depth comparison on this sampler.
	 * @see #setSamplerTextureCompareMode(OGLSampler, boolean)
	 */
	public void setSamplerTextureCompareFunction(OGLSampler sampler, LogicFunc func)
	{
		glSamplerParameteri(sampler.getName(), GL_TEXTURE_COMPARE_FUNC, func.glValue);
	}
	
}
