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
package com.blackrook.gloop.opengl.gl2.objects;

import com.blackrook.gloop.opengl.OGLObject;
import com.blackrook.gloop.opengl.exception.GraphicsException;
import com.blackrook.gloop.opengl.gl2.enums.ShaderProgramType;

import static org.lwjgl.opengl.GL20.*;

/**
 * GLSL Shader program.
 * @author Matthew Tropiano
 */
public abstract class OGLShaderProgram extends OGLObject
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

	/** Compile log. */
	protected String log;

	/**
	 * Protected constructor for the program class.
	 */
	protected OGLShaderProgram()
	{
		super();
	}

	/**
	 * Protected shared constructor for the program class.
	 * @param streamName the name of this stream.
	 * @param sourceCode the shader source code.
	 */
	protected void construct(String streamName, String sourceCode)
	{
		glShaderSource(getName(), sourceCode);
        glCompileShader(getName());
        this.log = glGetShaderInfoLog(getName());
        if (glGetShaderi(getName(), GL_COMPILE_STATUS) == 0)
        	throw new GraphicsException("Failed to compile \"" + streamName + "\"\n" + log);
	}
	
	@Override
	protected int allocate()
	{
		return glCreateShader(getType().glValue);
	}

	@Override
	protected void free()
	{
		glDeleteShader(getName());
	}

	/**
	 * @return the shader program type.
	 */
	public abstract ShaderProgramType getType();
	
	/**
	 * @return the log from this program's compiling.
	 */
	public String getLog()
	{
		return log;
	}

	/**
	 * Destroys undeleted programs abandoned from destroyed Java objects.
	 */
	public static void destroyUndeleted()
	{
		if (UNDELETED_LENGTH > 0)
		{
			for (int i = 0; i < UNDELETED_LENGTH; i++)
				glDeleteShader(UNDELETED_IDS[i]);
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
