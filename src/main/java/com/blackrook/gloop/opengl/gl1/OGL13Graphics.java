/*******************************************************************************
 * Copyright (c) 2021 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at 
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gloop.opengl.gl1;

import com.blackrook.gloop.opengl.OGLVersion;
import com.blackrook.gloop.opengl.enums.ColorFormat;
import com.blackrook.gloop.opengl.enums.TextureCubeFace;
import com.blackrook.gloop.opengl.enums.TextureFormat;
import com.blackrook.gloop.opengl.enums.TextureTargetType;
import com.blackrook.gloop.opengl.exception.GraphicsException;
import com.blackrook.gloop.opengl.util.TextureBuilder;
import com.blackrook.gloop.opengl.util.TextureUtils;

import static org.lwjgl.opengl.GL13.*;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.function.Function;

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
			this.maxTextureUnits = getInt(GL_MAX_TEXTURE_UNITS);
		}
	}
	
	/**
	 * Texture builder used for OpenGL 1.3.  
	 */
	private static class OGL13TextureBuilder extends TextureBuilder.Abstract<OGL13Graphics>
	{
		protected OGL13TextureBuilder(OGL13Graphics gl)
		{
			super(gl);
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
				
				// No auto mipmapgen.
				
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
				TextureFormat textureFormat = compressed ? TextureFormat.COMPRESSED_RGBA : TextureFormat.RGBA;
				
				switch (targetType)
				{
					case TEXTURE_1D:
						store1D(gl, dataFunc, colorFormat, textureFormat);
						break;

					case TEXTURE_2D:
					case TEXTURE_RECTANGLE:
						store2D(gl, dataFunc, colorFormat, textureFormat);
						break;

					case TEXTURE_CUBE:
						storeCube(gl, dataFunc, colorFormat, textureFormat);
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
				out.destroy();
				throw e;
			} finally {
				gl.unsetTexture(targetType);
			}
			
			return out;
		}
	}

	/** Current active texture unit. */
	private int currentActiveTexture;

	// Create OpenGL 1.3 context.
	public OGL13Graphics(boolean core)
	{
		super(core);
		this.currentActiveTexture = 0;
	}
	
	@Override
	public OGLVersion getVersion()
	{
		return OGLVersion.GL13;
	}
	
	@Override
	protected Info createInfo()
	{
		return new Info13();
	}
	
	@Override
	protected int getCurrentActiveTextureUnitState()
	{
		return currentActiveTexture;
	}
	
	@Override
	protected void setCurrentActiveTextureUnitState(int unit)
	{
		this.currentActiveTexture = unit;
	}
	
	/**
	 * Creates a texture builder.
	 * <p> This texture builder aids in building texture objects, and its
	 * {@link TextureBuilder#create()} method will bind a new texture to its required target,
	 * send the data, set the filtering and build mipmaps, unbind the target, and return the new object.
	 * <p> Limitations on this implementation version are: No auto mipmapgen.
	 * @return a new texture builder.
	 */
	@Override
	public TextureBuilder createTextureBuilder()
	{
		return new OGL13TextureBuilder(this);
	}
	
	/**
	 * @return the current "active" texture unit.
	 * @see #setTextureUnit(int)
	 */
	public int getTextureUnit()
	{
		return getCurrentActiveTextureUnitState();
	}
	
	/**
	 * Sets the current "active" texture unit for texture bindings and texture environment settings.
	 * @param unit the texture unit to switch to.
	 */
	public void setTextureUnit(int unit)
	{
		if (unit < 0 || unit >= getInfo().getMaxTextureUnits())
			throw new GraphicsException("Unit cannot be greater than " + getInfo().getMaxTextureUnits());
		
		glActiveTexture(GL_TEXTURE0 + unit);
		checkError();
		currentActiveTexture = unit;
	}

	/**
	 * Sends a texture into OpenGL's memory for the current texture bound to {@link TextureTargetType#TEXTURE_CUBE}.
	 * @param cubeFace the texture cube face.
	 * @param imageData the image to send.
	 * @param colorFormat the pixel storage format of the buffer data.
	 * @param format the internal format.
	 * @param texlevel the mipmapping level to copy this into (0 is topmost).
	 * @param width the texture width in texels.
	 * @param height the texture height in texels.
	 * @param border the texel border to add, if any.
	 * @throws GraphicsException if the buffer provided is not direct, or if no current cube texture.
	 */
	public void setTextureData(TextureCubeFace cubeFace, ByteBuffer imageData, ColorFormat colorFormat, TextureFormat format, int texlevel, int width, int height, int border)
	{
		if (getCurrentActiveTextureState(GL_TEXTURE_CUBE_MAP) == null)
			throw new GraphicsException("A Texture Cube target is not currently bound.");
		
		checkFeatureVersion(colorFormat);
		checkFeatureVersion(format);
	
		if (width > getInfo().getMaxTextureSize() || height > getInfo().getMaxTextureSize())
			throw new GraphicsException("Texture is too large. Maximum size is " + getInfo().getMaxTextureSize() + " pixels.");
	
		if (!imageData.isDirect())
			throw new GraphicsException("Data must be a direct buffer."); 
		
		clearError();
		glTexImage2D(
			cubeFace.glValue,
			texlevel,
			format.glValue, 
			width,
			height,
			border,
			colorFormat.glValue,
			GL_UNSIGNED_BYTE,
			imageData
		);
		checkError();
	}

	/**
	 * Sends a subset of data to the current texture bound to {@link TextureTargetType#TEXTURE_CUBE} already in OpenGL's memory.
	 * @param cubeFace the texture cube face.
	 * @param imageData the image to send.
	 * @param colorFormat the pixel storage format of the buffer data.
	 * @param texlevel	the mipmapping level to copy this into (0 is topmost).
	 * @param width the texture width in texels.
	 * @param height the texture height in texels.
	 * @param xoffs the texel offset.
	 * @param yoffs the texel offset.
	 * @throws GraphicsException if the buffer provided is not direct, or if no current cube texture.
	 */
	public void setTextureSubData(TextureCubeFace cubeFace, ByteBuffer imageData, ColorFormat colorFormat, int texlevel, int width, int height, int xoffs, int yoffs)
	{
		if (getCurrentActiveTextureState(GL_TEXTURE_CUBE_MAP) == null)
			throw new GraphicsException("A Texture Cube target is not currently bound.");
		
		checkFeatureVersion(colorFormat);
	
		if (!imageData.isDirect())
			throw new GraphicsException("Data must be a direct buffer."); 
	
		checkFeatureVersion(colorFormat);
	
		clearError();
		glTexSubImage2D(
			cubeFace.glValue,
			texlevel,
			xoffs,
			yoffs,
			width,
			height,
			colorFormat.glValue,
			GL_UNSIGNED_BYTE,
			imageData
		);
		checkError();
	}

	/**
	 * Sets the current client active texture (for coordinates submission).
	 * @param unit the texture unit for binding.
	 */
	public void setCurrentActiveTextureCoordArray(int unit)
	{
		checkNonCore();
		glClientActiveTexture(GL_TEXTURE0 + unit);
	}

}
