/*******************************************************************************
 * Copyright (c) 2014, 2015 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 *
 * Contributors:
 *     Matt Tropiano - initial API and implementation
 *******************************************************************************/
package com.blackrook.gloop.opengl.gl1;

import com.blackrook.gloop.opengl.OGLObject;

import org.lwjgl.opengl.GL33;

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
	
	private int glQueryType;
	
	/**
	 * Creates a new Occlusion Query object handle.
	 * @param glQueryType the query type (OpenGL enum).
	 */
	OGLQuery(int glQueryType)
	{
		super();
		this.glQueryType = glQueryType;
	}

	@Override
	protected int allocate()
	{
		return glGenQueries();
	}

	@Override
	protected void free()
	{
		glDeleteQueries(getName());
	}
	
	/**
	 * Starts this query.
	 */
	public void start()
	{
		glBeginQuery(glQueryType, getName());
	}

	/**
	 * Ends this query.
	 * The query is NOT FREED, and its results should be 
	 * fetched after verifying that it is complete, later.
	 */
	public void end()
	{
		glEndQuery(glQueryType);
	}

	/**
	 * @return true if this query's results are available, false otherwise.
	 */
	public boolean isReady()
	{
		return glGetQueryi(getName(), GL_QUERY_RESULT_AVAILABLE) == GL_TRUE;
	}
	
	/**
	 * Gets the result of the query as a long integer.
	 * If {@link #isReady()} is not checked beforehand, this will hold the thread until
	 * the query is finished.
	 * Depending on your OpenGL version, a 64-bit precision value may not be available.
	 * @return the long value of the result.
	 */
	public long getResult()
	{
		int bits = glGetQueryi(getName(), GL_QUERY_COUNTER_BITS);
		if (bits <= 32)
		{
			int result = glGetQueryi(getName(), GL_QUERY_RESULT);
			return 0x0ffffffffL & result;
		}
		else
		{
			return GL33.glGetQueryObjecti64(getName(), GL_QUERY_RESULT);
		}
	}

	/**
	 * Gets the result of the query as a boolean.
	 * If {@link #isReady()} is not checked beforehand, this will hold the thread until
	 * the query is finished.
	 * @return the boolean value of the result.
	 */
	public boolean getBooleanResult()
	{
		return glGetQueryi(getName(), GL_QUERY_RESULT) != GL_FALSE;
	}

	/**
	 * @return the query type as an OpenGL enum.
	 */
	public int getGLQueryType()
	{
		return glQueryType;
	}
	
	/**
	 * Destroys undeleted query objects abandoned from destroyed Java objects.
	 */
	public static void destroyUndeleted()
	{
		if (UNDELETED_LENGTH > 0)
		{
			for (int i = 0; i < UNDELETED_LENGTH; i++)
				glDeleteQueries(UNDELETED_IDS[i]);
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
