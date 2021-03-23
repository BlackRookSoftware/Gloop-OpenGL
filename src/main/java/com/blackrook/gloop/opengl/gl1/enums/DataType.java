/*******************************************************************************
 * Copyright (c) 2021 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at 
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gloop.opengl.gl1.enums;

import static org.lwjgl.opengl.GL11.*;

/**
 * Enumeration of DataTypes.
 * @author Matthew Tropiano
 */
public enum DataType
{
	BYTE(GL_BYTE, 1),
	UNSIGNED_BYTE(GL_UNSIGNED_BYTE, 1),
	SHORT(GL_SHORT, 2),
	UNSIGNED_SHORT(GL_UNSIGNED_SHORT, 2),
	FLOAT(GL_FLOAT, 4),
	INTEGER(GL_INT, 4),
	UNSIGNED_INTEGER(GL_UNSIGNED_INT, 4),
	DOUBLE(GL_DOUBLE, 8);
	
	public final int glValue;
	public final int size;
	private DataType(int val, int size) {glValue = val; this.size = size;}
}
