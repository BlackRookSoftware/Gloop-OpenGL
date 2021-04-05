/*******************************************************************************
 * Copyright (c) 2021 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at 
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gloop.opengl;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.opengl.GL11.GL_NO_ERROR;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glDisableClientState;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glEnableClientState;
import static org.lwjgl.opengl.GL11.glGetError;
import static org.lwjgl.opengl.GL11.glGetFloat;
import static org.lwjgl.opengl.GL11.glGetFloatv;
import static org.lwjgl.opengl.GL11.glGetInteger;
import static org.lwjgl.opengl.GL11.glGetIntegerv;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Supplier;

import org.lwjgl.opengl.ARBImaging;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL45;

import com.blackrook.gloop.opengl.enums.ShaderType;
import com.blackrook.gloop.opengl.enums.TextureMagFilter;
import com.blackrook.gloop.opengl.enums.TextureMinFilter;
import com.blackrook.gloop.opengl.enums.TextureTargetType;
import com.blackrook.gloop.opengl.enums.TextureWrapType;
import com.blackrook.gloop.opengl.exception.GraphicsException;
import com.blackrook.gloop.opengl.gl1.OGLTexture;
import com.blackrook.gloop.opengl.gl2.OGLProgram;

/**
 * A common encapsulator for all OpenGL implementations.
 * <p><b>NOTE: No functions outside of the rendering thread (the 
 * thread that is triggered by {@link OGLSystem#display()}) should call any function in this.
 * @author Matthew Tropiano
 */
public abstract class OGLGraphics implements OGLVersioned
{
	/**
	 * Information about this context implementation.
	 */
	public abstract class Info
	{
		/** OpenGL renderer name. */
		protected String renderer;
		/** OpenGL version name. */
		protected String version;
		/** OpenGL shader version name. */
		protected String shaderVersion;
		/** OpenGL vendor name. */
		protected String vendor;
		/** OpenGL list of extensions. */
		protected Set<String> extensions;
		
		/** Are we running NVidia architecture? */
		protected boolean isNVidia;
		/** Are we running AMD architecture? */
		protected boolean isAMD;
		/** Are we running ATi architecture? */
		protected boolean isATi;
		/** Are we running S3 architecture, and if so, WHY? */
		protected boolean isS3;
		/** Are we running Matrox architecture? */
		protected boolean isMatrox;
		/** Are we running Intel architecture? */
		protected boolean isIntel;

		/** Flag for presence of occlusion query extension. */
		protected boolean occlusionQueryExtensionPresent;
		/** Flag for presence of vertex shader extension. */
		protected boolean vertexShaderExtensionPresent;
		/** Flag for presence of fragment shader extension. */
		protected boolean fragmentShaderExtensionPresent;
		/** Flag for presence of geometry shader extension. */
		protected boolean geometryShaderExtensionPresent;
		/** Flag for presence of render buffer extension. */
		protected boolean renderBufferExtensionPresent;
		/** Flag for presence of vertex buffer extension. */
		protected boolean vertexBufferExtensionPresent;
		/** Flag for presence of non-power-of-two texture support. */
		protected boolean nonPowerOfTwoTextures;
		/** Flag for presence of point smoothing ability. */
		protected boolean pointSmoothingPresent;
		/** Flag for presence of point sprite extension. */
		protected boolean pointSpritesPresent;
		/** Flag for presence of texture anisotropy extension. */
		protected boolean textureAnisotropyPresent;
		
		/** Maximum bindable lights. */
		protected Integer maxLights;
		/** Maximum texture size. */
		protected Integer maxTextureSize;
		/** Minimum point size range. */
		protected Float minPointSize;
		/** Maximum point size range. */
		protected Float maxPointSize;
		/** Minimum line width range. */
		protected Float minLineWidth;
		/** Maximum line width range. */
		protected Float maxLineWidth;
		
		/** Maximum texture units. */
		protected Integer maxTextureUnits;
		/** Maximum texture anisotropy. */
		protected Float maxTextureAnisotropy;
		/** Maximum renderbuffer size. */
		protected Integer maxRenderBufferSize;
		/** Maximum draw buffers. */
		protected Integer maxDrawBuffers;
		/** Maximum vertex attributes. */
		protected Integer maxVertexAttribs;
		/** Maximum renderbuffer color attachments. */
		protected Integer maxRenderBufferColorAttachments;

		protected Info() {}
		
		/**
		 * Checks if an OpenGL extension is present.
		 * If you keep calling this method for the same extension, you are
		 * better off saving the results of the first call and using that, since
		 * the list of present extensions never change during runtime. 
		 * @param extensionName the extension name.
		 * @return true if so, false if not.
		 */
		public boolean extensionIsPresent(String extensionName)
		{
			return extensions.contains(extensionName);
		}

		/** 
		 * @return the maximum amount of lights. Null if not available.
		 */
		public Integer getMaxLights()
		{
			return maxLights;
		}

		/**
		 * @return the maximum amount of bindable texture units. Null if not available.
		 */
		public Integer getMaxTextureUnits()
		{
			return maxTextureUnits;
		}

		/**
		 * @return max texture size in pixels. Null if not available.
		 */
		public Integer getMaxTextureSize()
		{
			return maxTextureSize;
		}

		/**
		 * @return max draw buffers. Null if not available.
		 */
		public Integer getMaxDrawBuffers()
		{
			return maxDrawBuffers;
		}
		
		/**
		 * @return the maximum amount of bindable vertex attributes. Null if not available.
		 */
		public Integer getMaxVertexAttribs()
		{
			return maxVertexAttribs;
		}
		
		/**
		 * @return the maximum size of a render buffer object in pixels. Null if not available.
		 */
		public Integer getMaxRenderBufferSize()
		{
			return maxRenderBufferSize;
		}

		/**
		 * @return the maximum amount of color buffer attachments for a render buffer. Null if not available.
		 */
		public Integer getMaxRenderBufferColorAttachments()
		{
			return maxRenderBufferColorAttachments;
		}

		/**
		 * @return the minimum size a point can be rendered. Null if not available.
		 */
		public Float getMinPointSize()
		{
			return minPointSize;
		}

		/**
		 * @return the maximum size a point can be rendered. Null if not available.
		 */
		public Float getMaxPointSize()
		{
			return maxPointSize;
		}

		/**
		 * @return the minimum width for line geometry. Null if not available.
		 */
		public Float getMinLineWidth()
		{
			return minLineWidth;
		}

		/**
		 * @return the maximum width for line geometry. Null if not available.
		 */
		public Float getMaxLineWidth()
		{
			return maxLineWidth;
		}
		
		/**
		 * @return the maximum texture anisotropy factor for mipmap generation. Null if not available.
		 */
		public Float getMaxTextureAnisotropy()
		{
			return maxTextureAnisotropy;
		}

		/** 
		 * @return the rendering device of this GL system. Null if not available. 
		 */
		public String getRenderer()
		{
			return renderer;
		}

		/** 
		 * @return the version of this GL system. Null if not available. 
		 */
		public String getVersion()
		{
			return version;
		}

		/** 
		 * @return the vendor name of this GL system. Null if not available. 
		 */
		public String getVendor()
		{
			return vendor;
		}

		/** 
		 * @return the shader version of this GL system. 
		 */
		public String getShaderVersion()
		{
			return shaderVersion;
		}
		
		/**
		 * @return true if occlusion query extensions are present for the video device, false otherwise.
		 */
		public boolean supportsOcclusionQueries()
		{
			return occlusionQueryExtensionPresent;
		}

		/**
		 * @return true if vertex shader extensions are present for the video device, false otherwise.
		 */
		public boolean supportsVertexShaders()
		{
			return vertexShaderExtensionPresent;
		}

		/**
		 * @return true if fragment shader extensions are present for the video device, false otherwise.
		 */
		public boolean supportsFragmentShaders()
		{
			return fragmentShaderExtensionPresent;
		}

		/**
		 * @return true if geometry shader extensions are present for the video device, false otherwise.
		 */
		public boolean supportsGeometryShaders()
		{
			return geometryShaderExtensionPresent;
		}

		/**
		 * @return true if render buffer extensions are present for the video device, false otherwise.
		 */
		public boolean supportsRenderBuffers()
		{
			return renderBufferExtensionPresent;
		}

		/**
		 * @return true if vertex buffer extensions are present for the video device, false otherwise.
		 */
		public boolean supportsVertexBuffers()
		{
			return vertexBufferExtensionPresent;
		}

		/**
		 * @return true if this device supports non-power-of-two textures, false otherwise.
		 */
		public boolean supportsNonPowerOfTwoTextures()
		{
			return nonPowerOfTwoTextures;
		}

		/**
		 * @return true if this device supports smooth points, false otherwise.
		 */
		public boolean supportsPointSmoothing()
		{
			return pointSmoothingPresent;
		}

		/**
		 * @return true if this device supports point sprites, false otherwise.
		 */
		public boolean supportsPointSprites()
		{
			return pointSpritesPresent;
		}

		/**
		 * @return true if this device supports texture mipmap anisotropy, false otherwise.
		 */
		public boolean supportsTextureAnisotropy()
		{
			return textureAnisotropyPresent;
		}
		
		/** 
		 * @return true if this is running NVidia architecture.
		 */
		public boolean isNVidia()
		{
			return isNVidia;
		}

		/** 
		 * @return true if this is running ATi architecture.
		 */
		public boolean isATi()
		{
			return isATi;
		}

		/** 
		 * @return true if this is running AMD architecture.
		 */
		public boolean isAMD()
		{
			return isAMD;
		}

		/** 
		 * @return true if this is running S3 architecture.
		 */
		public boolean isS3()
		{
			return isS3;
		}

		/** 
		 * @return true if this is running Matrox architecture.
		 */
		public boolean isMatrox()
		{
			return isMatrox;
		}

		/** 
		 * @return true if this is running Intel architecture.
		 */
		public boolean isIntel()
		{
			return isIntel;
		}
		
	}

	/**
	 * Texture builder utility class.
	 * <p>
	 * This class is used to generate textures in a "builder" way.
	 * Mostly useful for small applications and test applications, not necessarily for
	 * large enterprise applications that may employ better methods for texture loading and assembly.
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
		 */
		TextureBuilder setTargetType(TextureTargetType targetType);

		/**
		 * Sets the minification filter on the texture.
		 * By default, this is {@link TextureMinFilter#NEAREST}.
		 * @param minFilter the minification filter to use. 
		 * @return this builder.
		 */
		TextureBuilder setMinFilter(TextureMinFilter minFilter);
		
		/**
		 * Sets the magnification filter on the texture.
		 * By default, this is {@link TextureMagFilter#NEAREST}.
		 * @param magFilter the magnification filter to use. 
		 * @return this builder.
		 */
		TextureBuilder setMagFilter(TextureMagFilter magFilter);
		
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
		 */
		TextureBuilder setWrapping(TextureWrapType wrapS);
		
		/**
		 * Sets the wrapping on the texture.
		 * By default, all wrapping is {@link TextureWrapType#TILE}.
		 * @param wrapS the S-coordinate wrap type. 
		 * @param wrapT the T-coordinate wrap type. 
		 * @return this builder.
		 */
		TextureBuilder setWrapping(TextureWrapType wrapS, TextureWrapType wrapT);
		
		/**
		 * Sets the wrapping on the texture.
		 * By default, all wrapping is {@link TextureWrapType#TILE}.
		 * @param wrapS the S-coordinate wrap type. 
		 * @param wrapT the T-coordinate wrap type. 
		 * @param wrapR the R-coordinate wrap type. 
		 * @return this builder.
		 */
		TextureBuilder setWrapping(TextureWrapType wrapS, TextureWrapType wrapT, TextureWrapType wrapR);
		
		/**
		 * Sets if the texture is stored in a compressed format.
		 * By default, this is <code>false</code>.
		 * @param enabled true to enable, false if disabled.
		 * @return this builder.
		 */
		TextureBuilder setCompressed(boolean enabled);
		
		/**
		 * Sets if this generator auto-generates mipmaps on or after data transfer.
		 * By default, this is <code>false</code>.
		 * @param autoGenerateMipMaps true to auto-generate mipmaps for this texture.
		 * @return this builder.
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
		 * Adds one or more textures to a mipmap level, each image in this batch
		 * past the first is considered part of the depth dimension (an array of 4 images implies a depth of 4, height of 4 if 1D Array).
		 * The first call to this is the topmost level, and each subsequent call is the next lower level.
		 * Every texture past the first one is ignored if mipmap auto-generation is enabled.
		 * @param images the image to add at this level.
		 * @return this builder.
		 */
		TextureBuilder addTexture(BufferedImage ... images);
		
		/**
		 * Creates this texture.
		 * @return the texture object created.
		 * @throws GraphicsException if the texture could not be created.
		 */
		OGLTexture create();
	}
	
	/**
	 * Shader builder utility class.
	 * <p>
	 * This class is used to generate linked shader programs in a "builder" way.
	 * Mostly useful for small applications and test applications, not necessarily for
	 * large enterprise applications that may employ better methods for shader loading and assembly.
	 * <p>
	 * Depending on implementation version, some methods may not be supported or available,
	 * and will throw {@link UnsupportedOperationException} if so.
	 */
	public interface ShaderBuilder
	{
		@FunctionalInterface
		public interface Listener
		{
			/**
			 * Called when a shader is compiled, and the log built.
			 * @param type the shader type built.
			 * @param log the log content.
			 */
			void onShaderLog(ShaderType type, String log);
		}
		
		/**
		 * Binds an attribute name to a specific location index.
		 * @param attributeName the attribute name.
		 * @param locationId the location id.
		 * @return this builder.
		 */
		ShaderBuilder attributeLocation(String attributeName, int locationId);
		
		/**
		 * Binds a fragment output attribute name to a specific output color index.
		 * @param attributeName the attribute name.
		 * @param index the index.
		 * @return this builder.
		 */
		ShaderBuilder fragmentDataLocation(String attributeName, int index);
		
		/**
		 * Sets a shader program and a shader source. 
		 * @param type the shader type.
		 * @param file the source file.
		 * @return this builder.
		 */
		ShaderBuilder setShader(ShaderType type, final File file);
		
		/**
		 * Sets a shader program and a shader source. 
		 * @param type the shader type.
		 * @param in the input stream to read from.
		 * @return this builder.
		 */
		ShaderBuilder setShader(ShaderType type, final InputStream in);
		
		/**
		 * Sets a shader program and a shader source. 
		 * @param type the shader type.
		 * @param reader the reader to read from.
		 * @return this builder.
		 */
		ShaderBuilder setShader(ShaderType type, final Reader reader);
		
		/**
		 * Sets a shader program and a shader source. 
		 * @param type the shader type.
		 * @param source the string that contains the source code.
		 * @return this builder.
		 */
		ShaderBuilder setShader(ShaderType type, final String source);
		
		/**
		 * Sets a shader program and a shader source. 
		 * @param type the shader type.
		 * @param source the source code supplier.
		 * @return this builder.
		 */
		ShaderBuilder setShader(ShaderType type, Supplier<String> source);

		/**
		 * Creates the program.
		 * @return the shader program created.
		 * @throws GraphicsException if the program could not be created.
		 */
		OGLProgram create();

	}
	
	/**
	 * Texture builder utility class.
	 * @param <GL> the graphics implementation that this executes on.
	 */
	protected static abstract class OGLTextureBuilderAbstract<GL extends OGLGraphics> implements TextureBuilder
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
		protected OGLTextureBuilderAbstract(GL gl)
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
			this.targetType = targetType;
			return this;
		}
	
		@Override
		public TextureBuilder setMinFilter(TextureMinFilter minFilter)
		{
			this.minFilter = minFilter;
			return this;
		}
		
		@Override
		public TextureBuilder setMagFilter(TextureMagFilter magFilter)
		{
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
			this.wrapS = wrapS;
			return this;
		}
		
		@Override
		public TextureBuilder setWrapping(TextureWrapType wrapS, TextureWrapType wrapT)
		{
			this.wrapS = wrapS;
			this.wrapT = wrapT;
			return this;
		}
		
		@Override
		public TextureBuilder setWrapping(TextureWrapType wrapS, TextureWrapType wrapT, TextureWrapType wrapR)
		{
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
		public TextureBuilder addTexture(BufferedImage ... images)
		{
			if (images.length == 0)
				throw new GraphicsException("Must add at least one image.");
			
			BufferedImage[] imageSet = new BufferedImage[images.length];
			System.arraycopy(images, 0, imageSet, 0, images.length);
			this.imageLevels.add(imageSet);
			return this;
		}
		
	}

	/**
	 * Shader builder utility class.
	 * @param <GL> the graphics implementation that this executes on.
	 */
	public static abstract class OGLShaderBuilderAbstract<GL extends OGLGraphics> implements ShaderBuilder
	{
		protected GL gl;
		protected Map<String, Integer> attributeLocationBindings;
		protected Map<String, Integer> fragmentDataBindings;
		protected Map<ShaderType, Supplier<String>> shaderPrograms;
		protected ShaderBuilder.Listener builderListener;

		/**
		 * Creates a new Shader Builder with defaults set.
		 * @param gl the graphics implementation that created this (and will execute this).
		 */
		protected OGLShaderBuilderAbstract(GL gl)
		{
			this.gl = gl;
			this.attributeLocationBindings = new TreeMap<>();
			this.fragmentDataBindings = new TreeMap<>();
			this.shaderPrograms = new TreeMap<>();
			this.builderListener = null;
		}
		
		// Reads source from a reader.
		private static String readSource(Reader reader) throws IOException
		{
			int c = 0;
			char[] cbuf = new char[4096];
			StringBuilder sb = new StringBuilder();
			while ((c = reader.read(cbuf)) > 0)
				sb.append(cbuf, 0, c);
			return sb.toString();
		}

		/**
		 * Binds an attribute name to a specific location index.
		 * @param attributeName the attribute name.
		 * @param locationId the location id.
		 * @return this builder.
		 */
		public ShaderBuilder attributeLocation(String attributeName, int locationId)
		{
			attributeLocationBindings.put(attributeName, locationId);
			return this;
		}
		
		/**
		 * Binds a fragment output attribute name to a specific output color index.
		 * @param attributeName the attribute name.
		 * @param index the index.
		 * @return this builder.
		 */
		public ShaderBuilder fragmentDataLocation(String attributeName, int index)
		{
			fragmentDataBindings.put(attributeName, index);
			return this;
		}
		
		/**
		 * Sets a shader program and a shader source. 
		 * @param type the shader type.
		 * @param file the source file.
		 * @return this builder.
		 */
		public ShaderBuilder setShader(ShaderType type, final File file)
		{
			return setShader(type, () ->
			{
				try (Reader reader = new InputStreamReader(new FileInputStream(file)))
				{
					return readSource(reader);
				}
				catch (FileNotFoundException e)
				{
					throw new GraphicsException("Shader source for " + type.name() + " could not be found.", e);
				}
				catch (IOException e)
				{
					throw new GraphicsException("Shader source for " + type.name() + " could not be read.", e);
				}
			});
		}
		
		/**
		 * Sets a shader program and a shader source. 
		 * @param type the shader type.
		 * @param in the input stream to read from.
		 * @return this builder.
		 */
		public ShaderBuilder setShader(ShaderType type, final InputStream in)
		{
			return setShader(type, () ->
			{
				try (Reader reader = new InputStreamReader(in))
				{
					return readSource(reader);
				}
				catch (FileNotFoundException e)
				{
					throw new GraphicsException("Shader source for " + type.name() + " could not be found.", e);
				}
				catch (IOException e)
				{
					throw new GraphicsException("Shader source for " + type.name() + " could not be read.", e);
				}
			});
		}
		
		/**
		 * Sets a shader program and a shader source. 
		 * @param type the shader type.
		 * @param reader the reader to read from.
		 * @return this builder.
		 */
		public ShaderBuilder setShader(ShaderType type, final Reader reader)
		{
			return setShader(type, () ->
			{
				try
				{
					return readSource(reader);
				}
				catch (FileNotFoundException e)
				{
					throw new GraphicsException("Shader source for " + type.name() + " could not be found.", e);
				}
				catch (IOException e)
				{
					throw new GraphicsException("Shader source for " + type.name() + " could not be read.", e);
				}
			});
		}
		
		/**
		 * Sets a shader program and a shader source. 
		 * @param type the shader type.
		 * @param source the string that contains the source code.
		 * @return this builder.
		 */
		public ShaderBuilder setShader(ShaderType type, final String source)
		{
			return setShader(type, ()->source);
		}
		
		/**
		 * Sets a shader program and a shader source. 
		 * @param type the shader type.
		 * @param source the source code supplier.
		 * @return this builder.
		 */
		public ShaderBuilder setShader(ShaderType type, Supplier<String> source)
		{
			shaderPrograms.put(type, source);
			return this;
		}

		/**
		 * Fires a log event to the listener, if attached.
		 * @param type the shader type.
		 * @param log the shader log.
		 */
		protected final void fireShaderLog(ShaderType type, String log)
		{
			if (builderListener != null)
				builderListener.onShaderLog(type, log);
		}
		
	}
	
	/** The current frame rendered. */
	private long currentFrame;
	/** The starting millisecond at creation. */
	private long startMilliseconds;
	/** The current millisecond at the beginning of the frame. */
	private long currentMilliseconds;
	/** The current nanosecond at the beginning of the frame. */
	private long currentNanos;
	/** The current state of the "blit bit" set at the beginning of the frame. */
	private boolean currentBlitBit;
	/** Time between frames. */
	private float currentTimeStepMillis;

	/** Last frame nanotime. */
	private long previousTimeNanos;
	/** Time between frames. */
	private long currentTimeStepNanos;
	
	/** Check errors? */
	private boolean errorChecking;
	
	/** Core profile. */
	private boolean core;
	/** Graphics info. */
	private Info info;

	/**
	 * Initializes this graphics.
	 * @param core true if this is a core implementation, false if not.
	 */
	protected OGLGraphics(boolean core)
	{
		this.core = core;
		this.currentFrame = 0L;
		this.startMilliseconds = System.currentTimeMillis();
		this.currentMilliseconds = -1L;
		this.currentNanos = -1L;
		this.currentBlitBit = false;
		this.currentTimeStepMillis = -1f;

		this.previousTimeNanos = -1L;
		this.currentTimeStepNanos = -1L;
		this.errorChecking = true;

		this.info = null;
	}
	
	/**
	 * Called once in order to fetch context info.
	 * @return the info object.
	 */
	protected abstract Info createInfo();
	
	/**
	 * Gets an info object that returns a lot of OpenGL 
	 * limits and such for this context implementation.
	 * <p> After this is called once in the OpenGL thread, this can be fetched by any thread.
	 * @return the graphics context info.
	 */
	public Info getInfo()
	{
		if (info == null)
			info = createInfo();
		return info;
	}
	
	/**
	 * Called at the beginning of each {@link OGLSystem#display()} call for each frame.
	 */
	final void startFrame()
	{
		currentMilliseconds = System.currentTimeMillis();
		currentNanos = System.nanoTime();
		
		if (currentTimeStepMillis < 0.0f)
		{
			currentTimeStepMillis = 0.0f;
			currentTimeStepNanos = 0L;
		}
		else
		{
			long n = currentNanos - previousTimeNanos;
			currentTimeStepNanos = n;
			currentTimeStepMillis = (float)((double)(n)/1000000.0);
		}

		previousTimeNanos = currentNanos;
		currentBlitBit = !currentBlitBit;
		currentFrame++;
	}

	/**
	 * Called on frame end - mostly does cleanup of objects that were abandoned in OpenGL.
	 */
	protected abstract void endFrame(); 

	/**
	 * Checks the version of this graphics implementation against a versioned object,
	 * and if the object is from a later version, throw an exception.
	 * This will also throw an exception if this version is core and the provided object is not.
	 * @param versioned the versioned element to check against.
	 * @throws GraphicsException if the versioned object is a later version than this one, or is not core if this graphics instance is.
	 */
	protected void checkFeatureVersion(OGLVersioned versioned)
	{
		if (getVersion().compareTo(versioned.getVersion()) < 0)
			throw new GraphicsException(versioned.getClass().getSimpleName() + " requires version " + versioned.getVersion().name());
		if (isCore() && !versioned.isCore())
			throw new GraphicsException("Using " + versioned.getClass().getSimpleName() + " requires it being part of the core spec, and it isn't.");
	}
	
	/**
	 * Checks if the version of this graphics implementation 
	 * is non-core, and if it is, it throws an exception.
	 * @throws UnsupportedOperationException if this graphics instance is a core implementation.
	 */
	protected void checkNonCore()
	{
		if (isCore())
			throw new UnsupportedOperationException("This is unavailable in a core implementation.");
	}
	
	@Override
	public boolean isCore()
	{
		return core;
	}
	
	/**
	 * @return the system milliseconds time, synced to the beginning of the current frame.
	 */
	public long currentTimeMillis()
	{
		return currentMilliseconds;
	}

	/**
	 * @return the seconds time from graphics creation, synced to the beginning of the current frame.
	 */
	public double currentTime()
	{
		return (currentMilliseconds - startMilliseconds) / 1000.0;
	}

	/**
	 * @return the system nanosecond time, synced to the beginning of the current frame.
	 */
	public long currentNanos()
	{
		return currentNanos;
	}

	/**
	 * Gets the amount of milliseconds passed between this frame and the last one.
	 * If this is the first frame, this is 0. If this is BEFORE the first frame,
	 * this is -1f.
	 * @return the time step in milliseconds.
	 */
	public float currentTimeStepMillis()
	{
		return currentTimeStepMillis;
	}
	
	/**
	 * Gets the fractional amount of nanoseconds passed between this frame and the last one.
	 * If this is the first frame, this is 0. If this is BEFORE the first frame,
	 * this is -1f.
	 * @return the fractional time step in nanoseconds.
	 */
	public float currentTimeStepNanos()
	{
		return currentTimeStepNanos;
	}
	
	/**
	 * Current blitting bit.
	 * This will alternate between true and false each frame.
	 * @return the current bit value.
	 */
	public boolean currentBlit()
	{
		return currentBlitBit;
	}
	
	/**
	 * @return current frame rendered (number).
	 */
	public long currentFrame()
	{
		return currentFrame;
	}
	
	/**
	 * Grabs an OpenGL context integer value using a GL value enum. 
	 * @param glEnum the GL enum.
	 * @return the value. 
	 */
	public int getInt(int glEnum)
	{
		return glGetInteger(glEnum);
	}

	/**
	 * Grabs a series of OpenGL context integer values using a GL value enum.
	 * @param glEnum the GL enum.
	 * @param out the output array.
	 */
	public void getInts(int glEnum, int[] out)
	{
		glGetIntegerv(glEnum, out);
	}

	/**
	 * Grabs an OpenGL context float value using a GL value enum. 
	 * @param glEnum the GL enum.
	 * @return the value. 
	 */
	public float getFloat(int glEnum)
	{
		return glGetFloat(glEnum);
	}

	/**
	 * Grabs a series of OpenGL context float values using a GL value enum.
	 * @param glEnum the GL enum.
	 * @param out the output array.
	 */
	public void getFloats(int glEnum, float[] out)
	{
		glGetFloatv(glEnum, out);
	}

	/**
	 * Enables/disables an OpenGL state bit.
	 * @param glEnum the OpenGL enumerant.
	 * @param flag if true, enable. if false, disable.
	 */
	public void setFlag(int glEnum, boolean flag)
	{
		if (flag)
			glEnable(glEnum);
		else
			glDisable(glEnum);
	}
	
	/**
	 * Enables/disables an OpenGL client state bit.
	 * @param glEnum the OpenGL enumerant.
	 * @param flag if true, enable. if false, disable.
	 */
	public void setClientFlag(int glEnum, boolean flag)
	{
		checkNonCore();
		if (flag)
			glEnableClientState(glEnum);
		else
			glDisableClientState(glEnum);
	}
	
	/**
	 * Converts a Java boolean to an OpenGL GL_TRUE or GL_FALSE value.
	 * @param val the boolean value.
	 * @return the GL boolean equivalent. 
	 */
	public int toGLBool(boolean val)
	{
		return val ? GL_TRUE : GL_FALSE;
	}

	/**
	 * Clears the error bits for the GL Error flags.
	 */
	public void clearError()
	{
		if (errorChecking)
			while (glGetError() != GL_NO_ERROR) {}
	}

	/**
	 * Tests for an OpenGL error via glGetError(), but only if error checking is enabled.
	 * If one is raised, this throws a GraphicsException with the error message.
	 * @throws GraphicsException if an error is raised. 
	 * @see #setErrorChecking(boolean)
	 */
	public void checkError()
	{
		if (!errorChecking)
			return;
		
		int error = glGetError();
		if (error != GL_NO_ERROR)
		{
			String errorName;
			switch (error)
			{
				case GL11.GL_INVALID_ENUM:
					errorName = "Invalid Enumeration";
					break;
				case GL11.GL_INVALID_VALUE:
					errorName = "Invalid Value";
					break;
				case GL11.GL_INVALID_OPERATION:
					errorName = "Invalid Operation";
					break;
				case GL11.GL_STACK_OVERFLOW:
					errorName = "Stack Overflow";
					break;
				case GL11.GL_STACK_UNDERFLOW:
					errorName = "Stack Underflow";
					break;
				case GL11.GL_OUT_OF_MEMORY:
					errorName = "Out of Memory";
					break;
				case GL30.GL_INVALID_FRAMEBUFFER_OPERATION:
					errorName = "Invalid Framebuffer Operation";
					break;
				case GL45.GL_CONTEXT_LOST:
					errorName = "Context Lost";
					break;
				case ARBImaging.GL_TABLE_TOO_LARGE:
					errorName = "Image Table Too Large";
					break;
				default:
					errorName = "(UNKNOWN ERROR CODE)";
					break;
			}
			throw new GraphicsException("OpenGL raised error code " + error + ": " + errorName);
		}
	}

	/**
	 * Checks if OpenGL error detection is enabled.
	 * If true, this could be reducing the amount of OpenGL calls this makes.
	 * @return true if so, false if not.
	 * @see #setErrorChecking(boolean)
	 */
	public boolean isErrorChecking() 
	{
		return errorChecking;
	}

	/**
	 * Sets if OpenGL error detection is enabled.
	 * If false, this could reduce the amount of OpenGL calls this makes.
	 * @param errorChecking if true, {@link #clearError()} and {@link #checkError()} do nothing. Else, they do stuff.
	 */
	public void setErrorChecking(boolean errorChecking)
	{
		this.errorChecking = errorChecking;
	}

}
