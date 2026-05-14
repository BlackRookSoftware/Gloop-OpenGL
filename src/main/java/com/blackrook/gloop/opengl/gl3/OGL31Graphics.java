/*******************************************************************************
 * Copyright (c) 2021-2024 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at 
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gloop.opengl.gl3;

import com.blackrook.gloop.opengl.OGLVersion;
import com.blackrook.gloop.opengl.enums.DataType;
import com.blackrook.gloop.opengl.enums.GeometryType;

import static org.lwjgl.opengl.GL31.*;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;


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
	
	/**
	 * Draws geometry using the current bound, enabled coordinate arrays/buffers as data.
	 * @param geometryType the geometry type - tells how to interpret the data.
	 * @param offset the starting offset in the bound buffers (in elements).
	 * @param elementCount the number of elements to draw using bound buffers.
	 * NOTE: an element is in terms of array elements, so if the bound buffers describe the coordinates of 4 vertices,
	 * <code>elementCount</code> should be 4.
	 * @param instances the number of instances of the element set to draw using bound buffers.
	 */
	public void drawGeometryArrayInstanced(GeometryType geometryType, int offset, int elementCount, int instances)
	{
		glDrawArraysInstanced(geometryType.glValue, offset, elementCount, instances);
		checkError();
	}
	
	/**
	 * Draws geometry using the current bound, enabled coordinate arrays/buffers as data,
	 * with the provided element buffer to describe the ordering.
	 * @param geometryType the geometry type - tells how to interpret the data.
	 * @param dataType the data type of the indices in <code>indices</code> (must be an unsigned type).
	 * @param indices the buffer of element indices to interpret.
	 * @param instances the number of instances of the element set to draw using bound buffers.
	 */
	public void drawGeometryElementsInstanced(GeometryType geometryType, DataType dataType, ByteBuffer indices, int instances)
	{
		glDrawElementsInstanced(geometryType.glValue, dataType.glValue, indices, instances);
		checkError();
	}

	/**
	 * Draws geometry using the current bound, enabled coordinate arrays/buffers as data, 
	 * with the provided element buffer to describe the ordering.
	 * @param geometryType the geometry type - tells how to interpret the data.
	 * @param indices the buffer of element indices.
	 * @param instances the number of instances of the element set to draw using bound buffers.
	 */
	public void drawGeometryElementsInstanced(GeometryType geometryType, ByteBuffer indices, int instances)
	{
		glDrawElementsInstanced(geometryType.glValue, indices, instances);
		checkError();
	}

	/**
	 * Draws geometry using the current bound, enabled coordinate arrays/buffers as data, 
	 * with the provided element buffer to describe the ordering.
	 * @param geometryType the geometry type - tells how to interpret the data.
	 * @param indices the buffer of element indices.
	 * @param instances the number of instances of the element set to draw using bound buffers.
	 */
	public void drawGeometryElementsInstanced(GeometryType geometryType, ShortBuffer indices, int instances)
	{
		glDrawElementsInstanced(geometryType.glValue, indices, instances);
		checkError();
	}

	/**
	 * Draws geometry using the current bound, enabled coordinate arrays/buffers as data, 
	 * with the provided element buffer to describe the ordering.
	 * @param geometryType the geometry type - tells how to interpret the data.
	 * @param indices the buffer of element indices.
	 * @param instances the number of instances of the element set to draw using bound buffers.
	 */
	public void drawGeometryElementsInstanced(GeometryType geometryType, IntBuffer indices, int instances)
	{
		glDrawElementsInstanced(geometryType.glValue, indices, instances);
		checkError();
	}

}
