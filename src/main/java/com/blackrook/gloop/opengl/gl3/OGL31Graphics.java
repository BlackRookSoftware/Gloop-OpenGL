/*******************************************************************************
 * Copyright (c) 2021-2024 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at 
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gloop.opengl.gl3;

import com.blackrook.gloop.opengl.OGLVersion;
import com.blackrook.gloop.opengl.OGLSystem.Options;

import static org.lwjgl.opengl.GL31.*;


/**
 * OpenGL 3.1 Graphics Implementation.
 * @author Matthew Tropiano
 */
public class OGL31Graphics extends OGL30Graphics
{
	protected class Info31 extends Info30
	{
		protected Info31()
		{
			super();
			this.maxTextureBufferSize = getInt(GL_MAX_TEXTURE_BUFFER_SIZE);
			this.maxVertexUniformBlocks = getInt(GL_MAX_VERTEX_UNIFORM_BLOCKS);
			this.maxGeometryUniformBlocks = getInt(GL_MAX_GEOMETRY_UNIFORM_BLOCKS);
			this.maxFragmentUniformBlocks = getInt(GL_MAX_FRAGMENT_UNIFORM_BLOCKS);
			this.maxCombinedUniformBlocks = getInt(GL_MAX_COMBINED_UNIFORM_BLOCKS);
			this.maxUniformBufferBindings = getInt(GL_MAX_UNIFORM_BUFFER_BINDINGS);
			this.maxUniformBlockSize = getInt(GL_MAX_UNIFORM_BLOCK_SIZE);
			this.maxCombinedVertexUniformComponents = getInt(GL_MAX_COMBINED_VERTEX_UNIFORM_COMPONENTS);
			this.maxCombinedGeometryUniformComponents = getInt(GL_MAX_COMBINED_GEOMETRY_UNIFORM_COMPONENTS);
			this.maxCombinedFragmentUniformComponents = getInt(GL_MAX_COMBINED_FRAGMENT_UNIFORM_COMPONENTS);
		}
	}
	
	public OGL31Graphics(Options options, boolean core)
	{
		super(options, core);
	}

	@Override
	public OGLVersion getVersion()
	{
		return OGLVersion.GL31;
	}

	@Override
	protected Info createInfo()
	{
		return new Info31();
	}
	
}
