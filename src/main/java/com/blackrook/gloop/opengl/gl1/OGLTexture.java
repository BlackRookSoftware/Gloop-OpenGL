/*******************************************************************************
 * Copyright (c) 2014-2015 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gloop.opengl.gl1;

import com.blackrook.gloop.opengl.OGLObject;

import static org.lwjgl.opengl.GL11.*;

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
	OGLTexture()
	{
		super();
	}
	
	@Override
	protected int allocate()
	{
		return glGenTextures();
	}
	
	@Override
	protected void free()
	{
		glDeleteTextures(getName());
	}
	
	/**
	 * Destroys undeleted texture objects abandoned from destroyed Java objects.
	 * <p><b>This is automatically called by OGLSystem after every frame and should NEVER be called manually!</b>
	 */
	public static void destroyUndeleted()
	{
		if (UNDELETED_LENGTH > 0)
		{
			for (int i = 0; i < UNDELETED_LENGTH; i++)
				glDeleteTextures(UNDELETED_IDS[i]);
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
