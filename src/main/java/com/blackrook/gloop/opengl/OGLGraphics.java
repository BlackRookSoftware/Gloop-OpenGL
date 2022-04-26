/*******************************************************************************
 * Copyright (c) 2021-2022 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at 
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gloop.opengl;

import java.util.Set;

import org.lwjgl.opengl.ARBImaging;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL45;

import com.blackrook.gloop.opengl.OGLSystem.Options;
import com.blackrook.gloop.opengl.exception.GraphicsException;

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
		/** Maximum multisample samples. */
		protected Integer maxSamples;

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
		 * @return the maximum amount of samples for multisampling. Null if not available.
		 */
		public Integer getMaxSamples()
		{
			return maxSamples;
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

	/** Canvas width. */
	private int canvasWidth;
	/** Canvas height. */
	private int canvasHeight;

	/** Last frame nanotime. */
	private long previousTimeNanos;
	/** Time between frames. */
	private long currentTimeStepNanos;
	
	/** Core profile. */
	private boolean core;
	/** System options. */
	private Options options;
	/** Graphics info. */
	private Info info;

	/**
	 * Initializes this graphics.
	 * @param options system options.
	 * @param core true if this is a core implementation, false if not.
	 */
	protected OGLGraphics(Options options, boolean core)
	{
		this.currentFrame = 0L;
		this.startMilliseconds = System.currentTimeMillis();
		this.currentMilliseconds = -1L;
		this.currentNanos = -1L;
		this.currentBlitBit = false;
		this.currentTimeStepMillis = -1f;

		this.previousTimeNanos = -1L;
		this.currentTimeStepNanos = -1L;

		this.core = core;
		this.options = options;
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
	 * @param width the width of the framebuffer in pixels for this frame.
	 * @param height the height of the framebuffer in pixels for this frame.
	 */
	final void startFrame(int width, int height)
	{
		canvasWidth = width;
		canvasHeight = height;
		
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
	 * and if the object is from a later version, return false.
	 * @param versioned the versioned element to check against.
	 * @return true if the object is supported by this graphics implementation's version, 
	 *     or false if if the versioned object is a later version than this one, or is not core if this graphics instance is.
	 */
	public boolean supports(OGLVersioned versioned)
	{
		if (getVersion().compareTo(versioned.getVersion()) < 0)
			return false;
		if (isCore() && !versioned.isCore())
			return false;
		
		return true;
	}
	
	/**
	 * Checks the version of this graphics implementation against a versioned object,
	 * and if the object is from a later version, throw an exception.
	 * This will also throw an exception if this version is core and the provided object is not.
	 * @param versioned the versioned element to check against.
	 * @throws UnsupportedOperationException if the versioned object is a later version than this one, or is not core if this graphics instance is.
	 */
	public void verifyFeatureSupport(OGLVersioned versioned)
	{
		if (!options.performVersionChecking())
			return;
			
		if (getVersion().compareTo(versioned.getVersion()) < 0)
			throw new UnsupportedOperationException(versioned.getClass().getSimpleName() + " requires version " + versioned.getVersion().name());
		if (isCore() && !versioned.isCore())
			throw new UnsupportedOperationException("Using " + versioned.getClass().getSimpleName() + " requires it being part of the core spec, and it isn't.");
	}
	
	/**
	 * Checks if the version of this graphics implementation 
	 * is non-core, and if it is, it throws an exception.
	 * @throws UnsupportedOperationException if this graphics instance is a core implementation.
	 */
	public void verifyNonCore()
	{
		if (!options.performVersionChecking())
			return;
		if (isCore())
			throw new UnsupportedOperationException("This is unavailable in a core implementation.");
	}
	
	@Override
	public boolean isCore()
	{
		return core;
	}
	
	/**
	 * @return the framebuffer width in pixels.
	 */
	public int getCanvasWidth()
	{
		return canvasWidth;
	}
	
	/**
	 * @return the framebuffer height in pixels.
	 */
	public int getCanvasHeight()
	{
		return canvasHeight;
	}

	/**
	 * @return the aspect ratio of the framebuffer, width to height.
	 */
	public float getCanvasAspect()
	{
		return (float)getCanvasWidth() / (float)getCanvasHeight();
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
		return GL11.glGetInteger(glEnum);
	}

	/**
	 * Grabs a series of OpenGL context integer values using a GL value enum.
	 * @param glEnum the GL enum.
	 * @param out the output array.
	 */
	public void getInts(int glEnum, int[] out)
	{
		GL11.glGetIntegerv(glEnum, out);
	}

	/**
	 * Grabs an OpenGL context float value using a GL value enum. 
	 * @param glEnum the GL enum.
	 * @return the value. 
	 */
	public float getFloat(int glEnum)
	{
		return GL11.glGetFloat(glEnum);
	}

	/**
	 * Grabs a series of OpenGL context float values using a GL value enum.
	 * @param glEnum the GL enum.
	 * @param out the output array.
	 */
	public void getFloats(int glEnum, float[] out)
	{
		GL11.glGetFloatv(glEnum, out);
	}

	/**
	 * Enables/disables an OpenGL state bit.
	 * @param glEnum the OpenGL enumerant.
	 * @param flag if true, enable. if false, disable.
	 */
	public void setFlag(int glEnum, boolean flag)
	{
		if (flag)
			GL11.glEnable(glEnum);
		else
			GL11.glDisable(glEnum);
	}
	
	/**
	 * Enables/disables an OpenGL client state bit.
	 * @param glEnum the OpenGL enumerant.
	 * @param flag if true, enable. if false, disable.
	 */
	public void setClientFlag(int glEnum, boolean flag)
	{
		verifyNonCore();
		if (flag)
			GL11.glEnableClientState(glEnum);
		else
			GL11.glDisableClientState(glEnum);
	}
	
	/**
	 * Converts a Java boolean to an OpenGL GL_TRUE or GL_FALSE value.
	 * @param val the boolean value.
	 * @return the GL boolean equivalent. 
	 */
	public int toGLBool(boolean val)
	{
		return val ? GL11.GL_TRUE : GL11.GL_FALSE;
	}

	/**
	 * Clears the error bits for the GL Error flags.
	 */
	public void clearError()
	{
		if (options.performErrorChecking())
			while (GL11.glGetError() != GL11.GL_NO_ERROR) {}
	}

	/**
	 * Tests for an OpenGL error via glGetError(), but only if error checking is enabled.
	 * If one is raised, this throws a GraphicsException with the error message.
	 * @throws GraphicsException if an error is raised. 
	 * @see Options#performErrorChecking()
	 */
	public void checkError()
	{
		if (!options.performErrorChecking())
			return;
		
		int error = GL11.glGetError();
		if (error != GL11.GL_NO_ERROR)
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
	 * Destroys an object, presumably created by this graphics object.
	 * @param object the object to destroy.
	 */
	protected void destroyObject(OGLObject object)
	{
		object.destroy();
	}
	
}
