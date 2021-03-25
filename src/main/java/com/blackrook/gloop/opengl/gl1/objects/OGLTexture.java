/*******************************************************************************
 * Copyright (c) 2014-2015 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gloop.opengl.gl1.objects;

import org.lwjgl.system.MemoryStack;

import com.blackrook.gloop.opengl.OGLObject;

import static org.lwjgl.opengl.GL11.*;

import java.nio.IntBuffer;

/**
 * Standard texture class.
 * @author Matthew Tropiano
 */
public class OGLTexture extends OGLObject
{
	/** List of OpenGL object ids that were not deleted properly. */
	protected static int[] UNDELETED_IDS;
	/** Amount of OpenGL object ids that were not deleted properly. */
	protected static int UNDELETED_LENGTH;
	
	static
	{
		UNDELETED_IDS = new int[32];
		UNDELETED_LENGTH = 0;
	}

	/**
	 * Creates a new blank texture object.
	 */
	public OGLTexture()
	{
		super();
	}
	
	@Override
	protected int allocate()
	{
		try (MemoryStack stack = MemoryStack.stackPush())
		{
			IntBuffer buf = stack.mallocInt(2);
			buf.put(0, 1);
			glGenTextures(buf);
			return buf.get(1);
		}
	}
	
	@Override
	protected void free()
	{
		try (MemoryStack stack = MemoryStack.stackPush())
		{
			IntBuffer buf = stack.mallocInt(2);
			buf.put(0, 1);
			buf.put(1, getName());
			glDeleteTextures(buf);
		}		
	}
	
	/**
	 * Destroys undeleted texture objects abandoned from destroyed Java objects.
	 * <p><b>This is automatically called by OGLSystem after every frame and should NEVER be called manually!</b>
	 */
	public static void destroyUndeleted()
	{
		if (UNDELETED_LENGTH > 0)
		{
			try (MemoryStack stack = MemoryStack.stackPush())
			{
				IntBuffer buf = stack.mallocInt(UNDELETED_LENGTH + 1);
				buf.put(UNDELETED_LENGTH);
				buf.put(UNDELETED_IDS, 0, UNDELETED_LENGTH);
				buf.rewind();
				glDeleteTextures(buf);
			}
			UNDELETED_LENGTH = 0;
		}
	}

	// adds the OpenGL Id to the UNDELETED_IDS list.
	private static void finalizeAddId(int id)
	{
		if (UNDELETED_LENGTH == UNDELETED_IDS.length)
			UNDELETED_IDS = expand(UNDELETED_IDS, UNDELETED_IDS.length * 2);
		UNDELETED_IDS[UNDELETED_LENGTH++] = id;
	}

	@Override
	public void finalize() throws Throwable
	{
		if (isAllocated())
			finalizeAddId(getName());
		super.finalize();
	}

}
