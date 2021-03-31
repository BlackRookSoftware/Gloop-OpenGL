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
	/** Size of a vertices. */
	private int vertices;
	/** Attribute sizes in components. */
	private int[] attribSizes;
	
	/** Size of a full stride. */
	private int strideSize;
	/** The buffer that holds the data. */
	private FloatBuffer buffer;

	private GeometryBuilder(int vertices, int ... attribSizes)
	{
		this.vertices = vertices;
		this.attribSizes = new int[attribSizes.length];
		System.arraycopy(attribSizes, 0, this.attribSizes, 0, attribSizes.length);
		
		this.strideSize = 0;
		for (int x : attribSizes)
			this.strideSize += x;
		buffer = BufferUtils.allocDirectFloatBuffer(vertices * strideSize);
	}
	
	// TODO: Finish this.
	
}
