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
 * Enum for fill modes.
 * @author Matthew Tropiano
 */
public enum FillMode
{
	/** Points rendered only. */
	POINTS(GL_POINT),
	/** Lines/edges rendered only. */
	LINES(GL_LINE),
	/** Filled polygons. */
	FILLED(GL_FILL);

	public final int glValue;
	FillMode(int gltype) 
		{glValue = gltype;}
}
