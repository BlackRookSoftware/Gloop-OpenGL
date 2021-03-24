/*******************************************************************************
 * Copyright (c) 2021 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at 
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gloop.opengl.gl1;

import java.nio.FloatBuffer;

import org.lwjgl.opengl.GL14;
import org.lwjgl.system.MemoryStack;

import static org.lwjgl.opengl.GL11.glTexEnvf;
import static org.lwjgl.opengl.GL14.*;

/**
 * OpenGL 1.4 Graphics Implementation.
 * @author Matthew Tropiano
 */
public class OGL14Graphics extends OGL11Graphics
{
	/**
	 * Sets the maximum size for the diameter of Point geometry when
	 * it is attenuated by point distance from the "camera".
	 * @param size the minimum size.
	 */
	public void setPointAttenuationMaximum(float size)
	{
		glPointParameterf(GL_POINT_SIZE_MAX, size);
	}

	/**
	 * Sets the minimum size for the diameter of Point geometry when
	 * it is attenuated by point distance from the "camera".
	 * @param size the minimum size.
	 */
	public void setPointAttenuationMinimum(float size)
	{
		glPointParameterf(GL_POINT_SIZE_MIN, size);
	}

	/**
	 * Sets the attenuation formula to use when changing the sizes
	 * of points based on their location in space.
	 * @param constant the formula constant coefficient.
	 * @param linear the formula linear coefficient.
	 * @param quadratic the formula quadratic coefficient.
	 */
	public void setPointAttenuationFormula(float constant, float linear, float quadratic)
	{
		try (MemoryStack stack = MemoryStack.stackPush())
		{
			FloatBuffer fbuf = stack.mallocFloat(3);
			fbuf.put(0, constant);
			fbuf.put(1, linear);
			fbuf.put(2, quadratic);
			glPointParameterfv(GL_POINT_DISTANCE_ATTENUATION, fbuf);
		}
	}

	/**
	 * Sets the Level Of Detail bias for automatic texture mipmapping.
	 * @param bias the bias value.
	 */
	public void setTextureLODBias(float bias)
	{
		glTexEnvf(GL14.GL_TEXTURE_FILTER_CONTROL, GL14.GL_TEXTURE_LOD_BIAS, bias);
	}

}
