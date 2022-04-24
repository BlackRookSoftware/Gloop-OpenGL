/*******************************************************************************
 * Copyright (c) 2021-2022 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at 
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
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
