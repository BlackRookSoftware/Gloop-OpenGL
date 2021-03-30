/*******************************************************************************
 * Copyright (c) 2021 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at 
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gloop.opengl.enums;

import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL32;
import org.lwjgl.opengl.GL40;
import org.lwjgl.opengl.GL43;

import com.blackrook.gloop.opengl.OGLVersion;
import com.blackrook.gloop.opengl.OGLVersioned;

/**
 * Shader program type.
 * @author Matthew Tropiano
 */
public enum ShaderProgramType implements OGLVersioned
{
	/** Vertex shader program. */
	VERTEX(OGLVersion.GL20, GL20.GL_VERTEX_SHADER),
	/** Tesselation evaluation program. */
	TESSELLATION_EVALUATION(OGLVersion.GL40, GL40.GL_TESS_EVALUATION_SHADER),
	/** Tesselation control program. */
	TESSELLATION_CONTROL(OGLVersion.GL40, GL40.GL_TESS_CONTROL_SHADER),
	/** Geometry shader program. */
	GEOMETRY(OGLVersion.GL32, GL32.GL_GEOMETRY_SHADER),
	/** Fragment shader program. */
	FRAGMENT(OGLVersion.GL20, GL20.GL_FRAGMENT_SHADER),
	/** Compute shader program. */
	COMPUTE(OGLVersion.GL43, GL43.GL_COMPUTE_SHADER);
	
	private final OGLVersion version;
	public final int glValue;
	
	private ShaderProgramType(OGLVersion version, int glValue) 
	{
		this.version = version;
		this.glValue = glValue;
	}
	
	@Override
	public OGLVersion getVersion()
	{
		return version;
	}
	
}

