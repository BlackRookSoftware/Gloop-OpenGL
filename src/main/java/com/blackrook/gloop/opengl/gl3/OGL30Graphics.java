/*******************************************************************************
 * Copyright (c) 2021 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at 
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gloop.opengl.gl3;

import com.blackrook.gloop.opengl.gl2.OGL21Graphics;
import com.blackrook.gloop.opengl.exception.GraphicsException;
import com.blackrook.gloop.opengl.gl1.OGLTexture;
import com.blackrook.gloop.opengl.gl3.enums.AttachPoint;
import com.blackrook.gloop.opengl.gl3.enums.RenderbufferFormat;

import java.nio.IntBuffer;
import java.util.Objects;

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
			this.maxRenderBufferSize = getInt(GL_MAX_RENDERBUFFER_SIZE);
			this.maxRenderBufferColorAttachments = getInt(GL_MAX_COLOR_ATTACHMENTS);
		}
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
	 * Generates mipmaps on-demand internally for the current 1D texture target.
	 */
	public void generateMipmapTexture1D()
	{
		glGenerateMipmap(GL_TEXTURE_1D);
	}

	/**
	 * Generates mipmaps on-demand internally for the current 1D texture array target.
	 */
	public void generateMipmapTexture1DArray()
	{
		glGenerateMipmap(GL_TEXTURE_1D_ARRAY);
	}

	/**
	 * Generates mipmaps on-demand internally for the current 2D texture target.
	 */
	public void generateMipmapTexture2D()
	{
		glGenerateMipmap(GL_TEXTURE_2D);
	}

	/**
	 * Generates mipmaps on-demand internally for the current 2D texture array target.
	 */
	public void generateMipmapTexture2DArray()
	{
		glGenerateMipmap(GL_TEXTURE_2D_ARRAY);
	}

	/**
	 * Generates mipmaps on-demand internally for the current texture cube target.
	 */
	public void generateMipmapTextureCube()
	{
		glGenerateMipmap(GL_TEXTURE_CUBE_MAP);
	}

	/**
	 * Generates mipmaps on-demand internally for the current 3D texture target.
	 */
	public void generateMipmapTexture3D()
	{
		glGenerateMipmap(GL_TEXTURE_3D);
	}

	/**
	 * Sets a uniform unsigned integer value on the currently-bound shader.
	 * @param locationId the uniform location.
	 * @param value the value to set.
	 */
	public void setShaderUniformUnsignedInt(int locationId, int value)
	{
		glUniform1ui(locationId, value);
	}

	/**
	 * Sets a uniform unsigned integer value array on the currently-bound shader.
	 * @param locationId the uniform location.
	 * @param values the values to set.
	 */
	public void setShaderUniformUnsignedIntArray(int locationId, int ... values)
	{
		glUniform1uiv(locationId, values);
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
		}
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
	}

	/**
	 * Unbinds a FrameRenderBuffer from the current context.
	 */
	public void unsetRenderbuffer()
	{
		glBindRenderbuffer(GL_RENDERBUFFER, 0);
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
		getError();
	}

	/**
	 * Detaches a texture from this frame buffer.
	 * @param attachPoint the attachment source point.
	 */
	public void detachFramebufferTexture2D(AttachPoint attachPoint)
	{
		clearError();
		glFramebufferTexture2D(GL_FRAMEBUFFER, attachPoint.glVal, GL_TEXTURE_2D, 0, 0);
		getError();
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
		getError();
	}

	/**
	 * Detaches a render buffer from the current frame buffer.
	 * @param attachPoint the attachment source point.
	 */
	public void detachFramebufferRenderbuffer(AttachPoint attachPoint)
	{
		clearError();
		glFramebufferRenderbuffer(GL_FRAMEBUFFER, attachPoint.glVal, GL_RENDERBUFFER, 0);
		getError();
	}

	/**
	 * Unbinds a FrameBuffer for rendering.
	 * The current buffer will then be the default target buffer.
	 */
	public void unsetFramebuffer()
	{
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
	}

}
