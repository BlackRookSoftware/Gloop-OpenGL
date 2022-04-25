/*******************************************************************************
 * Copyright (c) 2021-2022 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at 
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gloop.opengl.enums;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL32;

import com.blackrook.gloop.opengl.OGLVersion;
import com.blackrook.gloop.opengl.OGLVersioned;

/**
 * Geometry type 
 * @author Matthew Tropiano
 */
public enum GeometryType implements OGLVersioned
{
	POINTS(GL11.GL_POINTS, true, OGLVersion.GL11, true)
	{
		@Override
		public int calculatePolygonCount(int elementCount)
		{
			return elementCount;
		}
	},
	
	LINES(GL11.GL_LINES, true, OGLVersion.GL11, true)
	{
		@Override
		public int calculatePolygonCount(int elementCount)
		{
			return elementCount / 2;
		}
	},
	
	LINE_STRIP(GL11.GL_LINE_STRIP, false, OGLVersion.GL11, true)
	{
		@Override
		public int calculatePolygonCount(int elementCount)
		{
			return 1;
		}
	},
	
	LINE_LOOP(GL11.GL_LINE_LOOP, false, OGLVersion.GL11, true)
	{
		@Override
		public int calculatePolygonCount(int elementCount)
		{
			return 1;
		}
	},
	
	LINES_ADJACENCY(GL32.GL_LINES_ADJACENCY, true, OGLVersion.GL32, true)
	{
		@Override
		public int calculatePolygonCount(int elementCount)
		{
			return elementCount / 2;
		}
	},
	
	LINE_STRIP_ADJACENCY(GL32.GL_LINE_STRIP_ADJACENCY, false, OGLVersion.GL32, true)
	{
		@Override
		public int calculatePolygonCount(int elementCount)
		{
			return 1;
		}
	},
	
	TRIANGLES(GL11.GL_TRIANGLES, true, OGLVersion.GL11, true)
	{
		@Override
		public int calculatePolygonCount(int elementCount)
		{
			return elementCount / 3;
		}
	},
	
	TRIANGLE_STRIP(GL11.GL_TRIANGLE_STRIP, false, OGLVersion.GL11, true)
	{
		@Override
		public int calculatePolygonCount(int elementCount)
		{
			return elementCount - 2;
		}
	},
	
	TRIANGLE_FAN(GL11.GL_TRIANGLE_FAN, false, OGLVersion.GL11, true)
	{
		@Override
		public int calculatePolygonCount(int elementCount)
		{
			return elementCount - 2;
		}
	},
	
	TRIANGLES_ADJACENCY(GL32.GL_TRIANGLES_ADJACENCY, true, OGLVersion.GL32, true)
	{
		@Override
		public int calculatePolygonCount(int elementCount)
		{
			return elementCount / 3;
		}
	},
	
	TRIANGLE_STRIP_ADJACENCY(GL32.GL_TRIANGLE_STRIP_ADJACENCY, false, OGLVersion.GL32, true)
	{
		@Override
		public int calculatePolygonCount(int elementCount)
		{
			return elementCount - 2;
		}
	},
	
	QUADS(GL11.GL_QUADS, true, OGLVersion.GL11, false)
	{
		@Override
		public int calculatePolygonCount(int elementCount)
		{
			return elementCount / 4;
		}
	},
	
	QUAD_STRIP(GL11.GL_QUAD_STRIP, false, OGLVersion.GL11, false)
	{
		@Override
		public int calculatePolygonCount(int elementCount)
		{
			return (elementCount - 2) / 2;
		}
	},
	
	POLYGON(GL11.GL_POLYGON, false, OGLVersion.GL11, false)
	{
		@Override
		public int calculatePolygonCount(int elementCount)
		{
			return 1;
		}
	};
	
	public final int glValue;
	
	private final boolean batchable; 
	private final OGLVersion version; 
	private final boolean core; 
	
	private GeometryType(int gltype, boolean batchable, OGLVersion version, boolean core) 
	{
		this.glValue = gltype; 
		this.batchable = batchable;
		this.version = version;
		this.core = core;
	}

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
		return version;
	}
	
	@Override
	public boolean isCore()
	{
		return core;
	}
	
	/**
	 * Calculates the polygon count by how many elements/vertices it contained.
	 * @param elementCount the input count.
	 * @return the resultant polygon count. 
	 */
	public abstract int calculatePolygonCount(int elementCount);
	
}
