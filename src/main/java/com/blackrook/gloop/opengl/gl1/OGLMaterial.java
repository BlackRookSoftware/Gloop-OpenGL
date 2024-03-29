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
 * Material attribute for drawing geometry that interacts with light.
 * @author Matthew Tropiano
 */
public class OGLMaterial
{
	public static final OGLMaterial
	
	DEFAULT = new OGLMaterial(
		new Color(0.2f, 0.2f, 0.2f, 1.0f),
		new Color(0.8f, 0.8f, 0.8f, 1.0f),
		new Color(0.0f, 0.0f, 0.0f, 1.0f),
		null,
		100,
		true
	),

	BRASS = new OGLMaterial(
		new Color(0.329412f, 0.223529f, 0.027451f, 1.0f),
		new Color(0.780392f, 0.568627f, 0.113725f, 1.0f),
		new Color(0.992157f, 0.941176f, 0.807843f, 1.0f),
		null,
		27.8974f,
		true
	),

	BRONZE = new OGLMaterial(
		new Color(0.2125f, 0.1275f, 0.054f, 1.0f),
		new Color(0.714f, 0.4284f, 0.18144f, 1.0f),
		new Color(0.393548f, 0.271906f, 0.166721f, 1.0f),
		null,
		25.6f,
		true
	),
	
	POLISHED_BRONZE = new OGLMaterial(
		new Color(0.25f, 0.148f, 0.06475f, 1.0f),
		new Color(0.4f, 0.2368f, 0.1036f, 1.0f),
		new Color(0.774597f, 0.458561f, 0.200621f, 1.0f),
		null,
		76.8f,
		true
	),
	
	CHROME = new OGLMaterial(
		new Color(0.25f, 0.25f, 0.25f, 1.0f),
		new Color(0.4f, 0.4f, 0.4f, 1.0f),
		new Color(0.774597f, 0.774597f, 0.774597f, 1.0f),
		null,
		76.8f,
		true
	),
	
	COPPER = new OGLMaterial(
		new Color(0.19125f, 0.0735f, 0.0225f, 1.0f),
		new Color(0.7038f, 0.27048f, 0.0828f, 1.0f),
		new Color(0.256777f, 0.137622f, 0.086014f, 1.0f),
		null,
		12.8f,
		true
	),
	
	POLISHED_COPPER = new OGLMaterial(
		new Color(0.2295f, 0.08825f, 0.0275f, 1.0f),
		new Color(0.5508f, 0.2118f, 0.066f, 1.0f),
		new Color(0.580594f, 0.223257f, 0.0695701f, 1.0f),
		null,
		51.2f,
		true
	),
	
	GOLD = new OGLMaterial(
		new Color(0.24725f, 0.1995f, 0.0745f, 1.0f),
		new Color(0.75164f, 0.60648f, 0.22648f, 1.0f),
		new Color(0.628281f, 0.555802f, 0.366065f, 1.0f),
		null,
		51.2f,
		true
	),
	
	POLISHED_GOLD = new OGLMaterial(
		new Color(0.24725f, 0.2245f, 0.0645f, 1.0f),
		new Color(0.34615f, 0.3143f, 0.0903f, 1.0f),
		new Color(0.797357f, 0.723991f, 0.208006f, 1.0f),
		null,
		83.2f,
		true
	),
	
	PEWTER = new OGLMaterial(
		new Color(0.105882f, 0.058824f, 0.113725f, 1.0f),
		new Color(0.427451f, 0.470588f, 0.541176f, 1.0f),
		new Color(0.333333f, 0.333333f, 0.521569f, 1.0f),
		null,
		9.84615f,
		true
	),

	SILVER = new OGLMaterial(
		new Color(0.19225f, 0.19225f, 0.19225f, 1.0f),
		new Color(0.50754f, 0.50754f, 0.50754f, 1.0f),
		new Color(0.508273f, 0.508273f, 0.508273f, 1.0f),
		null,
		51.2f,
		true
	),
	
	POLISHED_SILVER = new OGLMaterial(
		new Color(0.23125f, 0.23125f, 0.23125f, 1.0f),
		new Color(0.2775f, 0.2775f, 0.2775f, 1.0f),
		new Color(0.773911f, 0.773911f, 0.773911f, 1.0f),
		null,
		89.6f,
		true
	),
	
	EMERALD = new OGLMaterial(
		new Color(0.0215f, 0.1745f, 0.0215f, 0.55f),
		new Color(0.07568f, 0.61424f, 0.07568f, 0.55f),
		new Color(0.633f, 0.727811f, 0.633f, 0.55f),
		null,
		76.8f,
		true
	),

	JADE = new OGLMaterial(
		new Color(0.135f, 0.2225f, 0.1575f, 0.95f),
		new Color(0.54f, 0.89f, 0.63f, 0.95f),
		new Color(0.316228f, 0.316228f, 0.316228f, 0.95f),
		null,
		12.8f,
		true
	),

	OBSIDIAN = new OGLMaterial(
		new Color(0.05375f, 0.05f, 0.06625f, 0.82f),
		new Color(0.18275f, 0.17f, 0.22525f, 0.82f),
		new Color(0.332741f, 0.328634f, 0.346435f, 0.82f),
		null,
		38.4f,
		true
	),

	PEARL = new OGLMaterial(
		new Color(0.25f, 0.20725f, 0.20725f, 0.922f),
		new Color(1.0f, 0.829f, 0.829f, 0.922f),
		new Color(0.296648f, 0.296648f, 0.296648f, 0.922f),
		null,
		11.264f,
		true
	),

	RUBY = new OGLMaterial(
		new Color(0.1745f, 0.01175f, 0.01175f, 0.55f),
		new Color(0.61424f, 0.04136f, 0.04136f, 0.55f),
		new Color(0.727811f, 0.626959f, 0.626959f, 0.55f),
		null,
		76.8f,
		true
	),
	
	TURQUOISE = new OGLMaterial(
		new Color(0.1f, 0.18725f, 0.1745f, 0.8f),
		new Color(0.396f, 0.74151f, 0.69102f, 0.8f),
		new Color(0.297254f, 0.30829f, 0.306678f, 0.8f),
		null,
		12.8f,
		true
	),
	
	BLACK_PLASTIC = new OGLMaterial(
		new Color(0.0f, 0.0f, 0.0f, 1.0f),
		new Color(0.01f, 0.01f, 0.01f, 1.0f),
		new Color(0.5f, 0.5f, 0.5f, 1.0f),
		null,
		32.0f,
		true
	),

	BLACK_RUBBER = new OGLMaterial(
		new Color(0.02f, 0.02f, 0.02f, 1.0f),
		new Color(0.01f, 0.01f, 0.01f, 1.0f),
		new Color(0.4f, 0.4f, 0.4f, 1.0f),
		null,
		10.0f,
		true
	);

	/** Is read only? */
	private boolean readonly;
	
	/** Material ambient color. */
	private Color ambientColor;
	/** Material diffuse color. */
	private Color diffuseColor;
	/** Material specular color. */
	private Color specularColor;
	/** Material emissive color. */
	private Color emissiveColor;
	/** Material shininess. */
	private float shininess;

	/**
	 * Creates a new material.
	 */
	public OGLMaterial()
	{
		ambientColor = Color.BLACK;
		diffuseColor = Color.BLACK;
		specularColor = Color.BLACK;
		emissiveColor = Color.BLACK;
		shininess = 0f;
	}
	
	/**
	 * Creates a new material.
	 * @param ambient	the material's ambient color.
	 * @param diffuse	the material's diffuse color.
	 * @param specular	the material's specular color.
	 * @param emissive	the material's emissive color.
	 * @param shine		the shininess factor.
	 */
	public OGLMaterial(Color ambient, Color diffuse, Color specular, Color emissive, float shine)
	{
		if (ambient != null)
			setAmbientColor(ambient);
		if (diffuse != null)
			setDiffuseColor(diffuse);
		if (specular != null)
			setSpecularColor(specular);
		if (emissive != null)
			setEmissionColor(emissive);
		setShininess(shine);
	}
	
	private OGLMaterial(Color ambient, Color diffuse, Color specular, Color emissive, float shine, boolean readonly)
	{
		this(ambient, diffuse, specular, emissive, shine);
		this.readonly = readonly;
	}
	
	/**
	 * Set up this material's ambient color.
	 * @param ambient	the material's ambient color.
	 */
	public void setAmbientColor(Color ambient)
	{
		setAmbientColor(ambient.getRed(),ambient.getGreen(),ambient.getBlue(),ambient.getAlpha());
	}
	
	/**
	 * Set up this material's ambient color.
	 * @param red	the material's ambient color, red component.
	 * @param green	the material's ambient color, green component.
	 * @param blue	the material's ambient color, blue component.
	 * @param alpha	the material's ambient color, alpha component.
	 */
	public void setAmbientColor(float red, float green, float blue, float alpha)
	{
		if (readonly)
			throw new IllegalStateException("You cannot modify this material.");
		ambientColor = new Color(red, green, blue, alpha);
	}
	
	/**
	 * Set up this material's diffuse color.
	 * @param diffuse	the material's diffuse color.
	 */
	public void setDiffuseColor(Color diffuse)
	{
		setDiffuseColor(diffuse.getRed(),diffuse.getGreen(),diffuse.getBlue(),diffuse.getAlpha());
	}
	
	/**
	 * Set up this material's diffuse color.
	 * @param red	the material's ambient color, red component.
	 * @param green	the material's ambient color, green component.
	 * @param blue	the material's ambient color, blue component.
	 * @param alpha	the material's ambient color, alpha component.
	 */
	public void setDiffuseColor(float red, float green, float blue, float alpha)
	{
		if (readonly)
			throw new IllegalStateException("You cannot modify this material.");
		diffuseColor = new Color(red, green, blue, alpha);
	}
	
	/**
	 * Set up this material's specular color.
	 * @param specular	the material's specular color.
	 */
	public void setSpecularColor(Color specular)
	{
		setSpecularColor(specular.getRed(),specular.getGreen(),specular.getBlue(),specular.getAlpha());
	}
	
	/**
	 * Set up this material's specular color.
	 * @param red	the material's ambient color, red component.
	 * @param green	the material's ambient color, green component.
	 * @param blue	the material's ambient color, blue component.
	 * @param alpha	the material's ambient color, alpha component.
	 */
	public void setSpecularColor(float red, float green, float blue, float alpha)
	{
		if (readonly)
			throw new IllegalStateException("You cannot modify this material.");
		specularColor = new Color(red, green, blue, alpha);
	}
	
	/**
	 * Set up this material's emission color.
	 * @param emission	the material's emission color.
	 */
	public void setEmissionColor(Color emission)
	{
		setEmissionColor(emission.getRed(),emission.getGreen(),emission.getBlue(),emission.getAlpha());
	}
	
	/**
	 * Set up this material's emission color.
	 * @param red	the material's ambient color, red component.
	 * @param green	the material's ambient color, green component.
	 * @param blue	the material's ambient color, blue component.
	 * @param alpha	the material's ambient color, alpha component.
	 */
	public void setEmissionColor(float red, float green, float blue, float alpha)
	{
		if (readonly)
			throw new IllegalStateException("You cannot modify this material.");
		emissiveColor = new Color(red, green, blue, alpha);
	}

	/**
	 * Set this material's shininess.
	 * @param shine	the shininess factor.
	 */
	public void setShininess(float shine)
	{
		if (readonly)
			throw new IllegalStateException("You cannot modify this material.");
		shininess = shine;
	}

	/**
	 * @return this material's ambient color.
	 */
	public Color getAmbientColor()
	{
		return ambientColor;
	}

	/**
	 * @return this material's diffuse color.
	 */
	public Color getDiffuseColor()
	{
		return diffuseColor;
	}

	/**
	 * @return this material's specular color.
	 */
	public Color getSpecularColor()
	{
		return specularColor;
	}

	/**
	 * @return this material's emissive color.
	 */
	public Color getEmissionColor()
	{
		return emissiveColor;
	}

	/**
	 * @return this material's shininess factor.
	 */
	public float getShininess()
	{
		return shininess;
	}
	
}
