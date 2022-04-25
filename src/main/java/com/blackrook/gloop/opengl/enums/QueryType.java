/*******************************************************************************
 * Copyright (c) 2021-2022 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at 
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gloop.opengl.enums;

import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL33;
import org.lwjgl.opengl.GL43;

import com.blackrook.gloop.opengl.OGLVersion;
import com.blackrook.gloop.opengl.OGLVersioned;

/**
 * Query type.
 * @author Matthew Tropiano
 */
public enum QueryType implements OGLVersioned
{
	SAMPLES_PASSED                           (OGLVersion.GL15, GL15.GL_SAMPLES_PASSED),
	GL_PRIMITIVES_GENERATED                  (OGLVersion.GL30, GL30.GL_PRIMITIVES_GENERATED),
	GL_TRANSFORM_FEEDBACK_PRIMITIVES_WRITTEN (OGLVersion.GL30, GL30.GL_TRANSFORM_FEEDBACK_PRIMITIVES_WRITTEN),
	GL_ANY_SAMPLES_PASSED                    (OGLVersion.GL33, GL33.GL_ANY_SAMPLES_PASSED),
	GL_TIME_ELAPSED                          (OGLVersion.GL33, GL33.GL_TIME_ELAPSED),
	GL_ANY_SAMPLES_PASSED_CONSERVATIVE       (OGLVersion.GL43, GL43.GL_ANY_SAMPLES_PASSED_CONSERVATIVE),
	;
	
	private final OGLVersion version;
	public final int glValue;
	
	private QueryType(OGLVersion version, int glValue) 
	{
		this.version = version;
		this.glValue = glValue;
	}
	
	@Override
	public OGLVersion getVersion()
	{
		return version;
	}
	
	@Override
	public boolean isCore()
	{
		return true;
	}
	
}

