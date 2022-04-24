/*******************************************************************************
 * Copyright (c) 2021-2022 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at 
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gloop.opengl.gl1;

import com.blackrook.gloop.opengl.OGLVersion;
import com.blackrook.gloop.opengl.enums.AccessType;
import com.blackrook.gloop.opengl.enums.BufferTargetType;
import com.blackrook.gloop.opengl.enums.CachingHint;
import com.blackrook.gloop.opengl.enums.DataType;
import com.blackrook.gloop.opengl.enums.FogCoordinateType;
import com.blackrook.gloop.opengl.exception.GraphicsException;
import com.blackrook.gloop.opengl.util.GeometryBuilder;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import static org.lwjgl.opengl.GL15.*;

/**
 * OpenGL 1.5 Graphics Implementation.
 * @author Matthew Tropiano
 */
public class OGL15Graphics extends OGL14Graphics
{
	/**
	 * Geometry builder used for OpenGL 1.5.  
	 */
	public static class OGL15GeometryBuilder extends GeometryBuilder.Abstract<OGL15Graphics>
	{
		protected OGL15GeometryBuilder(OGL15Graphics gl, int vertices, int ... attributeSizes)
		{
			super(gl, vertices, attributeSizes);
		}

		@Override
		public OGLBuffer create()
		{
			OGLBuffer out = gl.createBuffer(); 
			gl.setBuffer(BufferTargetType.GEOMETRY, out);
			gl.setBufferData(BufferTargetType.GEOMETRY, CachingHint.STATIC_DRAW, buffer);
			gl.unsetBuffer(BufferTargetType.GEOMETRY);
			return out;
		}

	}
	
	/** Current buffer binding map. */
	private Map<BufferTargetType, OGLBuffer> currentBuffer;

	// Create OpenGL 1.5 context.
	public OGL15Graphics(boolean core)
	{
		super(core);
		this.currentBuffer = null;
	}
	
	@Override
	public OGLVersion getVersion()
	{
		return OGLVersion.GL15;
	}
	
	/**
	 * Gets the current buffer for a binding target.
	 * @param type the binding type.
	 * @return the current buffer, or null if no current.
	 */
	protected OGLBuffer getCurrentBufferState(BufferTargetType type)
	{
		if (currentBuffer == null)
			return null;
		else
			return currentBuffer.get(type);
	}
	
	/**
	 * Sets the current texture for a binding target.
	 * @param type the binding type.
	 * @param buffer the buffer to set.
	 */
	protected void setCurrentBufferState(BufferTargetType type, OGLBuffer buffer)
	{
		if (currentBuffer == null)
			currentBuffer = new TreeMap<>();
		currentBuffer.put(type, buffer);
	}
	
	@Override
	protected void endFrame()
	{
	    // Clean up abandoned objects.
	    OGLQuery.destroyUndeleted();
	    OGLBuffer.destroyUndeleted();
	    super.endFrame();
	}

	/**
	 * Sets the origin of the calculation of the fog coordinate value that
	 * dictates "where" in the fog it is.
	 * @param coord the coordinate type.
	 */
	public void setFogCoordinateSource(FogCoordinateType coord)
	{
		checkNonCore();
		glFogi(GL_FOG_COORD_SRC, coord.glValue);
	}

	/**
	 * Creates a new sample query.
	 * @return a new sample query object.
	 * @throws GraphicsException if the object could not be created.
	 */
	public OGLQuery createSampleQuery()
	{
		return new OGLQuery(GL_SAMPLES_PASSED);
	}

	/**
	 * Creates a new geometry builder.
	 * The internal size of the buffer (number of float elements) is <code>vertices</code> times the sum of all of
	 * the attribute sizes, which would house the whole object.
	 * <p> This geometry builder aids in building buffer objects, and its
	 * {@link GeometryBuilder#create()} method will bind a new buffer to the {@link BufferTargetType#GEOMETRY} target,
	 * send the data, unbind the target, and return the new object.
	 * <p> 
	 * For example, if this is a builder of a four-vertex mesh with 3D spatial coordinates and an RGBA color,
	 * the constructor for this 4-vertex, 2 attribute builder is:
	 * <pre>GeometryBuilder.start(4, 3, 4)</pre>
	 * ...and the resultant buffer capacity is 28 (<code>4 * (3 + 4)</code>).
	 * @param vertices the number of individual vertices or attribute sets.
	 * @param attributeSizes the list of attribute sizes in components.
	 * @return the new builder.
	 */
	public GeometryBuilder createGeometryBuilder(int vertices, int ... attributeSizes)
	{
		return new OGL15GeometryBuilder(this, vertices, attributeSizes);
	}

	/**
	 * Creates a new buffer object.
	 * @return a new, uninitialized buffer object.
	 * @throws GraphicsException if the object could not be created.
	 */
	public OGLBuffer createBuffer()
	{
		return new OGLBuffer();
	}

	/**
	 * Gets the currently bound buffer for a binding target. 
	 * @param type the buffer binding target.
	 * @return the currently bound buffer, or null if no bound buffer to that target.
	 */
	public OGLBuffer getBuffer(BufferTargetType type)
	{
		return getCurrentBufferState(type);
	}
	
	/**
	 * Binds a buffer to the current context.
	 * @param type the buffer type to bind.
	 * @param buffer the buffer to bind. Null unbinds the currently bound buffer type.
	 */
	public void setBuffer(BufferTargetType type, OGLBuffer buffer)
	{
		Objects.requireNonNull(type);
		Objects.requireNonNull(buffer);
		checkFeatureVersion(type);
		glBindBuffer(type.glValue, buffer.getName());
		setCurrentBufferState(type, buffer);
	}

	/**
	 * Sets the capacity of the current buffer (sends no data).
	 * @param type the buffer type binding.
	 * @param dataType the data type.
	 * @param cachingHint the caching hint on this buffer's data.
	 * @param elements the amount of elements of the data type.
	 */
	public void setBufferCapacity(BufferTargetType type, DataType dataType, CachingHint cachingHint, int elements)
	{
		clearError();
		checkFeatureVersion(type);
		glBufferData(type.glValue, elements * dataType.size, cachingHint.glValue);
		checkError();
	}

	/**
	 * Sets the data of the current buffer.
	 * @param type the buffer type binding.
	 * @param cachingHint the caching hint on this buffer's data.
	 * @param data the data to send.
	 */
	public void setBufferData(BufferTargetType type, CachingHint cachingHint, ByteBuffer data)
	{
		if (!data.isDirect())
			throw new GraphicsException("Data must be a direct buffer."); 
		clearError();
		checkFeatureVersion(type);
		glBufferData(type.glValue, data, cachingHint.glValue);
		checkError();
	}

	/**
	 * Sets the data of the current buffer.
	 * @param type the buffer type binding.
	 * @param cachingHint the caching hint on this buffer's data.
	 * @param data the data to send.
	 */
	public void setBufferData(BufferTargetType type, CachingHint cachingHint, ShortBuffer data)
	{
		if (!data.isDirect())
			throw new GraphicsException("Data must be a direct buffer."); 
		clearError();
		checkFeatureVersion(type);
		glBufferData(type.glValue, data, cachingHint.glValue);
		checkError();
	}

	/**
	 * Sets the data of the current buffer.
	 * @param type the buffer type binding.
	 * @param cachingHint the caching hint on this buffer's data.
	 * @param data the data to send.
	 */
	public void setBufferData(BufferTargetType type, CachingHint cachingHint, IntBuffer data)
	{
		if (!data.isDirect())
			throw new GraphicsException("Data must be a direct buffer."); 
		clearError();
		checkFeatureVersion(type);
		glBufferData(type.glValue, data, cachingHint.glValue);
		checkError();
	}

	/**
	 * Sets the data of the current buffer.
	 * @param type the buffer type binding.
	 * @param cachingHint the caching hint on this buffer's data.
	 * @param data the data to send.
	 */
	public void setBufferData(BufferTargetType type, CachingHint cachingHint, FloatBuffer data)
	{
		if (!data.isDirect())
			throw new GraphicsException("Data must be a direct buffer."); 
		clearError();
		checkFeatureVersion(type);
		glBufferData(type.glValue, data, cachingHint.glValue);
		checkError();
	}

	/**
	 * Sets the data of the current buffer.
	 * @param type the buffer type binding.
	 * @param cachingHint the caching hint on this buffer's data.
	 * @param data the data to send.
	 */
	public void setBufferData(BufferTargetType type, CachingHint cachingHint, LongBuffer data)
	{
		if (!data.isDirect())
			throw new GraphicsException("Data must be a direct buffer."); 
		clearError();
		checkFeatureVersion(type);
		glBufferData(type.glValue, data, cachingHint.glValue);
		checkError();
	}

	/**
	 * Sets the data of the current buffer.
	 * @param type the buffer type binding.
	 * @param cachingHint the caching hint on this buffer's data.
	 * @param data the data to send.
	 */
	public void setBufferData(BufferTargetType type, CachingHint cachingHint, DoubleBuffer data)
	{
		if (!data.isDirect())
			throw new GraphicsException("Data must be a direct buffer."); 
		clearError();
		checkFeatureVersion(type);
		glBufferData(type.glValue, data, cachingHint.glValue);
		checkError();
	}

	/**
	 * Sets a subsection of data to the current buffer.
	 * @param type the buffer type binding.
	 * @param offset the offset into the buffer to copy.
	 * @param data the data to send.
	 */
	public void setBufferSubData(BufferTargetType type, int offset, ByteBuffer data)
	{
		if (!data.isDirect())
			throw new GraphicsException("Data must be a direct buffer."); 
		clearError();
		checkFeatureVersion(type);
		glBufferSubData(type.glValue, offset, data);
		checkError();
	}

	/**
	 * Sets a subsection of data to the current buffer.
	 * @param type the buffer type binding.
	 * @param offset the offset into the buffer to copy.
	 * @param data the data to send.
	 */
	public void setBufferSubData(BufferTargetType type, int offset, ShortBuffer data)
	{
		if (!data.isDirect())
			throw new GraphicsException("Data must be a direct buffer."); 
		clearError();
		checkFeatureVersion(type);
		glBufferSubData(type.glValue, offset, data);
		checkError();
	}

	/**
	 * Sets a subsection of data to the current buffer.
	 * @param type the buffer type binding.
	 * @param offset the offset into the buffer to copy.
	 * @param data the data to send.
	 */
	public void setBufferSubData(BufferTargetType type, int offset, IntBuffer data)
	{
		if (!data.isDirect())
			throw new GraphicsException("Data must be a direct buffer."); 
		clearError();
		checkFeatureVersion(type);
		glBufferSubData(type.glValue, offset, data);
		checkError();
	}

	/**
	 * Sets a subsection of data to the current buffer.
	 * @param type the buffer type binding.
	 * @param offset the offset into the buffer to copy.
	 * @param data the data to send.
	 */
	public void setBufferSubData(BufferTargetType type, int offset, FloatBuffer data)
	{
		if (!data.isDirect())
			throw new GraphicsException("Data must be a direct buffer."); 
		clearError();
		checkFeatureVersion(type);
		glBufferSubData(type.glValue, offset, data);
		checkError();
	}

	/**
	 * Sets a subsection of data to the current buffer.
	 * @param type the buffer type binding.
	 * @param offset the offset into the buffer to copy.
	 * @param data the data to send.
	 */
	public void setBufferSubData(BufferTargetType type, int offset, LongBuffer data)
	{
		if (!data.isDirect())
			throw new GraphicsException("Data must be a direct buffer."); 
		clearError();
		checkFeatureVersion(type);
		glBufferSubData(type.glValue, offset, data);
		checkError();
	}

	/**
	 * Sets a subsection of data to the current buffer.
	 * @param type the buffer type binding.
	 * @param offset the offset into the buffer to copy.
	 * @param data the data to send.
	 */
	public void setBufferSubData(BufferTargetType type, int offset, DoubleBuffer data)
	{
		if (!data.isDirect())
			throw new GraphicsException("Data must be a direct buffer."); 
		clearError();
		checkFeatureVersion(type);
		glBufferSubData(type.glValue, offset, data);
		checkError();
	}

	/**
	 * Maps the internal data of the current OGLBuffer to a local buffer for
	 * quick modification/read. 
	 * <p>
	 * Please note that the returned Buffer is special in how 
	 * it is used by OpenGL according to the AccessType.
	 * </p>
	 * @param type the binding target type.
	 * @param accessType an access hint for the returned buffer.
	 * @return a buffer suitable for application use.
	 */
	public ByteBuffer mapByteBuffer(BufferTargetType type, AccessType accessType)
	{
		checkFeatureVersion(type);
		return glMapBuffer(type.glValue, accessType.glValue);
	}

	/**
	 * Maps the internal data of the current OGLBuffer to a local buffer for
	 * quick modification/read. 
	 * <p>
	 * Please note that the returned Buffer is special in how 
	 * it is used by OpenGL according to the AccessType.
	 * </p>
	 * @param type the binding target type.
	 * @param accessType an access hint for the returned buffer.
	 * @return a buffer suitable for application use.
	 */
	public ShortBuffer mapShortBuffer(BufferTargetType type, AccessType accessType)
	{
		checkFeatureVersion(type);
		return mapByteBuffer(type, accessType).asShortBuffer();
	}

	/**
	 * Maps the internal data of the current OGLBuffer to a local buffer for
	 * quick modification/read. 
	 * <p>
	 * Please note that the returned Buffer is special in how 
	 * it is used by OpenGL according to the AccessType.
	 * </p>
	 * @param type the binding target type.
	 * @param accessType an access hint for the returned buffer.
	 * @return a buffer suitable for application use.
	 */
	public IntBuffer mapIntBuffer(BufferTargetType type, AccessType accessType)
	{
		checkFeatureVersion(type);
		return mapByteBuffer(type, accessType).asIntBuffer();
	}

	/**
	 * Maps the internal data of the current OGLBuffer to a local buffer for
	 * quick modification/read. 
	 * <p>
	 * Please note that the returned Buffer is special in how 
	 * it is used by OpenGL according to the AccessType.
	 * </p>
	 * @param type the binding target type.
	 * @param accessType an access hint for the returned buffer.
	 * @return a buffer suitable for application use.
	 */
	public LongBuffer mapLongBuffer(BufferTargetType type, AccessType accessType)
	{
		checkFeatureVersion(type);
		return mapByteBuffer(type, accessType).asLongBuffer();
	}

	/**
	 * Maps the internal data of the current OGLBuffer to a local buffer for
	 * quick modification/read. 
	 * <p>
	 * Please note that the returned Buffer is special in how 
	 * it is used by OpenGL according to the AccessType.
	 * </p>
	 * @param type the binding target type.
	 * @param accessType an access hint for the returned buffer.
	 * @return a buffer suitable for application use.
	 */
	public FloatBuffer mapFloatBuffer(BufferTargetType type, AccessType accessType)
	{
		checkFeatureVersion(type);
		return mapByteBuffer(type, accessType).asFloatBuffer();
	}

	/**
	 * Maps the internal data of the current OGLBuffer to a local buffer for
	 * quick modification/read. 
	 * <p>
	 * Please note that the returned Buffer is special in how 
	 * it is used by OpenGL according to the AccessType.
	 * </p>
	 * @param type the binding target type.
	 * @param accessType an access hint for the returned buffer.
	 * @return a buffer suitable for application use.
	 */
	public DoubleBuffer mapDoubleBuffer(BufferTargetType type, AccessType accessType)
	{
		checkFeatureVersion(type);
		return mapByteBuffer(type, accessType).asDoubleBuffer();
	}

	/**
	 * Unmaps a buffer after it has been mapped and manipulated/read by the calling
	 * client application. Please note that the Buffer that was mapped from this OGLBuffer
	 * will be completely invalidated upon unmapping it.
	 * @param type the binding target type.
	 * @return true if unmap successful, false if data corruption occurred on unmap.
	 */
	public boolean unmapBuffer(BufferTargetType type)
	{
		checkFeatureVersion(type);
		return glUnmapBuffer(type.glValue);
	}

	/**
	 * Unbinds the current buffer from a target.
	 * @param type the binding target type.
	 */
	public void unsetBuffer(BufferTargetType type)
	{
		checkFeatureVersion(type);
		glBindBuffer(type.glValue, 0);
		setCurrentBufferState(type, null);
	}

}
