package com.blackrook.gloop.opengl.util;

import java.nio.FloatBuffer;

import com.blackrook.gloop.opengl.OGLGraphics;
import com.blackrook.gloop.opengl.enums.BufferTargetType;
import com.blackrook.gloop.opengl.gl1.OGLBuffer;

/**
 * A builder class that assists the building of geometric data to later fill in a buffer. 
 * The underlying buffer that this builder creates is a Java Native I/O direct float buffer,
 * so its contents can be easily transferred to the OpenGL context.
 * <p>
 * Buffer contents are interleaved, so the draw calls for the created buffer after OpenGL transfer
 * should be set up using strides.
 * <p>
 * All of these methods can be called outside of the graphics thread except {@link #create()}.
 * <p>
 * Depending on implementation version, some methods may not be supported or available,
 * and will throw {@link UnsupportedOperationException} if so.
 * @author Matthew Tropiano
 */
public interface GeometryBuilder
{
	/**
	 * Adds the values for a one-component attribute.
	 * Next call to this method with the same will write to the next vertex.
	 * @param attributeId the attribute id.
	 * @param values the component values.
	 * @return this builder.
	 * @throws IllegalArgumentException if bad attribute id, the attribute 
	 * 		component count is incorrect, or no more vertices for this attribute.
	 */
	GeometryBuilder add(int attributeId, float ... values);
	
	/**
	 * @return the attribute count.
	 */
	int getAttributeCount();
	
	/**
	 * @return the stride size.
	 */
	int getStrideSize();
	
	/**
	 * Gets the element width for an attribute.
	 * @param attributeId the attribute id.
	 * @return the width for the provided attribute.
	 */
	int getWidth(int attributeId);
	
	/**
	 * Gets the stride offset for an attribute.
	 * @param attributeId the attribute id.
	 * @return the offset for the provided attribute.
	 */
	int getOffset(int attributeId);
	
	/**
	 * Creates a new buffer object bindable to the {@link BufferTargetType#GEOMETRY} target.
	 * @return the buffer created using this builder's data.
	 */
	OGLBuffer create();
	
	/**
	 * Geometry builder utility class.
	 * @param <GL> the graphics implementation that this executes on.
	 */
	public abstract class Abstract<GL extends OGLGraphics> implements GeometryBuilder
	{
		protected GL gl;
		
		/** Size amount of vertices. */
		protected int vertices;
		/** Attribute sizes in components. */
		protected int[] attributeSizes;
		
		/** Size of a full stride. */
		protected int strideSize;
		/** Attribute offsets. */
		protected int[] attributeOffsets;
		/** Current vertex per attribute. */
		protected int[] currentVertex;
		/** The buffer that holds the data. */
		protected FloatBuffer buffer;

		protected Abstract(GL gl, int vertices, int ... attributeSizes)
		{
			this.gl = gl;
			this.vertices = vertices;
			this.attributeSizes = new int[attributeSizes.length];
			System.arraycopy(attributeSizes, 0, this.attributeSizes, 0, attributeSizes.length);
			
			this.strideSize = 0;
			for (int x : attributeSizes)
				this.strideSize += x;

			this.attributeOffsets = new int[attributeSizes.length];
			for (int i = 0; i < attributeOffsets.length; i++)
			{
				if (i > 0)
					this.attributeOffsets[i] = this.attributeOffsets[i - 1] + this.attributeSizes[i - 1];
			}
				
			this.currentVertex = new int[attributeSizes.length];
			buffer = BufferUtils.allocDirectFloatBuffer(vertices * strideSize);
		}
		
		// Exception if id is less than 0 or greater or equal to attribute count,
		// or componentCount is the wrong amount for the attribute. 
		private void checkComponentCount(int id, int componentCount)
		{
			if (id < 0 || id >= attributeSizes.length)
				throw new IllegalArgumentException("Attribute id " + id + " is out of range: [0, " + (attributeSizes.length - 1) + "]");
			if (attributeSizes[id] != componentCount)
				throw new IllegalArgumentException("Attribute id " + id + " requires " + attributeSizes[id] + " components, not " + componentCount);
		}
		
		// Exception if there are no more vertices to add for this attribute. 
		private void checkVertexCount(int id)
		{
			if (currentVertex[id] >= vertices)
				throw new IllegalArgumentException("No more attributes to add for attribute id " + id);
		}
		
		@Override
		public GeometryBuilder add(int attributeId, float ... values)
		{
			checkComponentCount(attributeId, values.length);
			checkVertexCount(attributeId);
			for (int i = 0; i < values.length; i++)
				buffer.put(currentVertex[attributeId] * strideSize + i + attributeOffsets[attributeId], values[i]);
			currentVertex[attributeId]++;
			return this;
		}
		
		@Override
		public int getAttributeCount()
		{
			return attributeOffsets.length;
		}
		
		@Override
		public int getStrideSize()
		{
			return strideSize;
		}
		
		@Override
		public int getWidth(int attributeId)
		{
			return attributeSizes[attributeId];
		}
		
		@Override
		public int getOffset(int attributeId)
		{
			return attributeOffsets[attributeId];
		}
		
	}
	
}

