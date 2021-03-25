/*******************************************************************************
 * Copyright (c) 2021 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at 
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gloop.opengl.gl1;

import java.io.Closeable;
import java.nio.ByteBuffer;
import java.util.Objects;

import com.blackrook.gloop.opengl.exception.GraphicsException;
import com.blackrook.gloop.opengl.gl1.enums.ColorFormat;
import com.blackrook.gloop.opengl.gl1.enums.TextureCubeFace;
import com.blackrook.gloop.opengl.gl1.enums.TextureFormat;
import com.blackrook.gloop.opengl.gl1.enums.TextureMagFilter;
import com.blackrook.gloop.opengl.gl1.enums.TextureMinFilter;
import com.blackrook.gloop.opengl.gl1.enums.TextureWrapType;
import com.blackrook.gloop.opengl.gl1.objects.OGLTexture;

import static org.lwjgl.opengl.GL13.*;

/**
 * OpenGL 1.3 Graphics Implementation.
 * @author Matthew Tropiano
 */
public class OGL13Graphics extends OGL12Graphics
{
	protected class Info13 extends Info11
	{
		protected Info13()
		{
			super();
			this.maxMultitexture = getInt(GL_MAX_TEXTURE_UNITS);
		}
	}
	
	/**
	 * A try-with-resources latch that unbinds a texture Cube target 
	 * after it escapes the <code>try</code>. 
	 */
	public class TextureCubeLatch implements Closeable
	{
		@Override
		public void close()
		{
			unsetTextureCube();
		}
	}
	
	@Override
	protected Info createInfo()
	{
		return new Info13();
	}
	
	/**
	 * Sets if cube map texturing is enabled or not.
	 * @param enabled true to enable, false to disable.
	 */
	public void setTextureCubeEnabled(boolean enabled)
	{
		setFlag(GL_TEXTURE_CUBE_MAP, enabled);
	}

	/**
	 * Sets the current "active" texture unit for texture bindings and texture environment settings.
	 * @param unit the texture unit to switch to.
	 */
	public void setTextureUnit(int unit)
	{
		glActiveTexture(GL_TEXTURE0 + unit);
	}

	/**
	 * Binds a texture cube object to the current active texture unit.
	 * This returns an optional latch object for unbinding the texture 
	 * from the cube target if this is used in a try-with-resources block.
	 * @param texture the texture to bind.
	 * @return an optional latch object.
	 */
	public TextureCubeLatch setTextureCube(OGLTexture texture)
	{
		Objects.requireNonNull(texture);
		glBindTexture(GL_TEXTURE_CUBE_MAP, texture.getName());
		return new TextureCubeLatch();
	}

	/**
	 * Sets the current filtering for the current cube texture.
	 * @param minFilter the minification filter.
	 * @param magFilter the magnification filter.
	 */
	public void setTextureFilteringCube(TextureMinFilter minFilter, TextureMagFilter magFilter)
	{
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, magFilter.glid);
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, minFilter.glid);
	}

	/**
	 * Sets the current filtering for the current cube texture.
	 * @param minFilter the minification filter.
	 * @param magFilter the magnification filter.
	 * @param anisotropy the anisotropic filtering (2.0 or greater to enable, 1.0 is "off").
	 */
	public void setTextureFilteringCube(TextureMinFilter minFilter, TextureMagFilter magFilter, float anisotropy)
	{
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, magFilter.glid);
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, minFilter.glid);
		
		if (getInfo().supportsTextureAnisotropy())
		{
			anisotropy = Math.max(1.0f, Math.min(getInfo().getMaxTextureAnisotropy(), anisotropy));
			glTexParameterf(GL_TEXTURE_CUBE_MAP, 0x084FE, anisotropy);
		}
	}

	/**
	 * Sets the current wrapping for the current CubeMap texture.
	 * @param wrapS the wrapping mode, S-axis.
	 * @param wrapT the wrapping mode, T-axis.
	 * @param wrapR the wrapping mode, R-axis.
	 */
	public void setTextureWrappingCube(TextureWrapType wrapS, TextureWrapType wrapT, TextureWrapType wrapR)
	{
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_S, wrapS.glid);
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_T, wrapT.glid);
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_R, wrapR.glid);
	}

	/**
	 * Sends a texture into OpenGL's memory for the current CubeMap texture at the topmost mipmap level.
	 * @param face the cube face to set.
	 * @param imageData the image to send.
	 * @param colorFormat the pixel color format of the buffer data.
	 * @param format the internal format.
	 * @param width the texture width in texels.
	 * @param height the texture height in texels.
	 * @param border the texel border to add, if any.
	 * @throws GraphicsException if the buffer provided is not direct.
	 */
	public void setTextureDataCube(TextureCubeFace face, ByteBuffer imageData, ColorFormat colorFormat, TextureFormat format, int width, int height, int border)
	{
		setTextureDataCube(face, imageData, colorFormat, format, 0, width, height, border);
	}

	/**
	 * Sends a texture into OpenGL's memory for the current CubeMap texture.
	 * @param face the cube face to set.
	 * @param imageData the image to send.
	 * @param colorFormat the pixel color format of the buffer data.
	 * @param format the internal format.
	 * @param texlevel the mipmapping level to copy this into (0 is topmost).
	 * @param width the texture width in texels.
	 * @param height the texture height in texels.
	 * @param border the texel border to add, if any.
	 * @throws GraphicsException if the buffer provided is not direct.
	 */
	public void setTextureDataCube(TextureCubeFace face, ByteBuffer imageData, ColorFormat colorFormat, TextureFormat format, int texlevel, int width, int height, int border)
	{
		if (width > getInfo().getMaxTextureSize() || height > getInfo().getMaxTextureSize())
			throw new GraphicsException("Texture is too large. Maximum size is " + getInfo().getMaxTextureSize() + " pixels.");
		
		if (!imageData.isDirect())
			throw new GraphicsException("Data must be a direct buffer."); 
	
		clearError();
		glTexImage2D(
			face.glValue,
			texlevel,
			format.glid, 
			width,
			height,
			border,
			colorFormat.glid,
			GL_UNSIGNED_BYTE,
			imageData
		);
		getError();
	}

	/**
	 * Copies the contents of the current read frame buffer into the 
	 * current texture cube target already in OpenGL's memory at the topmost mipmap level.
	 * @param face      the cube face target.
	 * @param xoffset	the offset in pixels on this texture (x-coordinate) to put this texture data.
	 * @param yoffset	the offset in pixels on this texture (y-coordinate) to put this texture data.
	 * @param srcX		the screen-aligned x-coordinate of what to grab from the buffer (0 is the left side of the screen).
	 * @param srcY		the screen-aligned y-coordinate of what to grab from the buffer (0 is the bottom of the screen).
	 * @param width		the width of the screen in pixels to grab.
	 * @param height	the height of the screen in pixels to grab.
	 */
	public void copyBufferToTextureDataCube(TextureCubeFace face, int xoffset, int yoffset, int srcX, int srcY, int width, int height)
	{
		copyBufferToTextureDataCube(face, 0, xoffset, yoffset, srcX, srcY, width, height);
	}

	/**
	 * Copies the contents of the current read frame buffer into the 
	 * current texture cube target already in OpenGL's memory.
	 * @param face      the cube face target.
	 * @param texlevel	the mipmapping level to copy this into (0 is topmost level).
	 * @param xoffset	the offset in pixels on this texture (x-coordinate) to put this texture data.
	 * @param yoffset	the offset in pixels on this texture (y-coordinate) to put this texture data.
	 * @param srcX		the screen-aligned x-coordinate of what to grab from the buffer (0 is the left side of the screen).
	 * @param srcY		the screen-aligned y-coordinate of what to grab from the buffer (0 is the bottom of the screen).
	 * @param width		the width of the screen in pixels to grab.
	 * @param height	the height of the screen in pixels to grab.
	 */
	public void copyBufferToTextureDataCube(TextureCubeFace face, int texlevel, int xoffset, int yoffset, int srcX, int srcY, int width, int height)
	{
		glCopyTexImage2D(face.glValue, texlevel, xoffset, yoffset, srcX, srcY, width, height);
	}

	/**
	 * Sends a subset of data to the currently-bound texture cube 
	 * already in OpenGL's memory at the topmost mipmap level.
	 * @param face the cube face to set.
	 * @param imageData the BGRA image to send.
	 * @param colorFormat the pixel color format of the buffer data.
	 * @param width the texture width in texels.
	 * @param height the texture height in texels.
	 * @param xoffs the texel offset.
	 * @param yoffs the texel offset.
	 * @throws GraphicsException if the buffer provided is not direct.
	 */
	public void setTextureSubDataCube(TextureCubeFace face, ByteBuffer imageData, ColorFormat colorFormat, int width, int height, int xoffs, int yoffs)
	{
		setTextureSubDataCube(face, imageData, colorFormat, 0, width, height, xoffs, yoffs);
	}

	/**
	 * Sends a subset of data to the currently-bound texture cube already in OpenGL's memory.
	 * @param face the cube face to set.
	 * @param imageData the BGRA image to send.
	 * @param colorFormat the pixel color format of the buffer data.
	 * @param texlevel the mipmapping level to copy this into (0 is topmost).
	 * @param width the texture width in texels.
	 * @param height the texture height in texels.
	 * @param xoffs the texel offset.
	 * @param yoffs the texel offset.
	 * @throws GraphicsException if the buffer provided is not direct.
	 */
	public void setTextureSubDataCube(TextureCubeFace face, ByteBuffer imageData, ColorFormat colorFormat, int texlevel, int width, int height, int xoffs, int yoffs)
	{
		if (!imageData.isDirect())
			throw new GraphicsException("Data must be a direct buffer."); 
	
		clearError();
		glTexSubImage2D(
			face.glValue,
			texlevel,
			xoffs,
			yoffs,
			width,
			height,
			GL_BGRA,
			GL_UNSIGNED_BYTE,
			imageData
		);
		getError();
	}

	/**
	 * Copies the contents of the current read frame buffer into the 
	 * current texture cube target already in OpenGL's memory at the topmost mipmap level.
	 * @param face      the cube face target.
	 * @param xoffset	the offset in pixels on this texture (x-coordinate) to put this texture data.
	 * @param yoffset	the offset in pixels on this texture (y-coordinate) to put this texture data.
	 * @param srcX		the screen-aligned x-coordinate of what to grab from the buffer (0 is the left side of the screen).
	 * @param srcY		the screen-aligned y-coordinate of what to grab from the buffer (0 is the bottom of the screen).
	 * @param width		the width of the screen in pixels to grab.
	 * @param height	the height of the screen in pixels to grab.
	 */
	public void copyBufferToTextureSubDataCube(TextureCubeFace face, int xoffset, int yoffset, int srcX, int srcY, int width, int height)
	{
		copyBufferToTextureDataCube(face, 0, xoffset, yoffset, srcX, srcY, width, height);
	}

	/**
	 * Copies the contents of the current read frame buffer into the 
	 * current texture cube target already in OpenGL's memory.
	 * @param face      the cube face target.
	 * @param texlevel	the mipmapping level to copy this into (0 is topmost level).
	 * @param xoffset	the offset in pixels on this texture (x-coordinate) to put this texture data.
	 * @param yoffset	the offset in pixels on this texture (y-coordinate) to put this texture data.
	 * @param srcX		the screen-aligned x-coordinate of what to grab from the buffer (0 is the left side of the screen).
	 * @param srcY		the screen-aligned y-coordinate of what to grab from the buffer (0 is the bottom of the screen).
	 * @param width		the width of the screen in pixels to grab.
	 * @param height	the height of the screen in pixels to grab.
	 */
	public void copyBufferToTextureSubDataCube(TextureCubeFace face, int texlevel, int xoffset, int yoffset, int srcX, int srcY, int width, int height)
	{
		glCopyTexSubImage2D(face.glValue, texlevel, xoffset, yoffset, srcX, srcY, width, height);
	}

	/**
	 * Unbinds a texture cube from the current texture unit.
	 */
	public void unsetTextureCube()
	{
		glBindTexture(GL_TEXTURE_CUBE_MAP, 0);
	}

}