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
 * Accumulation operation enum. 
 * @author Matthew Tropiano
 */
public enum AccumOperation
{
	/** 
	 * Reads from the current read frame buffer, multiplied by the value, and added to the accum buffer. 
	 */
	ACCUMULATE(GL_ACCUM),
	
	/** 
	 * Accumulate from the current read frame buffer, replacing it. 
	 */
	LOAD(GL_LOAD),
	
	/**
	 * Multiplies each R, G, B, and A in the accumulation buffer by value and returns 
	 * the scaled component to its corresponding accumulation buffer location.
	 */
	MULTIPLY(GL_MULT),
	
	/**
	 * Adds value to each R, G, B, and A in the accumulation buffer.
	 */
	ADD(GL_ADD),

	/** 
	 * Send the contents of the accumulation buffer to the current write frame buffer,
	 * after first multiplying by the value. Stenciling, dithering, and color masking applies.
	 */
	RETURN(GL_RETURN),
	;

	public final int glValue;
	AccumOperation(int gltype) {glValue = gltype;}
	
}
