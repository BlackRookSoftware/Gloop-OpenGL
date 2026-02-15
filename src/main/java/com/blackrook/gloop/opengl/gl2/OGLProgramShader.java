/*******************************************************************************
 * Copyright (c) 2021-2022 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at 
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gloop.opengl.gl2;

import com.blackrook.gloop.opengl.OGLObject;
import com.blackrook.gloop.opengl.enums.ShaderType;
import com.blackrook.gloop.opengl.exception.GraphicsException;

import static org.lwjgl.opengl.GL20.*;

/**
 * A single GLSL shader in a program pipeline.
 * @author Matthew Tropiano
 */
public class OGLProgramShader extends OGLObject
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

	/** Shader type. */
	protected ShaderType type;

	/** Compile log. */
	protected String log;

	/**
	 * Constructor for the shader class.
	 * @param type the shader program type.
	 * @param streamName the source stream name.
	 * @param sourceCode the source code itself.
	 */
	OGLProgramShader(ShaderType type, String streamName, String sourceCode)
	{
		setName(glCreateShader(type.glValue));
		
		this.type = type;
		glShaderSource(getName(), sourceCode);
		glCompileShader(getName());
		this.log = glGetShaderInfoLog(getName());
		if (glGetShaderi(getName(), GL_COMPILE_STATUS) == 0)
			throw new GraphicsException("Failed to compile \"" + streamName + "\"\n" + log);
	}

	@Override
	protected void free()
	{
		glDeleteShader(getName());
	}

	/**
	 * @return the shader program type.
	 */
	public ShaderType getType()
	{
		return type;
	}
	
	/**
	 * @return the log from this program's compiling.
	 */
	public String getLog()
	{
		return log;
	}

	/**
	 * Destroys undeleted programs abandoned from destroyed Java objects.
	 * <p><b>This is automatically called by OGLSystem after every frame and should NEVER be called manually!</b>
	 * @return the amount of objects deleted.
	 */
	public static int destroyUndeleted()
	{
		if (UNDELETED_LENGTH > 0)
		{
			int out = UNDELETED_LENGTH;
			for (int i = 0; i < UNDELETED_LENGTH; i++)
				glDeleteShader(UNDELETED_IDS[i]);
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
