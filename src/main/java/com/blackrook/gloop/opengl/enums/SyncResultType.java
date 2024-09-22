/*******************************************************************************
 * Copyright (c) 2021-2022 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at 
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gloop.opengl.enums;

import static org.lwjgl.opengl.GL32.*;

/**
 * Synchronizing result type.
 * @author Matthew Tropiano
 */
public enum SyncResultType
{
	/** Sync was already signaled at the time of call. */
	ALREADY_SIGNALED(GL_ALREADY_SIGNALED),
	/** Timeout expired before sync was signaled. */
	TIMEOUT_EXPIRED(GL_TIMEOUT_EXPIRED),
	/** Sync was signaled before timeout expired. */
	CONDITION_SATISFIED(GL_CONDITION_SATISFIED),
	/** Sync await failed. An GL error will be thrown. */
	WAIT_FAILED(GL_WAIT_FAILED);
	
	public final int glValue;
	private SyncResultType(int gltype) {glValue = gltype;}

}
