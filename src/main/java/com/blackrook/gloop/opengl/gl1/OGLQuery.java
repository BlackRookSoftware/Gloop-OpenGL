/*******************************************************************************
 * Copyright (c) 2021-2022 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at 
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gloop.opengl.gl1;

import com.blackrook.gloop.opengl.OGLObject;

import static org.lwjgl.opengl.GL15.*;

/**
 * An encapsulation of a query object for OpenGL.
 * @author Matthew Tropiano
 */
public class OGLQuery extends OGLObject
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
	 * Creates a new Occlusion Query object handle.
	 */
	OGLQuery()
	{
		setName(glGenQueries());
	}

	@Override
	protected void free()
	{
		glDeleteQueries(getName());
	}
	
	/**
	 * Destroys undeleted query objects abandoned from destroyed Java objects.
	 * <p><b>This is automatically called by OGLSystem after every frame and should NEVER be called manually!</b>
	 * @return the amount of objects deleted.
	 */
	public static int destroyUndeleted()
	{
		if (UNDELETED_LENGTH > 0)
		{
			int out = UNDELETED_LENGTH;
			for (int i = 0; i < UNDELETED_LENGTH; i++)
				glDeleteQueries(UNDELETED_IDS[i]);
			UNDELETED_LENGTH = 0;
			return out;
		}
		return 0;
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
