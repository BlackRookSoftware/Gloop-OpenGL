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
 * Reusable logical enumerations. 
 * @author Matthew Tropiano
 */
public enum LogicFunc
{
	/** Always. */
	ALWAYS(GL_ALWAYS),
	/** Never. */
	NEVER(GL_NEVER),
	EQUAL(GL_EQUAL),
	NOT_EQUAL(GL_NOTEQUAL),
	LESS(GL_LESS),
	GREATER(GL_GREATER),
	LESS_OR_EQUAL(GL_LEQUAL),
	GREATER_OR_EQUAL(GL_GEQUAL);
	
	public final int glValue;
	private LogicFunc(int gltype) {glValue = gltype;}

}
