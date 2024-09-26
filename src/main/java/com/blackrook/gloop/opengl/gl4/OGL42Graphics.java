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

import static org.lwjgl.opengl.GL42.*;


/**
 * OpenGL 4.2 Graphics Implementation.
 * @author Matthew Tropiano
 */
public class OGL42Graphics extends OGL41Graphics
{
	protected class Info42 extends Info41
	{
		protected Info42()
		{
			super();
			this.maxVertexAtomicCounterBuffers = getInt(GL_MAX_VERTEX_ATOMIC_COUNTER_BUFFERS);
			this.maxTesselationControlAtomicCounterBuffers = getInt(GL_MAX_TESS_CONTROL_ATOMIC_COUNTER_BUFFERS);
			this.maxTesselationEvaluationAtomicCounterBuffers = getInt(GL_MAX_TESS_EVALUATION_ATOMIC_COUNTER_BUFFERS);
			this.maxGeometryAtomicCounterBuffers = getInt(GL_MAX_GEOMETRY_ATOMIC_COUNTER_BUFFERS);
			this.maxFragmentAtomicCounterBuffers = getInt(GL_MAX_FRAGMENT_ATOMIC_COUNTER_BUFFERS);
			this.maxCombinedAtomicCounterBuffers = getInt(GL_MAX_COMBINED_ATOMIC_COUNTER_BUFFERS);
			this.maxVertexAtomicCounters = getInt(GL_MAX_VERTEX_ATOMIC_COUNTERS);
			this.maxTesselationControlAtomicCounters = getInt(GL_MAX_TESS_CONTROL_ATOMIC_COUNTERS);
			this.maxTesselationEvaluationAtomicCounters = getInt(GL_MAX_TESS_EVALUATION_ATOMIC_COUNTERS);
			this.maxGeometryAtomicCounters = getInt(GL_MAX_GEOMETRY_ATOMIC_COUNTERS);
			this.maxFragmentAtomicCounters = getInt(GL_MAX_FRAGMENT_ATOMIC_COUNTERS);
			this.maxCombinedAtomicCounters = getInt(GL_MAX_COMBINED_ATOMIC_COUNTERS);
			this.maxAtomicCounterBufferSize = getInt(GL_MAX_ATOMIC_COUNTER_BUFFER_SIZE);
			this.maxAtomicCounterBufferBindings = getInt(GL_MAX_ATOMIC_COUNTER_BUFFER_BINDINGS);
		}
	}
	
	public OGL42Graphics(Options options, boolean core)
	{
		super(options, core);
	}

	@Override
	public OGLVersion getVersion()
	{
		return OGLVersion.GL42;
	}

	@Override
	protected Info createInfo()
	{
		return new Info42();
	}
	
}
