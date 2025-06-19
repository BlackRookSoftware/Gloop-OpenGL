package com.blackrook.gloop.opengl.enums;

import org.lwjgl.opengl.GL43;

/**
 * Enumeration of Debug control severity levels for debug messages.
 */
public enum DebugControlSeverity
{
	LOW(GL43.GL_DEBUG_SEVERITY_LOW),
	MEDIUM(GL43.GL_DEBUG_SEVERITY_MEDIUM),
	HIGH(GL43.GL_DEBUG_SEVERITY_HIGH),
	NOTIFICATION(GL43.GL_DEBUG_SEVERITY_NOTIFICATION),
	DONT_CARE(GL43.GL_DONT_CARE),
	;
	
	public final int glValue;
	
	private DebugControlSeverity(int glvalue) 
	{
		this.glValue = glvalue;
	}
	
}
