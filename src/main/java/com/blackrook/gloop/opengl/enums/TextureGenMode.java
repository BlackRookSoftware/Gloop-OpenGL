/*******************************************************************************
 * Copyright (c) 2021 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at 
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gloop.opengl.enums;

import static org.lwjgl.opengl.GL13.*;

/**
 * Texture coordinate generation constants.
 * @author Matthew Tropiano
 */
public enum TextureGenMode
{
	/** Coordinates are created relative to the object. */
	OBJECT(GL_OBJECT_LINEAR),
	/** Coordinates are created using the eye vector. */
	EYE(GL_EYE_LINEAR),
	/** Coordinates are created using geometry normals for sphere maps. */
	SPHERE(GL_SPHERE_MAP),
	/** Coordinates are created using geometry normals for sphere maps, as if the environment were reflected. */
	REFLECTION(GL_REFLECTION_MAP),
	/** Coordinates are created using geometry normals (cube map). */
	NORMAL(GL_NORMAL_MAP);
	
	public final int glValue;
	private TextureGenMode(int gltype) {glValue = gltype;}
}
