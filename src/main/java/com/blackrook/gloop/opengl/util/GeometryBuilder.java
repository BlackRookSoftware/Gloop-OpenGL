package com.blackrook.gloop.opengl.util;

import java.nio.FloatBuffer;

import com.blackrook.gloop.opengl.struct.BufferUtils;

/**
 * A builder class that assists the building of geometric data to later fill in a buffer. 
 * The underlying buffer that this builder creates is a Java Native I/O direct float buffer,
 * so its contents can be easily transferred to the OpenGL context.
 * <p>
 * Buffer contents are interleaved, so the draw calls for this buffer after OpenGL transfer
 * should be set up using strides.
 * @author Matthew Tropiano
 */
public class GeometryBuilder
{
	/** Size amount of vertices. */
	private int vertices;
	/** Attribute sizes in components. */
	private int[] attributeSizes;
	
	/** Size of a full stride. */
	private int strideSize;
	/** Current vertex per attribute. */
	private int[] currentVertex;
	/** The buffer that holds the data. */
	private FloatBuffer buffer;

	private GeometryBuilder(int vertices, int ... attributeSizes)
	{
		this.vertices = vertices;
		this.attributeSizes = new int[attributeSizes.length];
		System.arraycopy(attributeSizes, 0, this.attributeSizes, 0, attributeSizes.length);
		
		this.strideSize = 0;
		for (int x : attributeSizes)
			this.strideSize += x;
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
	
	/**
	 * Creates a new geometry builder.
	 * The internal size of the buffer (number of float elements) is <code>vertices</code> times the sum of all of
	 * the attribute sizes, which would house the whole object.
	 * <p> 
	 * For example, if this is a builder of a four-vertex mesh with 3D spatial coordinates and an RGBA color,
	 * the constructor for this 4-vertex, 2 attribute builder is:
	 * <pre>GeometryBuilder.start(4, 3, 4)</pre>
	 * ...and the resultant buffer capacity is 28 (<code>4 * (3 + 4)</code>).
	 * @param vertices the number of individual vertices or attribute sets.
	 * @param attributeSizes the list of attribute sizes in components.
	 * @return the new builder.
	 */
	public static GeometryBuilder start(int vertices, int ... attributeSizes)
	{
		return new GeometryBuilder(vertices, attributeSizes);
	}

	/**
	 * Adds the values for a one-component attribute.
	 * Next call to this method with the same will write to the next vertex.
	 * @param attributeId the attribute id.
	 * @param values the component values.
	 * @return this builder.
	 * @throws IllegalArgumentException if bad attribute id, the attribute 
	 * 		component count is incorrect, or no more vertices for this attribute.
	 */
	public GeometryBuilder add(int attributeId, float ... values)
	{
		checkComponentCount(attributeId, 1);
		checkVertexCount(attributeId);
		for (int i = 0; i < values.length; i++)
			buffer.put(currentVertex[attributeId] * strideSize + i, values[i]);
		currentVertex[attributeId]++;
		return this;
	}
	
	/**
	 * @return the reference to the float buffer that this builds.
	 */
	public FloatBuffer getBuffer()
	{
		return buffer;
	}
	
}
