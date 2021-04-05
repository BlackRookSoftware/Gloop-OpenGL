/*******************************************************************************
 * Copyright (c) 2021 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at 
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gloop.opengl.gl3;

import com.blackrook.gloop.opengl.gl2.OGL21Graphics;
import com.blackrook.gloop.opengl.gl2.OGLProgram;
import com.blackrook.gloop.opengl.gl2.OGLProgramShader;
import com.blackrook.gloop.opengl.OGLVersion;
import com.blackrook.gloop.opengl.enums.AttachPoint;
import com.blackrook.gloop.opengl.enums.RenderbufferFormat;
import com.blackrook.gloop.opengl.enums.ShaderType;
import com.blackrook.gloop.opengl.enums.TextureTargetType;
import com.blackrook.gloop.opengl.exception.GraphicsException;
import com.blackrook.gloop.opengl.gl1.OGLTexture;

import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

import org.lwjgl.system.MemoryStack;

import static org.lwjgl.opengl.GL11.GL_EXTENSIONS;
import static org.lwjgl.opengl.GL11.glGetString;
import static org.lwjgl.opengl.GL30.*;

/**
 * OpenGL 3.0 Graphics Implementation.
 * @author Matthew Tropiano
 * TODO: Make TextureBuilder for this version (better mipmap gen).
 */
public class OGL30Graphics extends OGL21Graphics
{
	public OGL30Graphics(boolean core)
	{
		super(core);
	}

	protected class Info30 extends Info20
	{
		protected Info30()
		{
			super();
			
			String extlist;
			if ((extlist = glGetString(GL_EXTENSIONS)) != null)
			{
				extensions.addAll(Arrays.asList(extlist.split("\\s+")));
			}
			else
			{
				int extensionCount = glGetInteger(GL_NUM_EXTENSIONS);
				for (int i = 0; i < extensionCount; i++)
					extensions.add(glGetStringi(GL_EXTENSIONS, i));
			}
			refreshExtensions();
			
			this.maxVertexAttribs = getInt(GL_MAX_VERTEX_ATTRIBS);
			this.maxRenderBufferSize = getInt(GL_MAX_RENDERBUFFER_SIZE);
			this.maxRenderBufferColorAttachments = getInt(GL_MAX_COLOR_ATTACHMENTS);
		}
	}
	
	/**
	 * Shader builder used for OpenGL 3.0.  
	 */
	public static class OGL30ShaderBuilder extends OGLShaderBuilderAbstract<OGL30Graphics>
	{
		protected OGL30ShaderBuilder(OGL30Graphics gl)
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
						if (ps != null) ps.destroy();
					}
				}
				
				for (Map.Entry<String, Integer> entry : attributeLocationBindings.entrySet())
					gl.setProgramVertexAttribLocation(out, entry.getKey(), entry.getValue());
				for (Map.Entry<String, Integer> entry : fragmentDataBindings.entrySet())
					gl.setProgramFragmentDataLocation(out, entry.getKey(), entry.getValue());
				
				gl.linkProgram(out);
				
			} catch (Exception e) {
				out.destroy();
				for (OGLProgramShader ps : list)
					ps.destroy();
				throw e;
			}
			return out;
		}
		
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
	 * @return the new object.
	 */
	public OGLVertexArrayState createVertexArrayState()
	{
		return new OGLVertexArrayState();
	}
	
	/**
	 * Sets the current vertex array state, which also restores all of the
	 * vertex attribute pointer and buffer target bindings associated with it.
	 * The following is saved by a vertex array state:
	 * <ul>
	 * <li> {@link #setVertexAttribEnabled(int, boolean)}
	 * <li> {@link #setVertexAttribBufferPointer(int, com.blackrook.gloop.opengl.enums.DataType, boolean, int, int, int)}
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
	 * If incomplete, this throws a GraphicsException with the error message.
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
		clearError();
		glFramebufferTexture2D(GL_FRAMEBUFFER, attachPoint.glVal, GL_TEXTURE_2D, texture.getName(), 0);
		checkError();
	}

	/**
	 * Detaches a texture from this frame buffer.
	 * @param attachPoint the attachment source point.
	 */
	public void detachFramebufferTexture2D(AttachPoint attachPoint)
	{
		clearError();
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
		clearError();
		glFramebufferRenderbuffer(GL_FRAMEBUFFER, attachPoint.glVal, GL_RENDERBUFFER, renderBuffer.getName());
		checkError();
	}

	/**
	 * Detaches a render buffer from the current frame buffer.
	 * @param attachPoint the attachment source point.
	 */
	public void detachFramebufferRenderbuffer(AttachPoint attachPoint)
	{
		clearError();
		glFramebufferRenderbuffer(GL_FRAMEBUFFER, attachPoint.glVal, GL_RENDERBUFFER, 0);
		checkError();
	}

	/**
	 * Unbinds a FrameBuffer for rendering.
	 * The current buffer will then be the default target buffer.
	 */
	public void unsetFramebuffer()
	{
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
		checkError();
	}

}
