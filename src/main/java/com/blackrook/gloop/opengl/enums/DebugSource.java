package com.blackrook.gloop.opengl.enums;

import org.lwjgl.opengl.GL43;

/**
 * Enumeration of Debug sources for debug messages.
 */
public enum DebugSource
{
	/** Originated from application. */
	APPLICATION(GL43.GL_DEBUG_SOURCE_APPLICATION),
	/** Originated from a third party. */
	THIRD_PARTY(GL43.GL_DEBUG_SOURCE_THIRD_PARTY),
	;
	
	public final int glValue;
	
	private DebugSource(int glvalue) 
	{
		this.glValue = glvalue;
	}
	
}
