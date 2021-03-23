/*******************************************************************************
 * Copyright (c) 2021 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at 
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gloop.opengl.gl2.enums;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL21.*;

/**
 * Type enumerant for VBO, Texture Buffer, or other one-dimensional buffer types.
 * @author Matthew Tropiano
 */
public enum BufferType
{
	/** Buffer holds GEOMETRY information (internally, this is GL_ARRAY_BUFFER). */
	GEOMETRY(GL_ARRAY_BUFFER),
	/** Buffer holds ELEMENT INDEX information (internally, this is GL_ELEMENT_ARRAY_BUFFER). */
	INDICES(GL_ELEMENT_ARRAY_BUFFER),
	/** Buffer contains unpacked data (raw pixel data to be sent to OpenGL or read from OpenGL to an application). */
	PIXEL(GL_PIXEL_UNPACK_BUFFER),
	/** Buffer contains packed data (raw data specific to OpenGL implementation). */
	DATA(GL_PIXEL_PACK_BUFFER);
	
	public final int glValue;
	private BufferType(int gltype) {glValue = gltype;}
}
