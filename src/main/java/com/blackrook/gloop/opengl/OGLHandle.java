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
 * Generic OpenGL handle type.
 * Essentially an object that is not an integer id - this wraps a memory address.
 * @author Matthew Tropiano
 */
public abstract class OGLHandle
{
	/**
	 * Allocates a new OpenGL handle.
	 */
	protected OGLHandle() {}
	
	/**
	 * @return this handle's OpenGL address handle.
	 */
	public abstract long getHandle();

	/**
	 * @return true if this handle was allocated, false if not.
	 */
	public abstract boolean isCreated(); 
	
	/**
	 * Destroys this handle. Does nothing if already destroyed.
	 * @throws GraphicsException if a problem occurs during free.
	 */
	public abstract void destroy();

	@Override
	public int hashCode() 
	{
		return Long.hashCode(getHandle());
	}

	@Override
	public boolean equals(Object obj) 
	{
		if (obj instanceof OGLHandle)
			return equals((OGLHandle)obj);
		return super.equals(obj);
	}

	/**
	 * Tests if this OpenAL handle equals the provided one.
	 * @param handle the handle to test.
	 * @return true if so, false if not.
	 */
	public boolean equals(OGLHandle handle) 
	{
		return getClass().equals(handle.getClass()) && this.getHandle() == handle.getHandle();
	}

	/**
	 * Frees this object from OpenGL.
	 * AS ALWAYS, NEVER CALL DIRECTLY. 
	 */
	@Override
	public void finalize() throws Throwable
	{
		destroy();
		super.finalize();
	}
	
	@Override
	public String toString()
	{
		return getClass().getSimpleName() + ' ' + getHandle();
	}

}
