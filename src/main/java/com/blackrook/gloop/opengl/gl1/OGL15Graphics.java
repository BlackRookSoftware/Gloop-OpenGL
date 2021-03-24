/*******************************************************************************
 * Copyright (c) 2021 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at 
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gloop.opengl.gl1;

import static org.lwjgl.opengl.GL11.glFogi;

import org.lwjgl.opengl.GL15;

import com.blackrook.gloop.opengl.gl1.enums.FogCoordinateType;

/**
 * OpenGL 1.5 Graphics Implementation.
 * @author Matthew Tropiano
 */
public class OGL15Graphics extends OGL14Graphics
{
	/**
	 * Sets the origin of the calculation of the fog coordinate value that
	 * dictates "where" in the fog it is.
	 * @param coord the coordinate type.
	 */
	public void setFogCoordinateSource(FogCoordinateType coord)
	{
		glFogi(GL15.GL_FOG_COORD_SRC, coord.glValue);
	}
}
