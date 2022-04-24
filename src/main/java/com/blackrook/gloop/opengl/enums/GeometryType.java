/*******************************************************************************
 * Copyright (c) 2021-2022 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at 
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gloop.opengl.enums;

import static org.lwjgl.opengl.GL11.*;

import com.blackrook.gloop.opengl.OGLVersion;
import com.blackrook.gloop.opengl.OGLVersioned;

/**
 * Geometry type 
 * @author Matthew Tropiano
 */
public enum GeometryType implements OGLVersioned
{
	POINTS(GL_POINTS, true)
	{
		@Override
		public int calculatePolygonCount(int elementCount)
		{
			return elementCount;
		}
	},
	
	LINES(GL_LINES, true)
	{
		@Override
		public int calculatePolygonCount(int elementCount)
		{
			return elementCount/2;
		}
	},
	
	LINE_STRIP(GL_LINE_STRIP, false)
	{
		@Override
		public int calculatePolygonCount(int elementCount)
		{
			return 1;
		}
	},
	
	LINE_LOOP(GL_LINE_LOOP, false)
	{
		@Override
		public int calculatePolygonCount(int elementCount)
		{
			return 1;
		}
	},
	
	TRIANGLES(GL_TRIANGLES, true)
	{
		@Override
		public int calculatePolygonCount(int elementCount)
		{
			return elementCount / 3;
		}
	},
	
	TRIANGLE_STRIP(GL_TRIANGLE_STRIP, false)
	{
		@Override
		public int calculatePolygonCount(int elementCount)
		{
			return elementCount - 2;
		}
	},
	
	TRIANGLE_FAN(GL_TRIANGLE_FAN, false)
	{
		@Override
		public int calculatePolygonCount(int elementCount)
		{
			return elementCount - 2;
		}
	},
	
	QUADS(GL_QUADS, true)
	{
		@Override
		public int calculatePolygonCount(int elementCount)
		{
			return elementCount / 4;
		}
		
		@Override
		public boolean isCore()
		{
			return false;
		}
		
	},
	
	QUAD_STRIP(GL_QUAD_STRIP, false)
	{
		@Override
		public int calculatePolygonCount(int elementCount)
		{
			return (elementCount - 2) / 2;
		}

		@Override
		public boolean isCore()
		{
			return false;
		}
		
	},
	
	POLYGON(GL_POLYGON, false)
	{
		@Override
		public int calculatePolygonCount(int elementCount)
		{
			return 1;
		}

		@Override
		public boolean isCore()
		{
			return false;
		}
		
	};
	
	public final int glValue;
	private final boolean batchable; 
	private GeometryType(int gltype, boolean batchable) {glValue = gltype; this.batchable = batchable;}

	/**
	 * Is this geometry type able to be put together in 
	 * geometry batches without ruining how it appears or is drawn?
	 * @return true if so, false if not. 
	 */
	public boolean isBatchable()
	{
		return batchable;
	}
	
	@Override
	public OGLVersion getVersion()
	{
		return OGLVersion.GL11;
	}
	
	@Override
	public boolean isCore()
	{
		return true;
	}
	
	/**
	 * Calculates the polygon count by how many elements/vertices it contained.
	 * @param elementCount the input count.
	 * @return the resultant polygon count. 
	 */
	public abstract int calculatePolygonCount(int elementCount);
	
}
