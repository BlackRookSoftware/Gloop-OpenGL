/*******************************************************************************
 * Copyright (c) 2021 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at 
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gloop.opengl.gl2;

import com.blackrook.gloop.opengl.gl1.OGL15Graphics;

import static org.lwjgl.opengl.GL20.*;

/**
 * OpenGL 2.1 Graphics Implementation.
 * @author Matthew Tropiano
 */
public class OGL20Graphics extends OGL15Graphics
{
	@Override
	public String getShadingLanguageVersion()
	{
		return glGetString(GL_SHADING_LANGUAGE_VERSION);
	}

	/**
	 * Enables/Disables point sprite conversion.
	 * Internally, OpenGL will convert point geometry into billboarded quads or
	 * actual polygonal information. 
	 * @param enabled true to enable, false to disable.
	 */
	public void setPointSpritesEnabled(boolean enabled)
	{
		setFlag(GL_POINT_SPRITE, enabled);
	}

	/**
	 * Sets if texture coordinates are to be generated across point geometry
	 * dimensions. Useful for Point Sprites, obviously.
	 * @param enabled true to enable, false to disable.
	 */
	public void setPointSpriteTexCoordGeneration(boolean enabled)
	{
		glTexEnvi(GL_POINT_SPRITE, GL_COORD_REPLACE, toGLBool(enabled));
	}

}
