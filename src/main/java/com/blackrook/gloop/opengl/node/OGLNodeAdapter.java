/*******************************************************************************
 * Copyright (c) 2021 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at 
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gloop.opengl.node;

import com.blackrook.gloop.opengl.OGLGraphics;

/**
 * An abstract framebuffer node class that basically overrides most
 * of {@link OGLNode} methods and does NOTHING with them.
 * It's merely a convenience class in the vein of "Adapter" classes
 * that helps reduce code clutter.
 * <p>
 * The input methods are overridden and return <code>false</code>. <br>
 * The methods {@link #getPolygonsRendered()} and {@link #getRenderTimeNanos()} return 0.<br>
 * The methods {@link #onDisplay(OGLGraphics)} and {@link #onFramebufferResize(int, int)} do nothing unless overridden.<br>
 * @param <GL> the graphics object to call.
 * @author Matthew Tropiano
 */
public abstract class OGLNodeAdapter<GL extends OGLGraphics> implements OGLNode<GL>
{

	@Override
	public void onDisplay(GL gl)
	{
		// Do nothing.
	}

	@Override
	public void onFramebufferResize(int newWidth, int newHeight)
	{
		// Do nothing.
	}

	@Override
	public int getPolygonsRendered()
	{
		return 0;
	}

	@Override
	public long getRenderTimeNanos()
	{
		return 0L;
	}

}
