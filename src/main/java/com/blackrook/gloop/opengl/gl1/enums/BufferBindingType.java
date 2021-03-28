/*******************************************************************************
 * Copyright (c) 2021 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at 
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gloop.opengl.gl1.enums;

import org.lwjgl.opengl.ARBIndirectParameters;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL21;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL31;
import org.lwjgl.opengl.GL40;
import org.lwjgl.opengl.GL42;
import org.lwjgl.opengl.GL43;

/**
 * Type target enumeration for one-dimensional buffer types.
 * @author Matthew Tropiano
 */
public enum BufferBindingType
{
	/** Buffer holds GEOMETRY information (internally, this is GL_ARRAY_BUFFER). */
	GEOMETRY(GL15.GL_ARRAY_BUFFER),
	/** Buffer holds ELEMENT INDEX information (internally, this is GL_ELEMENT_ARRAY_BUFFER). */
	INDICES(GL15.GL_ELEMENT_ARRAY_BUFFER),
	/** Buffer contains packed data (raw data specific to OpenGL implementation). */
	PIXEL_PACK(GL21.GL_PIXEL_PACK_BUFFER),
	/** Buffer contains unpacked data (raw pixel data to be sent to OpenGL or read from OpenGL to an application). */
	PIXEL_UNPACK(GL21.GL_PIXEL_UNPACK_BUFFER),
	/** Buffer is a Transform Feedback Buffer. */
	TRANSFORM_FEEDBACK(GL30.GL_TRANSFORM_FEEDBACK_BUFFER),
	/** Buffer is a Uniform Buffer. */
	UNIFORM(GL31.GL_UNIFORM_BUFFER),
	/** Buffer is a Texture Buffer. */
	TEXTURE(GL31.GL_TEXTURE_BUFFER),
	/** Buffer is a Copy Read Buffer. */
	COPY_READ(GL31.GL_COPY_READ_BUFFER),
	/** Buffer is a Copy Write Buffer. */
	COPY_WRITE(GL31.GL_COPY_WRITE_BUFFER),
	/** Buffer is a Draw Indirect Buffer. */
	DRAW_INDIRECT(GL40.GL_DRAW_INDIRECT_BUFFER),
	/** Buffer is an Atomic Counter Buffer. */
	ATOMIC_COUNTER(GL42.GL_ATOMIC_COUNTER_BUFFER),
	/** Buffer is a Dispatch Indirect Buffer. */
	DISPATCH_INDIRECT(GL43.GL_DISPATCH_INDIRECT_BUFFER),
	/** Buffer is a Shader Storage Buffer. */
	SHADER_STORAGE(GL43.GL_SHADER_STORAGE_BUFFER),
	/** Buffer is a Parameter Buffer. */
	PARAMETER_BUFFER(ARBIndirectParameters.GL_PARAMETER_BUFFER_ARB);
	
	public final int glValue;
	private BufferBindingType(int gltype) {glValue = gltype;}
}
