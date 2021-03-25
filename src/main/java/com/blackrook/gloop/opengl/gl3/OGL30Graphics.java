/*******************************************************************************
 * Copyright (c) 2021 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at 
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gloop.opengl.gl3;

import com.blackrook.gloop.opengl.gl2.OGL21Graphics;

import static org.lwjgl.opengl.GL30.*;

/**
 * OpenGL 3.0 Graphics Implementation.
 * @author Matthew Tropiano
 */
public class OGL30Graphics extends OGL21Graphics
{
	protected class Info30 extends Info20
	{
		protected Info30()
		{
			super();
			this.maxRenderBufferSize = getInt(GL_MAX_RENDERBUFFER_SIZE);
			this.maxRenderBufferColorAttachments = getInt(GL_MAX_COLOR_ATTACHMENTS);
		}
	}
	
	@Override
	protected Info createInfo()
	{
		return new Info30();
	}
	
	/**
	 * Generates mipmaps on-demand internally for the current 1D texture target.
	 */
	public void generateMipmapTexture1D()
	{
		glGenerateMipmap(GL_TEXTURE_1D);
	}

	/**
	 * Generates mipmaps on-demand internally for the current 1D texture array target.
	 */
	public void generateMipmapTexture1DArray()
	{
		glGenerateMipmap(GL_TEXTURE_1D_ARRAY);
	}

	/**
	 * Generates mipmaps on-demand internally for the current 2D texture target.
	 */
	public void generateMipmapTexture2D()
	{
		glGenerateMipmap(GL_TEXTURE_2D);
	}

	/**
	 * Generates mipmaps on-demand internally for the current 2D texture array target.
	 */
	public void generateMipmapTexture2DArray()
	{
		glGenerateMipmap(GL_TEXTURE_2D_ARRAY);
	}

	/**
	 * Generates mipmaps on-demand internally for the current texture cube target.
	 */
	public void generateMipmapTextureCube()
	{
		glGenerateMipmap(GL_TEXTURE_CUBE_MAP);
	}

	/**
	 * Generates mipmaps on-demand internally for the current 3D texture target.
	 */
	public void generateMipmapTexture3D()
	{
		glGenerateMipmap(GL_TEXTURE_3D);
	}

	/**
	 * Sets a uniform unsigned integer value on the currently-bound shader.
	 * @param locationId the uniform location.
	 * @param value the value to set.
	 */
	public void setShaderUniformIntUnsigned(int locationId, int value)
	{
		glUniform1ui(locationId, value);
	}

	/**
	 * Sets a uniform unsigned integer value array on the currently-bound shader.
	 * @param locationId the uniform location.
	 * @param values the values to set.
	 */
	public void setShaderUniformIntUnsignedArray(int locationId, int ... values)
	{
		glUniform1uiv(locationId, values);
	}

}
