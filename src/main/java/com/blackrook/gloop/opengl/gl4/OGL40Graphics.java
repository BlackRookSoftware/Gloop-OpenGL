/*******************************************************************************
 * Copyright (c) 2021-2022 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at 
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gloop.opengl.gl4;

import com.blackrook.gloop.opengl.OGLVersion;
import com.blackrook.gloop.opengl.enums.QueryTarget;
import com.blackrook.gloop.opengl.gl1.OGLQuery;
import com.blackrook.gloop.opengl.OGLSystem.Options;
import com.blackrook.gloop.opengl.gl3.OGL33Graphics;

import java.nio.FloatBuffer;

import org.lwjgl.system.MemoryStack;

import static org.lwjgl.opengl.GL40.*;


/**
 * OpenGL 4.0 Graphics Implementation.
 * @author Matthew Tropiano
 */
public class OGL40Graphics extends OGL33Graphics
{
	protected class Info40 extends Info32
	{
		protected Info40()
		{
			super();
			this.maxGeometryShaderInvocations = getInt(GL_MAX_GEOMETRY_SHADER_INVOCATIONS);
			this.minFragmentInterpolationOffset = getInt(GL_MIN_FRAGMENT_INTERPOLATION_OFFSET);
			this.maxFragmentInterpolationOffset = getInt(GL_MAX_FRAGMENT_INTERPOLATION_OFFSET);
			this.maxSubroutines = getInt(GL_MAX_SUBROUTINES);
			this.maxSubroutineUniformLocations = getInt(GL_MAX_SUBROUTINE_UNIFORM_LOCATIONS);
			this.maxPatchVertices = getInt(GL_MAX_PATCH_VERTICES);
			this.maxTesselationGenLevel = getInt(GL_MAX_TESS_GEN_LEVEL);
			this.maxTesselationControlUniformComponents = getInt(GL_MAX_TESS_CONTROL_UNIFORM_COMPONENTS);
			this.maxTesselationEvaluationUniformComponents = getInt(GL_MAX_TESS_EVALUATION_UNIFORM_COMPONENTS);
			this.maxTesselationControlTextureImageUnits = getInt(GL_MAX_TESS_CONTROL_TEXTURE_IMAGE_UNITS);
			this.maxTesselationEvaluationTextureImageUnits = getInt(GL_MAX_TESS_EVALUATION_TEXTURE_IMAGE_UNITS);
			this.maxTesselationControlOutputComponents = getInt(GL_MAX_TESS_CONTROL_OUTPUT_COMPONENTS);
			this.maxTesselationPatchComponents = getInt(GL_MAX_TESS_PATCH_COMPONENTS);
			this.maxTesselationControlTotalOutputComponents = getInt(GL_MAX_TESS_CONTROL_TOTAL_OUTPUT_COMPONENTS);
			this.maxTesselationEvaluationOutputComponents = getInt(GL_MAX_TESS_EVALUATION_OUTPUT_COMPONENTS);
			this.maxTesselationControlUniformBlocks = getInt(GL_MAX_TESS_CONTROL_UNIFORM_BLOCKS);
			this.maxTesselationEvaluationUniformBlocks = getInt(GL_MAX_TESS_EVALUATION_UNIFORM_BLOCKS);
			this.maxTesselationControlInputComponents = getInt(GL_MAX_TESS_CONTROL_INPUT_COMPONENTS);
			this.maxTesselationEvaluationInputComponents = getInt(GL_MAX_TESS_EVALUATION_INPUT_COMPONENTS);
			this.maxCombinedTesselationControlUniformComponents = getInt(GL_MAX_COMBINED_TESS_CONTROL_UNIFORM_COMPONENTS);
			this.maxCombinedTesselationEvaluationUniformComponents = getInt(GL_MAX_COMBINED_TESS_EVALUATION_UNIFORM_COMPONENTS);
		}
	}
	
	public OGL40Graphics(Options options, boolean core)
	{
		super(options, core);
	}

	@Override
	public OGLVersion getVersion()
	{
		return OGLVersion.GL40;
	}

	@Override
	protected Info createInfo()
	{
		return new Info40();
	}
	
	/**
	 * Sets the tessellation patch inner levels for tessellation shaders.
	 * @param level1 the first level.
	 * @param level2 the second level.
	 */
	public void setTessellationPatchInnerLevel(float level1, float level2)
	{
		try (MemoryStack stack = MemoryStack.stackPush())
		{
			FloatBuffer fbuf = stack.mallocFloat(2);
			fbuf.put(0, level1);
			fbuf.put(1, level2);
			glPatchParameterfv(GL_PATCH_DEFAULT_INNER_LEVEL, fbuf);
		}
	}

	/**
	 * Sets the tessellation patch outer levels for tessellation shaders.
	 * @param level1 the first level.
	 * @param level2 the second level.
	 * @param level3 the third level.
	 * @param level4 the fourth level.
	 */
	public void setTessellationPatchOuterLevel(float level1, float level2, float level3, float level4)
	{
		try (MemoryStack stack = MemoryStack.stackPush())
		{
			FloatBuffer fbuf = stack.mallocFloat(4);
			fbuf.put(0, level1);
			fbuf.put(1, level2);
			fbuf.put(2, level3);
			fbuf.put(3, level4);
			glPatchParameterfv(GL_PATCH_DEFAULT_OUTER_LEVEL, fbuf);
		}
	}

	/**
	 * Starts an indexed query.
	 * The index corresponds to a query target-driven maximum.
	 * @param queryTarget the query target. 
	 * @param index the corresponding index for the target.
	 * @param query the query object to attach results to.
	 */
	public void startQueryIndexed(QueryTarget queryTarget, int index, OGLQuery query)
	{
		verifyFeatureSupport(queryTarget);
		glBeginQueryIndexed(queryTarget.glValue, index, query.getName());
		checkError();
	}
	
	/**
	 * Ends an indexed query.
	 * The index corresponds to a query target-driven maximum.
	 * @param queryTarget the query target. 
	 * @param index the corresponding index for the target.
	 */
	public void endQueryIndexed(QueryTarget queryTarget, int index)
	{
		glEndQueryIndexed(queryTarget.glValue, index);
		checkError();
	}
	
}
