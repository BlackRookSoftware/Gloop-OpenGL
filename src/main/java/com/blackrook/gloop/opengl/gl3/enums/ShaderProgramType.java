/*******************************************************************************
 * Copyright (c) 2021 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at 
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gloop.opengl.gl3.enums;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL33.*;

/**
 * Shader program type.
 * @author Matthew Tropiano
 */
public enum ShaderProgramType
{
	/** Vertex shader program. */
	VERTEX(GL_VERTEX_SHADER),
	/** Fragment shader program. */
	FRAGMENT(GL_FRAGMENT_SHADER),
	/** Geometry shader program. */
	GEOMETRY(GL_GEOMETRY_SHADER);
	
	public final int glValue;
	private ShaderProgramType(int glValue) {this.glValue = glValue;}

}
