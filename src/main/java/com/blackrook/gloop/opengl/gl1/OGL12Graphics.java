/*******************************************************************************
 * Copyright (c) 2021 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at 
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gloop.opengl.gl1;

import com.blackrook.gloop.opengl.OGLVersion;
import com.blackrook.gloop.opengl.enums.BufferTargetType;
import com.blackrook.gloop.opengl.enums.ColorFormat;
import com.blackrook.gloop.opengl.enums.DataType;
import com.blackrook.gloop.opengl.enums.GeometryType;
import com.blackrook.gloop.opengl.enums.TextureFormat;
import com.blackrook.gloop.opengl.enums.TextureMagFilter;
import com.blackrook.gloop.opengl.enums.TextureMinFilter;
import com.blackrook.gloop.opengl.enums.TextureWrapType;
import com.blackrook.gloop.opengl.exception.GraphicsException;

import static org.lwjgl.opengl.GL12.*;

import java.nio.ByteBuffer;
import java.util.Objects;

/**
 * OpenGL 1.2 Graphics Implementation.
 * @author Matthew Tropiano
 */
public class OGL12Graphics extends OGL11Graphics
{
	/** Current 3D texture binding. */
	private OGLTexture currentTexture3D;

	// Create OpenGL 1.2 context.
	public OGL12Graphics(boolean core)
	{
		super(core);
		this.currentTexture3D = null;
	}

	@Override
	public OGLVersion getVersion()
	{
		return OGLVersion.GL12;
	}
	
	/**
	 * Gets the currently bound 3D texture. 
	 * @return the texture, or null if no bound texture.
	 */
	public OGLTexture getTexture3D()
	{
		return currentTexture3D;
	}

	/**
	 * Sets if 3D texturing is enabled or not.
	 * @param enabled true to enable, false to disable.
	 */
	public void setTexture3DEnabled(boolean enabled)
	{
		setFlag(GL_TEXTURE_3D, enabled);
	}

	/**
	 * Binds a 3D texture object to the current active texture unit.
	 * @param texture the texture to bind.
	 */
	public void setTexture3D(OGLTexture texture)
	{
		Objects.requireNonNull(texture);
		glBindTexture(GL_TEXTURE_3D, texture.getName());
		currentTexture3D = texture;
	}

	/**
	 * Sets the current filtering for the current 3D texture.
	 * @param minFilter the minification filter.
	 * @param magFilter the magnification filter.
	 */
	public void setTexture3DFiltering(TextureMinFilter minFilter, TextureMagFilter magFilter)
	{
		glTexParameteri(GL_TEXTURE_3D, GL_TEXTURE_MAG_FILTER, magFilter.glid);
		glTexParameteri(GL_TEXTURE_3D, GL_TEXTURE_MIN_FILTER, minFilter.glid);
	}
	
	/**
	 * Sets the current filtering for the current 3D texture.
	 * @param minFilter the minification filter.
	 * @param magFilter the magnification filter.
	 * @param anisotropy the anisotropic filtering (2.0 or greater to enable, 1.0 is "off").
	 */
	public void setTexture3DFiltering(TextureMinFilter minFilter, TextureMagFilter magFilter, float anisotropy)
	{
		glTexParameteri(GL_TEXTURE_3D, GL_TEXTURE_MAG_FILTER, magFilter.glid);
		glTexParameteri(GL_TEXTURE_3D, GL_TEXTURE_MIN_FILTER, minFilter.glid);
		
		if (getInfo().supportsTextureAnisotropy())
		{
			anisotropy = Math.max(1.0f, Math.min(getInfo().getMaxTextureAnisotropy(), anisotropy));
			glTexParameterf(GL_TEXTURE_3D, 0x084FE, anisotropy);
		}
	}

	/**
	 * Sets the current wrapping for the current 3D texture.
	 * @param wrapS the wrapping mode, S-axis.
	 * @param wrapT the wrapping mode, T-axis.
	 * @param wrapR the wrapping mode, R-axis.
	 */
	public void setTexture3DWrapping(TextureWrapType wrapS, TextureWrapType wrapT, TextureWrapType wrapR)
	{
		checkFeatureVersion(wrapS);
		checkFeatureVersion(wrapT);
		checkFeatureVersion(wrapR);
		glTexParameteri(GL_TEXTURE_3D, GL_TEXTURE_WRAP_S, wrapS.glValue);
		glTexParameteri(GL_TEXTURE_3D, GL_TEXTURE_WRAP_T, wrapT.glValue);
		glTexParameteri(GL_TEXTURE_3D, GL_TEXTURE_WRAP_R, wrapR.glValue);
	}

	/**
	 * Sends a texture into OpenGL's memory for the current 3D texture at the topmost mipmap level.
	 * @param imageData the image to send.
	 * @param colorFormat the pixel storage format of the buffer data.
	 * @param format the internal format.
	 * @param width the texture width in texels.
	 * @param height the texture height in texels.
	 * @param depth the texture depth in texels.
	 * @param border the texel border to add, if any.
	 * @throws GraphicsException if the buffer provided is not direct.
	 */
	public void setTexture3DData(ByteBuffer imageData, ColorFormat colorFormat, TextureFormat format, int width, int height, int depth, int border)
	{
		setTexture3DData(imageData, colorFormat, format, 0, width, height, depth, border);
	}

	/**
	 * Sends a texture into OpenGL's memory for the current 3D texture.
	 * @param imageData the image to send.
	 * @param colorFormat the pixel storage format of the buffer data.
	 * @param format the internal format.
	 * @param texlevel the mipmapping level to copy this into (0 is topmost).
	 * @param width the texture width in texels.
	 * @param height the texture height in texels.
	 * @param depth the texture depth in texels.
	 * @param border the texel border to add, if any.
	 * @throws GraphicsException if the buffer provided is not direct.
	 */
	public void setTexture3DData(ByteBuffer imageData, ColorFormat colorFormat, TextureFormat format, int texlevel, int width, int height, int depth, int border)
	{
		if (width > getInfo().getMaxTextureSize() || height > getInfo().getMaxTextureSize() || depth > getInfo().getMaxTextureSize())
			throw new GraphicsException("Texture is too large. Maximum size is " + getInfo().getMaxTextureSize() + " pixels.");
	
		if (!imageData.isDirect())
			throw new GraphicsException("Data must be a direct buffer."); 
		
		checkFeatureVersion(colorFormat);

		clearError();
		glTexImage3D(
			GL_TEXTURE_3D,
			texlevel,
			format.glValue, 
			width,
			height,
			depth,
			border,
			colorFormat.glValue,
			GL_UNSIGNED_BYTE,
			imageData
		);
		getError();
	}

	/**
	 * Sends a subset of data to the currently-bound 2D texture 
	 * already in OpenGL's memory at the topmost mipmap level.
	 * @param imageData the image to send.
	 * @param colorFormat the pixel storage format of the buffer data.
	 * @param width the texture width in texels.
	 * @param height the texture height in texels.
	 * @param depth the texture depth in texels.
	 * @param xoffs the texel offset.
	 * @param yoffs the texel offset.
	 * @throws GraphicsException if the buffer provided is not direct.
	 */
	public void setTexture3DSubData(ByteBuffer imageData, ColorFormat colorFormat, int width, int height, int depth, int xoffs, int yoffs)
	{
		setTexture3DSubData(imageData, colorFormat, 0, width, height, xoffs, yoffs);
	}

	/**
	 * Sends a subset of data to the currently-bound 2D texture already in OpenGL's memory.
	 * @param imageData the image to send.
	 * @param colorFormat the pixel storage format of the buffer data.
	 * @param texlevel	the mipmapping level to copy this into (0 is topmost).
	 * @param width the texture width in texels.
	 * @param height the texture height in texels.
	 * @param depth the texture depth in texels.
	 * @param xoffs the texel offset.
	 * @param yoffs the texel offset.
	 * @param zoffs the texel offset.
	 * @throws GraphicsException if the buffer provided is not direct.
	 */
	public void setTexture3DSubData(ByteBuffer imageData, ColorFormat colorFormat, int texlevel, int width, int depth, int height, int xoffs, int yoffs, int zoffs)
	{
		if (!imageData.isDirect())
			throw new GraphicsException("Data must be a direct buffer."); 
	
		checkFeatureVersion(colorFormat);

		clearError();
		glTexSubImage3D(
			GL_TEXTURE_3D,
			texlevel,
			xoffs,
			yoffs,
			zoffs,
			width,
			height,
			depth,
			colorFormat.glValue,
			GL_UNSIGNED_BYTE,
			imageData
		);
		getError();
	}

	/**
	 * Unbinds a 3D texture from the current texture unit.
	 */
	public void unsetTexture3D()
	{
		glBindTexture(GL_TEXTURE_3D, 0);
		currentTexture3D = null;
	}

	/**
	 * Draws geometry using the current bound, enabled coordinate arrays/buffers as data, plus
	 * an element buffer to describe the ordering.
	 * @param geometryType the geometry type - tells how to interpret the data.
	 * @param dataType the data type of the indices in the {@link BufferTargetType#INDICES}-bound buffer (must be an unsigned type).
	 * @param startIndex the starting index into the {@link BufferTargetType#INDICES}-bound buffer.
	 * @param endIndex the ending index in the range.
	 * @param count the amount of element indices to read.
	 * @see #setVertexArrayEnabled(boolean)
	 * @see #setTextureCoordinateArrayEnabled(boolean)
	 * @see #setNormalArrayEnabled(boolean)
	 * @see #setColorArrayEnabled(boolean)
	 * @see #setVertexArrayPointer(DataType, int, int, int)
	 * @see #setTextureCoordinateArrayPointer(DataType, int, int, int)
	 * @see #setNormalArrayPointer(DataType, int, int)
	 * @see #setColorArrayPointer(DataType, int, int, int)
	 */
	public void drawGeometryElementRange(GeometryType geometryType, DataType dataType, int startIndex, int endIndex, int count)
	{
		glDrawRangeElements(geometryType.glValue, startIndex, endIndex, count, dataType.glValue, 0L);
		getError();
	}
	
	

}
