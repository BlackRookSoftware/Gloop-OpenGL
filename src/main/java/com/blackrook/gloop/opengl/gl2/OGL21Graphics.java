/*******************************************************************************
 * Copyright (c) 2021-2022 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at 
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gloop.opengl.gl2;

import java.nio.FloatBuffer;

import org.lwjgl.system.MemoryStack;

import com.blackrook.gloop.opengl.OGLVersion;
import com.blackrook.gloop.opengl.OGLSystem.Options;

import static org.lwjgl.opengl.GL21.*;

/**
 * OpenGL 2.1 Graphics Implementation.
 * @author Matthew Tropiano
 */
public class OGL21Graphics extends OGL20Graphics
{
	public OGL21Graphics(Options options, boolean core)
	{
		super(options, core);
	}

	@Override
	public OGLVersion getVersion()
	{
		return OGLVersion.GL21;
	}
	
	/**
	 * Sets a uniform matrix (mat2x3) value on the currently-bound shader.
	 * @param locationId the uniform location.
	 * @param matrix the column-major array of values.
	 * @throws ArrayIndexOutOfBoundsException if matrix is not 6 elements or greater and a value is fetched out-of-bounds.
	 */
	public void setProgramUniformMatrix2x3(int locationId, float[] matrix)
	{
		// Fill in column major order!
		try (MemoryStack stack = MemoryStack.stackPush())
		{
			FloatBuffer fbuf = stack.mallocFloat(6);
			fbuf.put(matrix);
			fbuf.flip();
			glUniformMatrix2x3fv(locationId, false, fbuf);
		}
	}
	
	/**
	 * Sets a uniform matrix (mat2x3) value on the currently-bound shader.
	 * @param locationId the uniform location.
	 * @param matrix the multidimensional array of values, each array as one row of values.
	 * @throws ArrayIndexOutOfBoundsException if matrix is not 2x3 or greater and a value is fetched out-of-bounds.
	 */
	public void setProgramUniformMatrix2x3(int locationId, float[][] matrix)
	{
		// Fill in column major order!
		try (MemoryStack stack = MemoryStack.stackPush())
		{
			FloatBuffer fbuf = stack.mallocFloat(6);
			fbuf.put(0, matrix[0][0]);
			fbuf.put(1, matrix[1][0]);
			fbuf.put(2, matrix[0][1]);
			fbuf.put(3, matrix[1][1]);
			fbuf.put(4, matrix[0][2]);
			fbuf.put(5, matrix[1][2]);
			glUniformMatrix2x3fv(locationId, false, fbuf);
		}
	}
	
	/**
	 * Sets a uniform matrix (mat2x4) value on the currently-bound shader.
	 * @param locationId the uniform location.
	 * @param matrix the column-major array of values.
	 * @throws ArrayIndexOutOfBoundsException if matrix is not 8 elements or greater and a value is fetched out-of-bounds.
	 */
	public void setProgramUniformMatrix2x4(int locationId, float[] matrix)
	{
		// Fill in column major order!
		try (MemoryStack stack = MemoryStack.stackPush())
		{
			FloatBuffer fbuf = stack.mallocFloat(8);
			fbuf.put(matrix);
			fbuf.flip();
			glUniformMatrix2x4fv(locationId, false, fbuf);
		}
	}	
	
	/**
	 * Sets a uniform matrix (mat2x4) value on the currently-bound shader.
	 * @param locationId the uniform location.
	 * @param matrix the multidimensional array of values, each array as one row of values.
	 * @throws ArrayIndexOutOfBoundsException if matrix is not 2x4 or greater and a value is fetched out-of-bounds.
	 */
	public void setProgramUniformMatrix2x4(int locationId, float[][] matrix)
	{
		// Fill in column major order!
		try (MemoryStack stack = MemoryStack.stackPush())
		{
			FloatBuffer fbuf = stack.mallocFloat(8);
			fbuf.put(0, matrix[0][0]);
			fbuf.put(1, matrix[1][0]);
			fbuf.put(2, matrix[0][1]);
			fbuf.put(3, matrix[1][1]);
			fbuf.put(4, matrix[0][2]);
			fbuf.put(5, matrix[1][2]);
			fbuf.put(6, matrix[0][3]);
			fbuf.put(7, matrix[1][3]);
			glUniformMatrix2x4fv(locationId, false, fbuf);
		}
	}	
	
	/**
	 * Sets a uniform matrix (mat3x2) value on the currently-bound shader.
	 * @param locationId the uniform location.
	 * @param matrix the column-major array of values.
	 * @throws ArrayIndexOutOfBoundsException if matrix is not 6 elements or greater and a value is fetched out-of-bounds.
	 */
	public void setProgramUniformMatrix3x2(int locationId, float[] matrix)
	{
		// Fill in column major order!
		try (MemoryStack stack = MemoryStack.stackPush())
		{
			FloatBuffer fbuf = stack.mallocFloat(6);
			fbuf.put(matrix);
			fbuf.flip();
			glUniformMatrix3x2fv(locationId, false, fbuf);
		}
	}
	
	/**
	 * Sets a uniform matrix (mat3x2) value on the currently-bound shader.
	 * @param locationId the uniform location.
	 * @param matrix the multidimensional array of values, each array as one row of values.
	 * @throws ArrayIndexOutOfBoundsException if matrix is not 3x2 or greater and a value is fetched out-of-bounds.
	 */
	public void setProgramUniformMatrix3x2(int locationId, float[][] matrix)
	{
		// Fill in column major order!
		try (MemoryStack stack = MemoryStack.stackPush())
		{
			FloatBuffer fbuf = stack.mallocFloat(6);
			fbuf.put(0, matrix[0][0]);
			fbuf.put(1, matrix[1][0]);
			fbuf.put(2, matrix[2][0]);
			fbuf.put(3, matrix[0][1]);
			fbuf.put(4, matrix[1][1]);
			fbuf.put(5, matrix[2][1]);
			glUniformMatrix3x2fv(locationId, false, fbuf);
		}
	}
	
	/**
	 * Sets a uniform matrix (mat3x4) value on the currently-bound shader.
	 * @param locationId the uniform location.
	 * @param matrix the column-major array of values.
	 * @throws ArrayIndexOutOfBoundsException if matrix is not 12 elements or greater and a value is fetched out-of-bounds.
	 */
	public void setProgramUniformMatrix3x4(int locationId, float[] matrix)
	{
		// Fill in column major order!
		try (MemoryStack stack = MemoryStack.stackPush())
		{
			FloatBuffer fbuf = stack.mallocFloat(12);
			fbuf.put(matrix);
			fbuf.flip();
			glUniformMatrix3x4fv(locationId, false, fbuf);
		}
	}	
	
	/**
	 * Sets a uniform matrix (mat3x4) value on the currently-bound shader.
	 * @param locationId the uniform location.
	 * @param matrix the multidimensional array of values, each array as one row of values.
	 * @throws ArrayIndexOutOfBoundsException if matrix is not 3x4 or greater and a value is fetched out-of-bounds.
	 */
	public void setProgramUniformMatrix3x4(int locationId, float[][] matrix)
	{
		// Fill in column major order!
		try (MemoryStack stack = MemoryStack.stackPush())
		{
			FloatBuffer fbuf = stack.mallocFloat(12);
			fbuf.put(0,  matrix[0][0]);
			fbuf.put(1,  matrix[1][0]);
			fbuf.put(2,  matrix[2][0]);
			fbuf.put(3,  matrix[0][1]);
			fbuf.put(4,  matrix[1][1]);
			fbuf.put(5,  matrix[2][1]);
			fbuf.put(6,  matrix[0][2]);
			fbuf.put(7,  matrix[1][2]);
			fbuf.put(8,  matrix[2][2]);
			fbuf.put(9,  matrix[0][3]);
			fbuf.put(10, matrix[1][3]);
			fbuf.put(11, matrix[2][3]);
			glUniformMatrix3x4fv(locationId, false, fbuf);
		}
	}	
	
	/**
	 * Sets a uniform matrix (mat4x2) value on the currently-bound shader.
	 * @param locationId the uniform location.
	 * @param matrix the column-major array of values.
	 * @throws ArrayIndexOutOfBoundsException if matrix is not 8 elements or greater and a value is fetched out-of-bounds.
	 */
	public void setProgramUniformMatrix4x2(int locationId, float[] matrix)
	{
		// Fill in column major order!
		try (MemoryStack stack = MemoryStack.stackPush())
		{
			FloatBuffer fbuf = stack.mallocFloat(8);
			fbuf.put(matrix);
			fbuf.flip();
			glUniformMatrix4x2fv(locationId, false, fbuf);
		}
	}	
	
	/**
	 * Sets a uniform matrix (mat4x2) value on the currently-bound shader.
	 * @param locationId the uniform location.
	 * @param matrix the multidimensional array of values, each array as one row of values.
	 * @throws ArrayIndexOutOfBoundsException if matrix is not 4x2 or greater and a value is fetched out-of-bounds.
	 */
	public void setProgramUniformMatrix4x2(int locationId, float[][] matrix)
	{
		// Fill in column major order!
		try (MemoryStack stack = MemoryStack.stackPush())
		{
			FloatBuffer fbuf = stack.mallocFloat(8);
			fbuf.put(0, matrix[0][0]);
			fbuf.put(1, matrix[1][0]);
			fbuf.put(2, matrix[2][0]);
			fbuf.put(3, matrix[3][0]);
			fbuf.put(4, matrix[0][1]);
			fbuf.put(5, matrix[1][1]);
			fbuf.put(6, matrix[2][1]);
			fbuf.put(7, matrix[3][1]);
			glUniformMatrix4x2fv(locationId, false, fbuf);
		}
	}	
	
	/**
	 * Sets a uniform matrix (mat4x3) value on the currently-bound shader.
	 * @param locationId the uniform location.
	 * @param matrix the column-major array of values.
	 * @throws ArrayIndexOutOfBoundsException if matrix is not 12 elements or greater and a value is fetched out-of-bounds.
	 */
	public void setProgramUniformMatrix4x3(int locationId, float[] matrix)
	{
		// Fill in column major order!
		try (MemoryStack stack = MemoryStack.stackPush())
		{
			FloatBuffer fbuf = stack.mallocFloat(12);
			fbuf.put(matrix);
			fbuf.flip();
			glUniformMatrix4x3fv(locationId, false, fbuf);
		}
	}	
	
	/**
	 * Sets a uniform matrix (mat4x3) value on the currently-bound shader.
	 * @param locationId the uniform location.
	 * @param matrix the multidimensional array of values, each array as one row of values.
	 * @throws ArrayIndexOutOfBoundsException if matrix is not 4x3 or greater and a value is fetched out-of-bounds.
	 */
	public void setProgramUniformMatrix4x3(int locationId, float[][] matrix)
	{
		// Fill in column major order!
		try (MemoryStack stack = MemoryStack.stackPush())
		{
			FloatBuffer fbuf = stack.mallocFloat(12);
			fbuf.put(0,  matrix[0][0]);
			fbuf.put(1,  matrix[1][0]);
			fbuf.put(2,  matrix[2][0]);
			fbuf.put(3,  matrix[3][0]);
			fbuf.put(4,  matrix[0][1]);
			fbuf.put(5,  matrix[1][1]);
			fbuf.put(6,  matrix[2][1]);
			fbuf.put(7,  matrix[3][1]);
			fbuf.put(8,  matrix[0][2]);
			fbuf.put(9,  matrix[1][2]);
			fbuf.put(10, matrix[2][2]);
			fbuf.put(11, matrix[3][2]);
			glUniformMatrix4x3fv(locationId, false, fbuf);
		}
	}	
	
}
