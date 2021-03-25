/*******************************************************************************
 * Copyright (c) 2014-2015 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gloop.opengl.gl2.objects;

import com.blackrook.gloop.opengl.gl2.enums.ShaderProgramType;

/**
 * Vertex shader program.
 * @author Matthew Tropiano
 */
public class OGLShaderProgramVertex extends OGLShaderProgram
{
	/**
	 * Creates a new vertex shader program.
	 * @param streamName the name of the source stream.
	 * @param sourceCode the input stream.
	 */
	public OGLShaderProgramVertex(String streamName, String sourceCode)
	{
		super();
		construct(streamName, sourceCode);
	}

	@Override
	public ShaderProgramType getType()
	{
		return ShaderProgramType.VERTEX;
	}
	
}
