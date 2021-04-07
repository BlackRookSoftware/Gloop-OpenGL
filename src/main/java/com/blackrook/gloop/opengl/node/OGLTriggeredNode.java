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
 * A framebuffer node that performs a one-time set of graphics commands.
 * Can be reset so that the one-time function can be performed again.
 * @author Matthew Tropiano
 * @param <GL> the graphics object to call.
 */
public abstract class OGLTriggeredNode<GL extends OGLGraphics> extends OGLNodeAdapter<GL>
{
	/** If true, the triggered function is called. */
	protected boolean trigger;
	
	/**
	 * Creates a new triggered node where the trigger starts set.
	 * In other words, this will call the triggered function once,
	 * and not again until triggered.
	 */
	public OGLTriggeredNode()
	{
		this(true);
	}

	/**
	 * Creates a new triggered node where the trigger starts
	 * set or unset, according to the programmer.
	 * @param triggerFlag the initial state of the trigger.
	 */
	public OGLTriggeredNode(boolean triggerFlag)
	{
		this.trigger = triggerFlag;
	}

	@Override
	public final void onDisplay(GL gl)
	{
		if (trigger)
		{
			trigger = false;
			doTriggeredFunction(gl);
		}
	}

	/**
	 * Sets the trigger on this node.
	 */
	public void setTrigger()
	{
		trigger = true;
	}
	
	/**
	 * This is the method called by {@link #onDisplay(OGLGraphics)} 
	 * for when the triggered method needs to run.
	 * @param gl the OGLGraphics context. 
	 */
	public abstract void doTriggeredFunction(GL gl);

}
