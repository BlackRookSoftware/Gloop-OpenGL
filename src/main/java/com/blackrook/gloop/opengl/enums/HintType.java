/*******************************************************************************
 * Copyright (c) 2021 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at 
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gloop.opengl.enums;

import static org.lwjgl.opengl.GL14.*;

/**
 * Hint enum types for GL Hints.
 * @author Matthew Tropiano
 */
public enum HintType
{
	/** Point smoothing hints. */
	POINT_SMOOTHING(GL_POINT_SMOOTH_HINT),
	/** Line smoothing hints. */
	LINE_SMOOTHING(GL_LINE_SMOOTH_HINT),
	/** Polygon smoothing hints. */
	POLYGON_SMOOTHING(GL_POLYGON_SMOOTH_HINT),
	/** Fog rendering hints. */
	FOG(GL_FOG_HINT),
	/** Mipmap generation hints. */
	MIPMAPPING(GL_GENERATE_MIPMAP_HINT),
	/** Texture compression hint. */
	TEXTURE_COMPRESSION(GL_TEXTURE_COMPRESSION_HINT),
	/** Perspective compression hint. */
	PERSPECTIVE_CORRECTION(GL_PERSPECTIVE_CORRECTION_HINT);
	
	public final int glValue;
	private HintType(int gltype) {glValue = gltype;}

}
