/*******************************************************************************
 * Copyright (c) 2021-2022 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at 
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gloop.opengl.exception;

/**
 * This is commonly thrown when a graphics object couldn't be allocated.
 * @author Matthew Tropiano
 */
public class GraphicsException extends RuntimeException 
{
	private static final long serialVersionUID = 2351998919562585908L;

	public GraphicsException()
	{
		super("A new graphics object couldn't be allocated.");
	}

	public GraphicsException(String message)
	{
		super(message);
	}
	
	public GraphicsException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
