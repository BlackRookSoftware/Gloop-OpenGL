/*******************************************************************************
 * Copyright (c) 2021 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at 
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gloop.opengl.enums;

import static org.lwjgl.opengl.GL21.*;

/**
 * Enumeration of blend source/destination arguments.
 * @author Matthew Tropiano
 */
public enum BlendArg
{
	ZERO(GL_ZERO),
	ONE(GL_ONE),
	SOURCE_COLOR(GL_SRC_COLOR),
	ONE_MINUS_SOURCE_COLOR(GL_ONE_MINUS_SRC_COLOR),
	DEST_COLOR(GL_DST_COLOR),
	ONE_MINUS_DEST_COLOR(GL_ONE_MINUS_DST_COLOR),
	SOURCE_ALPHA(GL_SRC_ALPHA),
	ONE_MINUS_SOURCE_ALPHA(GL_ONE_MINUS_SRC_ALPHA),
	DEST_ALPHA(GL_DST_ALPHA),
	ONE_MINUS_DEST_ALPHA(GL_ONE_MINUS_DST_ALPHA),
	SOURCE_ALPHA_SATURATE(GL_SRC_ALPHA_SATURATE);
	
	public final int glValue;
	BlendArg(int gltype) {glValue = gltype;}

}
