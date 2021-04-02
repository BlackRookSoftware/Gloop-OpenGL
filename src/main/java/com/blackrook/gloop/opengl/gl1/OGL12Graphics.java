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
import com.blackrook.gloop.opengl.enums.TextureTargetType;
import com.blackrook.gloop.opengl.enums.TextureWrapType;
import com.blackrook.gloop.opengl.exception.GraphicsException;

import static org.lwjgl.opengl.GL12.*;

import java.nio.ByteBuffer;

/**
 * OpenGL 1.2 Graphics Implementation.
 * @author Matthew Tropiano
 */
public class OGL12Graphics extends OGL11Graphics
{
	// Create OpenGL 1.2 context.
	public OGL12Graphics(boolean core)
	{
		super(core);
	}

	@Override
	public OGLVersion getVersion()
	{
		return OGLVersion.GL12;
	}
	
	/**
	 * Sets the current wrapping for the current texture bound to the specified target.
	 * @param target the texture target.
	 * @param wrapS the wrapping mode, S-axis.
	 * @param wrapT the wrapping mode, T-axis.
	 * @param wrapR the wrapping mode, R-axis.
	 * @throws GraphicsException if the target is not a three-dimensionally-sampled target.
	 */
	public void setTextureWrapping(TextureTargetType target, TextureWrapType wrapS, TextureWrapType wrapT, TextureWrapType wrapR)
	{
		checkFeatureVersion(target);
		checkFeatureVersion(wrapS);
		checkFeatureVersion(wrapT);
		checkFeatureVersion(wrapR);
		target.checkSampleDimensions(3);
		glTexParameteri(target.glValue, GL_TEXTURE_WRAP_S, wrapS.glValue);
		glTexParameteri(target.glValue, GL_TEXTURE_WRAP_T, wrapT.glValue);
		glTexParameteri(target.glValue, GL_TEXTURE_WRAP_R, wrapR.glValue);
	}
	
	/**
	 * Sends a texture into OpenGL's memory for the current texture bound to the specified target.
	 * @param target the texture target.
	 * @param imageData the image to send.
	 * @param colorFormat the pixel storage format of the buffer data.
	 * @param format the internal format.
	 * @param texlevel the mipmapping level to copy this into (0 is topmost).
	 * @param width the texture width in texels.
	 * @param height the texture height in texels.
	 * @param depth the texture depth in texels.
	 * @param border the texel border to add, if any.
	 * @throws GraphicsException if the buffer provided is not direct, or if the target is not stored three-dimensionally.
	 */
	public void setTextureData(TextureTargetType target, ByteBuffer imageData, ColorFormat colorFormat, TextureFormat format, int texlevel, int width, int height, int depth, int border)
	{
		checkFeatureVersion(target);
		checkFeatureVersion(colorFormat);
		checkFeatureVersion(format);
		target.checkStorageDimensions(3);

		if (width > getInfo().getMaxTextureSize() || height > getInfo().getMaxTextureSize() || depth > getInfo().getMaxTextureSize())
			throw new GraphicsException("Texture is too large. Maximum size is " + getInfo().getMaxTextureSize() + " pixels.");
	
		if (!imageData.isDirect())
			throw new GraphicsException("Data must be a direct buffer."); 
		
		clearError();
		glTexImage3D(
			target.glValue,
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
	 * Sends a subset of data to the currently-bound 2D texture already in OpenGL's memory.
	 * @param target the texture target.
	 * @param imageData the image to send.
	 * @param colorFormat the pixel storage format of the buffer data.
	 * @param texlevel	the mipmapping level to copy this into (0 is topmost).
	 * @param width the texture width in texels.
	 * @param height the texture height in texels.
	 * @param depth the texture depth in texels.
	 * @param xoffs the texel offset.
	 * @param yoffs the texel offset.
	 * @param zoffs the texel offset.
	 * @throws GraphicsException if the buffer provided is not direct, or if the target is not stored three-dimensionally.
	 */
	public void setTextureSubData(TextureTargetType target, ByteBuffer imageData, ColorFormat colorFormat, int texlevel, int width, int depth, int height, int xoffs, int yoffs, int zoffs)
	{
		checkFeatureVersion(target);
		checkFeatureVersion(colorFormat);
		target.checkStorageDimensions(3);

		if (!imageData.isDirect())
			throw new GraphicsException("Data must be a direct buffer."); 
	
		checkFeatureVersion(colorFormat);

		clearError();
		glTexSubImage3D(
			target.glValue,
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
