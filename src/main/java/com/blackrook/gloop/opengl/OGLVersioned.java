package com.blackrook.gloop.opengl;

/**
 * Describes an object that can only be used with specific versions of OpenGL.
 * @author Matthew Tropiano
 */
public interface OGLVersioned
{
	/**
	 * @return the version that this object or value is available for (and higher).
	 */
	OGLVersion getVersion();
	
	/**
	 * @return true if this object is considered part of core spec, false otherwise.
	 */
	boolean isCore();
	
}
