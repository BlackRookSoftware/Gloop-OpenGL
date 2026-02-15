package com.blackrook.gloop.opengl.gl3;

import static org.lwjgl.opengl.GL32.glDeleteSync;

import com.blackrook.gloop.opengl.OGLObject;

public class OGLSync extends OGLObject
{
	/** List of OpenGL object ids that were not deleted properly. */
	protected static long[] UNDELETED_IDS;
	/** Amount of OpenGL object ids that were not deleted properly. */
	protected static int UNDELETED_LENGTH;
	
	static
	{
		UNDELETED_IDS = new long[32];
		UNDELETED_LENGTH = 0;
	}

	OGLSync(long syncId)
	{
		setLongName(syncId);
	}
	
	@Override
	protected void free() 
	{
		glDeleteSync(getLongName());
	}

	/**
	 * Destroys undeleted sync objects abandoned from destroyed Java objects.
	 * <p><b>This is automatically called by OGLSystem after every frame and should NEVER be called manually!</b>
	 * @return the amount of objects deleted.
	 */
	public static int destroyUndeleted()
	{
		if (UNDELETED_LENGTH > 0)
		{
			int out = UNDELETED_LENGTH;
			for (int i = 0; i < UNDELETED_LENGTH; i++)
				glDeleteSync(UNDELETED_IDS[i]);
			UNDELETED_LENGTH = 0;
			return out;
		}
		return 0;
	}

	// adds the OpenGL Id to the UNDELETED_IDS list.
	private static void finalizeAddId(long id)
	{
		if (UNDELETED_LENGTH == UNDELETED_IDS.length)
			UNDELETED_IDS = expand(UNDELETED_IDS, UNDELETED_IDS.length * 2);
		UNDELETED_IDS[UNDELETED_LENGTH++] = id;
	}
	
	@Override
	public void finalize() throws Throwable
	{
		if (isAllocated())
			finalizeAddId(getLongName());
		super.finalize();
	}

}
