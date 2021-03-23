/*******************************************************************************
 * Copyright (c) 2021 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at 
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gloop.opengl.gl1.enums;

import static org.lwjgl.opengl.GL14.*;

/**
 * Enumeration of testing function constants.
 * @author Matthew Tropiano
 */
public enum StencilTestFunc
{
	KEEP(GL_KEEP),
	ZERO(GL_ZERO),
	REPLACE(GL_REPLACE),
	INCREMENT(GL_INCR),
	INCREMENT_WRAP(GL_INCR_WRAP),
	DECREMENT(GL_DECR),
	DECREMENT_WRAP(GL_DECR_WRAP),
	INVERT(GL_INVERT);
	
	public final int glValue;
	private StencilTestFunc(int gltype) {glValue = gltype;}

}
