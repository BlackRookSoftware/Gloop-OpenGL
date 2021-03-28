/*******************************************************************************
 * Copyright (c) 2021 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at 
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gloop.opengl.gl1;

import com.blackrook.gloop.opengl.gl1.enums.BufferBindingType;
import com.blackrook.gloop.opengl.gl1.enums.DataType;
import com.blackrook.gloop.opengl.gl1.enums.GeometryType;

import static org.lwjgl.opengl.GL12.*;

/**
 * OpenGL 1.2 Graphics Implementation.
 * @author Matthew Tropiano
 */
public class OGL12Graphics extends OGL11Graphics
{
	/**
	 * Draws geometry using the current bound, enabled coordinate arrays/buffers as data, plus
	 * an element buffer to describe the ordering.
	 * @param geometryType the geometry type - tells how to interpret the data.
	 * @param dataType the data type of the indices in the {@link BufferBindingType#INDICES}-bound buffer (must be an unsigned type).
	 * @param startIndex the starting index into the {@link BufferBindingType#INDICES}-bound buffer.
	 * @param endIndex the ending index in the range.
	 * @param count the amount of element indices to read.
	 * @see #setVertexArrayEnabled(boolean)
	 * @see #setTextureCoordArrayEnabled(boolean)
	 * @see #setNormalArrayEnabled(boolean)
	 * @see #setColorArrayEnabled(boolean)
	 * @see #setPointerVertex(DataType, int, int, int)
	 * @see #setPointerTextureCoordinate(DataType, int, int, int)
	 * @see #setPointerNormal(DataType, int, int)
	 * @see #setPointerColor(DataType, int, int, int)
	 */
	public void drawGeometryElementRange(GeometryType geometryType, DataType dataType, int startIndex, int endIndex, int count)
	{
		glDrawRangeElements(geometryType.glValue, startIndex, endIndex, count, dataType.glValue, 0L);
		getError();
	}

}
