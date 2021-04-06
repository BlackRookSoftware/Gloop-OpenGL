/*******************************************************************************
 * Copyright (c) 2021 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at 
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gloop.opengl.gl1;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.function.Function;

import org.lwjgl.system.MemoryStack;

import com.blackrook.gloop.opengl.OGLVersion;
import com.blackrook.gloop.opengl.enums.ColorFormat;
import com.blackrook.gloop.opengl.enums.TextureFormat;
import com.blackrook.gloop.opengl.enums.TextureMagFilter;
import com.blackrook.gloop.opengl.enums.TextureMinFilter;
import com.blackrook.gloop.opengl.enums.TextureTargetType;
import com.blackrook.gloop.opengl.exception.GraphicsException;
import com.blackrook.gloop.opengl.util.TextureBuilder;
import com.blackrook.gloop.opengl.util.TextureUtils;

import static org.lwjgl.opengl.GL14.*;

/**
 * OpenGL 1.4 Graphics Implementation.
 * @author Matthew Tropiano
 */
public class OGL14Graphics extends OGL13Graphics
{
	/**
	 * Texture builder used for OpenGL 1.4.  
	 */
	private static class OGL14TextureBuilder extends TextureBuilder.Abstract<OGL14Graphics>
	{
		protected OGL14TextureBuilder(OGL14Graphics gl)
		{
			super(gl);
		}
	
		@Override
		public OGLTexture create()
		{
			OGLTexture out = gl.createTexture();
			try {
				
				if (imageLevels.isEmpty())
					throw new GraphicsException("No data to store for texture.");
				
				gl.setTexture(targetType, out);
				gl.setTextureFiltering(targetType, minFilter, magFilter, anisotropy, autoGenerateMipMaps);
				
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
	 * Creates a texture builder.
	 * <p> This texture builder aids in building texture objects, and its
	 * {@link TextureBuilder#create()} method will bind a new texture to its required target,
	 * send the data, set the filtering and build mipmaps, unbind the target, and return the new object.
	 * @return a new texture builder.
	 */
	@Override
	public TextureBuilder createTextureBuilder()
	{
		return new OGL14TextureBuilder(this);
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
