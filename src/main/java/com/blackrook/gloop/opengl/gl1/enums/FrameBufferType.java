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
 * Enumeration of OpenGL FrameBuffer types.
 * @author Matthew Tropiano
 */
public enum FrameBufferType
{
	NONE(GL_NONE),
	FRONT(GL_FRONT),
	BACK(GL_BACK),
	LEFT(GL_LEFT),
	RIGHT(GL_RIGHT),
	FRONT_LEFT(GL_FRONT_LEFT),
	FRONT_RIGHT(GL_FRONT_RIGHT),
	BACK_LEFT(GL_BACK_LEFT),
	BACK_RIGHT(GL_BACK_RIGHT),
	FRONT_AND_BACK(GL_FRONT_AND_BACK),
	AUX0(GL_AUX0),
	AUX1(GL_AUX1),
	AUX2(GL_AUX2),
	AUX3(GL_AUX3);
	
	public final int glValue;
	private FrameBufferType (int val) {glValue = val;}
}
