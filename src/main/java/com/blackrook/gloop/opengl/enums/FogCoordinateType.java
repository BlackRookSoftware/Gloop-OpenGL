/*******************************************************************************
 * Copyright (c) 2021-2022 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at 
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gloop.opengl.enums;

import static org.lwjgl.opengl.GL15.*;

/**
 * Enumeration of fog coordinate calculation types.
 * @author Matthew Tropiano
 */
public enum FogCoordinateType
{
	/**
	 * Fog coordinate is taken from fog coordinate attributes. 
	 */
	COORDINATE(GL_FOG_COORD),
	/**
	 * Fog coordinate is taken from fragment depth (only effective
	 * if the depth buffer is active). 
	 */
	DEPTH(GL_FRAGMENT_DEPTH);
	
	public final int glValue;
	private FogCoordinateType (int val) {glValue = val;}
}
