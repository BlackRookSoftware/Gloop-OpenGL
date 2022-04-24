/*******************************************************************************
 * Copyright (c) 2021-2022 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at 
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gloop.opengl.enums;

import static org.lwjgl.opengl.GL11.*;

/**
 * Minification filters.
 * @author Matthew Tropiano
 */
public enum TextureMinFilter
{
	/** 
	 * Nearest minification - color using nearest neighbor (aliased - bad approximation). 
	 */
	NEAREST(GL_NEAREST),
	
	/** 
	 * Linear minification - color using cluster average (okay approximation). 
	 */
	LINEAR(GL_LINEAR),
	
	/** 
	 * Bilinear minification - color using cluster average and next mipmap's
	 * nearest neighbor (better approximation). 
	 */
	BILINEAR(GL_LINEAR_MIPMAP_NEAREST),
	
	/** 
	 * Trilinear minification - color using cluster average and next mipmap's 
	 * cluster average (best approximation).
	 * Also called "bicubic" or "cubic." 
	 */
	TRILINEAR(GL_LINEAR_MIPMAP_LINEAR);
	
	public final int glid;
	private TextureMinFilter(int id) {glid = id;}
}

