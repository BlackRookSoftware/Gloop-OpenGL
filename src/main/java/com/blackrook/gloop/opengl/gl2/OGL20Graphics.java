/*******************************************************************************
 * Copyright (c) 2021 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at 
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gloop.opengl.gl2;

import com.blackrook.gloop.opengl.exception.GraphicsException;
import com.blackrook.gloop.opengl.gl1.OGL15Graphics;
import com.blackrook.gloop.opengl.gl1.objects.OGLTexture;
import com.blackrook.gloop.opengl.gl2.enums.ShaderProgramType;
import com.blackrook.gloop.opengl.gl2.objects.OGLShader;
import com.blackrook.gloop.opengl.gl2.objects.OGLShaderProgram;
import com.blackrook.gloop.opengl.gl2.objects.OGLShaderProgramFragment;
import com.blackrook.gloop.opengl.gl2.objects.OGLShaderProgramVertex;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.FloatBuffer;
import java.util.Objects;

import org.lwjgl.system.MemoryStack;

import static org.lwjgl.opengl.GL20.*;

/**
 * OpenGL 2.1 Graphics Implementation.
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
		}
	}
	
	/**
	 * A try-with-resources latch that unbinds a shader
	 * after it escapes the <code>try</code>. 
	 */
	public class ShaderLatch implements Closeable
	{
		@Override
		public void close()
		{
			unsetShader();
		}
	}
	
	@Override
	protected Info createInfo()
	{
		return new Info20();
	}
	
	/**
	 * Enables/Disables point sprite conversion.
	 * Internally, OpenGL will convert point geometry into billboarded quads or
	 * actual polygonal information. 
	 * @param enabled true to enable, false to disable.
	 */
	public void setPointSpritesEnabled(boolean enabled)
	{
		setFlag(GL_POINT_SPRITE, enabled);
	}

	/**
	 * Sets if texture coordinates are to be generated across point geometry
	 * dimensions. Useful for Point Sprites, obviously.
	 * @param enabled true to enable, false to disable.
	 */
	public void setPointSpriteTexCoordGeneration(boolean enabled)
	{
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
		switch (type)
		{
			case VERTEX:
				return new OGLShaderProgramVertex(streamName, sourceCode);
			case FRAGMENT:
				return new OGLShaderProgramFragment(streamName, sourceCode);
			default:
				throw new GraphicsException("Bad shader program type.");
		}
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
	 * Binds a 1D texture object to the current active texture unit.
	 * This returns an optional latch object for unbinding the texture 
	 * from the 1D target if this is used in a try-with-resources block.
	 * @param texture the texture to bind.
	 * @return an optional latch object.
	 */
	public Texture1DLatch setTexture1D(OGLTexture texture)
	{
		Objects.requireNonNull(texture);
		glBindTexture(GL_TEXTURE_1D, texture.getName());
		return new Texture1DLatch();
	}

	/**
	 * Binds a shader to the current context.
	 * This returns an optional latch object for unbinding the shader 
	 * if this is used in a try-with-resources block.
	 * @param shader the texture to bind.
	 * @return an optional latch object.
	 */
	public ShaderLatch setShader(OGLShader shader)
	{
		Objects.requireNonNull(shader);
		glUseProgram(shader.getName());
		return new ShaderLatch();
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
	 * Sets a uniform value on the currently-bound shader.
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
	 * Sets a uniform value on the currently-bound shader.
	 * @param locationId the uniform location.
	 * @param value0 the first value to set.
	 * @param value1 the second value to set.
	 * @param value2 the third value to set.
	 */
	public void setShaderUniformVec3(int locationId, float value0, float value1, float value2)
	{
		try (MemoryStack stack = MemoryStack.stackPush())
		{
			FloatBuffer fbuf = stack.mallocFloat(2);
			fbuf.put(0, value0);
			fbuf.put(1, value1);
			fbuf.put(2, value2);
			glUniform3fv(locationId, fbuf);
		}
	}
	
	/**
	 * Sets a uniform value on the currently-bound shader.
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
			FloatBuffer fbuf = stack.mallocFloat(2);
			fbuf.put(0, value0);
			fbuf.put(1, value1);
			fbuf.put(2, value2);
			fbuf.put(3, value2);
			glUniform4fv(locationId, fbuf);
		}
	}
	
	/**
	 * Unbinds a shader from the current context.
	 */
	public void unsetShader()
	{
		glUseProgram(0);
	}

}
