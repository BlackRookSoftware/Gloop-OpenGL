package com.blackrook.gloop.opengl;

/**
 * Enumeration of OpenGL versions.
 * This is used for verifying that certain values can be used with certain implementation levels.
 * @author Matthew Tropiano
 */
public enum OGLVersion implements Comparable<OGLVersion>
{
	GL11,
	GL12,
	GL13,
	GL14,
	GL15,
	GL20,
	GL21,
	GL30,
	GL31,
	GL32,
	GL33,
	GL40,
	GL41,
	GL42,
	GL43,
	GL44,
	GL45,
	GL46;
}
