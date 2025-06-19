package com.blackrook.gloop.opengl.enums;

import org.lwjgl.opengl.GL43;

/**
 * Enumeration of Debug sources for debug messages.
 */
public enum DebugControlSource
{
	/** Originated from application. */
	APPLICATION(GL43.GL_DEBUG_SOURCE_APPLICATION),
	/** Originated from a third party. */
	THIRD_PARTY(GL43.GL_DEBUG_SOURCE_THIRD_PARTY),
	/** Originated from the API. */
	API(GL43.GL_DEBUG_SOURCE_API),
	/** Originated from the shader compiler. */
	SHADER_COMPILER(GL43.GL_DEBUG_SOURCE_SHADER_COMPILER),
	/** Originated from the window system. */
	WINDOW_SYSTEM(GL43.GL_DEBUG_SOURCE_WINDOW_SYSTEM),
	/** Originated from some other source. */
	OTHER(GL43.GL_DEBUG_SOURCE_OTHER),
	/** Don't care where it originated. */
	DONT_CARE(GL43.GL_DONT_CARE),
	;
	
	public final int glValue;
	
	private DebugControlSource(int glvalue) 
	{
		this.glValue = glvalue;
	}
	
}
