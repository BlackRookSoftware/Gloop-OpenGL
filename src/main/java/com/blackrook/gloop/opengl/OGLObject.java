/*******************************************************************************
 * Copyright (c) 2021-2022 Black Rook Software
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
 * <p>
 * @author Matthew Tropiano
 */
public abstract class OGLObject
{
	/** This object's GLId. */
	private int glId;
	/** This object's long GLId. */
	private long longGLId;
	/** Was this object allocated? */
	private boolean allocated;

	/**
	 * Allocates a new OpenGL object. 
	 */
	protected OGLObject()
	{
		// Must override.
	}
	
	/**
	 * Sets this object's OpenGL name/id.
	 * Set either this or the longer one.
	 * @param glId the new id.
	 * @throws GraphicsException if the id is zero.
	 */
	protected void setName(int glId)
	{
		if (glId == 0)
			throw new GraphicsException("Object could not be created.");
		this.glId = glId;
		this.allocated = true; 
	}
	
	/**
	 * Sets this object's long OpenGL name/id.
	 * Set either this or the shorter one.
	 * @param glId the new id.
	 * @throws GraphicsException if the id is zero.
	 */
	protected void setLongName(long glId)
	{
		if (glId == 0)
			throw new GraphicsException("Object could not be created.");
		this.longGLId = glId;
		this.allocated = true; 
	}
	
	/**
	 * @return this OGLObject OpenGL object id.
	 */
	public final int getName()
	{
		return glId;
	}

	/**
	 * @return this OGLObject OpenGL object id.
	 */
	public final long getLongName()
	{
		return longGLId;
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
		return getClass().equals(obj.getClass()) && glId == obj.glId && longGLId == obj.longGLId;
	}

	/**
	 * Destroys this object.
	 * @throws GraphicsException if an error occurred destroying the object.
	 */
	void destroy() 
	{
		if (allocated)
		{
			free();
			glId = 0;
			longGLId = 0L;
		}
		allocated = false;
	}

	/**
	 * @return true if allocated in OpenGL, false if not.
	 */
	protected boolean isAllocated()
	{
		return allocated;
	}
	
	/**
	 * Destroys this object (deallocates it on OpenAL).
	 * This is called by destroy().
	 * @throws GraphicsException if the deallocation cannot happen.
	 */
	protected abstract void free();

	/**
	 * Utility function to expand the "undeleted" GL name pool if it is below a certain length.
	 * @param input the input array.
	 * @param targetLength the target length.
	 * @return the array itself (<code>input</code>) if no expansion needed, or a new array with the contents copied if
	 * <code>input.length &lt; targetLength</code>. 
	 */
	protected static int[] expand(int[] input, int targetLength)
	{
		if (input.length < targetLength)
		{
			int[] newarr = new int[targetLength];
			System.arraycopy(input, 0, newarr, 0, input.length);
			input = newarr;
		}
		return input;
	}
	
	/**
	 * Utility function to expand the "undeleted" GL name pool if it is below a certain length.
	 * @param input the input array.
	 * @param targetLength the target length.
	 * @return the array itself (<code>input</code>) if no expansion needed, or a new array with the contents copied if
	 * <code>input.length &lt; targetLength</code>. 
	 */
	protected static long[] expand(long[] input, int targetLength)
	{
		if (input.length < targetLength)
		{
			long[] newarr = new long[targetLength];
			System.arraycopy(input, 0, newarr, 0, input.length);
			input = newarr;
		}
		return input;
	}
	
	@Override
	public String toString()
	{
		return getClass().getSimpleName() + ' ' + getName();
	}

}
