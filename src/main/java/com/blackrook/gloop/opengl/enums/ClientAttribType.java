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
 * Attribute types for attribute states
 * @author Matthew Tropiano
 */
public enum ClientAttribType
{
	ALL((int)(GL_CLIENT_ALL_ATTRIB_BITS & 0xffffffff)),
	PIXEL_STORE(GL_CLIENT_PIXEL_STORE_BIT),
	VERTEX_ARRAY(GL_CLIENT_VERTEX_ARRAY_BIT);

	public final int glValue;
	private ClientAttribType (int val) {glValue = val;}

}
