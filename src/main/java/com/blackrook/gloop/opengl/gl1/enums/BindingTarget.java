/*******************************************************************************
 * Copyright (c) 2021 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at 
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/

package com.blackrook.gloop.opengl.gl1.enums;

import static org.lwjgl.opengl.GL11.*;

/**
 * Target to bind buffer objects to.
 * @author Matthew Tropiano
 */
public enum BindingTarget
{
	NONE(0),
	VERTEX(GL_VERTEX_ARRAY),
	NORMAL(GL_NORMAL_ARRAY),
	TEXTURE_COORD(GL_TEXTURE_COORD_ARRAY),
	COLOR(GL_COLOR_ARRAY);
	
	final int glValue;
	private BindingTarget (int val) {glValue = val;}
}
