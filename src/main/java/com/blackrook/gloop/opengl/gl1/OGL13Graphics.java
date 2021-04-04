/*******************************************************************************
 * Copyright (c) 2021 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at 
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gloop.opengl.gl1;

import com.blackrook.gloop.opengl.OGLVersion;
import com.blackrook.gloop.opengl.exception.GraphicsException;

import static org.lwjgl.opengl.GL13.*;

/**
 * OpenGL 1.3 Graphics Implementation.
 * @author Matthew Tropiano
 */
public class OGL13Graphics extends OGL12Graphics
{
	protected class Info13 extends Info11
	{
		protected Info13()
		{
			super();
			this.maxTextureUnits = getInt(GL_MAX_TEXTURE_UNITS);
		}
	}
	
	/** Current active texture unit. */
	private int currentActiveTexture;

	// Create OpenGL 1.3 context.
	public OGL13Graphics(boolean core)
	{
		super(core);
		this.currentActiveTexture = 0;
	}
	
	@Override
	public OGLVersion getVersion()
	{
		return OGLVersion.GL13;
	}
	
	@Override
	protected Info createInfo()
	{
		return new Info13();
	}
	
	@Override
	protected int getCurrentActiveTextureUnitState()
	{
		return currentActiveTexture;
	}
	
	@Override
	protected void setCurrentActiveTextureUnitState(int unit)
	{
		this.currentActiveTexture = unit;
	}
	
	/**
	 * @return the current "active" texture unit.
	 * @see #setTextureUnit(int)
	 */
	public int getTextureUnit()
	{
		return getCurrentActiveTextureUnitState();
	}
	
	/**
	 * Sets the current "active" texture unit for texture bindings and texture environment settings.
	 * @param unit the texture unit to switch to.
	 */
	public void setTextureUnit(int unit)
	{
		if (unit < 0 || unit >= getInfo().getMaxTextureUnits())
			throw new GraphicsException("Unit cannot be greater than " + getInfo().getMaxTextureUnits());
		
		glActiveTexture(GL_TEXTURE0 + unit);
		checkError();
		currentActiveTexture = unit;
	}

	/**
	 * Sets the current client active texture (for coordinates submission).
	 * @param unit the texture unit for binding.
	 */
	public void setCurrentActiveTextureCoordArray(int unit)
	{
		checkNonCore();
		glClientActiveTexture(GL_TEXTURE0 + unit);
	}

}
