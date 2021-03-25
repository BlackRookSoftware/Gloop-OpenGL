/*******************************************************************************
 * Copyright (c) 2021 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at 
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gloop.opengl.gl1.enums;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;

/**
 * Color pixel storage format.
 */
public enum ColorFormat
{
	COLOR_INDEX(GL_COLOR_INDEX),
	STENCIL_INDEX(GL_STENCIL_INDEX),
	DEPTH_COMPONENT(GL_DEPTH_COMPONENT),
	RED(GL_RED),
	GREEN(GL_GREEN),
	BLUE(GL_BLUE),
	ALPHA(GL_ALPHA),
	RGB(GL_RGB),
	RGBA(GL_RGBA),
	LUMINANCE(GL_LUMINANCE),
	LUMINANCE_ALPHA(GL_LUMINANCE_ALPHA),
	BGR(GL_BGR),
	BGRA(GL_BGRA);
	
	public final int glid;
	private ColorFormat(int id) {glid = id;}
	public int getGLValue() {return glid;}
}

