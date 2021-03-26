/*******************************************************************************
 * Copyright (c) 2021 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at 
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gloop.opengl.gl3.enums;

import static org.lwjgl.opengl.GL11.*;

/**
 * Enumeration of render buffer formats.
 * @author Matthew Tropiano
 */
public enum RenderbufferFormat
{
	RGB(GL_RGB),
	RGBA(GL_RGBA),
	DEPTH(GL_DEPTH_COMPONENT),
	STENCIL(GL_STENCIL_INDEX);
	
	public final int glid;
	private RenderbufferFormat(int id) {glid = id;}
}
