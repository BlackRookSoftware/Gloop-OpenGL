/*******************************************************************************
 * Copyright (c) 2021-2022 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at 
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gloop.opengl.enums;

import static org.lwjgl.opengl.GL30.*;

/**
 * Feedback buffer output mode.
 * @author Matthew Tropiano
 */
public enum FeedbackBufferType
{
	/** Send back as values in separate regions. */
	SEPARATE_ATTRIBS(GL_SEPARATE_ATTRIBS),
	/** Send back as interleaved values. */
	INTERLEAVED_ATTRIBS(GL_INTERLEAVED_ATTRIBS);
	
	public final int glValue;
	private FeedbackBufferType(int gltype) {glValue = gltype;}

}
