/*******************************************************************************
 * Copyright (c) 2021-2022 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at 
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gloop.opengl.enums;

import static org.lwjgl.opengl.GL15.*;

/**
 * Caching hint for vertex/pixel buffer data.
 * @author Matthew Tropiano
 */
public enum CachingHint
{
	/** Write once, use many, app to GL. */
	STATIC_DRAW(GL_STATIC_DRAW),
	/** Write once, use many, GL to app. */
	STATIC_READ(GL_STATIC_READ),
	/** Write once, use many, GL to GL. */
	STATIC_COPY(GL_STATIC_COPY),
	/** Write many, use many, app to GL. */
	DYNAMIC_DRAW(GL_DYNAMIC_DRAW),
	/** Write many, use many, GL to app. */
	DYNAMIC_READ(GL_DYNAMIC_READ),
	/** Write many, use many, GL to GL. */
	DYNAMIC_COPY(GL_DYNAMIC_COPY),
	/** Write once, use once, app to GL. */
	STREAM_DRAW(GL_STREAM_DRAW),
	/** Write once, use once, GL to app. */
	STREAM_READ(GL_STREAM_READ),
	/** Write once, use once, GL to GL. */
	STREAM_COPY(GL_STREAM_COPY);
	
	public final int glValue;
	private CachingHint (int val) {glValue = val;}

}
