/*******************************************************************************
 * Copyright (c) 2021 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at 
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gloop.opengl;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL11.GL_NO_ERROR;
import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glDisableClientState;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glEnableClientState;
import static org.lwjgl.opengl.GL11.glGetError;
import static org.lwjgl.opengl.GL11.glGetFloat;
import static org.lwjgl.opengl.GL11.glGetFloatv;
import static org.lwjgl.opengl.GL11.glGetInteger;
import static org.lwjgl.opengl.GL11.glGetIntegerv;

import java.util.Set;

import com.blackrook.gloop.opengl.exception.GraphicsException;

/**
 * A common encapsulator for all OpenGL implementations.
 * @author Matthew Tropiano
 */
public abstract class OGLGraphics
{
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

	/**
	 * Initializes this graphics.
	 */
	protected OGLGraphics()
	{
		this.currentFrame = 0L;
		this.startMilliseconds = System.currentTimeMillis();
		this.currentMilliseconds = -1L;
		this.currentNanos = -1L;
		this.currentBlitBit = false;
		this.currentTimeStepMillis = -1f;

		this.previousTimeNanos = -1L;
		this.currentTimeStepNanos = -1L;
		this.errorChecking = true;
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
	 * Called on frame end - does object cleanup.
	 */
	final void endFrame() 
	{
	    // Clean up abandoned objects.
		// TODO: Finish this.
		/*
	    OGLBuffer.destroyUndeleted(this);
	    OGLFrameBuffer.destroyUndeleted(this);
	    OGLRenderBuffer.destroyUndeleted(this);
	    OGLOcclusionQuery.destroyUndeleted(this);
	    OGLShader.destroyUndeleted(this);
	    OGLShaderProgram.destroyUndeleted(this);
	    OGLTexture.destroyUndeleted(this);
	    */
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
	public float currentTime()
	{
		return (currentMilliseconds - startMilliseconds) / 1000f;
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
	 * Tests for an OpenGL error via glGetError(). 
	 * If one is raised, this throws a GraphicsException with the error message.
	 */
	public void getError()
	{
		if (errorChecking)
		{
			int error = glGetError();
			if (error != GL_NO_ERROR)
				throw new GraphicsException("OpenGL raised error code " + error);
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
	 * @param errorChecking if true, {@link #clearError()} and {@link #getError()} do nothing. Else, they do stuff.
	 */
	public void setErrorChecking(boolean errorChecking)
	{
		this.errorChecking = errorChecking;
	}

	/**
	 * @return the OpenGL version string, according to the native implementation.
	 */
	public abstract String getVersion();
	
	/**
	 * @return the OpenGL GLSL version string, according to the native implementation.
	 */
	public abstract String getShadingLanguageVersion();

	/**
	 * @return the OpenGL vendor string, according to the native implementation.
	 */
	public abstract String getVendor();

	/**
	 * @return the OpenGL renderer string, according to the native implementation.
	 */
	public abstract String getRenderer();

	/**
	 * @return the OpenGL extension names as a Set, according to the native implementation.
	 */
	public abstract Set<String> getExtensionNames();

}
