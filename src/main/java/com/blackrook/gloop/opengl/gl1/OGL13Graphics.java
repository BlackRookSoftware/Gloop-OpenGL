/*******************************************************************************
 * Copyright (c) 2021 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at 
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gloop.opengl.gl1;

import static org.lwjgl.opengl.GL13.*;

/**
 * OpenGL 1.3 Graphics Implementation.
 * @author Matthew Tropiano
 */
public class OGL13Graphics extends OGL12Graphics
{
	/**
	 * Sets if cube map texturing is enabled or not.
	 * @param enabled true to enable, false to disable.
	 */
	public void setTextureCubeEnabled(boolean enabled)
	{
		setFlag(GL_TEXTURE_CUBE_MAP, enabled);
	}

	/**
	 * Sets the current "active" texture unit for texture bindings and texture environment settings.
	 * @param unit the texture unit to switch to.
	 */
	public void setTextureUnit(int unit)
	{
		glActiveTexture(GL_TEXTURE0 + unit);
	}

}
