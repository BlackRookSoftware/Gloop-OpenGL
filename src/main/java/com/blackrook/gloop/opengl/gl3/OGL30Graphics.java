/*******************************************************************************
 * Copyright (c) 2021-2022 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at 
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gloop.opengl.gl3;

import com.blackrook.gloop.opengl.gl2.OGL21Graphics;
import com.blackrook.gloop.opengl.gl2.OGLProgram;
import com.blackrook.gloop.opengl.gl2.OGLProgramShader;
import com.blackrook.gloop.opengl.math.Matrix4F;
import com.blackrook.gloop.opengl.math.MatrixStack;
import com.blackrook.gloop.opengl.util.GeometryBuilder;
import com.blackrook.gloop.opengl.util.ProgramBuilder;
import com.blackrook.gloop.opengl.OGLVersion;
import com.blackrook.gloop.opengl.OGLSystem.Options;
import com.blackrook.gloop.opengl.enums.AttachPoint;
import com.blackrook.gloop.opengl.enums.BufferTargetType;
import com.blackrook.gloop.opengl.enums.DataType;
import com.blackrook.gloop.opengl.enums.FeedbackBufferType;
import com.blackrook.gloop.opengl.enums.GeometryType;
import com.blackrook.gloop.opengl.enums.MatrixMode;
import com.blackrook.gloop.opengl.enums.PrimitiveMode;
import com.blackrook.gloop.opengl.enums.QueryWaitType;
import com.blackrook.gloop.opengl.enums.RenderbufferFormat;
import com.blackrook.gloop.opengl.enums.ShaderType;
import com.blackrook.gloop.opengl.enums.TextureMagFilter;
import com.blackrook.gloop.opengl.enums.TextureMinFilter;
import com.blackrook.gloop.opengl.enums.TextureTargetType;
import com.blackrook.gloop.opengl.exception.GraphicsException;
import com.blackrook.gloop.opengl.gl1.OGLBuffer;
import com.blackrook.gloop.opengl.gl1.OGLQuery;
import com.blackrook.gloop.opengl.gl1.OGLTexture;

import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

import org.lwjgl.system.MemoryStack;

import static org.lwjgl.opengl.GL30.*;

/**
 * OpenGL 3.0 Graphics Implementation.
 * @author Matthew Tropiano
 */
public class OGL30Graphics extends OGL21Graphics
{
	protected class Info30 extends Info20
	{
		protected Info30()
		{
			super();
			this.maxSamples = getInt(GL_MAX_SAMPLES);
			this.maxVertexAttribs = getInt(GL_MAX_VERTEX_ATTRIBS);
			this.maxRenderBufferSize = getInt(GL_MAX_RENDERBUFFER_SIZE);
			this.maxRenderBufferColorAttachments = getInt(GL_MAX_COLOR_ATTACHMENTS);
		}
		
		@Override
		protected void addExtensions(Set<String> set)
		{
			if (!isCore())
			{
				set.addAll(Arrays.asList(glGetString(GL_EXTENSIONS).split("\\s+")));
			}
			else
			{
				int extensionCount = glGetInteger(GL_NUM_EXTENSIONS);
				for (int i = 0; i < extensionCount; i++)
					set.add(glGetStringi(GL_EXTENSIONS, i));
			}
		}
		
	}
	
	/**
	 * Shader builder used for OpenGL 3.0.  
	 */
	public static class OGL30ProgramBuilder extends ProgramBuilder.Abstract<OGL30Graphics>
	{
		protected OGL30ProgramBuilder(OGL30Graphics gl)
		{
			super(gl);
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
				for (Map.Entry<String, Integer> entry : fragmentDataBindings.entrySet())
					gl.setProgramFragmentDataLocation(out, entry.getKey(), entry.getValue());
				
				gl.linkProgram(out);
				
			} catch (Exception e) {
				gl.destroyProgram(out);
				for (OGLProgramShader ps : list)
					gl.destroyProgramShader(ps);
				throw e;
			}
			return out;
		}
		
	}
	
	private boolean conditionalRenderActive;
	private boolean transformFeedbackActive;
	
	public OGL30Graphics(Options options, boolean core)
	{
		super(options, core);
		conditionalRenderActive = false;
		transformFeedbackActive = false;
	}

	@Override
	public OGLVersion getVersion()
	{
		return OGLVersion.GL30;
	}
	
	@Override
	protected Info createInfo()
	{
		return new Info30();
	}
	
	@Override
	protected void endFrame()
	{
		// Clean up abandoned objects.
		OGLRenderbuffer.destroyUndeleted();
		OGLFramebuffer.destroyUndeleted();
		super.endFrame();
	}

	@Override
	public void matrixMode(MatrixMode mode)
	{
		if (isCore())
			throw new UnsupportedOperationException("Matrix mode is not available in the core implementation.");
		super.matrixMode(mode);
	}

	@Override
	public void matrixGet(MatrixMode matrixType, float[] outArray)
	{
		if (isCore())
			throw new UnsupportedOperationException("Matrix mode is not available in the core implementation.");
		super.matrixGet(matrixType, outArray);
	}

	@Override
	public void matrixGet(MatrixMode matrixType, Matrix4F matrix)
	{
		if (isCore())
			throw new UnsupportedOperationException("Matrix mode is not available in the core implementation.");
		super.matrixGet(matrixType, matrix);
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
		super.setProgramUniformMatrix4(locationId, matrixMode);
	}

	@Override
	public void setTextureFiltering(TextureTargetType target, TextureMinFilter minFilter, TextureMagFilter magFilter, boolean genMipmaps)
	{
		setTextureFiltering(target, minFilter, magFilter);
		if (genMipmaps)
			generateMipmaps(target);
	}

	@Override
	public void setTextureFiltering(TextureTargetType target, TextureMinFilter minFilter, TextureMagFilter magFilter, float anisotropy, boolean genMipmaps)
	{
		setTextureFiltering(target, minFilter, magFilter, anisotropy);
		if (genMipmaps)
			generateMipmaps(target);
	}

	/**
	 * Creates a new program builder.
	 * <p> This program builder aids in building shader program objects, and its
	 * {@link ProgramBuilder#create()} method will compile and link all of the shaders and return the new object.
	 * @return a new program builder.
	 */
	public ProgramBuilder createProgramBuilder()
	{
		return new OGL30ProgramBuilder(this);
	}

	/**
	 * Sets the current matrix index.
	 * <p> Use this if you want matrix stack functionality in a core profile.
	 * @param id the new current matrix id.
	 * @throws UnsupportedOperationException if this profile is not core OpenGL.
	 */
	public void matrixId(int id)
	{
		if (!isCore())
			throw new UnsupportedOperationException("Matrix ids are not available in the non-core implementation.");
		setCurrentMatrixId(id);
	}

	/**
	 * Reads a current matrix into an array.
	 * <p>This is technically not available in Core OpenGL, but is instead 
	 * implemented using {@link MatrixStack} for core implementations.  
	 * @param matrixId the matrix id to fetch.
	 * @param outArray the output array. Must be length 16 or greater.
	 * @throws ArrayIndexOutOfBoundsException if the array length is less than 16.
	 */
	public void matrixGet(int matrixId, float[] outArray)
	{
		if (!isCore())
			throw new UnsupportedOperationException("Matrix ids are not available in the non-core implementation.");
		getCurrentMatrixStack(matrixId).peek().getFloats(outArray);
	}

	/**
	 * Reads a current matrix into a matrix.
	 * <p>This is technically not available in Core OpenGL, but is instead 
	 * implemented using {@link MatrixStack} for core implementations.  
	 * @param matrixId the matrix id to fetch.
	 * @param matrix the output matrix.
	 */
	public void matrixGet(int matrixId, Matrix4F matrix)
	{
		if (!isCore())
			throw new UnsupportedOperationException("Matrix ids are not available in the non-core implementation.");
		matrix.set(getCurrentMatrixStack(matrixId).peek());
	}

	/**
	 * Sets the transform feedback varying variables for the bound shader program.
	 * Must be called before {@link #linkProgram(OGLProgram)} for the provided program.
	 * @param program the program to set the variables for.
	 * @param type the feedback output type.
	 * @param variableNames the names of the variables.
	 * @throws IllegalStateException if the provided program was already linked.
	 */
	public void setTransformFeedbackVaryings(OGLProgram program, FeedbackBufferType type, String ... variableNames)
	{
		if (program.isLinked())
			throw new IllegalStateException("Program was already linked.");
		
		glTransformFeedbackVaryings(program.getName(), variableNames, type.glValue);
		checkError();
	}

	/**
	 * Sets a uniform matrix (mat4) value on the currently-bound program using a matrix in the matrix stack.
	 * @param locationId the uniform location.
	 * @param matrixId the matrix stack id.
	 */
	public void setProgramUniformMatrix4(int locationId, int matrixId)
	{
		if (!isCore())
			throw new UnsupportedOperationException("Matrix ids are not available in the non-core implementation.");
		setProgramUniformMatrix4(locationId, getCurrentMatrixStack(matrixId).peek());
	}

	/**
	 * Generates mipmaps on-demand internally for the current texture bound to the provided target.
	 * @param target the texture target.
	 */
	public void generateMipmaps(TextureTargetType target)
	{
		glGenerateMipmap(target.glValue);
		checkError();
	}

	/**
	 * Sets a uniform unsigned integer value on the currently-bound shader.
	 * @param locationId the uniform location.
	 * @param value the value to set.
	 */
	public void setShaderUniformUnsignedInt(int locationId, int value)
	{
		glUniform1ui(locationId, value);
		checkError();
	}

	/**
	 * Sets a uniform unsigned integer value array on the currently-bound shader.
	 * @param locationId the uniform location.
	 * @param values the values to set.
	 */
	public void setShaderUniformUnsignedIntArray(int locationId, int ... values)
	{
		glUniform1uiv(locationId, values);
		checkError();
	}

	/**
	 * Sets a uniform integer vec2 value on the currently-bound shader.
	 * @param locationId the uniform location.
	 * @param value0 the first value to set.
	 * @param value1 the second value to set.
	 */
	public void setShaderUniformUnsignedIVec2(int locationId, int value0, int value1)
	{
		try (MemoryStack stack = MemoryStack.stackPush())
		{
			IntBuffer ibuf = stack.mallocInt(2);
			ibuf.put(0, value0);
			ibuf.put(1, value1);
			glUniform2uiv(locationId, ibuf);
			checkError();
		}
	}

	/**
	 * Sets a uniform integer vec3 value on the currently-bound shader.
	 * @param locationId the uniform location.
	 * @param value0 the first value to set.
	 * @param value1 the second value to set.
	 * @param value2 the third value to set.
	 */
	public void setShaderUniformUnsignedIVec3(int locationId, int value0, int value1, int value2)
	{
		try (MemoryStack stack = MemoryStack.stackPush())
		{
			IntBuffer ibuf = stack.mallocInt(3);
			ibuf.put(0, value0);
			ibuf.put(1, value1);
			ibuf.put(2, value2);
			glUniform3uiv(locationId, ibuf);
			checkError();
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
	public void setShaderUniformUnsignedIVec4(int locationId, int value0, int value1, int value2, int value3)
	{
		try (MemoryStack stack = MemoryStack.stackPush())
		{
			IntBuffer ibuf = stack.mallocInt(4);
			ibuf.put(0, value0);
			ibuf.put(1, value1);
			ibuf.put(2, value2);
			ibuf.put(3, value3);
			glUniform4uiv(locationId, ibuf);
			checkError();
		}
	}

	/**
	 * Creates a vertex array state object, which maintains the state of bound
	 * vertex attributes and bound buffer targets.
	 * This uses the content of a {@link GeometryBuilder} and the buffer it created 
	 * to set the pointers used by the array state. 
	 * @param buffer the buffer created by the builder.
	 * @param builder the builder to get the attribute data from.
	 * @return the new object.
	 */
	public OGLVertexArrayState createVertexArrayState(OGLBuffer buffer, GeometryBuilder builder)
	{
		OGLVertexArrayState out = createVertexArrayState();
		setVertexArrayState(out);
		setBuffer(BufferTargetType.GEOMETRY, buffer);

		for (int i = 0; i < builder.getAttributeCount(); i++)
		{
			setVertexAttribEnabled(i, true);
			setVertexAttribBufferPointer(i, DataType.FLOAT, false, builder.getWidth(i), builder.getStrideSize(), builder.getOffset(i));
		}
		
		unsetBuffer(BufferTargetType.GEOMETRY);
		unsetVertexArrayState();
		return out;
	}
	
	/**
	 * Creates a vertex array state object, which maintains the state of bound
	 * vertex attributes and bound buffer targets.
	 * @return the new object.
	 */
	public OGLVertexArrayState createVertexArrayState()
	{
		return new OGLVertexArrayState();
	}
	
	/**
	 * Destroys a vertex array state.
	 * @param arrayState the vertex array state to destroy.
	 */
	public void destroyVertexArrayState(OGLVertexArrayState arrayState)
	{
		destroyObject(arrayState);
		checkError();
	}
	
	/**
	 * Sets the current vertex array state, which also restores all of the
	 * vertex attribute pointer and buffer target bindings associated with it.
	 * The following is saved by a vertex array state:
	 * <ul>
	 * <li> {@link #setVertexAttribEnabled(int, boolean)}
	 * <li> {@link #setVertexAttribBufferPointer(int, DataType, boolean, int, int, int)}
	 * </ul>
	 * @param state the state to set.
	 */
	public void setVertexArrayState(OGLVertexArrayState state)
	{
		Objects.requireNonNull(state);
		glBindVertexArray(state.getName());
		checkError();
	}
	
	/**
	 * Unbinds a vertex array state from the current context.
	 */
	public void unsetVertexArrayState()
	{
		glBindVertexArray(0);
		checkError();
	}

	/**
	 * Sets the location of a fragment output.  
	 * Must be done before program link.
	 * @param program the shader to set it on.
	 * @param colorIndex the color index (draw buffer index).
	 * @param outVariableName the name of the output variable.
	 * @throws GraphicsException if the program shader has been linked already.
	 */
	public void setProgramFragmentDataLocation(OGLProgram program, String outVariableName, int colorIndex)
	{
		if (program.isLinked())
			throw new GraphicsException("Target program has already been linked!");
		if (colorIndex >= getInfo().getMaxDrawBuffers())
			throw new GraphicsException("Color index cannot be greater than " + getInfo().getMaxDrawBuffers());
		glBindFragDataLocation(program.getName(), colorIndex, outVariableName);
		checkError();
	}

	/**
	 * Creates a new render buffer object.
	 * @return a new, uninitialized render buffer object.
	 */
	public OGLRenderbuffer createRenderbuffer()
	{
		return new OGLRenderbuffer();
	}

	/**
	 * Destroys a render buffer.
	 * @param renderBuffer the render buffer to destroy.
	 */
	public void destroyRenderbuffer(OGLRenderbuffer renderBuffer)
	{
		destroyObject(renderBuffer);
		checkError();
	}
	
	/**
	 * Binds a FrameRenderBuffer to the current context.
	 * @param renderbuffer the render buffer to bind to the current render buffer.
	 */
	public void setRenderbuffer(OGLRenderbuffer renderbuffer)
	{
		Objects.requireNonNull(renderbuffer);
		glBindRenderbuffer(GL_RENDERBUFFER, renderbuffer.getName());
		checkError();
	}

	/**
	 * Sets a render buffer's internal format and size.
	 * @param format the buffer format.
	 * @param width the width in pixel data.
	 * @param height the height in pixel data.
	 */
	public void setRenderbufferSize(RenderbufferFormat format, int width, int height)
	{
		if (width < 1 || height < 1)
			throw new GraphicsException("Render buffer size cannot be less than 1 in any dimension.");
		glRenderbufferStorage(GL_RENDERBUFFER, format.glid, width, height);
		checkError();
	}

	/**
	 * Unbinds a FrameRenderBuffer from the current context.
	 */
	public void unsetRenderbuffer()
	{
		glBindRenderbuffer(GL_RENDERBUFFER, 0);
		checkError();
	}

	/**
	 * Creates a new framebuffer object.
	 * @return a new, uninitialized framebuffer object.
	 * @throws GraphicsException if the object could not be created.
	 */
	public OGLFramebuffer createFramebuffer()
	{
		return new OGLFramebuffer();
	}

	/**
	 * Destroys a framebuffer.
	 * @param frameBuffer the framebuffer to destroy.
	 */
	public void destroyFramebuffer(OGLFramebuffer frameBuffer)
	{
		destroyObject(frameBuffer);
		checkError();
	}
	
	/**
	 * Binds a FrameBuffer for rendering.
	 * @param framebuffer the framebuffer to set as the current one.
	 */
	public void setFramebuffer(OGLFramebuffer framebuffer)
	{
		Objects.requireNonNull(framebuffer);
		glBindFramebuffer(GL_FRAMEBUFFER, framebuffer.getName());
		checkError();
	}

	/**
	 * Tests for frame buffer completeness on the bound framebuffer. 
	 * If incomplete, this throws an exception.
	 * @throws GraphicsException if the framebuffer is incomplete.
	 */
	public void checkFramebufferStatus()
	{
		int status = glCheckFramebufferStatus(GL_FRAMEBUFFER);
		String errorString = null;
		if (status != GL_FRAMEBUFFER_COMPLETE) 
		{
			switch (status)
			{
				case GL_FRAMEBUFFER_UNSUPPORTED:
					errorString = "Framebuffer object format is unsupported by the video hardware.";
					break;
				case GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT:
					errorString = "Incomplete attachment.";
					break;
				case GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT:
					errorString = "Incomplete missing attachment.";
					break;
				case GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER:
					errorString = "Incomplete draw buffer.";
					break;
				case GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER:
					errorString = "Incomplete read buffer.";
					break;
				case GL_FRAMEBUFFER_INCOMPLETE_MULTISAMPLE:
					errorString = "Incomplete multisample buffer.";
					break;
				default:
					errorString = "Framebuffer object status is invalid due to unknown error.";
					break;
			}
			throw new GraphicsException("OpenGL raised error: "+errorString);
		}
	}

	/**
	 * Attaches a texture to this frame buffer for rendering directly to a texture.
	 * @param attachPoint the attachment source point.
	 * @param texture the texture to attach this to.
	 */
	public void attachFramebufferTexture2D(AttachPoint attachPoint, OGLTexture texture)
	{
		glFramebufferTexture2D(GL_FRAMEBUFFER, attachPoint.glVal, GL_TEXTURE_2D, texture.getName(), 0);
		checkError();
	}

	/**
	 * Detaches a texture from this frame buffer.
	 * @param attachPoint the attachment source point.
	 */
	public void detachFramebufferTexture2D(AttachPoint attachPoint)
	{
		glFramebufferTexture2D(GL_FRAMEBUFFER, attachPoint.glVal, GL_TEXTURE_2D, 0, 0);
		checkError();
	}

	/**
	 * Attaches a render buffer to the current frame buffer.
	 * @param attachPoint the attachment source point.
	 * @param renderBuffer the render buffer to attach this to.
	 */
	public void attachFramebufferRenderbuffer(AttachPoint attachPoint, OGLRenderbuffer renderBuffer)
	{
		glFramebufferRenderbuffer(GL_FRAMEBUFFER, attachPoint.glVal, GL_RENDERBUFFER, renderBuffer.getName());
		checkError();
	}

	/**
	 * Detaches a render buffer from the current frame buffer.
	 * @param attachPoint the attachment source point.
	 */
	public void detachFramebufferRenderbuffer(AttachPoint attachPoint)
	{
		glFramebufferRenderbuffer(GL_FRAMEBUFFER, attachPoint.glVal, GL_RENDERBUFFER, 0);
		checkError();
	}

	/**
	 * Unbinds a FrameBuffer for rendering.
	 * The current draw buffer will then be the default target buffer.
	 */
	public void unsetFramebuffer()
	{
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
		checkError();
	}

	/**
	 * Starts a conditional render.
	 * @param query the query object to use.
	 * @param waitType the wait type for query results.
	 * @throws IllegalStateException if a conditional render is already active.
	 */
	public void startConditionalRender(OGLQuery query, QueryWaitType waitType)
	{
		if (conditionalRenderActive)
			throw new IllegalStateException("A conditional render is already active.");
		
		glBeginConditionalRender(query.getName(), waitType.glValue);
		conditionalRenderActive = true;
		checkError();
	}
	
	/**
	 * Ends a conditional render.
	 * @throws IllegalStateException if a conditional render is not active.
	 */
	public void endConditionalRender()
	{
		if (!conditionalRenderActive)
			throw new IllegalStateException("A conditional render is not active.");
		
		glEndConditionalRender();
		conditionalRenderActive = false;
		checkError();
	}
	
	/**
	 * Starts a transform feedback.
	 * Requires batch calling for draw, an attached geometry program, 
	 * and a bound {@link BufferTargetType#TRANSFORM_FEEDBACK} buffer for the results.
	 * @param primitiveMode the primitive draw mode.
	 * @see #attachProgramShaders(OGLProgram, OGLProgramShader...)
	 * @see #setProgram(OGLProgram)
	 * @see #drawGeometryArray(GeometryType, int, int)
	 * @see #drawGeometryElements(GeometryType, DataType, int, int)
	 * @see #setBuffer(BufferTargetType, OGLBuffer)
	 */
	public void startTransformFeedback(PrimitiveMode primitiveMode)
	{
		if (transformFeedbackActive)
			throw new IllegalStateException("A transform feedback is already active.");
		
		glBeginTransformFeedback(primitiveMode.glValue);
		checkError();
		transformFeedbackActive = true;
	}
	
	/**
	 * Ends a transform feedback.
	 */
	public void endTransformFeedback()
	{
		if (!transformFeedbackActive)
			throw new IllegalStateException("A transform feedback is not active.");
		
		glEndTransformFeedback();
		checkError();
		transformFeedbackActive = false;
	}
	
}
