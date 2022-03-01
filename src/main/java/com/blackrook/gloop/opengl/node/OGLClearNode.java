/*******************************************************************************
 * Copyright (c) 2021 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at 
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gloop.opengl.node;

import com.blackrook.gloop.opengl.gl1.OGL11Graphics;

/**
 * Node that clears one or more buffers.
 * @author Matthew Tropiano
 */
public class OGLClearNode implements OGLNode<OGL11Graphics>
{
	/** The clear color to use for clearing - red component. */
	private float clearRed;
	/** The clear color to use for clearing - green component. */
	private float clearGreen;
	/** The clear color to use for clearing - blue component. */
	private float clearBlue;
	/** The clear color to use for clearing - alpha component. */
	private float clearAlpha;
	/** The depth buffer value to use for clearing. */
	private float depthValue;
	/** Are we clearing the color buffer in this step? */
	private boolean clearColorBuffer;
	/** Are we clearing the depth buffer in this step? */
	private boolean clearDepthBuffer;
	/** Are we clearing the color buffer in this step? */
	private boolean clearAccumulationBuffer;
	/** Are we clearing the stencil buffer in this step? */
	private boolean clearStencilBuffer;
	
	/** Clear time in nanoseconds. */
	private long renderTimeNanos;
	
	/**
	 * Creates a new OGLClearNode.
	 */
	public OGLClearNode()
	{
		this(false, false, false, false);
	}

	/**
	 * Creates a new OGLClearNode.
	 * @param color clear the color buffer?
	 * @param depth clear the depth buffer?
	 * @param accum clear the accum buffer?
	 * @param stencil clear the stencil buffer?
	 */
	public OGLClearNode(boolean color, boolean depth, boolean accum, boolean stencil)
	{
		this.clearColorBuffer = color;
		this.clearDepthBuffer = depth;
		this.clearAccumulationBuffer = accum;
		this.clearStencilBuffer = stencil;
		this.clearRed = 0f;
		this.clearGreen = 0f;
		this.clearBlue = 0f;
		this.clearAlpha = 0f;
		this.depthValue = 1f;
	}
	
	@Override
	public void onDisplay(OGL11Graphics gl)
	{
		long nanos = System.nanoTime();
		if (clearColorBuffer)
			gl.setClearColor(clearRed, clearGreen, clearBlue, clearAlpha);
		if (clearDepthBuffer)
			gl.setClearDepth(depthValue);
		gl.clear(clearColorBuffer, clearDepthBuffer, clearAccumulationBuffer, clearStencilBuffer);
		renderTimeNanos = System.nanoTime() - nanos;
	}
	
	@Override
	public long getRenderTimeNanos()
	{
		return renderTimeNanos;
	}

	/**
	 * Sets the color used with which to clear the color buffer.
	 * @param red the red component of the color to use (0 to 1).
	 * @param green the green component of the color to use (0 to 1).
	 * @param blue the blue component of the color to use (0 to 1).
	 * @param alpha the alpha component of the color to use (0 to 1).
	 */
	public void setClearColor(float red, float green, float blue, float alpha)
	{
		this.clearRed = red;
		this.clearGreen = green;
		this.clearBlue = blue;
		this.clearAlpha = alpha;
	}

	/**
	 * @return the depth value used with which to clear the depth buffer.
	 */
	public float getDepthValue()
	{
		return depthValue;
	}

	/**
	 * Sets the depth value used with which to clear the depth buffer.
	 * @param depthValue the depth value.
	 */
	public void setDepthValue(float depthValue)
	{
		this.depthValue = depthValue;
	}

	/**
	 * Gets if this clears the color buffer.
	 * @return true if so, false if not.
	 */
	public boolean clearsColorBuffer()
	{
		return clearColorBuffer;
	}

	/**
	 * Sets if this clears the color buffer.
	 * @param clearColorBuffer true if so, false if not.
	 */
	public void setClearColorBuffer(boolean clearColorBuffer)
	{
		this.clearColorBuffer = clearColorBuffer;
	}

	/**
	 * Gets if this clears the depth buffer.
	 * @return true if so, false if not.
	 */
	public boolean clearsDepthBuffer()
	{
		return clearDepthBuffer;
	}

	/**
	 * Sets if this clears the depth buffer.
	 * @param clearDepthBuffer true if so, false if not.
	 */
	public void setClearDepthBuffer(boolean clearDepthBuffer)
	{
		this.clearDepthBuffer = clearDepthBuffer;
	}

	/**
	 * Gets if this clears the accumulation buffer.
	 * @return true if so, false if not.
	 */
	public boolean clearsAccumulationBuffer()
	{
		return clearAccumulationBuffer;
	}

	/**
	 * Sets if this clears the accumulation buffer.
	 * @param clearAccumulationBuffer true if so, false if not.
	 */
	public void setClearAccumulationBuffer(boolean clearAccumulationBuffer)
	{
		this.clearAccumulationBuffer = clearAccumulationBuffer;
	}

	/**
	 * Gets if this clears the stencil buffer.
	 * @return true if so, false if not.
	 */
	public boolean clearsStencilBuffer()
	{
		return clearStencilBuffer;
	}

	/**
	 * Sets if this clears the stencil buffer.
	 * @param clearStencilBuffer true if so, false if not.
	 */
	public void setClearStencilBuffer(boolean clearStencilBuffer)
	{
		this.clearStencilBuffer = clearStencilBuffer;
	}

}
