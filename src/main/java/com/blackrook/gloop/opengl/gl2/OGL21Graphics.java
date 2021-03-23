/*******************************************************************************
 * Copyright (c) 2021 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at 
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gloop.opengl.gl2;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL21.*;

import com.blackrook.gloop.opengl.gl1.OGL15Graphics;

/**
 * OpenGL 2.1 Graphics Implementation.
 * @author Matthew Tropiano
 */
public class OGL21Graphics extends OGL15Graphics implements OGL2XGraphics
{
	@Override
	public String getShadingLanguageVersion()
	{
		return glGetString(GL_SHADING_LANGUAGE_VERSION);
	}

}
