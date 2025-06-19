package com.blackrook.gloop.opengl.enums;

import java.util.Map;

import org.lwjgl.opengl.GL43;

import com.blackrook.gloop.opengl.struct.EnumUtils;

/**
 * Enumeration of Debug types for debug messages.
 */
public enum DebugType
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
	;
	
	private static final Map<Integer, DebugType> GL_TO_VALUE = EnumUtils.createMap(DebugType.class, (ordinal, e) -> e.glValue); 

	public final int glValue;
	
	private DebugType(int glvalue) 
	{
		this.glValue = glvalue;
	}
	
	/**
	 * Gets an enum by its corresponding GL value.
	 * @param glValue the GL value to use.
	 * @return the enum value, or null for no corresponding value.
	 */
	public static DebugType getByGLValue(int glValue)
	{
		return GL_TO_VALUE.get(glValue);
	}
	
}
