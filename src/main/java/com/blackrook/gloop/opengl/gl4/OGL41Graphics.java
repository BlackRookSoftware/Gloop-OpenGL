/*******************************************************************************
 * Copyright (c) 2021-2024 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at 
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gloop.opengl.gl4;

import com.blackrook.gloop.opengl.OGLVersion;
import com.blackrook.gloop.opengl.exception.GraphicsException;
import com.blackrook.gloop.opengl.gl1.OGL11Graphics;
import com.blackrook.gloop.opengl.gl2.OGLProgram;
import com.blackrook.gloop.opengl.OGLSystem.Options;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.lwjgl.system.MemoryStack;

import static org.lwjgl.opengl.GL41.*;


/**
 * OpenGL 4.1 Graphics Implementation.
 * @author Matthew Tropiano
 */
public class OGL41Graphics extends OGL40Graphics
{
	protected class Info41 extends Info40
	{
		protected Info41()
		{
			super();
			this.maxVertexUniformVectors = getInt(GL_MAX_VERTEX_UNIFORM_VECTORS);
			this.maxVaryingVectors = getInt(GL_MAX_VARYING_VECTORS);
			this.maxFragmentUniformVectors = getInt(GL_MAX_FRAGMENT_UNIFORM_VECTORS);
			this.maxViewports = getInt(GL_MAX_VIEWPORTS);
		}
	}
	
	public OGL41Graphics(Options options, boolean core)
	{
		super(options, core);
	}

	@Override
	public OGLVersion getVersion()
	{
		return OGLVersion.GL41;
	}

	@Override
	protected Info createInfo()
	{
		return new Info41();
	}
	
	/**
	 * Sets a viewport at a specific index.
	 * @param index the index to set.
	 * @param x x-coordinate origin of the screen.
	 * @param y y-coordinate origin of the screen.
	 * @param width	the width of the viewport in pixels.
	 * @param height the height of the viewport in pixels.
	 * @see OGL11Graphics#setViewport(int, int, int, int)
	 */
	public void setViewportIndex(int index, int x, int y, int width, int height)
	{
		glViewportIndexedf(index, x, y, width, height);
	}
	
	/**
	 * Sets a hint on a program to tell OpenGL that you are going to retrieve a binary representation from it.
	 * You should set this flag before linking a program.
	 * @param program the program to set the hint on.
	 * @param enabled true to enable, false to disable.
	 */
	public void setProgramBinaryRetriveableHint(OGLProgram program, boolean enabled)
	{
		glProgramParameteri(program.getName(), GL_PROGRAM_BINARY_RETRIEVABLE_HINT, toGLBool(enabled));
	}
	
	/**
	 * Sets a flag on a program to tell OpenGL that you are planning to add this to a pipeline later.
	 * You should set this flag before linking a program.
	 * @param program the program to set the hint on.
	 * @param enabled true to enable, false to disable.
	 */
	public void setProgramSeparable(OGLProgram program, boolean enabled)
	{
		glProgramParameteri(program.getName(), GL_PROGRAM_SEPARABLE, toGLBool(enabled));
	}
	
	/**
	 * Retrieves the binary representation of a compiled and linked program.
	 * @param program the program to get the binary data from.
	 * @return a ProgramBinary object that contains the program binary representation.
	 * @throws GraphicsException if the program is not linked.
	 * @see #setProgramBinary(OGLProgram, ProgramBinary)
	 */
	public ProgramBinary getProgramBinary(OGLProgram program)
	{
		if (!program.isLinked())
			throw new GraphicsException("Program has not been successfully linked.");
		
		int binaryLen = glGetProgrami(program.getName(), GL_PROGRAM_BINARY_LENGTH);
		
		int programFormatId;
		byte[] programBinaryBytes;
		
		try (MemoryStack stack = MemoryStack.stackPush())
		{
			IntBuffer length = stack.ints(binaryLen);
			IntBuffer format = stack.mallocInt(1);
			ByteBuffer binary = stack.malloc(binaryLen);
			
			glGetProgramBinary(program.getName(), length, format, binary);
			
			programFormatId = format.get(0);
			programBinaryBytes = new byte[binary.capacity()];
			binary.get(programBinaryBytes);
		}
		
		return new ProgramBinary(programFormatId, programBinaryBytes);
	}
	
	/**
	 * Creates a program from a binary representation.
	 * @param program the program to update.
	 * @param programBinary the binary representation to use.
	 * @throws GraphicsException if the program could not be uploaded.
	 * @see #getProgramBinary(OGLProgram)
	 */
	public void setProgramBinary(OGLProgram program, ProgramBinary programBinary)
	{
		try (MemoryStack stack = MemoryStack.stackPush())
		{
			ByteBuffer binary = stack.bytes(programBinary.getProgramBytes());
			glProgramBinary(program.getName(), programBinary.getProgramFormat(), binary);
			checkError();
		}
		refreshProgramLinkStatusAndUniforms(program);
	}
	
	/**
	 * Creates a new Program Pipeline object.
	 * @return a new program pipeline object.
	 */
	public OGLProgramPipeline createProgramPipeline()
	{
		return new OGLProgramPipeline();
	}
	
	/**
	 * Destroys a program pipeline.
	 * @param pipeline the pipeline to destroy.
	 */
	public void destroyProgramPipeline(OGLProgramPipeline pipeline)
	{
		destroyObject(pipeline);
	}
	
	/**
	 * Fetches a program pipeline's info log aas a string.
	 * @param pipeline the pipeline to query.
	 * @return the pipeline's info log as a string.
	 */
	public String getProgramPipelineInfoLog(OGLProgramPipeline pipeline)
	{
		return glGetProgramPipelineInfoLog(pipeline.getName());
	}
	
	/**
	 * Checks the validity of a program pipeline's executables against this OpenGL context.
	 * @param pipeline the pipeline to check.
	 * @throws GraphicsException if the pipeline is invalid.
	 */
	public void validateProgramPipeline(OGLProgramPipeline pipeline)
	{
		glValidateProgramPipeline(pipeline.getName());
		checkError();
	}
	
	/**
	 * An OpenGL Shader Program binary representation. 
	 */
	public static final class ProgramBinary
	{
		int programFormat;
		byte[] programBytes;
		
		/**
		 * Creates a new program binary from a format id and an array of bytes.
		 * @param format the program format.
		 * @param bytes the program bytes.
		 */
		public ProgramBinary(int format, byte[] bytes)
		{
			this.programFormat = format;
			this.programBytes = bytes;
		}
		
		/**
		 * Gets the program's format. May be implementation-dependent.
		 * @return the program format id.
		 */
		public int getProgramFormat() 
		{
			return programFormat;
		}
		
		/**
		 * Gets the program's binary representation, as a byte array.
		 * @return the binary representation.
		 */
		public byte[] getProgramBytes() 
		{
			return programBytes;
		}
		
	}
}
