/*******************************************************************************
 * Copyright (c) 2021-2022 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at 
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gloop.opengl.util;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

import com.blackrook.gloop.opengl.OGLGraphics;
import com.blackrook.gloop.opengl.enums.ColorFormat;
import com.blackrook.gloop.opengl.enums.TextureCubeFace;
import com.blackrook.gloop.opengl.enums.TextureFormat;
import com.blackrook.gloop.opengl.enums.TextureMagFilter;
import com.blackrook.gloop.opengl.enums.TextureMinFilter;
import com.blackrook.gloop.opengl.enums.TextureTargetType;
import com.blackrook.gloop.opengl.enums.TextureWrapType;
import com.blackrook.gloop.opengl.exception.GraphicsException;
import com.blackrook.gloop.opengl.gl1.OGL11Graphics;
import com.blackrook.gloop.opengl.gl1.OGL12Graphics;
import com.blackrook.gloop.opengl.gl1.OGL13Graphics;
import com.blackrook.gloop.opengl.gl1.OGLTexture;

/**
 * Texture builder utility class.
 * <p>
 * This class is used to generate textures in a "builder" way.
 * Mostly useful for small applications and test applications, not necessarily for
 * large enterprise applications that may employ better methods for texture loading and assembly.
 * <p>
 * All of these methods can be called outside of the graphics thread except {@link #create()}.
 * <p>
 * Depending on implementation version, some methods may not be supported or available,
 * and will throw {@link UnsupportedOperationException} if so.
 */
public interface TextureBuilder
{
	/**
	 * Sets the texture target type.
	 * By default, this is {@link TextureTargetType#TEXTURE_2D}.
	 * @param targetType the target type to use.
	 * @return this builder.
	 * @throws UnsupportedOperationException if the provided target type is unavailable in this version.
	 */
	TextureBuilder setTargetType(TextureTargetType targetType);

	/**
	 * Sets the minification and magnification filter on the texture.
	 * By default, this is {@link TextureMinFilter#NEAREST} for both.
	 * @param minFilter the minification filter to use. 
	 * @param magFilter the magnification filter to use. 
	 * @return this builder.
	 */
	TextureBuilder setFiltering(TextureMinFilter minFilter, TextureMagFilter magFilter);
	
	/**
	 * Sets the border size on the texture in texels.
	 * By default, this is <code>0</code>.
	 * @param texels the texel width. 
	 * @return this builder.
	 */
	TextureBuilder setBorder(int texels);
	
	/**
	 * Sets the wrapping on the texture.
	 * By default, all wrapping is {@link TextureWrapType#TILE}.
	 * @param wrapS the S-coordinate wrap type. 
	 * @return this builder.
	 * @throws UnsupportedOperationException if the provided type is unavailable in this version.
	 */
	TextureBuilder setWrapping(TextureWrapType wrapS);
	
	/**
	 * Sets the wrapping on the texture.
	 * By default, all wrapping is {@link TextureWrapType#TILE}.
	 * @param wrapS the S-coordinate wrap type. 
	 * @param wrapT the T-coordinate wrap type. 
	 * @return this builder.
	 * @throws UnsupportedOperationException if any of the provided types are unavailable in this version.
	 */
	TextureBuilder setWrapping(TextureWrapType wrapS, TextureWrapType wrapT);
	
	/**
	 * Sets the wrapping on the texture.
	 * By default, all wrapping is {@link TextureWrapType#TILE}.
	 * @param wrapS the S-coordinate wrap type. 
	 * @param wrapT the T-coordinate wrap type. 
	 * @param wrapR the R-coordinate wrap type. 
	 * @return this builder.
	 * @throws UnsupportedOperationException if any of the provided types are unavailable in this version.
	 */
	TextureBuilder setWrapping(TextureWrapType wrapS, TextureWrapType wrapT, TextureWrapType wrapR);
	
	/**
	 * Sets if the texture is stored in a compressed format.
	 * By default, this is <code>false</code>.
	 * @param enabled true to enable, false if disabled.
	 * @return this builder.
	 * @throws UnsupportedOperationException if this feature is unavailable in this version.
	 */
	TextureBuilder setCompressed(boolean enabled);
	
	/**
	 * Sets if this generator auto-generates mipmaps on or after data transfer.
	 * By default, this is <code>false</code>.
	 * @param autoGenerateMipMaps true to auto-generate mipmaps for this texture.
	 * @return this builder.
	 * @throws UnsupportedOperationException if this feature is unavailable in this version.
	 */
	TextureBuilder setAutoGenerateMipMaps(boolean autoGenerateMipMaps);
	
	/**
	 * Sets texture anisotropy level.
	 * By default, this is <code>1.0f</code>.
	 * @param anisotropy the anisotropy level (1.0f or less = no anisotropy).
	 * @return this builder.
	 */
	TextureBuilder setAnisotropy(float anisotropy);
	
	/**
	 * Adds one or more textures to a mipmap level. 
	 * <p> Each image in the batch past the first is considered part of the 
	 * depth dimension (an array of 4 images implies a depth of 4, height of 4 if 1D Array).
	 * If texture cube, then this expects 6 textures in the order: PX, NX, PY, NY, PZ, NZ.
	 * <p>
	 * The first call to this is the topmost level, and each subsequent call is the next lower mipmap level.
	 * Every texture past the first one is ignored if mipmap auto-generation is enabled.
	 * @param images the image to add at this level.
	 * @return this builder.
	 * @throws GraphicsException if images is length 0, or no images were provided.
	 */
	TextureBuilder addTextureImage(BufferedImage ... images);
	
	/**
	 * Creates this texture.
	 * @return the texture object created.
	 * @throws GraphicsException if the texture could not be created.
	 */
	OGLTexture create();

	/**
	 * Texture builder utility class.
	 * @param <GL> the graphics implementation that this executes on.
	 */
	public static abstract class Abstract<GL extends OGLGraphics> implements TextureBuilder
	{
		protected GL gl;
		protected TextureTargetType targetType;
		protected TextureMinFilter minFilter;
		protected TextureMagFilter magFilter;
		protected TextureWrapType wrapS;
		protected TextureWrapType wrapT;
		protected TextureWrapType wrapR;
		protected int border;
		protected boolean compressed;
		protected boolean autoGenerateMipMaps;
		protected float anisotropy;
		protected List<BufferedImage[]> imageLevels;
		
		/**
		 * Creates a new Texture Builder with defaults set.
		 * @param gl the graphics implementation that created this (and will execute this).
		 */
		protected Abstract(GL gl)
		{
			this.gl = gl;
			this.targetType = TextureTargetType.TEXTURE_2D;
			this.minFilter = TextureMinFilter.NEAREST;
			this.magFilter = TextureMagFilter.NEAREST;
			this.wrapS = TextureWrapType.TILE;
			this.wrapT = TextureWrapType.TILE;
			this.wrapR = TextureWrapType.TILE;
			this.border = 0;
			this.compressed = false;
			this.autoGenerateMipMaps = false;
			this.anisotropy = 1.0f;
			this.imageLevels = new LinkedList<>();
		}
	
		@Override
		public TextureBuilder setTargetType(TextureTargetType targetType)
		{
			gl.checkFeatureVersion(targetType);
			this.targetType = targetType;
			return this;
		}
	
		@Override
		public TextureBuilder setFiltering(TextureMinFilter minFilter, TextureMagFilter magFilter)
		{
			this.minFilter = minFilter;
			this.magFilter = magFilter;
			return this;
		}
		
		@Override
		public TextureBuilder setBorder(int texels)
		{
			this.border = texels;
			return this;
		}
		
		@Override
		public TextureBuilder setWrapping(TextureWrapType wrapS)
		{
			gl.checkFeatureVersion(wrapS);
			this.wrapS = wrapS;
			return this;
		}
		
		@Override
		public TextureBuilder setWrapping(TextureWrapType wrapS, TextureWrapType wrapT)
		{
			gl.checkFeatureVersion(wrapS);
			gl.checkFeatureVersion(wrapT);
			this.wrapS = wrapS;
			this.wrapT = wrapT;
			return this;
		}
		
		@Override
		public TextureBuilder setWrapping(TextureWrapType wrapS, TextureWrapType wrapT, TextureWrapType wrapR)
		{
			gl.checkFeatureVersion(wrapS);
			gl.checkFeatureVersion(wrapT);
			gl.checkFeatureVersion(wrapR);
			this.wrapS = wrapS;
			this.wrapT = wrapT;
			this.wrapR = wrapR;
			return this;
		}
		
		@Override
		public TextureBuilder setCompressed(boolean enabled)
		{
			this.compressed = enabled;
			return this;
		}
		
		@Override
		public TextureBuilder setAutoGenerateMipMaps(boolean autoGenerateMipMaps)
		{
			this.autoGenerateMipMaps = autoGenerateMipMaps;
			return this;
		}
		
		@Override
		public TextureBuilder setAnisotropy(float anisotropy)
		{
			this.anisotropy = anisotropy;
			return this;
		}
	
		@Override
		public TextureBuilder addTextureImage(BufferedImage ... images)
		{
			if (images.length == 0)
				throw new GraphicsException("Must add at least one image.");
			
			BufferedImage[] imageSet = new BufferedImage[images.length];
			System.arraycopy(images, 0, imageSet, 0, images.length);
			this.imageLevels.add(imageSet);
			return this;
		}
		
		protected void store1D(OGL11Graphics gl, Function<BufferedImage, ByteBuffer> dataFunc, ColorFormat colorFormat, TextureFormat textureFormat)
		{
			int i = 0;
			gl.setTextureWrapping(targetType, wrapS);
			for (BufferedImage[] imageArray : imageLevels)
			{
				gl.setTextureData(
					targetType, 
					dataFunc.apply(imageArray[0]), 
					colorFormat, 
					textureFormat, 
					i, imageArray[0].getWidth(), border
				);
				i++;
			}
		}

		protected void store2D(OGL11Graphics gl, Function<BufferedImage, ByteBuffer> dataFunc, ColorFormat colorFormat, TextureFormat textureFormat)
		{
			int i = 0;
			gl.setTextureWrapping(targetType, wrapS, wrapT);
			for (BufferedImage[] imageArray : imageLevels)
			{
				gl.setTextureData(
					targetType, 
					dataFunc.apply(imageArray[0]), 
					colorFormat, 
					textureFormat, 
					i, imageArray[0].getWidth(), imageArray[0].getHeight(), border
				);
				i++;
			}
		}

		protected void store1DArray(OGL11Graphics gl, Function<BufferedImage[], ByteBuffer> dataArrayFunc, ColorFormat colorFormat, TextureFormat textureFormat)
		{
			int i = 0;
			gl.setTextureWrapping(targetType, wrapS, wrapT);
			for (BufferedImage[] imageArray : imageLevels)
			{
				gl.setTextureData(
					targetType, 
					dataArrayFunc.apply(imageArray), 
					colorFormat, 
					textureFormat, 
					i, imageArray[0].getWidth(), imageArray.length, border
				);
				i++;
			}
		}

		protected void store2DArray(OGL12Graphics gl, Function<BufferedImage[], ByteBuffer> dataArrayFunc, ColorFormat colorFormat, TextureFormat textureFormat)
		{
			int i = 0;
			gl.setTextureWrapping(targetType, wrapS, wrapT);
			for (BufferedImage[] imageArray : imageLevels)
			{
				gl.setTextureData(
					targetType, 
					dataArrayFunc.apply(imageArray), 
					colorFormat, 
					textureFormat, 
					i, imageArray[0].getWidth(), imageArray[0].getHeight(), imageArray.length, border
				);
				i++;
			}
		}

		protected void storeCube(OGL13Graphics gl, Function<BufferedImage, ByteBuffer> dataFunc, ColorFormat colorFormat, TextureFormat textureFormat)
		{
			TextureCubeFace[] faces = TextureCubeFace.values();
			int i = 0;
			gl.setTextureWrapping(targetType, wrapS, wrapT);
			for (BufferedImage[] imageArray : imageLevels)
			{
				if (imageArray.length < 6)
					throw new GraphicsException("Texture target is " + targetType.name() + ", and provided image array is less than 6 elements.");
				for (int x = 0; x < 6; x++)
				{
					gl.setTextureData(
						faces[x], 
						dataFunc.apply(imageArray[x]), 
						colorFormat, 
						textureFormat, 
						i, imageArray[x].getWidth(), imageArray[x].getHeight(), border
					);
				}
				i++;
			}
		}

		protected void store3D(OGL12Graphics gl, Function<BufferedImage[], ByteBuffer> dataArrayFunc, ColorFormat colorFormat, TextureFormat textureFormat)
		{
			int i = 0;
			gl.setTextureWrapping(targetType, wrapS, wrapT, wrapR);
			for (BufferedImage[] imageArray : imageLevels)
			{
				gl.setTextureData(
					targetType, 
					dataArrayFunc.apply(imageArray), 
					colorFormat, 
					textureFormat, 
					i, imageArray[0].getWidth(), imageArray[0].getHeight(), imageArray.length, border
				);
				i++;
			}
		}
		
	}
	
}
