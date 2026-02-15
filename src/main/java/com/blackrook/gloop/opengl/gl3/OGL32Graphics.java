/*******************************************************************************
 * Copyright (c) 2021-2024 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at 
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gloop.opengl.gl3;

import com.blackrook.gloop.opengl.OGLVersion;
import com.blackrook.gloop.opengl.enums.SyncResultType;
import com.blackrook.gloop.opengl.OGLSystem.Options;

import static org.lwjgl.opengl.GL32.*;


/**
 * OpenGL 3.2 Graphics Implementation.
 * @author Matthew Tropiano
 */
public class OGL32Graphics extends OGL31Graphics
{
	protected class Info32 extends Info31
	{
		protected Info32()
		{
			super();
			this.maxVertexOutputComponents = getInt(GL_MAX_VERTEX_OUTPUT_COMPONENTS);
			this.maxGeometryInputComponents = getInt(GL_MAX_GEOMETRY_INPUT_COMPONENTS);
			this.maxGeometryOutputComponents = getInt(GL_MAX_GEOMETRY_OUTPUT_COMPONENTS);
			this.maxFragmentInputComponents = getInt(GL_MAX_FRAGMENT_INPUT_COMPONENTS);
		}
	}
	
	public OGL32Graphics(Options options, boolean core)
	{
		super(options, core);
	}

	@Override
	public OGLVersion getVersion()
	{
		return OGLVersion.GL32;
	}

	@Override
	protected Info createInfo()
	{
		return new Info32();
	}
	
	@Override
	protected void endFrame() 
	{
		// Clean up abandoned objects.
		handleUndeletedObjects(OGLSync.class, OGLSync.destroyUndeleted());
		super.endFrame();
	}
	
	/**
	 * Retrieves the location of a sample as two pixel coordinates, referring to
	 * the X and Y locations of the GL pixel space of the sample.
	 * @param sampleIndex the index of the sample.
	 * @param outArray the output array for the result. Must be length 2 or greater.
	 */
	public void getMultisample(int sampleIndex, float[] outArray)
	{
		glGetMultisamplefv(GL_SAMPLE_POSITION, sampleIndex, outArray);
	}

	/**
	 * Creates a new fence synching object for OPENGL Sync operations.
	 * @return a new OGLSync object.
	 */
	public OGLSync createFenceSync()
	{
		return new OGLSync(glFenceSync(GL_SYNC_GPU_COMMANDS_COMPLETE, 0));
	}
	
	/**
	 * Destroys a sync object.
	 * @param sync the object to destroy.
	 */
	public void destroySync(OGLSync sync)
	{
		destroyObject(sync);
	}
	
	/**
	 * Awaits signal from the GL server that a sync object is signaled.
	 * @param sync the sync object.
	 */
	public void awaitSync(OGLSync sync)
	{
		glWaitSync(sync.getLongName(), 0, GL_TIMEOUT_IGNORED);
	}
	
	/**
	 * Awaits signal from OpenGL that OpenGL has flushed all of its commands.
	 * @param sync the sync object.
	 * @param timeoutNanos the time in nanoseconds to wait, maximum.
	 * @return the result type from the wait.
	 */
	public SyncResultType awaitClentFlushedCommandsSync(OGLSync sync, long timeoutNanos)
	{
		int result = glClientWaitSync(sync.getLongName(), GL_SYNC_FLUSH_COMMANDS_BIT, timeoutNanos);
		switch (result)
		{
			case GL_ALREADY_SIGNALED:
				return SyncResultType.ALREADY_SIGNALED;
			case GL_TIMEOUT_EXPIRED:
				return SyncResultType.TIMEOUT_EXPIRED;
			case GL_CONDITION_SATISFIED:
				return SyncResultType.CONDITION_SATISFIED;
			default:
			case GL_WAIT_FAILED:
				return SyncResultType.WAIT_FAILED;
		}
	}
	
}
