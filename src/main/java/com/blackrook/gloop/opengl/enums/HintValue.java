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
 * Hint enum types for GL Hints.
 * @author Matthew Tropiano
 */
public enum HintValue
{
	/** Don't care about the quality. */
	DONT_CARE(GL_DONT_CARE),
	/** Use the best performing method. */
	FASTEST(GL_FASTEST),
	/** Use the best quality method. */
	NICEST(GL_NICEST);

	public final int glValue;
	private HintValue(int gltype) {glValue = gltype;}

}
