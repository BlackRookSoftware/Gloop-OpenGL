/*******************************************************************************
 * Copyright (c) 2021 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at 
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gloop.opengl.node;

import java.util.Set;

import com.blackrook.gloop.opengl.OGLGraphicsAbstract;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL21.*;
import static org.lwjgl.opengl.GL33.*;

/**
 * A node that gathers information from OpenGL to be queried later.
 * @author Matthew Tropiano
 */
public class OGLInfoNode extends OGLTriggeredNode<OGLGraphicsAbstract>
{
	/** OpenGL renderer name. */
	private String renderer;
	/** OpenGL version name. */
	private String version;
	/** OpenGL shader version name. */
	private String shaderVersion;
	/** OpenGL vendor name. */
	private String vendor;
	/** OpenGL list of extensions. */
	private Set<String> extensions;
	
	/** Are we running NVidia architecture? */
	private boolean isNVidia;
	/** Are we running AMD architecture? */
	private boolean isAMD;
	/** Are we running ATi architecture? */
	private boolean isATi;
	/** Are we running S3 architecture, and if so, WHY? */
	private boolean isS3;
	/** Are we running Matrox architecture? */
	private boolean isMatrox;
	/** Are we running Intel architecture? */
	private boolean isIntel;

	/** Flag for presence of occlusion query extension. */
	private boolean occlusionQueryExtensionPresent;
	/** Flag for presence of vertex shader extension. */
	private boolean vertexShaderExtensionPresent;
	/** Flag for presence of fragment shader extension. */
	private boolean fragmentShaderExtensionPresent;
	/** Flag for presence of geometry shader extension. */
	private boolean geometryShaderExtensionPresent;
	/** Flag for presence of render buffer extension. */
	private boolean renderBufferExtensionPresent;
	/** Flag for presence of vertex buffer extension. */
	private boolean vertexBufferExtensionPresent;
	/** Flag for presence of non-power-of-two texture support. */
	private boolean nonPowerOfTwoTextures;
	/** Flag for presence of point smoothing ability. */
	private boolean pointSmoothingPresent;
	/** Flag for presence of point sprite extension. */
	private boolean pointSpritesPresent;
	
	/** Maximum bindable lights. */
	private Integer maxLights;
	/** Maximum multitexture texture units. */
	private Integer maxMultitexture;
	/** Maximum texture units. */
	private Integer maxTextureUnits;
	/** Maximum texture size. */
	private Integer maxTextureSize;
	/** Maximum renderbuffer size. */
	private Integer maxRenderBufferSize;
	/** Maximum renderbuffer color attachments. */
	private Integer maxRenderBufferColorAttachments;
	/** Minimum point size range. */
	private Float minPointSize;
	/** Maximum point size range. */
	private Float maxPointSize;
	/** Minimum line width range. */
	private Float minLineWidth;
	/** Maximum line width range. */
	private Float maxLineWidth;

	@Override
	public void doTriggeredFunction(OGLGraphicsAbstract gl)
	{
		vendor = gl.getVendor();
		version = gl.getVersion();
		shaderVersion = gl.getShadingLanguageVersion();
		renderer = gl.getRenderer();
		extensions = gl.getExtensionNames();
		setArch(gl);
		setExtVars(gl);
	}

	// Sets the architecture flags.
	protected void setArch(OGLGraphicsAbstract gl)
	{
		String rend = new String(renderer.toLowerCase());
		isNVidia = rend.contains("nvidia");
		isAMD = rend.contains("amd");
		isATi = rend.contains("ati"); 
		isS3 = rend.contains("s3"); 
		isMatrox = rend.contains("matrox");
		isIntel = rend.contains("intel");
	}
	
	// Sets the extension flags and values.
	protected void setExtVars(OGLGraphicsAbstract gl)
	{
		occlusionQueryExtensionPresent = extensionIsPresent("gl_arb_occlusion_query");
		vertexShaderExtensionPresent = extensionIsPresent("gl_arb_vertex_program");
		fragmentShaderExtensionPresent = extensionIsPresent("gl_arb_fragment_program");
		geometryShaderExtensionPresent = 
			extensionIsPresent("gl_ext_geometry_program4") || 
			extensionIsPresent("gl_nv_geometry_shader4") || 
			extensionIsPresent("gl_arb_geometry_shader4");
		renderBufferExtensionPresent = extensionIsPresent("gl_ext_framebuffer_object");
		vertexBufferExtensionPresent = extensionIsPresent("gl_arb_vertex_buffer_object");
		nonPowerOfTwoTextures =
			extensionIsPresent("GL_ARB_texture_non_power_of_two") ||
			extensionIsPresent("GL_texture_rectangle_ext") ||
			extensionIsPresent("GL_texture_rectangle_nv") ||
			extensionIsPresent("GL_texture_rectangle_arb");
		pointSmoothingPresent = extensionIsPresent("gl_arb_point_smooth");
		pointSpritesPresent = extensionIsPresent("gl_arb_point_sprite");

		maxLights = gl.getInt(GL_MAX_LIGHTS);
		maxTextureSize = gl.getInt(GL_MAX_TEXTURE_SIZE);
		maxMultitexture = gl.getInt(GL_MAX_TEXTURE_UNITS);

		float[] FLOAT_STATE = new float[2];
		gl.getFloats(GL_POINT_SIZE_RANGE, FLOAT_STATE);
		minPointSize = FLOAT_STATE[0];
		maxPointSize = FLOAT_STATE[1];
		gl.getFloats(GL_LINE_WIDTH_RANGE, FLOAT_STATE);
		minLineWidth = FLOAT_STATE[0];
		maxLineWidth = FLOAT_STATE[1];

		maxTextureUnits = gl.getInt(GL_MAX_TEXTURE_IMAGE_UNITS);

		maxRenderBufferSize = gl.getInt(GL_MAX_RENDERBUFFER_SIZE);
		maxRenderBufferColorAttachments = gl.getInt(GL_MAX_COLOR_ATTACHMENTS);
	}

	/**
	 * Checks if an OpenGL extension is present.
	 * If you keep calling this method for the same extension, you are
	 * better off saving the results of the first call and using that, since
	 * the list of present extensions never change during runtime. 
	 * @param extensionName the extension name.
	 * @return true if so, false if not.
	 */
	public boolean extensionIsPresent(String extensionName)
	{
		return extensions.contains(extensionName);
	}

	/** 
	 * @return the maximum amount of lights. 
	 */
	public Integer getMaxLights()
	{
		return maxLights;
	}

	/**
	 * @return the maximum amount of multitexture units.
	 */
	public Integer getMaxMultitexture()
	{
		return maxMultitexture;
	}

	/**
	 * @return the maximum amount of bindable texture units.
	 */
	public Integer getMaxTextureUnits()
	{
		return maxTextureUnits;
	}

	/**
	 * @return max texture size in pixels.
	 */
	public Integer getMaxTextureSize()
	{
		return maxTextureSize;
	}

	/**
	 * @return the maximum size of a render buffer object in pixels.
	 */
	public Integer getMaxRenderBufferSize()
	{
		return maxRenderBufferSize;
	}

	/**
	 * @return the maximum amount of color buffer attachments for a render buffer.
	 */
	public Integer getMaxRenderBufferColorAttachments()
	{
		return maxRenderBufferColorAttachments;
	}

	/**
	 * @return the minimum size a point can be rendered.
	 */
	public Float getMinPointSize()
	{
		return minPointSize;
	}

	/**
	 * @return the maximum size a point can be rendered.
	 */
	public Float getMaxPointSize()
	{
		return maxPointSize;
	}

	/**
	 * @return the minimum width for line geometry.
	 */
	public Float getMinLineWidth()
	{
		return minLineWidth;
	}

	/**
	 * @return the maximum width for line geometry.
	 */
	public Float getMaxLineWidth()
	{
		return maxLineWidth;
	}

	/** 
	 * @return the rendering device of this GL system. 
	 */
	public String getRenderer()
	{
		return renderer;
	}

	/** 
	 * @return the version of this GL system. 
	 */
	public String getVersion()
	{
		return version;
	}

	/** 
	 * @return the vendor name of this GL system. 
	 */
	public String getVendor()
	{
		return vendor;
	}

	/** 
	 * @return the shader version of this GL system. 
	 */
	public String getShaderVersion()
	{
		return shaderVersion;
	}
	
	/**
	 * @return true if occlusion query extensions are present for the video device, false otherwise.
	 */
	public boolean supportsOcclusionQueries()
	{
		return occlusionQueryExtensionPresent;
	}

	/**
	 * @return true if vertex shader extensions are present for the video device, false otherwise.
	 */
	public boolean supportsVertexShaders()
	{
		return vertexShaderExtensionPresent;
	}

	/**
	 * @return true if fragment shader extensions are present for the video device, false otherwise.
	 */
	public boolean supportsFragmentShaders()
	{
		return fragmentShaderExtensionPresent;
	}

	/**
	 * @return true if geometry shader extensions are present for the video device, false otherwise.
	 */
	public boolean supportsGeometryShaders()
	{
		return geometryShaderExtensionPresent;
	}

	/**
	 * @return true if render buffer extensions are present for the video device, false otherwise.
	 */
	public boolean supportsRenderBuffers()
	{
		return renderBufferExtensionPresent;
	}

	/**
	 * @return true if vertex buffer extensions are present for the video device, false otherwise.
	 */
	public boolean supportsVertexBuffers()
	{
		return vertexBufferExtensionPresent;
	}

	/**
	 * @return true if this device supports non-power-of-two textures, false otherwise.
	 */
	public boolean supportsNonPowerOfTwoTextures()
	{
		return nonPowerOfTwoTextures;
	}

	/**
	 * @return true if this device supports smooth points, false otherwise.
	 */
	public boolean supportsPointSmoothing()
	{
		return pointSmoothingPresent;
	}

	/**
	 * @return true if this device supports point sprites, false otherwise.
	 */
	public boolean supportsPointSprites()
	{
		return pointSpritesPresent;
	}

	/** 
	 * @return true if this is running NVidia architecture.
	 */
	public boolean isNVidia()
	{
		return isNVidia;
	}

	/** 
	 * @return true if this is running ATi architecture.
	 */
	public boolean isATi()
	{
		return isATi;
	}

	/** 
	 * @return true if this is running AMD architecture.
	 */
	public boolean isAMD()
	{
		return isAMD;
	}

	/** 
	 * @return true if this is running S3 architecture.
	 */
	public boolean isS3()
	{
		return isS3;
	}

	/** 
	 * @return true if this is running Matrox architecture.
	 */
	public boolean isMatrox()
	{
		return isMatrox;
	}

	/** 
	 * @return true if this is running Intel architecture.
	 */
	public boolean isIntel()
	{
		return isIntel;
	}

}
