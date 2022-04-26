/*******************************************************************************
 * Copyright (c) 2021-2022 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at 
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gloop.opengl.gl1;

import com.blackrook.gloop.opengl.OGLVersion;
import com.blackrook.gloop.opengl.OGLSystem.Options;
import com.blackrook.gloop.opengl.enums.BufferTargetType;
import com.blackrook.gloop.opengl.enums.ColorFormat;
import com.blackrook.gloop.opengl.enums.DataType;
import com.blackrook.gloop.opengl.enums.GeometryType;
import com.blackrook.gloop.opengl.enums.TextureFormat;
import com.blackrook.gloop.opengl.enums.TextureTargetType;
import com.blackrook.gloop.opengl.enums.TextureWrapType;
import com.blackrook.gloop.opengl.exception.GraphicsException;
import com.blackrook.gloop.opengl.util.TextureBuilder;
import com.blackrook.gloop.opengl.util.TextureUtils;

import static org.lwjgl.opengl.GL12.*;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.function.Function;

/**
 * OpenGL 1.2 Graphics Implementation.
 * @author Matthew Tropiano
 */
public class OGL12Graphics extends OGL11Graphics
{
	/**
	 * Texture builder used for OpenGL 1.2.  
	 */
	private static class OGL12TextureBuilder extends TextureBuilder.Abstract<OGL12Graphics>
	{
		protected OGL12TextureBuilder(OGL12Graphics gl)
		{
			super(gl);
		}

		@Override
		public TextureBuilder setCompressed(boolean enabled)
		{
			throw new UnsupportedOperationException("Texture compression is not supported in this implementation.");
		}
		
		@Override
		public TextureBuilder setAutoGenerateMipMaps(boolean autoGenerateMipMaps)
		{
			throw new UnsupportedOperationException("Mipmap auto-generation is not supported in this implementation.");
		}

		@Override
		public OGLTexture create()
		{
			OGLTexture out = gl.createTexture();
			try {
				
				if (imageLevels.isEmpty())
					throw new GraphicsException("No data to store for texture.");
				
				// No compression, no auto mipmapgen.
				
				gl.setTexture(targetType, out);
				gl.setTextureFiltering(targetType, minFilter, magFilter, anisotropy);
				
				ByteOrder nativeByteOrder = ByteOrder.nativeOrder();
				Function<BufferedImage, ByteBuffer> dataFunc = nativeByteOrder == ByteOrder.LITTLE_ENDIAN 
					? TextureUtils::getBGRAByteData
					: TextureUtils::getRGBAByteData
				;
				Function<BufferedImage[], ByteBuffer> dataArrayFunc = nativeByteOrder == ByteOrder.LITTLE_ENDIAN 
					? TextureUtils::getBGRAByteData
					: TextureUtils::getRGBAByteData
				;
				ColorFormat colorFormat = nativeByteOrder == ByteOrder.LITTLE_ENDIAN
					? ColorFormat.BGRA
					: ColorFormat.RGBA
				;
				TextureFormat textureFormat = TextureFormat.RGBA;
				
				switch (targetType)
				{
					case TEXTURE_1D:
						store1D(gl, dataFunc, colorFormat, textureFormat);
						break;

					case TEXTURE_2D:
					case TEXTURE_RECTANGLE:
						store2D(gl, dataFunc, colorFormat, textureFormat);
						break;

					case TEXTURE_1D_ARRAY:
						store1DArray(gl, dataArrayFunc, colorFormat, textureFormat);
						break;
					
					case TEXTURE_3D:
						store3D(gl, dataArrayFunc, colorFormat, textureFormat);
						break;
					
					case TEXTURE_2D_ARRAY:
						store2DArray(gl, dataArrayFunc, colorFormat, textureFormat);
						break;
					
					default:
						throw new GraphicsException("Unsupported texture target: " + targetType.name());
				}
				
			} catch (Exception e) {
				gl.destroyTexture(out);
				throw e;
			} finally {
				gl.unsetTexture(targetType);
			}
			
			return out;
		}
	}
	
	// Create OpenGL 1.2 context.
	public OGL12Graphics(Options options, boolean core)
	{
		super(options, core);
	}

	@Override
	public OGLVersion getVersion()
	{
		return OGLVersion.GL12;
	}
	
	/**
	 * Creates a texture builder.
	 * <p> This texture builder aids in building texture objects, and its
	 * {@link TextureBuilder#create()} method will bind a new texture to its required target,
	 * send the data, set the filtering and build mipmaps, unbind the target, and return the new object.
	 * <p> Limitations on this implementation version are: No compression, no auto mipmapgen.
	 * @return a new texture builder.
	 */
	public TextureBuilder createTextureBuilder()
	{
		return new OGL12TextureBuilder(this);
	}
	
	/**
	 * Sets the current wrapping for the current texture bound to the specified target.
	 * @param target the texture target.
	 * @param wrapS the wrapping mode, S-axis.
	 * @param wrapT the wrapping mode, T-axis.
	 * @param wrapR the wrapping mode, R-axis.
	 * @throws UnsupportedOperationException if any provided type is unavailable in this version.
	 * @throws GraphicsException if the target is not a three-dimensionally-sampled target.
	 */
	public void setTextureWrapping(TextureTargetType target, TextureWrapType wrapS, TextureWrapType wrapT, TextureWrapType wrapR)
	{
		verifyFeatureSupport(target);
		verifyFeatureSupport(wrapS);
		verifyFeatureSupport(wrapT);
		verifyFeatureSupport(wrapR);
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
	 * @throws UnsupportedOperationException if any provided type or format is unavailable in this version.
	 * @throws GraphicsException if the buffer provided is not direct, or if the target is not stored three-dimensionally.
	 */
	public void setTextureData(TextureTargetType target, ByteBuffer imageData, ColorFormat colorFormat, TextureFormat format, int texlevel, int width, int height, int depth, int border)
	{
		verifyFeatureSupport(target);
		verifyFeatureSupport(colorFormat);
		verifyFeatureSupport(format);
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
		checkError();
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
	 * @throws UnsupportedOperationException if any provided type or format is unavailable in this version.
	 * @throws GraphicsException if the buffer provided is not direct, or if the target is not stored three-dimensionally.
	 */
	public void setTextureSubData(TextureTargetType target, ByteBuffer imageData, ColorFormat colorFormat, int texlevel, int width, int height, int depth, int xoffs, int yoffs, int zoffs)
	{
		verifyFeatureSupport(target);
		verifyFeatureSupport(colorFormat);
		target.checkStorageDimensions(3);

		if (!imageData.isDirect())
			throw new GraphicsException("Data must be a direct buffer."); 
	
		verifyFeatureSupport(colorFormat);

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
		checkError();
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
		checkError();
	}

}
