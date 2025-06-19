package com.blackrook.gloop.opengl.enums;

import org.lwjgl.opengl.GL43;

/**
 * Enumeration of Debug control types for debug messages.
 */
public enum DebugControlType
{
	MARKER(GL43.GL_DEBUG_TYPE_MARKER),
	DEPRECATED_BEHAVIOR(GL43.GL_DEBUG_TYPE_DEPRECATED_BEHAVIOR),
	UNDEFINED_BEHAVIOR(GL43.GL_DEBUG_TYPE_UNDEFINED_BEHAVIOR),
	PUSH_GROUP(GL43.GL_DEBUG_TYPE_PUSH_GROUP),
	POP_GROUP(GL43.GL_DEBUG_TYPE_POP_GROUP),
	PERFORMANCE(GL43.GL_DEBUG_TYPE_PERFORMANCE),
	PORTABILITY(GL43.GL_DEBUG_TYPE_PORTABILITY),
	OTHER(GL43.GL_DEBUG_TYPE_OTHER),
	ERROR(GL43.GL_DEBUG_TYPE_ERROR),
	DONT_CARE(GL43.GL_DONT_CARE),
	;
	
	public final int glValue;
	
	private DebugControlType(int glvalue) 
	{
		this.glValue = glvalue;
	}
	
}
