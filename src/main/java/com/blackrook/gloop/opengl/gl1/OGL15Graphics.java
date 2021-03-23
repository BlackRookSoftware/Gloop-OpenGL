/*******************************************************************************
 * Copyright (c) 2021 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at 
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gloop.opengl.gl1;

import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

import com.blackrook.gloop.opengl.OGLGraphicsAbstract;
import com.blackrook.gloop.opengl.gl1.enums.FaceSide;
import com.blackrook.gloop.opengl.gl1.enums.FillMode;
import com.blackrook.gloop.opengl.gl1.enums.HintType;
import com.blackrook.gloop.opengl.gl1.enums.HintValue;
import com.blackrook.gloop.opengl.gl1.enums.MatrixType;

import static org.lwjgl.opengl.GL15.*;

/**
 * OpenGL 2.1 Graphics Implementation.
 * @author Matthew Tropiano
 */
public class OGL15Graphics extends OGLGraphicsAbstract implements OGL1XGraphics
{
	private static Set<String> EXTENSIONS;

	@Override
	public String getVersion()
	{
		return glGetString(GL_VERSION);
	}

	@Override
	public String getShadingLanguageVersion()
	{
		return null;
	}

	@Override
	public String getVendor()
	{
		return glGetString(GL_VENDOR);
	}

	@Override
	public String getRenderer()
	{
		return glGetString(GL_RENDERER);
	}

	@Override
	public Set<String> getExtensionNames()
	{
		if (EXTENSIONS == null)
		{
			Set<String> set = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
			set.addAll(Arrays.asList(glGetString(GL_EXTENSIONS).split("\\s+")));
			EXTENSIONS = set;
		}
		return EXTENSIONS;
	}

	@Override
	public void matrixType(MatrixType type)
	{
		glMatrixMode(type.glValue);
	}

	@Override
	public void matrixFrustum(double left, double right, double top, double bottom, double near, double far)
	{
		glFrustum(left, right, bottom, bottom, near, far);
	}

	@Override
	public void setClearColor(float clearRed, float clearGreen, float clearBlue, float clearAlpha)
	{
		glClearColor(clearRed, clearGreen, clearBlue, clearAlpha);
	}

	@Override
	public void setDepthClear(double depthValue)
	{
		glClearDepth(depthValue);
	}

	@Override
	public void clearFrameBuffers(boolean clearColorBuffer, boolean clearDepthBuffer, boolean clearAccumulationBuffer, boolean clearStencilBuffer)
	{
		glClear(
			(clearColorBuffer ? GL_COLOR_BUFFER_BIT : 0)
			| (clearDepthBuffer ? GL_DEPTH_BUFFER_BIT : 0)
			| (clearAccumulationBuffer ? GL_ACCUM_BUFFER_BIT : 0)
			| (clearStencilBuffer ? GL_STENCIL_BUFFER_BIT : 0)
		);
	}

	@Override
	public void setHint(HintType type, HintValue value)
	{
		glHint(type.glValue, value.glValue);
	}

	@Override
	public void setFillMode(FillMode mode)
	{
		glPolygonMode(FaceSide.FRONT_AND_BACK.glValue, mode.glValue);
	}

	@Override
	public void setFrontFillMode(FillMode fillMode)
	{
	   	glPolygonMode(FaceSide.FRONT.glValue, fillMode.glValue);
	}

	@Override
	public void setBackFillMode(FillMode fillMode)
	{
	   	glPolygonMode(FaceSide.BACK.glValue, fillMode.glValue);
	}

}
