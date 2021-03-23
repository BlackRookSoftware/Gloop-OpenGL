/*******************************************************************************
 * Copyright (c) 2021 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at 
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gloop.opengl.exception;

/**
 * This is commonly thrown when an exception occurs in the graphics system.
 * @author Matthew Tropiano
 */
public class GraphicsSystemException extends RuntimeException 
{
	private static final long serialVersionUID = -3748230790883081437L;

	public GraphicsSystemException()
	{
		super("Something couldn't be allocated.");
	}

	public GraphicsSystemException(String message)
	{
		super(message);
	}
}
