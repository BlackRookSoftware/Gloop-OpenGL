/*******************************************************************************
 * Copyright (c) 2021-2022 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at 
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gloop.opengl.enums;

import org.lwjgl.opengl.GL11;

/**
 * Transform feedback primitive modes.
 * @author Matthew Tropiano
 */
public enum PrimitiveMode
{
	/** 
	 * Point feedback mode.
	 * <p> Valid drawing primitives are: {@link GeometryType#POINTS}.
	 * <p> Geometry shader must output <code>points</code>.
	 */
	POINTS(GL11.GL_POINTS),
	
	/** 
	 * Lines feedback mode. 
	 * <p> Valid drawing primitives are: {@link GeometryType#LINES}, {@link GeometryType#LINE_STRIP}, 
	 * {@link GeometryType#LINE_LOOP}, {@link GeometryType#LINES_ADJACENCY}, {@link GeometryType#LINE_STRIP_ADJACENCY}.
	 * <p> Geometry shader must output <code>line_strip</code>.
	 */
	LINES(GL11.GL_LINES),

	/** 
	 * Triangles feedback mode.
	 * <p> Valid drawing primitives are: {@link GeometryType#TRIANGLES}, {@link GeometryType#TRIANGLE_STRIP}, 
	 * {@link GeometryType#TRIANGLE_FAN}, {@link GeometryType#TRIANGLES_ADJACENCY}, {@link GeometryType#TRIANGLE_STRIP_ADJACENCY}.
	 * <p> Geometry shader must output <code>triangle_strip</code>.
	 */
	TRIANGLES(GL11.GL_TRIANGLES);
	
	public final int glValue;
	
	private PrimitiveMode(int glValue) 
	{
		this.glValue = glValue;
	}
	
}
