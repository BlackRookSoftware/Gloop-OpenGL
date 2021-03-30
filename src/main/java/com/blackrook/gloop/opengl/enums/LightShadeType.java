/*******************************************************************************
 * Copyright (c) 2021 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at 
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gloop.opengl.enums;

import static org.lwjgl.opengl.GL11.*;

/**
 * Enumeration for shading types.
 * @author Matthew Tropiano
 */
public enum LightShadeType
{
	/** Use smooth (Gouraud) shading on polygons. */
	SMOOTH(GL_SMOOTH),
	/** Use flat shading on polygons (one face, one color). */
	FLAT(GL_FLAT);
	
	public final int glValue;
	private LightShadeType(int gltype) {glValue = gltype;}
}

