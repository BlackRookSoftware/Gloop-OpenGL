/*******************************************************************************
 * Copyright (c) 2021-2022 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at 
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gloop.opengl.node;

import com.blackrook.gloop.opengl.OGLGraphics;
import com.blackrook.gloop.opengl.OGLSystem;

/**
 * A significant entry point whose methods that get executed upon the
 * graphics system requesting a framebuffer redraw or resize.
 * <p>
 * Nodes are added to a {@link OGLSystem}, and drawn in the order in which they were added.
 * They may also receive other events.
 * @param <GL> the graphics object to call.
 * @author Matthew Tropiano
 */
public interface OGLNode<GL extends OGLGraphics>
{
	/**
	 * Displays this node.
	 * The rendering thread for the target window enters this method,
	 * so it is safe to call all {@link OGLGraphics} functions here. 
	 * @param gl the graphics object used for issuing commands to OpenGL.
	 */
	default void onDisplay(GL gl)
	{
		// Do nothing.
	}
	
	/**
	 * Called when the system canvas gets resized, or once this node gets added to the system.
	 * @param newWidth the new framebuffer width. 
	 * @param newHeight	the new framebuffer height.
	 */
	default void onFramebufferResize(int newWidth, int newHeight)
	{
		// Do nothing.
	}

	/**
	 * Returns the length of time it took to render this node, in nanoseconds.
	 * Results of this call should not be considered accurate until the node 
	 * has had {@link #onDisplay(OGLGraphics)} called on it.
	 * @return the length of time it took to render this node, in nanoseconds.
	 */
	default long getRenderTimeNanos()
	{
		// Do nothing.
		return 0L;
	}

	/**
	 * Gets the number of polygonal objects rendered in this layer.
	 * <p> This is mostly for statistical purposes. 
	 * Results of this call should not be considered accurate until the 
	 * node has had {@link #onDisplay(OGLGraphics)} called on it.
	 * @return the number of polygonal objects rendered in this layer.
	 */
	default int getPolygonsRendered()
	{
		// Do nothing.
		return 0;
	}

}
