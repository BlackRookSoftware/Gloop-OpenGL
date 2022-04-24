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
 * Enumeration of Matrix types. 
 * @author Matthew Tropiano
 */
public enum MatrixMode
{
	MODELVIEW(GL_MODELVIEW, GL_MODELVIEW_MATRIX),
	PROJECTION(GL_PROJECTION, GL_PROJECTION_MATRIX),
	TEXTURE(GL_TEXTURE, GL_TEXTURE_MATRIX);
	
	public final int glValue;
	public final int glReadValue;
	private MatrixMode(int glValue, int glReadValue) 
	{
		this.glValue = glValue;
		this.glReadValue = glReadValue;
	}

}
