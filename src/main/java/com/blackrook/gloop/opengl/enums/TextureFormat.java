/*******************************************************************************
 * Copyright (c) 2021 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at 
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gloop.opengl.enums;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import com.blackrook.gloop.opengl.OGLVersion;
import com.blackrook.gloop.opengl.OGLVersioned;

/**
 * Texture internal storage format.
 */
public enum TextureFormat implements OGLVersioned
{
	/** Grayscale, No alpha, default bit depth. */
	LUMINANCE(OGLVersion.GL11, GL11.GL_LUMINANCE, false, 1f),
	/** Grayscale with alpha, default bit depth. */
	LUMINANCE_ALPHA(OGLVersion.GL11, GL11.GL_LUMINANCE_ALPHA, false, 2f),
	/** White, Alpha only, default bit depth. */
	INTENSITY(OGLVersion.GL11, GL11.GL_INTENSITY, false, 1f),
	/** RGB, No alpha, default bit depth. */
	RGB(OGLVersion.GL11, GL11.GL_RGB, false, 3f),
	/** RGBA, default bit depth. */
	RGBA(OGLVersion.GL11, GL11.GL_RGBA, false, 4f),
	/** RGBA, forced 16-bit. */
	RGBA4(OGLVersion.GL11, GL11.GL_RGB4, false, 2f),
	/** RGBA, forced 16-bit. */
	RGB5A1(OGLVersion.GL11, GL11.GL_RGB5_A1, false, 2f),
	/** RGBA, forced 32-bit. */
	RGBA8(OGLVersion.GL11, GL11.GL_RGB8, false, 4f),

	/** Compressed Alpha Only. */
	COMPRESSED_ALPHA(OGLVersion.GL13, GL13.GL_COMPRESSED_ALPHA, true, 1f),
	/** Compressed Alpha Only. */
    COMPRESSED_LUMINANCE(OGLVersion.GL13, GL13.GL_COMPRESSED_LUMINANCE, true, 1f),
	/** Compressed Alpha Only. */
    COMPRESSED_LUMINANCE_ALPHA(OGLVersion.GL13, GL13.GL_COMPRESSED_LUMINANCE_ALPHA, true, 2f),
	/** Compressed Alpha Only. */
    COMPRESSED_INTENSITY(OGLVersion.GL13, GL13.GL_COMPRESSED_INTENSITY, true, 1f),
	/** Compressed Alpha Only. */
    COMPRESSED_RGB(OGLVersion.GL13, GL13.GL_COMPRESSED_RGB, true, 3f),
	/** Compressed Alpha Only. */
    COMPRESSED_RGBA(OGLVersion.GL13, GL13.GL_COMPRESSED_RGBA, true, 4f);

	private OGLVersion version;
	public final int glValue;
	public final boolean compressed;
	public final float sizeFactor;
	private TextureFormat(OGLVersion version, int id, boolean c, float factor) {glValue = id; compressed = c; sizeFactor = factor;}
	public boolean isCompressed() {return compressed;}
	public int getGLValue() {return glValue;}

	@Override
	public OGLVersion getVersion()
	{
		return version;
	}
}

