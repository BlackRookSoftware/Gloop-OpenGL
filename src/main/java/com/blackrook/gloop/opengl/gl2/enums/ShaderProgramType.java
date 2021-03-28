/*******************************************************************************
 * Copyright (c) 2021 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at 
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gloop.opengl.gl2.enums;

import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL32;
import org.lwjgl.opengl.GL40;
import org.lwjgl.opengl.GL43;

/**
 * Shader program type.
 * @author Matthew Tropiano
 */
public enum ShaderProgramType
{
	/** Vertex shader program. */
	VERTEX(GL20.GL_VERTEX_SHADER),
	/** Tesselation evaluation program. */
	TESSELATION_EVALUATION(GL40.GL_TESS_EVALUATION_SHADER),
	/** Tesselation control program. */
	TESSELATION_CONTROL(GL40.GL_TESS_CONTROL_SHADER),
	/** Geometry shader program. */
	GEOMETRY(GL32.GL_GEOMETRY_SHADER),
	/** Fragment shader program. */
	FRAGMENT(GL20.GL_FRAGMENT_SHADER),
	/** Compute shader program. */
	COMPUTE(GL43.GL_COMPUTE_SHADER);
	
	public final int glValue;
	private ShaderProgramType(int glValue) {this.glValue = glValue;}
}
