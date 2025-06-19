package com.blackrook.gloop.opengl.enums;

import java.util.Map;

import org.lwjgl.opengl.GL43;

import com.blackrook.gloop.opengl.struct.EnumUtils;

/**
 * Enumeration of Debug severity level types for debug messages.
 */
public enum DebugSeverity
{
	LOW(GL43.GL_DEBUG_SEVERITY_LOW),
	MEDIUM(GL43.GL_DEBUG_SEVERITY_MEDIUM),
	HIGH(GL43.GL_DEBUG_SEVERITY_HIGH),
	NOTIFICATION(GL43.GL_DEBUG_SEVERITY_NOTIFICATION),
	;
	
	private static final Map<Integer, DebugSeverity> GL_TO_VALUE = EnumUtils.createMap(DebugSeverity.class, (ordinal, e) -> e.glValue); 

	public final int glValue;
	
	private DebugSeverity(int glvalue) 
	{
		this.glValue = glvalue;
	}
	
	/**
	 * Gets an enum by its corresponding GL value.
	 * @param glValue the GL value to use.
	 * @return the enum value, or null for no corresponding value.
	 */
	public static DebugSeverity getByGLValue(int glValue)
	{
		return GL_TO_VALUE.get(glValue);
	}
	
}
