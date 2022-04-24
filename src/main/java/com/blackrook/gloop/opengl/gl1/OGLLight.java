/*******************************************************************************
 * Copyright (c) 2021-2022 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at 
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gloop.opengl.gl1;

import java.awt.Color;

/**
 * An object that represents a light source.
 * @author Matthew Tropiano
 */
public class OGLLight
{
	/** The light's position in the OpenGL world. */
	private float[] position;

	private float constantAttenuation;
	/** This Light's linear attenuation. */
	private float linearAttenuation;
	/** This Light's quadratic attenuation. */
	private float quadraticAttenuation;
	
	/** Light ambient color. */
	private Color ambientColor;
	/** Light diffuse color. */
	private Color diffuseColor;
	/** Light specular color. */
	private Color specularColor;
	
	/**
	 * Creates a new Light.
	 */
	public OGLLight()
	{
		position = new float[4];
		setPosition(0,0,0,0);
		setAmbientColor(Color.BLACK);
		setDiffuseColor(Color.WHITE);
		setSpecularColor(Color.BLACK);
		setAttenuation(1, 0, 0);
	}
	
	public final float[] getPosition()
	{
		return position;
	}

	/** 
	 * Gets the light's eye position in the OpenGL world, X coordinate. 
	 * @return the x-coordinate.
	 */
	public final float getXPosition()
	{
		return position[0];
	}
	
	/** 
	 * Gets the light's eye position in the OpenGL world, Y coordinate. 
	 * @return the y-coordinate.
	 */
	public final float getYPosition()
	{
		return position[1];
	}
	
	/** 
	 * Gets the light's eye position in the OpenGL world, Z coordinate. 
	 * @return the z-coordinate.
	 */
	public final float getZPosition()
	{
		return position[2];
	}
	
	/** 
	 * Gets the light's position in the OpenGL world, W coordinate.
	 * 0 if ambient (direction), 1 if source.
	 * @return the w-coordinate.
	 */
	public final float getWPosition()
	{
		return position[3];
	}
	
	/** 
	 * Gets this Light's linear attenuation. 
	 * @return the coefficient.
	 */
	public final float getLinearAttenuation()
	{
		return linearAttenuation;
	}
	
	/** 
	 * Gets this Light's quadratic attenuation. 
	 * @return the coefficient.
	 */
	public final float getQuadraticAttenuation()
	{
		return quadraticAttenuation;
	}
	
	/** 
	 * Gets this Light's constant attenuation. 
	 * @return the coefficient.
	 */
	public final float getConstantAttenuation()
	{
		return constantAttenuation;
	}

	public final Color getAmbientColor()
	{
		return ambientColor;
	}

	public final Color getDiffuseColor()
	{
		return diffuseColor;
	}
	
	public final Color getSpecularColor()
	{
		return specularColor;
	}
	
	public final void setPosition(float x, float y, float z, float w)
	{
		setXPosition(x);
		setYPosition(y);
		setZPosition(z);
		setWPosition(w);
	}
	
	public final void setXPosition(float x)
	{
		position[0] = x;
	}
	
	public final void setYPosition(float y)
	{
		position[1] = y;
	}
	
	public final void setZPosition(float z)
	{
		position[2] = z;
	}
	
	public final void setWPosition(float w)
	{
		position[3] = w;
	}
	
	public final void setAttenuation(float constant, float linear, float quadratic)
	{
		setConstantAttenuation(constant);
		setLinearAttenuation(linear);
		setQuadraticAttenuation(quadratic);
	}
	
	public final void setLinearAttenuation(float linearAttenuation)
	{
		this.linearAttenuation = linearAttenuation;
	}
	
	public final void setQuadraticAttenuation(float quadraticAttenuation)
	{
		this.quadraticAttenuation = quadraticAttenuation;
	}
	
	public final void setConstantAttenuation(float constantAttenuation)
	{
		this.constantAttenuation = constantAttenuation;
	}

	public final void setAmbientColor(Color ambientColor)
	{
		this.ambientColor = ambientColor;
	}

	public final void setDiffuseColor(Color diffuseColor)
	{
		this.diffuseColor = diffuseColor;
	}
	
	public final void setSpecularColor(Color specularColor)
	{
		this.specularColor = specularColor;
	}
	
}
