/*******************************************************************************
 * Copyright (c) 2021-2022 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at 
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gloop.opengl.enums;

import org.lwjgl.opengl.GL30;

/**
 * Query wait types, for conditional rendering.
 * @author Matthew Tropiano
 */
public enum QueryWaitType
{
	/** Wait for query to complete. */
	WAIT(GL30.GL_QUERY_WAIT),
	/** Don't wait for query to complete, use current result state. */
	NO_WAIT(GL30.GL_QUERY_NO_WAIT),
	/** Wait for query to complete, but optionally discard based on framebuffer region. */
	BY_REGION_WAIT(GL30.GL_QUERY_BY_REGION_WAIT),
	/** Don't wait for query to complete and use current result state, but optionally discard based on framebuffer region. */
	BY_REGION_NO_WAIT(GL30.GL_QUERY_BY_REGION_NO_WAIT);
	
	public final int glValue;
	
	private QueryWaitType(int glValue) 
	{
		this.glValue = glValue;
	}
	
}
