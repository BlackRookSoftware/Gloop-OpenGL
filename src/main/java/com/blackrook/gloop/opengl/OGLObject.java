/*******************************************************************************
 * Copyright (c) 2021 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at 
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gloop.opengl;

import com.blackrook.gloop.opengl.exception.GraphicsException;

/**
 * Generic OpenGL object type.
 * Since this is managed internally by OpenGL, the handles to these
 * objects are "names" (aka integer IDs) rather than memory addresses.
 * @author Matthew Tropiano
 */
public abstract class OGLObject
{
	/** This object's GLId. */
	private int glId;
	/** Was this object allocated? */
	private boolean allocated;

	/**
	 * Allocates a new OpenGL object. 
	 * Calls allocate().
	 * @see #allocate()
	 */
	protected OGLObject()
	{
		this.glId = allocate(); 
		this.allocated = true; 
	}
	
	/**
	 * @return this OGLObject OpenGL object id.
	 */
	public final int getName()
	{
		return glId;
	}

	@Override
	public int hashCode() 
	{
		return Integer.hashCode(glId);
	}
	
	@Override
	public boolean equals(Object obj) 
	{
		if (obj instanceof OGLObject)
			return equals((OGLObject)obj);
		return super.equals(obj);
	}
	
	/**
	 * Tests if this OpenAL object equals the provided one.
	 * @param obj the object to test.
	 * @return true if so, false if not.
	 */
	public boolean equals(OGLObject obj) 
	{
		return getClass().equals(obj.getClass()) && glId == obj.glId;
	}

	/**
	 * Destroys this object.
	 * @throws GraphicsException if an error occurred destroying the object.
	 */
	public void destroy() 
	{
		if (allocated)
		{
			free();
			glId = 0;
		}
		allocated = false;
	}

	/**	 
	 * Allocates a new type of this object in OpenAL.
	 * Called by OALObject constructor.
	 * @return the ALId of this new object.
	 * @throws GraphicsException if the allocation cannot happen.
	 */
	protected abstract int allocate();
	
	/**
	 * Destroys this object (deallocates it on OpenAL).
	 * This is called by destroy().
	 * @throws GraphicsException if the deallocation cannot happen.
	 */
	protected abstract void free();
	
	@Override
	public String toString()
	{
		return getClass().getSimpleName() + ' ' + getName();
	}

}
