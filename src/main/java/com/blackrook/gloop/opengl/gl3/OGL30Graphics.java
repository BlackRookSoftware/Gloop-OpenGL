/*******************************************************************************
 * Copyright (c) 2021 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at 
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gloop.opengl.gl3;

import com.blackrook.gloop.opengl.gl2.OGL21Graphics;
import com.blackrook.gloop.opengl.gl2.OGLShader;
import com.blackrook.gloop.opengl.OGLVersion;
import com.blackrook.gloop.opengl.enums.AttachPoint;
import com.blackrook.gloop.opengl.enums.ColorFormat;
import com.blackrook.gloop.opengl.enums.RenderbufferFormat;
import com.blackrook.gloop.opengl.enums.TextureFormat;
import com.blackrook.gloop.opengl.enums.TextureMagFilter;
import com.blackrook.gloop.opengl.enums.TextureMinFilter;
import com.blackrook.gloop.opengl.enums.TextureWrapType;
import com.blackrook.gloop.opengl.exception.GraphicsException;
import com.blackrook.gloop.opengl.gl1.OGLTexture;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Objects;

import org.lwjgl.system.MemoryStack;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glCopyTexImage2D;
import static org.lwjgl.opengl.GL11.glCopyTexSubImage2D;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameterf;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL11.glTexSubImage2D;
import static org.lwjgl.opengl.GL30.*;

/**
 * OpenGL 3.0 Graphics Implementation.
 * @author Matthew Tropiano
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
			this.maxVertexAttribs = getInt(GL_MAX_VERTEX_ATTRIBS);
			this.maxRenderBufferSize = getInt(GL_MAX_RENDERBUFFER_SIZE);
			this.maxRenderBufferColorAttachments = getInt(GL_MAX_COLOR_ATTACHMENTS);
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
	 * Gets the currently bound 1D texture array. 
	 * @return the texture, or null if no bound texture.
	 */
	public OGLTexture getTexture1DArray()
	{
		return getCurrentActiveTextureState(getTextureUnit(), GL_TEXTURE_1D_ARRAY);
	}

	/**
	 * Sets if 1D array texturing is enabled or not.
	 * @param enabled true to enable, false to disable.
	 */
	public void setTexture1DArrayEnabled(boolean enabled)
	{
		setFlag(GL_TEXTURE_1D_ARRAY, enabled);
	}

	/**
	 * Binds a 1D texture array object to the current active texture unit.
	 * @param texture the texture to bind.
	 */
	public void setTexture1DArray(OGLTexture texture)
	{
		Objects.requireNonNull(texture);
		glBindTexture(GL_TEXTURE_1D_ARRAY, texture.getName());
		setCurrentActiveTextureState(getTextureUnit(), GL_TEXTURE_1D_ARRAY, texture);
	}

	/**
	 * Sets the current filtering for the current 1D texture array.
	 * @param minFilter the minification filter.
	 * @param magFilter the magnification filter.
	 */
	public void setTexture1DArrayFiltering(TextureMinFilter minFilter, TextureMagFilter magFilter)
	{
		glTexParameteri(GL_TEXTURE_1D_ARRAY, GL_TEXTURE_MAG_FILTER, magFilter.glid);
		glTexParameteri(GL_TEXTURE_1D_ARRAY, GL_TEXTURE_MIN_FILTER, minFilter.glid);
	}

	/**
	 * Sets the current filtering for the current 1D texture array.
	 * @param minFilter the minification filter.
	 * @param magFilter the magnification filter.
	 * @param anisotropy the anisotropic filtering (2.0 or greater to enable, 1.0 is "off").
	 */
	public void setTexture1DArrayFiltering(TextureMinFilter minFilter, TextureMagFilter magFilter, float anisotropy)
	{
		glTexParameteri(GL_TEXTURE_1D_ARRAY, GL_TEXTURE_MAG_FILTER, magFilter.glid);
		glTexParameteri(GL_TEXTURE_1D_ARRAY, GL_TEXTURE_MIN_FILTER, minFilter.glid);
		
		if (getInfo().supportsTextureAnisotropy())
		{
			anisotropy = Math.max(1.0f, Math.min(getInfo().getMaxTextureAnisotropy(), anisotropy));
			glTexParameterf(GL_TEXTURE_1D_ARRAY, 0x084FE, anisotropy);
		}
	}

	/**
	 * Sets the current wrapping for the current 1D texture array.
	 * @param wrapS the wrapping mode, S-axis.
	 */
	public void setTexture1DArrayWrapping(TextureWrapType wrapS)
	{
		checkFeatureVersion(wrapS);
		glTexParameteri(GL_TEXTURE_1D_ARRAY, GL_TEXTURE_WRAP_S, wrapS.glValue);
	}

	/**
	 * Sends a texture into OpenGL's memory for the current 1D texture array at the topmost mipmap level.
	 * @param imageData the image to send.
	 * @param colorFormat the pixel storage format of the buffer data.
	 * @param format the internal format.
	 * @param width the texture width in texels.
	 * @param height the texture height in texels (amount of textures).
	 * @param border the texel border to add, if any.
	 * @throws GraphicsException if the buffer provided is not direct.
	 */
	public void setTexture1DArrayData(ByteBuffer imageData, ColorFormat colorFormat, TextureFormat format, int width, int height, int border)
	{
		setTexture1DArrayData(imageData, colorFormat, format, 0, width, height, border);
	}

	/**
	 * Sends a texture into OpenGL's memory for the current 1D texture array.
	 * @param imageData the image to send.
	 * @param colorFormat the pixel storage format of the buffer data.
	 * @param format the internal format.
	 * @param texlevel the mipmapping level to copy this into (0 is topmost).
	 * @param width the texture width in texels.
	 * @param height the texture height in texels (amount of textures).
	 * @param border the texel border to add, if any.
	 * @throws GraphicsException if the buffer provided is not direct.
	 */
	public void setTexture1DArrayData(ByteBuffer imageData, ColorFormat colorFormat, TextureFormat format, int texlevel, int width, int height, int border)
	{
		if (width > getInfo().getMaxTextureSize() || height > getInfo().getMaxTextureSize())
			throw new GraphicsException("Texture is too large. Maximum size is " + getInfo().getMaxTextureSize() + " pixels.");
	
		if (!imageData.isDirect())
			throw new GraphicsException("Data must be a direct buffer."); 
		
		checkFeatureVersion(colorFormat);
		checkFeatureVersion(format);
	
		clearError();
		glTexImage2D(
			GL_TEXTURE_1D_ARRAY,
			texlevel,
			format.glValue, 
			width,
			height,
			border,
			colorFormat.glValue,
			GL_UNSIGNED_BYTE,
			imageData
		);
		getError();
	}

	/**
	 * Copies the contents of the current read frame buffer into the current 1D texture array target at the topmost mipmap level.
	 * @param format    the internal format.
	 * @param srcX		the screen-aligned x-coordinate of what to grab from the buffer (0 is the left side of the screen).
	 * @param srcY		the screen-aligned y-coordinate of what to grab from the buffer (0 is the bottom of the screen).
	 * @param width		the width of the screen in pixels to grab.
	 * @param height	the height of the screen in pixels to grab (amount of textures).
	 * @param border    the texel border to add, if any.
	 */
	public void setTexture1DArrayDataFromReadBuffer(TextureFormat format, int srcX, int srcY, int width, int height, int border)
	{
		setTexture1DArrayDataFromReadBuffer(format, 0, srcX, srcY, width, height, border);
	}

	/**
	 * Copies the contents of the current read frame buffer into the current 1D texture array target.
	 * @param format    the internal format.
	 * @param texlevel	the mipmapping level to copy this into (0 is topmost level).
	 * @param srcX		the screen-aligned x-coordinate of what to grab from the buffer (0 is the left side of the screen).
	 * @param srcY		the screen-aligned y-coordinate of what to grab from the buffer (0 is the bottom of the screen).
	 * @param width		the width of the screen in pixels to grab.
	 * @param height	the height of the screen in pixels to grab (amount of textures).
	 * @param border    the texel border to add, if any.
	 */
	public void setTexture1DArrayDataFromReadBuffer(TextureFormat format, int texlevel, int srcX, int srcY, int width, int height, int border)
	{
		checkFeatureVersion(format);
		glCopyTexImage2D(GL_TEXTURE_1D_ARRAY, format.glValue, texlevel, srcX, srcY, width, height, border);
	}

	/**
	 * Sends a subset of data to the currently-bound 1D texture array 
	 * already in OpenGL's memory at the topmost mipmap level.
	 * @param imageData the image to send.
	 * @param colorFormat the pixel storage format of the buffer data.
	 * @param width the texture width in texels.
	 * @param height the texture height in texels.
	 * @param xoffs the texel offset.
	 * @param yoffs the texel offset (starting texture index).
	 * @throws GraphicsException if the buffer provided is not direct.
	 */
	public void setTexture1DArraySubData(ByteBuffer imageData, ColorFormat colorFormat, int width, int height, int xoffs, int yoffs)
	{
		setTexture1DArraySubData(imageData, colorFormat, 0, width, height, xoffs, yoffs);
	}

	/**
	 * Sends a subset of data to the currently-bound 1D texture array already in OpenGL's memory.
	 * @param imageData the image to send.
	 * @param colorFormat the pixel storage format of the buffer data.
	 * @param texlevel	the mipmapping level to copy this into (0 is topmost).
	 * @param width the texture width in texels.
	 * @param height the texture height in texels (amount of textures).
	 * @param xoffs the texel offset.
	 * @param yoffs the texel offset (starting texture index).
	 * @throws GraphicsException if the buffer provided is not direct.
	 */
	public void setTexture1DArraySubData(ByteBuffer imageData, ColorFormat colorFormat, int texlevel, int width, int height, int xoffs, int yoffs)
	{
		if (!imageData.isDirect())
			throw new GraphicsException("Data must be a direct buffer."); 
	
		checkFeatureVersion(colorFormat);
	
		clearError();
		glTexSubImage2D(
			GL_TEXTURE_1D_ARRAY,
			texlevel,
			xoffs,
			yoffs,
			width,
			height,
			colorFormat.glValue,
			GL_UNSIGNED_BYTE,
			imageData
		);
		getError();
	}

	/**
	 * Copies the contents of the current read frame buffer into the 
	 * current 1D texture array target already in OpenGL's memory at the topmost mipmap level.
	 * @param xoffset	the offset in pixels on this texture (x-coordinate) to put this texture data.
	 * @param yoffset	the offset in pixels on this texture (y-coordinate) to put this texture data (starting texture index).
	 * @param srcX		the screen-aligned x-coordinate of what to grab from the buffer (0 is the left side of the screen).
	 * @param srcY		the screen-aligned y-coordinate of what to grab from the buffer (0 is the bottom of the screen).
	 * @param width		the width of the screen in pixels to grab.
	 * @param height	the height of the screen in pixels to grab (amount of textures).
	 */
	public void setTexture1DArraySubDataFromReadBuffer(int xoffset, int yoffset, int srcX, int srcY, int width, int height)
	{
		setTexture1DArraySubDataFromReadBuffer(0, xoffset, yoffset, srcX, srcY, width, height);
	}

	/**
	 * Copies the contents of the current read frame buffer into the 
	 * current 1D texture array target already in OpenGL's memory.
	 * @param texlevel	the mipmapping level to copy this into (0 is topmost level).
	 * @param xoffset	the offset in pixels on this texture (x-coordinate) to put this texture data.
	 * @param yoffset	the offset in pixels on this texture (y-coordinate) to put this texture data.
	 * @param srcX		the screen-aligned x-coordinate of what to grab from the buffer (0 is the left side of the screen).
	 * @param srcY		the screen-aligned y-coordinate of what to grab from the buffer (0 is the bottom of the screen).
	 * @param width		the width of the screen in pixels to grab.
	 * @param height	the height of the screen in pixels to grab (amount of textures).
	 */
	public void setTexture1DArraySubDataFromReadBuffer(int texlevel, int xoffset, int yoffset, int srcX, int srcY, int width, int height)
	{
		glCopyTexSubImage2D(GL_TEXTURE_1D_ARRAY, texlevel, xoffset, yoffset, srcX, srcY, width, height);
	}

	/**
	 * Unbinds a texture from the current 1D array target.
	 */
	public void unsetTexture1DArray()
	{
		glBindTexture(GL_TEXTURE_1D_ARRAY, 0);
		setCurrentActiveTextureState(getTextureUnit(), GL_TEXTURE_1D_ARRAY, null);
	}

	/**
	 * Gets the currently bound 3D texture. 
	 * @return the texture, or null if no bound texture.
	 */
	public OGLTexture getTexture2DArray()
	{
		return getCurrentActiveTextureState(getTextureUnit(), GL_TEXTURE_2D_ARRAY);
	}

	/**
	 * Sets if 2D array texturing is enabled or not.
	 * @param enabled true to enable, false to disable.
	 */
	public void setTexture2DArrayEnabled(boolean enabled)
	{
		setFlag(GL_TEXTURE_2D_ARRAY, enabled);
	}

	/**
	 * Binds a 3D texture object to the current active texture unit.
	 * @param texture the texture to bind.
	 */
	public void setTexture2DArray(OGLTexture texture)
	{
		Objects.requireNonNull(texture);
		glBindTexture(GL_TEXTURE_2D_ARRAY, texture.getName());
		setCurrentActiveTextureState(getTextureUnit(), GL_TEXTURE_2D_ARRAY, texture);
	}

	/**
	 * Sets the current filtering for the current 2D texture array.
	 * @param minFilter the minification filter.
	 * @param magFilter the magnification filter.
	 */
	public void setTexture2DArrayFiltering(TextureMinFilter minFilter, TextureMagFilter magFilter)
	{
		glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_MAG_FILTER, magFilter.glid);
		glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_MIN_FILTER, minFilter.glid);
	}

	/**
	 * Sets the current filtering for the current 2D texture array.
	 * @param minFilter the minification filter.
	 * @param magFilter the magnification filter.
	 * @param anisotropy the anisotropic filtering (2.0 or greater to enable, 1.0 is "off").
	 */
	public void setTexture2DArrayFiltering(TextureMinFilter minFilter, TextureMagFilter magFilter, float anisotropy)
	{
		glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_MAG_FILTER, magFilter.glid);
		glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_MIN_FILTER, minFilter.glid);
		
		if (getInfo().supportsTextureAnisotropy())
		{
			anisotropy = Math.max(1.0f, Math.min(getInfo().getMaxTextureAnisotropy(), anisotropy));
			glTexParameterf(GL_TEXTURE_2D_ARRAY, 0x084FE, anisotropy);
		}
	}

	/**
	 * Sets the current wrapping for the current 2D texture array.
	 * @param wrapS the wrapping mode, S-axis.
	 * @param wrapT the wrapping mode, T-axis.
	 */
	public void setTexture2DArrayWrapping(TextureWrapType wrapS, TextureWrapType wrapT)
	{
		checkFeatureVersion(wrapS);
		checkFeatureVersion(wrapT);
		glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_WRAP_S, wrapS.glValue);
		glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_WRAP_T, wrapT.glValue);
	}

	/**
	 * Sends a texture into OpenGL's memory for the current 2D texture array at the topmost mipmap level.
	 * @param imageData the image to send.
	 * @param colorFormat the pixel storage format of the buffer data.
	 * @param format the internal format.
	 * @param width the texture width in texels.
	 * @param height the texture height in texels.
	 * @param depth the texture depth in texels (amount of textures).
	 * @param border the texel border to add, if any.
	 * @throws GraphicsException if the buffer provided is not direct.
	 */
	public void setTexture2DArrayData(ByteBuffer imageData, ColorFormat colorFormat, TextureFormat format, int width, int height, int depth, int border)
	{
		setTexture2DArrayData(imageData, colorFormat, format, 0, width, height, depth, border);
	}

	/**
	 * Sends a texture into OpenGL's memory for the current 2D texture array.
	 * @param imageData the image to send.
	 * @param colorFormat the pixel storage format of the buffer data.
	 * @param format the internal format.
	 * @param texlevel the mipmapping level to copy this into (0 is topmost).
	 * @param width the texture width in texels.
	 * @param height the texture height in texels.
	 * @param depth the texture depth in texels (amount of textures).
	 * @param border the texel border to add, if any.
	 * @throws GraphicsException if the buffer provided is not direct.
	 */
	public void setTexture2DArrayData(ByteBuffer imageData, ColorFormat colorFormat, TextureFormat format, int texlevel, int width, int height, int depth, int border)
	{
		if (width > getInfo().getMaxTextureSize() || height > getInfo().getMaxTextureSize() || depth > getInfo().getMaxTextureSize())
			throw new GraphicsException("Texture is too large. Maximum size is " + getInfo().getMaxTextureSize() + " pixels.");
	
		if (!imageData.isDirect())
			throw new GraphicsException("Data must be a direct buffer."); 
		
		checkFeatureVersion(colorFormat);
		checkFeatureVersion(format);
	
		clearError();
		glTexImage3D(
			GL_TEXTURE_2D_ARRAY,
			texlevel,
			format.glValue, 
			width,
			height,
			depth,
			border,
			colorFormat.glValue,
			GL_UNSIGNED_BYTE,
			imageData
		);
		getError();
	}

	/**
	 * Sends a subset of data to the currently-bound 2D texture 
	 * already in OpenGL's memory at the topmost mipmap level.
	 * @param imageData the image to send.
	 * @param colorFormat the pixel storage format of the buffer data.
	 * @param width the texture width in texels.
	 * @param height the texture height in texels.
	 * @param depth the texture depth in texels (amount of textures).
	 * @param xoffs the texel offset.
	 * @param yoffs the texel offset.
	 * @param zoffs the texel offset (starting texture index).
	 * @throws GraphicsException if the buffer provided is not direct.
	 */
	public void setTexture2DArraySubData(ByteBuffer imageData, ColorFormat colorFormat, int width, int height, int depth, int xoffs, int yoffs, int zoffs)
	{
		setTexture2DArraySubData(imageData, colorFormat, 0, width, height, depth, xoffs, yoffs, zoffs);
	}

	/**
	 * Sends a subset of data to the currently-bound 2D texture already in OpenGL's memory.
	 * @param imageData the image to send.
	 * @param colorFormat the pixel storage format of the buffer data.
	 * @param texlevel	the mipmapping level to copy this into (0 is topmost).
	 * @param width the texture width in texels.
	 * @param height the texture height in texels.
	 * @param depth the texture depth in texels (amount of textures).
	 * @param xoffs the texel offset.
	 * @param yoffs the texel offset.
	 * @param zoffs the texel offset (starting texture index).
	 * @throws GraphicsException if the buffer provided is not direct.
	 */
	public void setTexture2DArraySubData(ByteBuffer imageData, ColorFormat colorFormat, int texlevel, int width, int depth, int height, int xoffs, int yoffs, int zoffs)
	{
		if (!imageData.isDirect())
			throw new GraphicsException("Data must be a direct buffer."); 
	
		checkFeatureVersion(colorFormat);
	
		clearError();
		glTexSubImage3D(
			GL_TEXTURE_2D_ARRAY,
			texlevel,
			xoffs,
			yoffs,
			zoffs,
			width,
			height,
			depth,
			colorFormat.glValue,
			GL_UNSIGNED_BYTE,
			imageData
		);
		getError();
	}

	/**
	 * Unbinds a 2D texture array from the current texture unit.
	 */
	public void unsetTexture2DArray()
	{
		glBindTexture(GL_TEXTURE_2D_ARRAY, 0);
		setCurrentActiveTextureState(getTextureUnit(), GL_TEXTURE_2D_ARRAY, null);
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
	 * <li> {@link #setVertexAttribArrayEnabled(int, boolean)}
	 * <li> {@link #setVertexAttribArrayPointer(int, com.blackrook.gloop.opengl.enums.DataType, boolean, int, int, int)}
	 * </ul>
	 * @param state the state to set.
	 */
	public void setVertexArrayState(OGLVertexArrayState state)
	{
		Objects.requireNonNull(state);
		glBindVertexArray(state.getName());
	}
	
	/**
	 * Unbinds a vertex array state from the current context.
	 */
	public void unsetVertexArrayState()
	{
		glBindVertexArray(0);
	}

	/**
	 * Sets the location of a fragment output.  
	 * @param shader the shader to set it on.
	 * @param colorIndex the color index (draw buffer index).
	 * @param name the name of the attribute.
	 */
	public void setFragmentDataLocation(OGLShader shader, int colorIndex, String name)
	{
		if (colorIndex >= getInfo().getMaxDrawBuffers())
			throw new GraphicsException("Color index cannot be greater than " + getInfo().getMaxDrawBuffers());
		glBindFragDataLocation(shader.getName(), colorIndex, name);
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
