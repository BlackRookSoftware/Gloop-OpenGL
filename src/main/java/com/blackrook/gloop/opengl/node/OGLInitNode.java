/*******************************************************************************
 * Copyright (c) 2021 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at 
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gloop.opengl.node;

import com.blackrook.gloop.opengl.gl1.OGL1XGraphics;
import com.blackrook.gloop.opengl.gl1.enums.FillMode;
import com.blackrook.gloop.opengl.gl1.enums.HintType;
import com.blackrook.gloop.opengl.gl1.enums.HintValue;

/**
 * A one-time triggered node that performs usually one-time
 * OpenGL setup commands, like clear color, VSYNC, hints, or whatever.
 * <p>If you change anything on this node before it is triggered, you
 * will have to set it to be triggered again in order to take effect.
 * @author Matthew Tropiano
 */
public class OGLInitNode extends OGLTriggeredNode<OGL1XGraphics>
{
	/** Point smoothing hint. */
	private HintValue pointSmoothHint;
	/** Line smoothing hint. */
	private HintValue lineSmoothHint;
	/** Polygon smoothing hint. */
	private HintValue polygonSmoothHint;
	/** Fog calculation hint. */
	private HintValue fogCalculationHint;
	/** Perspective correction hint. */
	private HintValue perspectiveCorrectionHint;
	/** Generate Mipmapping hint. */
	private HintValue generateMipmapHint;
	/** Texture compression hint. */
	private HintValue textureCompressionHint;
	/** Global Fill mode. */
	private FillMode fillMode;

	/**
	 * Creates a new init node.<br>
	 * By default:
	 * <p>
	 * VSYNC is disabled.<br>
	 * All hints are set to {@link HintValue}.DONT_CARE;<br>
	 * The clear color is (0,0,0,0), RGBA.<br>
	 * The fill mode is FILLED.
	 */
	public OGLInitNode()
	{
		setAllHintsTo(HintValue.DONT_CARE);
		setFillMode(FillMode.FILLED);
	}
	
	@Override
	public void doTriggeredFunction(OGL1XGraphics gl)
	{
		gl.setHint(HintType.POINT_SMOOTHING, pointSmoothHint);
		gl.setHint(HintType.LINE_SMOOTHING, lineSmoothHint);
		gl.setHint(HintType.POLYGON_SMOOTHING, polygonSmoothHint);
		gl.setHint(HintType.FOG, fogCalculationHint);
		gl.setHint(HintType.MIPMAPPING, generateMipmapHint);
		gl.setHint(HintType.TEXTURE_COMPRESSION, textureCompressionHint);
		gl.setHint(HintType.PERSPECTIVE_CORRECTION, perspectiveCorrectionHint);
		gl.setFillMode(fillMode);
	}

	/**
	 * @return the current point smooth hint value.
	 */
	public HintValue getPointSmoothHint()
	{
		return pointSmoothHint;
	}

	/**
	 * @return the current line smooth hint value.
	 */
	public HintValue getLineSmoothHint()
	{
		return lineSmoothHint;
	}

	/**
	 * @return the current polygon smooth hint value.
	 */
	public HintValue getPolygonSmoothHint()
	{
		return polygonSmoothHint;
	}

	/**
	 * @return the current fog color calculation hint.
	 */
	public HintValue getFogCalculationHint()
	{
		return fogCalculationHint;
	}

	/**
	 * @return the current mipmap generation hint.
	 */
	public HintValue getGenerateMipmapHint()
	{
		return generateMipmapHint;
	}

	/**
	 * @return the current perspective correction hint. 
	 */
	public HintValue getPerspectiveCorrectionHint()
	{
		return perspectiveCorrectionHint;
	}

	/**
	 * @return the current texture compression hint.
	 */
	public HintValue getTextureCompressionHint()
	{
		return textureCompressionHint;
	}

	/**
	 * Set point smooth hint value.
	 * @param value the hint value. 
	 */
	public void setPointSmoothHint(HintValue value)
	{
		this.pointSmoothHint = value;
	}

	/**
	 * Set line smooth hint value.
	 * @param value the hint value. 
	 */
	public void setLineSmoothHint(HintValue value)
	{
		this.lineSmoothHint = value;
	}

	/**
	 * Set polygon smooth hint value.
	 * @param value the hint value. 
	 */
	public void setPolygonSmoothHint(HintValue value)
	{
		this.polygonSmoothHint = value;
	}

	/**
	 * Set fog calculation hint.
	 * @param value the hint value. 
	 */
	public void setFogCalculationHint(HintValue value)
	{
		this.fogCalculationHint = value;
	}

	/**
	 * Set mipmap generation hint.
	 * @param value the hint value. 
	 */
	public void setGenerateMipmapHint(HintValue value)
	{
		this.generateMipmapHint = value;
	}

	/**
	 * Set prespective correction hint. 
	 * @param value the hint value. 
	 */
	public void setPerspectiveCorrectionHint(HintValue value)
	{
		this.perspectiveCorrectionHint = value;
	}

	/**
	 * Set texture compression hint.
	 * @param value the hint value. 
	 */
	public void setTextureCompressionHint(HintValue value)
	{
		this.textureCompressionHint = value;
	}

	/**
	 * Sets all hint options to a single value.
	 * @param hint the value to set to.
	 */
	public void setAllHintsTo(HintValue hint)
	{
		setFogCalculationHint(hint);
		setGenerateMipmapHint(hint);
		setLineSmoothHint(hint);
		setPerspectiveCorrectionHint(hint);
		setPointSmoothHint(hint);
		setPolygonSmoothHint(hint);
		setTextureCompressionHint(hint);
	}

	/**
	 * Sets the global filling mode.
	 * @param fillMode the fill mode to use.
	 */
	public void setFillMode(FillMode fillMode)
	{
		this.fillMode = fillMode;
	}
	
	/**
	 * @return the current the global filling mode.
	 */
	public FillMode getFillMode()
	{
		return fillMode;
	}
	
}
