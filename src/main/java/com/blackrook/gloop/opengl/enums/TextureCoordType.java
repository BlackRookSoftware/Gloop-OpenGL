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
 * Texture coordinate generation constants.
 * @author Matthew Tropiano
 */
public enum TextureCoordType
{
	/** Texture S coordinate (width). */
	S(GL_S),
	/** Texture T coordinate (height). */
	T(GL_T),
	/** Texture R coordinate (depth). */
	R(GL_R),
	/** Texture Q coordinate (I don't know). */
	Q(GL_Q);
	
	public final int glValue;
	private TextureCoordType(int gltype) {glValue = gltype;}

}
