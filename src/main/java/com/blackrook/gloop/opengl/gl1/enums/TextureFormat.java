/*******************************************************************************
 * Copyright (c) 2021 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at 
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gloop.opengl.gl1.enums;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;

/**
 * Texture internal storage format.
 */
public enum TextureFormat
{
	/** Grayscale, No alpha, default bit depth. */
	LUMINANCE(GL_LUMINANCE, false, 1f),
	/** Grayscale with alpha, default bit depth. */
	LUMINANCE_ALPHA(GL_LUMINANCE_ALPHA, false, 2f),
	/** White, Alpha only, default bit depth. */
	INTENSITY(GL_INTENSITY, false, 1f),
	/** RGB, No alpha, default bit depth. */
	RGB(GL_RGB, false, 3f),
	/** RGBA, default bit depth. */
	RGBA(GL_RGBA, false, 4f),
	/** RGBA, forced 16-bit. */
	RGBA4(GL_RGB4, false, 2f),
	/** RGBA, forced 16-bit. */
	RGB5A1(GL_RGB5_A1, false, 2f),
	/** RGBA, forced 32-bit. */
	RGBA8(GL_RGB8, false, 4f),

	/** Compressed Alpha Only. */
	COMPRESSED_ALPHA(GL_COMPRESSED_ALPHA, true, 1f),
	/** Compressed Alpha Only. */
    COMPRESSED_LUMINANCE(GL_COMPRESSED_LUMINANCE, true, 1f),
	/** Compressed Alpha Only. */
    COMPRESSED_LUMINANCE_ALPHA(GL_COMPRESSED_LUMINANCE_ALPHA, true, 2f),
	/** Compressed Alpha Only. */
    COMPRESSED_INTENSITY(GL_COMPRESSED_INTENSITY, true, 1f),
	/** Compressed Alpha Only. */
    COMPRESSED_RGB(GL_COMPRESSED_RGB, true, 3f),
	/** Compressed Alpha Only. */
    COMPRESSED_RGBA(GL_COMPRESSED_RGBA, true, 4f);
	
	public final int glid;
	public final boolean compressed;
	public final float sizeFactor;
	private TextureFormat(int id, boolean c, float factor) {glid = id; compressed = c; sizeFactor = factor;}
	public boolean isCompressed() {return compressed;}
	public int getGLValue() {return glid;}
}

