/*******************************************************************************
 * Copyright (c) 2021-2024 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at 
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gloop.opengl.gl4;

import com.blackrook.gloop.opengl.OGLVersion;
import com.blackrook.gloop.opengl.OGLSystem.Options;

import static org.lwjgl.opengl.GL43.*;


/**
 * OpenGL 4.3 Graphics Implementation.
 * @author Matthew Tropiano
 */
public class OGL43Graphics extends OGL42Graphics
{
	protected class Info43 extends Info42
	{
		protected Info43()
		{
			super();
			this.maxComputeUniformBlocks = getInt(GL_MAX_COMPUTE_UNIFORM_BLOCKS);
			this.maxComputeTextureImageUnits = getInt(GL_MAX_COMPUTE_TEXTURE_IMAGE_UNITS);
			this.maxComputeImageUniforms = getInt(GL_MAX_COMPUTE_IMAGE_UNIFORMS);
			this.maxComputeSharedMemorySize = getInt(GL_MAX_COMPUTE_SHARED_MEMORY_SIZE);
			this.maxComputeUniformComponents = getInt(GL_MAX_COMPUTE_UNIFORM_BLOCKS);
			this.maxComputeAtomicCounterBuffers = getInt(GL_MAX_COMPUTE_ATOMIC_COUNTER_BUFFERS);
			this.maxComputeAtomicCounters = getInt(GL_MAX_COMPUTE_ATOMIC_COUNTERS);
			this.maxCombinedComputeUniformComponents = getInt(GL_MAX_COMBINED_COMPUTE_UNIFORM_COMPONENTS);
			this.maxComputeWorkGroupInvocations = getInt(GL_MAX_COMPUTE_WORK_GROUP_INVOCATIONS);
			this.maxDebugMessageLength = getInt(GL_MAX_DEBUG_MESSAGE_LENGTH);
			this.maxDebugLoggedMessages = getInt(GL_MAX_DEBUG_LOGGED_MESSAGES);
			this.maxDebugGroupStackDepth = getInt(GL_MAX_DEBUG_GROUP_STACK_DEPTH);
			this.maxLabelLength = getInt(GL_MAX_LABEL_LENGTH);
			this.maxVertexShaderStorageBlocks = getInt(GL_MAX_VERTEX_SHADER_STORAGE_BLOCKS);
			this.maxGeometryShaderStorageBlocks = getInt(GL_MAX_GEOMETRY_SHADER_STORAGE_BLOCKS);
			this.maxTesselationControlShaderStorageBlocks = getInt(GL_MAX_TESS_CONTROL_SHADER_STORAGE_BLOCKS);
			this.maxTesselationEvaluationShaderStorageBlocks = getInt(GL_MAX_TESS_EVALUATION_SHADER_STORAGE_BLOCKS);
			this.maxFragmentShaderStorageBlocks = getInt(GL_MAX_FRAGMENT_SHADER_STORAGE_BLOCKS);
			this.maxComputeShaderStorageBlocks = getInt(GL_MAX_COMPUTE_SHADER_STORAGE_BLOCKS);
			this.maxCombinedShaderStorageBlocks = getInt(GL_MAX_COMBINED_SHADER_STORAGE_BLOCKS);
			this.maxShaderStorageBufferBindings = getInt(GL_MAX_SHADER_STORAGE_BUFFER_BINDINGS);
			this.maxShaderStorageBlockSize = getInt(GL_MAX_SHADER_STORAGE_BLOCK_SIZE);
			this.shaderStorageBufferOffsetAlignment = getInt(GL_SHADER_STORAGE_BUFFER_OFFSET_ALIGNMENT);
		}
	}
	
	public OGL43Graphics(Options options, boolean core)
	{
		super(options, core);
	}

	@Override
	public OGLVersion getVersion()
	{
		return OGLVersion.GL43;
	}

	@Override
	protected Info createInfo()
	{
		return new Info43();
	}
	
	// TODO: Finish.

}
