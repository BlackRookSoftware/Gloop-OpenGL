/*******************************************************************************
 * Copyright (c) 2021-2022 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at 
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gloop.opengl.enums;

import static org.lwjgl.opengl.GL11.*;

/**
 * Texture environment mode constants.
 * @author Matthew Tropiano
 */
public enum TextureMode
{
	DECAL(GL_DECAL),
	/** Texels replace fragment information. */
	REPLACE(GL_REPLACE),
	/** Texels are multiplied with fragment color/material information. */
	MODULATE(GL_MODULATE),
	/** Texels are blended with fragment color/material information using the current blend function. */
	BLEND(GL_BLEND),
	/** Texels are added to fragment color/material information. */
	ADD(GL_ADD);
	
	public final int glValue;
	private TextureMode(int gltype) {glValue = gltype;}
}
