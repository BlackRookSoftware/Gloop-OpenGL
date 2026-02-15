/*******************************************************************************
 * Copyright (c) 2021-2022 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at 
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gloop.opengl.gl2;

import com.blackrook.gloop.opengl.OGLVersion;
import com.blackrook.gloop.opengl.OGLSystem.Options;
import com.blackrook.gloop.opengl.enums.BufferTargetType;
import com.blackrook.gloop.opengl.enums.DataType;
import com.blackrook.gloop.opengl.enums.FeedbackBufferType;
import com.blackrook.gloop.opengl.enums.FrameBufferType;
import com.blackrook.gloop.opengl.enums.MatrixMode;
import com.blackrook.gloop.opengl.enums.ShaderType;
import com.blackrook.gloop.opengl.exception.GraphicsException;
import com.blackrook.gloop.opengl.gl1.OGL15Graphics;
import com.blackrook.gloop.opengl.math.Matrix4F;
import com.blackrook.gloop.opengl.struct.IOUtils;
import com.blackrook.gloop.opengl.util.ProgramBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

import org.lwjgl.system.MemoryStack;

import static org.lwjgl.opengl.GL20.*;

/**
 * OpenGL 2.0 Graphics Implementation.
 * @author Matthew Tropiano
 */
public class OGL20Graphics extends OGL15Graphics
{
	private static final ThreadLocal<Matrix4F> MATRIX = ThreadLocal.withInitial(()->new Matrix4F());

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
	
	/**
	 * Shader builder used for OpenGL 2.0.  
	 */
	public static class OGL20ProgramBuilder extends ProgramBuilder.Abstract<OGL20Graphics>
	{
		protected OGL20ProgramBuilder(OGL20Graphics gl)
		{
			super(gl);
		}

		@Override
		public ProgramBuilder fragmentDataLocation(String attributeName, int index)
		{
			throw new UnsupportedOperationException("Cannot bind fragment locations in this implementation.");
		}

		@Override
		public ProgramBuilder transformFeedbackVaryingNames(FeedbackBufferType type, String... variableNames) 
		{
			throw new UnsupportedOperationException("Cannot set transform feedback information in this implementation.");
		}
		
		@Override
		public OGLProgram create()
		{
			OGLProgram out = gl.createProgram();
			List<OGLProgramShader> list = new LinkedList<>();
			try {
				for (Map.Entry<ShaderType, Supplier<String>> entry : shaderPrograms.entrySet())
				{
					OGLProgramShader ps = null;
					try {
						ShaderType type = entry.getKey();
						ps = gl.createProgramShader(type, type.name(), entry.getValue());
						fireShaderLog(type, ps.getLog());
						gl.attachProgramShaders(out, ps);
						list.add(ps);
					} catch (Exception e) {
						if (ps != null) 
							gl.destroyProgramShader(ps);
					}
				}
				
				for (Map.Entry<String, Integer> entry : attributeLocationBindings.entrySet())
					gl.setProgramVertexAttribLocation(out, entry.getKey(), entry.getValue());
				
				gl.linkProgram(out);

				// clean up shaders after link
				for (OGLProgramShader ps : list)
					gl.destroyProgramShader(ps);
				
			} catch (Exception e) {
				gl.destroyProgram(out);
				for (OGLProgramShader ps : list)
					gl.destroyProgramShader(ps);
				throw e;
			}
			return out;
		}
		
	}
	
	/** Current program. */
	private OGLProgram currentProgram;

	public OGL20Graphics(Options options, boolean core)
	{
		super(options, core);
		this.currentProgram = null;
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
		handleUndeletedObjects(OGLProgram.class, OGLProgram.destroyUndeleted());
		handleUndeletedObjects(OGLProgramShader.class, OGLProgramShader.destroyUndeleted());
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
		verifyNonCore();
		setFlag(GL_POINT_SPRITE, enabled);
	}

	/**
	 * Sets if texture coordinates are to be generated across point geometry
	 * dimensions. Useful for Point Sprites, obviously.
	 * @param enabled true to enable, false to disable.
	 */
	public void setPointSpriteTexCoordGeneration(boolean enabled)
	{
		verifyNonCore();
		glTexEnvi(GL_POINT_SPRITE, GL_COORD_REPLACE, toGLBool(enabled));
	}

	/**
	 * Sets the multiple buffers to write to for pixel drawing/rasterizing operations.
	 * By default, this is just the BACK buffer in double-buffered contexts.
	 * @param types the buffers to write to from now on, made available to shader programs.
	 * @throws GraphicsException if one of the types is NONE, LEFT, RIGHT, FRONT, BACK, or FRONT_AND_BACK.
	 */
	public void setFrameBufferWrite(FrameBufferType ... types)
	{
		try (MemoryStack stack = MemoryStack.stackPush())
		{
			IntBuffer buf = stack.mallocInt(types.length);
			for (int i = 0 ; i < types.length; i++)
				buf.put(i, types[i].glValue);
			glDrawBuffers(buf);
			checkError();
		}
	}
	
	/**
	 * Creates a new program builder.
	 * <p> This program builder aids in building shader program objects, and its
	 * {@link ProgramBuilder#create()} method will compile and link all of the shaders and return the new object.
	 * <p> Limitations on this implementation version are: No fragment data binding.
	 * @return a new program builder.
	 */
	public ProgramBuilder createProgramBuilder()
	{
		return new OGL20ProgramBuilder(this);
	}
	
	/**
	 * Creates a new shader object (vertex, fragment, etc.).
	 * @param type the shader type. if not a valid shader type, this throws an exception.
	 * @param streamName the name of the originating stream (can appear in exceptions).
	 * @param sourceCode the code to compile.
	 * @return the instantiated shader.
	 * @throws UnsupportedOperationException if the provided shader type is unavailable in this version.
	 * @throws NullPointerException if either string is null.
	 */
	public OGLProgramShader createProgramShader(ShaderType type, String streamName, final String sourceCode)
	{
		verifyFeatureSupport(type);
		Objects.requireNonNull(sourceCode);
		return createProgramShader(type, streamName, ()->sourceCode);
	}

	/**
	 * Creates a new shader object (vertex, fragment, etc.).
	 * @param type the shader type. if not a valid shader type, this throws an exception.
	 * @param streamName the name of the originating stream (can appear in exceptions).
	 * @param sourceSupplier the supplier function for the source code.
	 * @return the instantiated shader.
	 * @throws UnsupportedOperationException if the provided shader type is unavailable in this version.
	 * @throws NullPointerException if either string is null.
	 */
	public OGLProgramShader createProgramShader(ShaderType type, String streamName, Supplier<String> sourceSupplier)
	{
		verifyFeatureSupport(type);
		Objects.requireNonNull(type);
		Objects.requireNonNull(streamName);
		Objects.requireNonNull(sourceSupplier);
		return new OGLProgramShader(type, streamName, sourceSupplier.get());
	}

	/**
	 * Destroys a program shader.
	 * @param shader the shader to destroy.
	 */
	public void destroyProgramShader(OGLProgramShader shader)
	{
		destroyObject(shader);
		checkError();
	}
	
	/**
	 * Creates a new program object.
	 * @return a new program object.
	 * @throws GraphicsException if the object could not be created.
	 */
	public OGLProgram createProgram()
	{
		OGLProgram out = new OGLProgram(); 
		checkError();
		return out;
	}

	/**
	 * Destroys a program.
	 * @param program the program to destroy.
	 */
	public void destroyProgram(OGLProgram program)
	{
		destroyObject(program);
		checkError();
	}
	
	/**
	 * Attaches a shader to a program.
	 * Throws an error if this program was already linked, or if a program of the same type was already attached.
	 * @param program the program to attach the shaders to.
	 * @param shaders the shaders to attach.
	 * @throws GraphicsException if this program was already linked, or if a shader of the same type was already attached.
	 */
	public void attachProgramShaders(OGLProgram program, OGLProgramShader ... shaders)
	{
		if (program.isLinked())
			throw new GraphicsException("Cannot attach shader: this program was already linked!");

		for (OGLProgramShader shader : shaders)
			glAttachShader(program.getName(), shader.getName());
	}
	
	/**
	 * Detaches a shader from a program.
	 * Throws an error if a program of the same type was already detached.
	 * @param program the program to detach shaders from.
	 * @param shaders the shaders to detach.
	 */
	public void detachProgramShaders(OGLProgram program, OGLProgramShader ... shaders)
	{
		for (OGLProgramShader shader : shaders)
			glDetachShader(program.getName(), shader.getName());
	}
	
	/**
	 * Refreshes an OGLProgram's link status, uniforms, and attributes.
	 * Should be called when a program is updated via binary upload.
	 * @param program the program to update.
	 */
	protected void refreshProgramLinkStatusAndUniforms(OGLProgram program)
	{
		program.refreshLinkStatus();
		if (program.isLinked())
			program.refreshUniformsAndAttribs();
	}
	
	/**
	 * Binds a specific index to a vertex attribute by name.
	 * Must be done before program link.
	 * @param program the program.
	 * @param attribName the vertex attribute name in the shader.
	 * @param index the desired index.
	 * @throws GraphicsException if the program shader has been linked already.
	 */
	public void setProgramVertexAttribLocation(OGLProgram program, String attribName, int index)
	{
		if (program.isLinked())
			throw new GraphicsException("Target program has already been linked!");
		glBindAttribLocation(program.getName(), index, attribName);
		checkError();
	}
	
	/**
	 * Links the program with its attached program shaders.
	 * @param program the program to link.
	 * @throws GraphicsException if an error occurred during link.
	 */
	public void linkProgram(OGLProgram program)
	{
		program.link();
		checkError();
	}
	
	/**
	 * Gets the currently bound program. 
	 * @return the program, or null if no bound program.
	 */
	public OGLProgram getProgram()
	{
		return currentProgram;
	}

	/**
	 * Binds a program to the current context.
	 * @param program the program to bind.
	 */
	public void setProgram(OGLProgram program)
	{
		Objects.requireNonNull(program);
		if (!program.isLinked())
			throw new GraphicsException("Program has not been successfully linked yet!");
		glUseProgram(program.getName());
		currentProgram = program;
	}

	/**
	 * Sets a uniform integer value on the currently-bound program.
	 * @param locationId the uniform location.
	 * @param value the value to set.
	 */
	public void setProgramUniformInt(int locationId, int value)
	{
		glUniform1i(locationId, value);
	}
	
	/**
	 * Sets a uniform integer value array on the currently-bound program.
	 * @param locationId the uniform location.
	 * @param values the values to set.
	 */
	public void setProgramUniformIntArray(int locationId, int ... values)
	{
		glUniform1iv(locationId, values);
	}
	
	/**
	 * Sets a uniform float value on the currently-bound program.
	 * @param locationId the uniform location.
	 * @param value the value to set.
	 */
	public void setProgramUniformFloat(int locationId, float value)
	{
		glUniform1f(locationId, value);
	}
	
	/**
	 * Sets a uniform float array value on the currently-bound program.
	 * @param locationId the uniform location.
	 * @param values the values to set.
	 */
	public void setProgramUniformFloatArray(int locationId, float ... values)
	{
		glUniform1fv(locationId, values);
	}
	
	/**
	 * Sets a uniform vec2 value on the currently-bound program.
	 * @param locationId the uniform location.
	 * @param value0 the first value to set.
	 * @param value1 the second value to set.
	 */
	public void setProgramUniformVec2(int locationId, float value0, float value1)
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
	 * Sets a uniform vec3 value on the currently-bound program.
	 * @param locationId the uniform location.
	 * @param value0 the first value to set.
	 * @param value1 the second value to set.
	 * @param value2 the third value to set.
	 */
	public void setProgramUniformVec3(int locationId, float value0, float value1, float value2)
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
	 * Sets a uniform vec4 value on the currently-bound program.
	 * @param locationId the uniform location.
	 * @param value0 the first value to set.
	 * @param value1 the second value to set.
	 * @param value2 the third value to set.
	 * @param value3 the fourth value to set.
	 */
	public void setProgramUniformVec4(int locationId, float value0, float value1, float value2, float value3)
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
	 * Sets a uniform integer vec2 value on the currently-bound program.
	 * @param locationId the uniform location.
	 * @param value0 the first value to set.
	 * @param value1 the second value to set.
	 */
	public void setProgramUniformIVec2(int locationId, int value0, int value1)
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
	 * Sets a uniform integer vec3 value on the currently-bound program.
	 * @param locationId the uniform location.
	 * @param value0 the first value to set.
	 * @param value1 the second value to set.
	 * @param value2 the third value to set.
	 */
	public void setProgramUniformIVec3(int locationId, int value0, int value1, int value2)
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
	 * Sets a uniform integer vec4 value on the currently-bound program.
	 * @param locationId the uniform location.
	 * @param value0 the first value to set.
	 * @param value1 the second value to set.
	 * @param value2 the third value to set.
	 * @param value3 the fourth value to set.
	 */
	public void setProgramUniformIVec4(int locationId, int value0, int value1, int value2, int value3)
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
	 * Sets a uniform matrix (mat2) value on the currently-bound program.
	 * @param locationId the uniform location.
	 * @param matrix the column-major array of values.
	 * @throws ArrayIndexOutOfBoundsException if matrix is not 4 elements or greater and a value is fetched out-of-bounds.
	 */
	public void setProgramUniformMatrix2(int locationId, float[] matrix)
	{
		// Fill in column major order!
		try (MemoryStack stack = MemoryStack.stackPush())
		{
			FloatBuffer fbuf = stack.mallocFloat(4);
			fbuf.put(matrix);
			fbuf.flip();
			glUniformMatrix2fv(locationId, false, fbuf);
		}
	}

	/**
	 * Sets a uniform matrix (mat2) value on the currently-bound program.
	 * @param locationId the uniform location.
	 * @param matrix the multidimensional array of values, each array as one row of values.
	 * @throws ArrayIndexOutOfBoundsException if matrix is not 2x2 or greater and a value is fetched out-of-bounds.
	 */
	public void setProgramUniformMatrix2(int locationId, float[][] matrix)
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
	 * Sets a uniform matrix (mat3) value on the currently-bound program.
	 * @param locationId the uniform location.
	 * @param matrix the column-major array of values.
	 * @throws ArrayIndexOutOfBoundsException if matrix is not 9 elements or greater and a value is fetched out-of-bounds.
	 */
	public void setProgramUniformMatrix3(int locationId, float[] matrix)
	{
		// Fill in column major order!
		try (MemoryStack stack = MemoryStack.stackPush())
		{
			FloatBuffer fbuf = stack.mallocFloat(9);
			fbuf.put(matrix);
			fbuf.flip();
			glUniformMatrix3fv(locationId, false, fbuf);
		}
	}

	/**
	 * Sets a uniform matrix (mat3) value on the currently-bound program.
	 * @param locationId the uniform location.
	 * @param matrix the multidimensional array of values, each array as one row of values.
	 * @throws ArrayIndexOutOfBoundsException if matrix is not 3x3 or greater and a value is fetched out-of-bounds.
	 */
	public void setProgramUniformMatrix3(int locationId, float[][] matrix)
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
	 * Sets a uniform matrix (mat4) value on the currently-bound program.
	 * @param locationId the uniform location.
	 * @param matrix the column-major array of values.
	 * @throws ArrayIndexOutOfBoundsException if matrix is not 16 elements or greater and a value is fetched out-of-bounds.
	 */
	public void setProgramUniformMatrix4(int locationId, float[] matrix)
	{
		// Fill in column major order!
		try (MemoryStack stack = MemoryStack.stackPush())
		{
			FloatBuffer fbuf = stack.mallocFloat(16);
			fbuf.put(matrix);
			fbuf.flip();
			glUniformMatrix4fv(locationId, false, fbuf);
		}
	}

	/**
	 * Sets a uniform matrix (mat4) value on the currently-bound program.
	 * @param locationId the uniform location.
	 * @param matrix the multidimensional array of values, each array as one row of values.
	 * @throws ArrayIndexOutOfBoundsException if matrix is not 4x4 or greater and a value is fetched out-of-bounds.
	 */
	public void setProgramUniformMatrix4(int locationId, float[][] matrix)
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
	 * Sets a uniform matrix (mat4) value on the currently-bound program.
	 * @param locationId the uniform location.
	 * @param matrix the multidimensional array of values, each array as one row of values.
	 */
	public void setProgramUniformMatrix4(int locationId, Matrix4F matrix)
	{
		setProgramUniformMatrix4(locationId, matrix.getArray());
	}

	/**
	 * Sets a uniform matrix (mat4) value on the currently-bound program using a matrix in the matrix stack.
	 * @param locationId the uniform location.
	 * @param matrixMode the matrix to grab values from.
	 * @throws UnsupportedOperationException if matrix modes are unavailable in this version (core implementation).
	 */
	public void setProgramUniformMatrix4(int locationId, MatrixMode matrixMode)
	{
		if (isCore())
			throw new UnsupportedOperationException("Matrix mode is not available in the core implementation.");
		else
		{
			Matrix4F mat4 = MATRIX.get();
			matrixGet(matrixMode, mat4);
			setProgramUniformMatrix4(locationId, mat4);
		}
	}

	/**
	 * Unbinds a program from the current context.
	 */
	public void unsetProgram()
	{
		glUseProgram(0);
		currentProgram = null;
	}

	/**
	 * Enables or disables the processing of bound vertex arrays and/or buffers at a specific attrib index.
	 * @param index the attribute index or uniform location id.
	 * @param enable true to enable, false to disable.
	 */
	public void setVertexAttribEnabled(int index, boolean enable)
	{
		if (enable)
			glEnableVertexAttribArray(index);
		else
			glDisableVertexAttribArray(index);
		checkError();
	}

	/**
	 * Sets what positions in the current {@link BufferTargetType#GEOMETRY}-bound buffer are used to draw polygonal information:
	 * This sets the vertex attribute pointers.
	 * The index on this can also be a uniform location for an attrib pointer.
	 * @param index the attribute index.
	 * @param dataType the data type contained in the buffer that will be read (calculates actual sizes of data).
	 * @param normalize if true, the data is normalized on read ([-1, 1] for signed values, [0, 1] for unsigned). Else, read as-is.
	 * @param dimensions the dimensions of a full set of attribute components (in elements; 3-dimensional vertices = 3).
	 * @param stride the distance (in elements) between each attribute.
	 * @param offset the offset in each stride where each attribute starts (in elements).  
	 * @see #setVertexAttribEnabled(int, boolean)   
	 */
	public void setVertexAttribBufferPointer(int index, DataType dataType, boolean normalize, int dimensions, int stride, int offset)
	{
		glVertexAttribPointer(index, dimensions, dataType.glValue, normalize, stride * dataType.size, offset * dataType.size);
		checkError();
	}

	/**
	 * Creates a supplier that gets the source from a file.
	 * Assumes system encoding.
	 * @param sourceFile the source file.
	 * @return a supplier function for retrieving the source data.
	 * @throws FileNotFoundException if the source file was not found. 
	 * @throws IOException if the file could not be opened.
	 * @throws SecurityException if the file could not be opened, due to OS restrictions.
	 */
	public static Supplier<String> createFileSourceSupplier(File sourceFile) throws FileNotFoundException, IOException
	{
		return createFileSourceSupplier(sourceFile, Charset.defaultCharset());
	}

	/**
	 * Creates a supplier that gets the source from a file.
	 * @param sourceFile the source file.
	 * @param encoding the encoding type of the file.
	 * @return a supplier function for retrieving the source data.
	 * @throws FileNotFoundException if the source file was not found. 
	 * @throws IOException if the file could not be opened.
	 * @throws SecurityException if the file could not be opened, due to OS restrictions.
	 */
	public static Supplier<String> createFileSourceSupplier(File sourceFile, Charset encoding) throws FileNotFoundException, IOException
	{
		return createStreamSourceSupplier(new FileInputStream(sourceFile), encoding);
	}

	/**
	 * Creates a supplier that gets the source from an input stream.
	 * Assumes system encoding.
	 * @param sourceStream the input stream.
	 * @return a supplier function for retrieving the source data.
	 */
	public static Supplier<String> createStreamSourceSupplier(InputStream sourceStream)
	{
		return createReaderSourceSupplier(new InputStreamReader(sourceStream, Charset.defaultCharset()));
	}

	/**
	 * Creates a supplier that gets the source from an input stream.
	 * @param sourceStream the input stream.
	 * @param encoding the encoding type of the incoming data.
	 * @return a supplier function for retrieving the source data.
	 */
	public static Supplier<String> createStreamSourceSupplier(InputStream sourceStream, Charset encoding)
	{
		return createReaderSourceSupplier(new InputStreamReader(sourceStream, encoding));
	}

	/**
	 * Creates a supplier that gets the source from a Reader.
	 * The reader is closed after the read.
	 * @param reader the supplied reader. 
	 * @return a supplier function for retrieving the source data.
	 */
	public static Supplier<String> createReaderSourceSupplier(final Reader reader)
	{
		return () -> 
		{
			try (StringWriter writer = new StringWriter())
			{
				IOUtils.relay(reader, writer);
				return writer.toString();
			} 
			catch (IOException e) 
			{
				return e.getClass().getSimpleName() + ": " + e.getLocalizedMessage();
			}
			finally
			{
				IOUtils.close(reader);
			}
		};
	}
	
}
