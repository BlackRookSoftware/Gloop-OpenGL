/*******************************************************************************
 * Copyright (c) 2021 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at 
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gloop.opengl.gl2;

import com.blackrook.gloop.opengl.OGLVersion;
import com.blackrook.gloop.opengl.enums.BufferTargetType;
import com.blackrook.gloop.opengl.enums.DataType;
import com.blackrook.gloop.opengl.enums.ShaderProgramType;
import com.blackrook.gloop.opengl.exception.GraphicsException;
import com.blackrook.gloop.opengl.gl1.OGL15Graphics;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.charset.Charset;
import java.util.Objects;

import org.lwjgl.system.MemoryStack;

import static org.lwjgl.opengl.GL20.*;

/**
 * OpenGL 2.0 Graphics Implementation.
 * @author Matthew Tropiano
 */
public class OGL20Graphics extends OGL15Graphics
{
	protected class Info20 extends Info13
	{
		protected Info20()
		{
			super();
			this.shaderVersion = glGetString(GL_SHADING_LANGUAGE_VERSION);
			this.maxTextureUnits = getInt(GL_MAX_TEXTURE_IMAGE_UNITS);
			this.maxDrawBuffers = getInt(GL_MAX_DRAW_BUFFERS);
		}
	}
	
	/** Current shader. */
	private OGLShader currentShader;

	public OGL20Graphics(boolean core)
	{
		super(core);
		this.currentShader = null;
	}
	
	@Override
	public OGLVersion getVersion()
	{
		return OGLVersion.GL20;
	}
	
	@Override
	protected Info createInfo()
	{
		return new Info20();
	}
	
	@Override
	protected void endFrame()
	{
	    // Clean up abandoned objects.
	    OGLShader.destroyUndeleted();
	    OGLShaderProgram.destroyUndeleted();
	    super.endFrame();
	}

	/**
	 * Enables/Disables point sprite conversion.
	 * Internally, OpenGL will convert point geometry into billboarded quads or
	 * actual polygonal information. 
	 * @param enabled true to enable, false to disable.
	 */
	public void setPointSpritesEnabled(boolean enabled)
	{
		checkNonCore();
		setFlag(GL_POINT_SPRITE, enabled);
	}

	/**
	 * Sets if texture coordinates are to be generated across point geometry
	 * dimensions. Useful for Point Sprites, obviously.
	 * @param enabled true to enable, false to disable.
	 */
	public void setPointSpriteTexCoordGeneration(boolean enabled)
	{
		checkNonCore();
		glTexEnvi(GL_POINT_SPRITE, GL_COORD_REPLACE, toGLBool(enabled));
	}

	/**
	 * Creates a new shader program object (vertex, fragment, etc.).
	 * @param type the program type. if not a valid program type, this throws an exception.
	 * @param file the source file to read for compiling.
	 * @return the instantiated program.
	 * @throws NullPointerException if type or file is null.
	 * @throws IOException if the source of the source code can't be read.
	 * @throws FileNotFoundException if the source file does not exist.
	 */
	public OGLShaderProgram createShaderProgram(ShaderProgramType type, File file) throws IOException
	{
		try (FileInputStream fis = new FileInputStream(file))
		{
			return createShaderProgram(type, file.getPath(), new InputStreamReader(fis));
		}
	}
	
	/**
	 * Creates a new shader program object (vertex, fragment, etc.).
	 * @param type the program type. if not a valid program type, this throws an exception.
	 * @param streamName the name of the stream (can appear in exceptions).
	 * @param in the stream to read the source from, assuming platform encoding.
	 * @return the instantiated program.
	 * @throws NullPointerException if type, streamName, or in is null.
	 * @throws IOException if the source of the source code can't be read.
	 * @throws FileNotFoundException if the source file does not exist.
	 */
	public OGLShaderProgram createShaderProgram(ShaderProgramType type, String streamName, InputStream in) throws IOException
	{
		return createShaderProgram(type, streamName, new InputStreamReader(in));
	}
	
	/**
	 * Creates a new shader program object (vertex, fragment, etc.).
	 * @param type the program type. if not a valid program type, this throws an exception.
	 * @param streamName the name of the stream (can appear in exceptions).
	 * @param in the stream to read the source from, assuming platform encoding.
	 * @param charset the encoding charset for the input stream.
	 * @return the instantiated program.
	 * @throws NullPointerException if type, streamName, or in is null.
	 * @throws IOException if the source of the source code can't be read.
	 * @throws FileNotFoundException if the source file does not exist.
	 */
	public OGLShaderProgram createShaderProgram(ShaderProgramType type, String streamName, InputStream in, Charset charset) throws IOException
	{
		return createShaderProgram(type, streamName, new InputStreamReader(in, charset));
	}
	
	/**
	 * Creates a new shader program object (vertex, fragment, etc.).
	 * @param type the program type. if not a valid program type, this throws an exception.
	 * @param streamName the name of the stream (can appear in exceptions).
	 * @param reader the reader to read the source from.
	 * @return the instantiated program.
	 * @throws NullPointerException if type, streamName, or reader is null.
	 * @throws IOException if the source of the source code can't be read.
	 * @throws FileNotFoundException if the source file does not exist.
	 */
	public OGLShaderProgram createShaderProgram(ShaderProgramType type, String streamName, Reader reader) throws IOException
	{
		char[] cbuf = new char[4096];
		StringBuilder sb = new StringBuilder();
		int c = 0;
		while ((c = reader.read(cbuf)) > 0)
			sb.append(cbuf, 0, c);
		return createShaderProgram(type, streamName, sb.toString());
	}
	
	/**
	 * Creates a new shader program object (vertex, fragment, etc.).
	 * @param type the program type. if not a valid program type, this throws an exception.
	 * @param streamName the name of the originating stream (can appear in exceptions).
	 * @param sourceCode the code to compile.
	 * @return the instantiated program.
	 * @throws NullPointerException if file is null.
	 * @throws IOException if the source of the source code can't be read.
	 * @throws FileNotFoundException if the source file does not exist.
	 */
	public OGLShaderProgram createShaderProgram(ShaderProgramType type, String streamName, String sourceCode) throws IOException
	{
		checkFeatureVersion(type);
		return new OGLShaderProgram(type, streamName, sourceCode);
	}

	/**
	 * Creates a new shader object.
	 * @param programs the programs to attach.
	 * @return a new, linked shader object.
	 * @throws GraphicsException if the object could not be created, or compilation/linking failed.
	 */
	public OGLShader createShader(OGLShaderProgram ... programs)
	{
		return new OGLShader(programs);
	}
	
	/**
	 * Gets the currently bound shader. 
	 * @return the texture, or null if no bound texture.
	 */
	public OGLShader getShader()
	{
		return currentShader;
	}
	
	/**
	 * Binds a shader to the current context.
	 * @param shader the texture to bind.
	 */
	public void setShader(OGLShader shader)
	{
		Objects.requireNonNull(shader);
		glUseProgram(shader.getName());
		currentShader = shader;
	}
	
	/**
	 * Sets a uniform integer value on the currently-bound shader.
	 * @param locationId the uniform location.
	 * @param value the value to set.
	 */
	public void setShaderUniformInt(int locationId, int value)
	{
		glUniform1i(locationId, value);
	}
	
	/**
	 * Sets a uniform integer value array on the currently-bound shader.
	 * @param locationId the uniform location.
	 * @param values the values to set.
	 */
	public void setShaderUniformIntArray(int locationId, int ... values)
	{
		glUniform1iv(locationId, values);
	}
	
	/**
	 * Sets a uniform float value on the currently-bound shader.
	 * @param locationId the uniform location.
	 * @param value the value to set.
	 */
	public void setShaderUniformFloat(int locationId, float value)
	{
		glUniform1f(locationId, value);
	}
	
	/**
	 * Sets a uniform float array value on the currently-bound shader.
	 * @param locationId the uniform location.
	 * @param values the values to set.
	 */
	public void setShaderUniformFloatArray(int locationId, float ... values)
	{
		glUniform1fv(locationId, values);
	}
	
	/**
	 * Sets a uniform vec2 value on the currently-bound shader.
	 * @param locationId the uniform location.
	 * @param value0 the first value to set.
	 * @param value1 the second value to set.
	 */
	public void setShaderUniformVec2(int locationId, float value0, float value1)
	{
		try (MemoryStack stack = MemoryStack.stackPush())
		{
			FloatBuffer fbuf = stack.mallocFloat(2);
			fbuf.put(0, value0);
			fbuf.put(1, value1);
			glUniform2fv(locationId, fbuf);			
		}
	}
	
	/**
	 * Sets a uniform vec3 value on the currently-bound shader.
	 * @param locationId the uniform location.
	 * @param value0 the first value to set.
	 * @param value1 the second value to set.
	 * @param value2 the third value to set.
	 */
	public void setShaderUniformVec3(int locationId, float value0, float value1, float value2)
	{
		try (MemoryStack stack = MemoryStack.stackPush())
		{
			FloatBuffer fbuf = stack.mallocFloat(3);
			fbuf.put(0, value0);
			fbuf.put(1, value1);
			fbuf.put(2, value2);
			glUniform3fv(locationId, fbuf);
		}
	}
	
	/**
	 * Sets a uniform vec4 value on the currently-bound shader.
	 * @param locationId the uniform location.
	 * @param value0 the first value to set.
	 * @param value1 the second value to set.
	 * @param value2 the third value to set.
	 * @param value3 the fourth value to set.
	 */
	public void setShaderUniformVec4(int locationId, float value0, float value1, float value2, float value3)
	{
		try (MemoryStack stack = MemoryStack.stackPush())
		{
			FloatBuffer fbuf = stack.mallocFloat(4);
			fbuf.put(0, value0);
			fbuf.put(1, value1);
			fbuf.put(2, value2);
			fbuf.put(3, value2);
			glUniform4fv(locationId, fbuf);
		}
	}
	
	/**
	 * Sets a uniform integer vec2 value on the currently-bound shader.
	 * @param locationId the uniform location.
	 * @param value0 the first value to set.
	 * @param value1 the second value to set.
	 */
	public void setShaderUniformIVec2(int locationId, int value0, int value1)
	{
		try (MemoryStack stack = MemoryStack.stackPush())
		{
			IntBuffer ibuf = stack.mallocInt(2);
			ibuf.put(0, value0);
			ibuf.put(1, value1);
			glUniform2iv(locationId, ibuf);			
		}
	}

	/**
	 * Sets a uniform integer vec3 value on the currently-bound shader.
	 * @param locationId the uniform location.
	 * @param value0 the first value to set.
	 * @param value1 the second value to set.
	 * @param value2 the third value to set.
	 */
	public void setShaderUniformIVec3(int locationId, int value0, int value1, int value2)
	{
		try (MemoryStack stack = MemoryStack.stackPush())
		{
			IntBuffer ibuf = stack.mallocInt(3);
			ibuf.put(0, value0);
			ibuf.put(1, value1);
			ibuf.put(2, value2);
			glUniform3iv(locationId, ibuf);			
		}
	}

	/**
	 * Sets a uniform integer vec4 value on the currently-bound shader.
	 * @param locationId the uniform location.
	 * @param value0 the first value to set.
	 * @param value1 the second value to set.
	 * @param value2 the third value to set.
	 * @param value3 the fourth value to set.
	 */
	public void setShaderUniformIVec4(int locationId, int value0, int value1, int value2, int value3)
	{
		try (MemoryStack stack = MemoryStack.stackPush())
		{
			IntBuffer ibuf = stack.mallocInt(4);
			ibuf.put(0, value0);
			ibuf.put(1, value1);
			ibuf.put(2, value2);
			ibuf.put(3, value3);
			glUniform4iv(locationId, ibuf);			
		}
	}

	/**
	 * Sets a uniform matrix (mat2) value on the currently-bound shader.
	 * @param locationId the uniform location.
	 * @param matrix the multidimensional array of values, each array as one row of values.
	 * @throws ArrayIndexOutOfBoundsException if matrix is not 2x2 or greater and a value is fetched out-of-bounds.
	 */
	public void setShaderUniformMatrix2(int locationId, float[][] matrix)
	{
		// Fill in column major order!
		try (MemoryStack stack = MemoryStack.stackPush())
		{
			FloatBuffer fbuf = stack.mallocFloat(4);
			fbuf.put(0, matrix[0][0]);
			fbuf.put(1, matrix[1][0]);
			fbuf.put(2, matrix[0][1]);
			fbuf.put(3, matrix[1][1]);
			glUniformMatrix2fv(locationId, false, fbuf);			
		}
	}

	/**
	 * Sets a uniform matrix (mat3) value on the currently-bound shader.
	 * @param locationId the uniform location.
	 * @param matrix the multidimensional array of values, each array as one row of values.
	 * @throws ArrayIndexOutOfBoundsException if matrix is not 3x3 or greater and a value is fetched out-of-bounds.
	 */
	public void setShaderUniformMatrix3(int locationId, float[][] matrix)
	{
		// Fill in column major order!
		try (MemoryStack stack = MemoryStack.stackPush())
		{
			FloatBuffer fbuf = stack.mallocFloat(9);
			fbuf.put(0, matrix[0][0]);
			fbuf.put(1, matrix[1][0]);
			fbuf.put(2, matrix[2][0]);
			fbuf.put(3, matrix[0][1]);
			fbuf.put(4, matrix[1][1]);
			fbuf.put(5, matrix[2][1]);
			fbuf.put(6, matrix[0][2]);
			fbuf.put(7, matrix[1][2]);
			fbuf.put(8, matrix[2][2]);
			glUniformMatrix3fv(locationId, false, fbuf);			
		}
	}

	/**
	 * Sets a uniform matrix (mat4) value on the currently-bound shader.
	 * @param locationId the uniform location.
	 * @param matrix the multidimensional array of values, each array as one row of values.
	 * @throws ArrayIndexOutOfBoundsException if matrix is not 4x4 or greater and a value is fetched out-of-bounds.
	 */
	public void setShaderUniformMatrix4(int locationId, float[][] matrix)
	{
		// Fill in column major order!
		try (MemoryStack stack = MemoryStack.stackPush())
		{
			FloatBuffer fbuf = stack.mallocFloat(16);
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
			fbuf.put(12, matrix[0][3]);
			fbuf.put(13, matrix[1][3]);
			fbuf.put(14, matrix[2][3]);
			fbuf.put(15, matrix[3][3]);
			glUniformMatrix4fv(locationId, false, fbuf);			
		}
	}

	/**
	 * Unbinds a shader from the current context.
	 */
	public void unsetShader()
	{
		glUseProgram(0);
		currentShader = null;
	}

	/**
	 * Enables or disables the processing of bound vertex arrays and/or buffers at a specific attrib index.
	 * @param index the attribute index or uniform location id.
	 * @param enable true to enable, false to disable.
	 */
	public void setVertexAttribArrayEnabled(int index, boolean enable)
	{
		if (enable)
			glEnableVertexAttribArray(index);
		else
			glDisableVertexAttribArray(index);
	}

	/**
	 * Sets what positions in the current {@link BufferTargetType#GEOMETRY}-bound buffer are used to draw polygonal information:
	 * This sets the vertex attribute pointers.
	 * The index on this can also be a uniform location for an attrib pointer.
	 * @param index the attribute index.
	 * @param dataType the data type contained in the buffer that will be read (calculates actual sizes of data).
	 * @param normalize if true, the data is normalized on read ([-1, 1] for signed values, [0, 1] for unsigned). Else, read as-is.
	 * @param width the width of a full set of attribute components (in elements; 3-dimensional vertices = 3).
	 * @param stride the distance (in elements) between each attribute.    
	 * @param offset the offset in each stride where each attribute starts (in elements).  
	 * @see #setVertexAttribArrayEnabled(int, boolean)   
	 */
	public void setVertexAttribArrayPointer(int index, DataType dataType, boolean normalize, int width, int stride, int offset)
	{
		glVertexAttribPointer(index, width, dataType.glValue, normalize, stride * dataType.size, offset * dataType.size);
		getError();
	}
	
}
