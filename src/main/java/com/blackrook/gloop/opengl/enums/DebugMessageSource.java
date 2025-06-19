package com.blackrook.gloop.opengl.enums;

import java.util.Map;

import org.lwjgl.opengl.GL43;

import com.blackrook.gloop.opengl.struct.EnumUtils;

/**
 * Enumeration of Debug sources for debug messages.
 */
public enum DebugMessageSource
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
	;

	private static final Map<Integer, DebugMessageSource> GL_TO_VALUE = EnumUtils.createMap(DebugMessageSource.class, (ordinal, e) -> e.glValue); 
	
	public final int glValue;
	
	private DebugMessageSource(int glvalue) 
	{
		this.glValue = glvalue;
	}

	/**
	 * Gets an enum by its corresponding GL value.
	 * @param glValue the GL value to use.
	 * @return the enum value, or null for no corresponding value.
	 */
	public static DebugMessageSource getByGLValue(int glValue)
	{
		return GL_TO_VALUE.get(glValue);
	}
	
}
