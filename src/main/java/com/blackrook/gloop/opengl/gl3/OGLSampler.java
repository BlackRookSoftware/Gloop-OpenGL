/*******************************************************************************
 * Copyright (c) 2021-2024 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at 
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gloop.opengl.gl3;

import static org.lwjgl.opengl.GL33.glDeleteSamplers;
import static org.lwjgl.opengl.GL33.glGenSamplers;

import com.blackrook.gloop.opengl.OGLObject;

/**
 * A single sampler used to sample from GL pixel buffers.
 * @author Matthew Tropiano
 */
public class OGLSampler extends OGLObject 
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
	 * Constructs a new Sampler object.
	 */
	OGLSampler()
	{
		setName(glGenSamplers());
	}

	@Override
	protected void free()
	{
		glDeleteSamplers(getName());
	}

	/**
	 * Destroys undeleted texture objects abandoned from destroyed Java objects.
	 * <p><b>This is automatically called by OGLSystem after every frame and should NEVER be called manually!</b>
	 * @return the amount of objects deleted.
	 */
	public static int destroyUndeleted()
	{
		if (UNDELETED_LENGTH > 0)
		{
			int out = UNDELETED_LENGTH;
			for (int i = 0; i < UNDELETED_LENGTH; i++)
				glDeleteSamplers(UNDELETED_IDS[i]);
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
