/*******************************************************************************
 * Copyright (c) 2021 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at 
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gloop.opengl.node;

import java.util.ArrayList;
import java.util.List;

import com.blackrook.gloop.opengl.OGLGraphics;

/**
 * An listener node that contains other {@link OGLNode}s.
 * Contains methods to invoke before they are displayed and after they are displayed.
 * @param <GL> the graphics object to call.
 * @author Matthew Tropiano
 */
public class OGLMultiNode<GL extends OGLGraphics> implements OGLNode<GL>
{
	/** Node list. */
	private List<OGLNode<? super GL>> nodeList;

	/** Is this layer (and its children) enabled? */
	private boolean enabled;

	/** Render time in nanos. */
	protected long renderTimeNanos;
	/** Polygons Rendered */
	protected int polygonsRendered;

	/**
	 * The default constructor.
	 */
	public OGLMultiNode()
	{
		this.nodeList = new ArrayList<>(4);
		this.enabled = true;
	}

	/**
	 * Adds an OGLNode to this multinode.
	 * @param node the node to add.
	 */
	public void addNode(OGLNode<? super GL> node)
	{
		synchronized (nodeList)
		{
			nodeList.add(node);
		}
	}

	/**
	 * Removes an OGLNode from this multinode.
	 * @param node the node to remove.
	 * @return true if removed, false if not (was never added).
	 */
	public boolean removeNode(OGLNode<? super GL> node)
	{
		synchronized (nodeList)
		{
			return nodeList.remove(node);
		}
	}

	/**
	 * Called by {@link #onFramebufferResize(int, int)} before all of 
	 * the attached nodes have {@link #onFramebufferResize(int, int)} called on them.
	 * Does nothing by default.
	 * @param newWidth the new framebuffer width.
	 * @param newHeight  the new framebuffer height.
	 */
	public void preFramebufferResize(int newWidth, int newHeight)
	{
		// Do nothing.
	}
	
	/**
	 * Called when the system canvas gets resized, or once this node gets added to the system.
	 * This calls {@link #onFramebufferResize(int, int)} on all of the added child nodes in the order that they were added.
	 * @param newWidth the new framebuffer width. 
	 * @param newHeight	the new framebuffer height.
	 */
	@Override
	public void onFramebufferResize(int newWidth, int newHeight)
	{
		preFramebufferResize(newWidth, newHeight);
		for (OGLNode<? super GL> sys : nodeList)
			sys.onFramebufferResize(newWidth, newHeight);
		postFramebufferResize(newWidth, newHeight);
	}

	/**
	 * Called by {@link #onFramebufferResize(int, int)} after all of 
	 * the added child nodes have {@link #onFramebufferResize(int, int)} called on them.
	 * Does nothing by default.
	 * @param newWidth the new framebuffer width.
	 * @param newHeight  the new framebuffer height.
	 */
	public void postFramebufferResize(int newWidth, int newHeight)
	{
		// Do nothing.
	}
	
	/**
	 * Called by display() before all of the added child nodes are displayed.
	 * Does nothing by default.
	 * @param gl the GL object used for issuing commands to OpenGL.
	 */
	public void preNodeDisplay(GL gl)
	{
		// Do nothing.
	}
	
	/**
	 * Displays this node's children in the order they were added, if enabled.
	 * The rendering thread for the target window enters this method,
	 * so it is safe to call all {@link OGLGraphics} functions here. 
	 * @param gl the graphics object used for issuing commands to OpenGL.
	 */
	@Override
	public void onDisplay(GL gl)
	{
		if (!enabled)
			return;
		
		polygonsRendered = 0;
		long nanos = System.nanoTime();
		preNodeDisplay(gl);
		for (OGLNode<? super GL> node : nodeList)
		{
			node.onDisplay(gl);
			polygonsRendered += node.getPolygonsRendered();
		}
		postNodeDisplay(gl);
		renderTimeNanos = System.nanoTime() - nanos;
	}

	/**
	 * Called by display() after all of the added child nodes are displayed.
	 * Does nothing by default.
	 * @param gl the GL object used for issuing commands to OpenGL.
	 */
	public void postNodeDisplay(GL gl)
	{
		// Do nothing.
	}
	
	@Override
	public int getPolygonsRendered()
	{
		return polygonsRendered;
	}

	@Override
	public long getRenderTimeNanos()
	{
		return renderTimeNanos;
	}

	/**
	 * @return true if this multinode is enabled, false if not.
	 * @see #setEnabled(boolean)
	 */
	public boolean isEnabled()
	{
		return enabled;
	}

	/**
	 * Sets if this node (and all of its added child nodes) are displayed.
	 * If this node is NOT enabled, neither {@link #preNodeDisplay(OGLGraphics)},
	 * {@link #onDisplay(OGLGraphics)}, nor {@link #postNodeDisplay(OGLGraphics)} is called.
	 * It will still receive calls to resize itself.
	 * @param enabled true if so, false if not.
	 * @see #isEnabled()
	 * @see OGLMultiNode#onDisplay(OGLGraphics)
	 */
	public void setEnabled(boolean enabled)
	{
		this.enabled = enabled;
	}
	
}
