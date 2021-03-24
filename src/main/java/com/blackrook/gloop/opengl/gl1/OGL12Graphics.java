/*******************************************************************************
 * Copyright (c) 2021 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at 
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gloop.opengl.gl1;

import java.nio.ByteBuffer;

import com.blackrook.gloop.opengl.exception.GraphicsException;

import static org.lwjgl.opengl.GL12.*;

/**
 * OpenGL 1.2 Graphics Implementation.
 * @author Matthew Tropiano
 */
public class OGL12Graphics extends OGL11Graphics
{
	/**
	 * Reads from the current-bound frame buffer into a target buffer.
	 * @param imageData	the buffer to write the RGBA pixel data to (must be direct).
	 * @param x the starting screen offset, x-coordinate (0 is left).
	 * @param y the starting screen offset, y-coordinate (0 is bottom).
	 * @param width the capture width in pixels.
	 * @param height the capture height in pixels.
	 * @throws GraphicsException if the buffer provided is not direct.
	 */
	public void readFrameBuffer(ByteBuffer imageData, int x, int y, int width, int height)
	{
		if (!imageData.isDirect())
			throw new GraphicsException("Data must be a direct buffer.");
		glReadPixels(x, y, width, height, GL_BGRA, GL_UNSIGNED_BYTE, imageData);
	}

}
