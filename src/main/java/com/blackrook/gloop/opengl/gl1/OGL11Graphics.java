/*******************************************************************************
 * Copyright (c) 2021-2022 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at 
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gloop.opengl.gl1;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.lwjgl.system.MemoryStack;

import com.blackrook.gloop.opengl.OGLGraphics;
import com.blackrook.gloop.opengl.OGLVersion;
import com.blackrook.gloop.opengl.OGLSystem.Options;
import com.blackrook.gloop.opengl.enums.AccumOperation;
import com.blackrook.gloop.opengl.enums.AttribType;
import com.blackrook.gloop.opengl.enums.BlendArg;
import com.blackrook.gloop.opengl.enums.BlendFunc;
import com.blackrook.gloop.opengl.enums.BufferTargetType;
import com.blackrook.gloop.opengl.enums.ClientAttribType;
import com.blackrook.gloop.opengl.enums.ColorFormat;
import com.blackrook.gloop.opengl.enums.DataType;
import com.blackrook.gloop.opengl.enums.FaceSide;
import com.blackrook.gloop.opengl.enums.FillMode;
import com.blackrook.gloop.opengl.enums.FogFormulaType;
import com.blackrook.gloop.opengl.enums.FrameBufferType;
import com.blackrook.gloop.opengl.enums.GeometryType;
import com.blackrook.gloop.opengl.enums.HintType;
import com.blackrook.gloop.opengl.enums.HintValue;
import com.blackrook.gloop.opengl.enums.LightShadeType;
import com.blackrook.gloop.opengl.enums.LogicFunc;
import com.blackrook.gloop.opengl.enums.MatrixMode;
import com.blackrook.gloop.opengl.enums.StencilTestFunc;
import com.blackrook.gloop.opengl.enums.TextureCoordType;
import com.blackrook.gloop.opengl.enums.TextureFormat;
import com.blackrook.gloop.opengl.enums.TextureGenMode;
import com.blackrook.gloop.opengl.enums.TextureMagFilter;
import com.blackrook.gloop.opengl.enums.TextureMinFilter;
import com.blackrook.gloop.opengl.enums.TextureMode;
import com.blackrook.gloop.opengl.enums.TextureTargetType;
import com.blackrook.gloop.opengl.enums.TextureWrapType;
import com.blackrook.gloop.opengl.exception.GraphicsException;
import com.blackrook.gloop.opengl.math.Matrix4F;
import com.blackrook.gloop.opengl.math.MatrixStack;
import com.blackrook.gloop.opengl.util.TextureBuilder;
import com.blackrook.gloop.opengl.util.TextureUtils;

import static org.lwjgl.opengl.GL11.*;

/**
 * OpenGL 1.1 Graphics Implementation.
 * @author Matthew Tropiano
 */
public class OGL11Graphics extends OGLGraphics
{
	private static final ThreadLocal<Matrix4F> MATRIX = ThreadLocal.withInitial(()->new Matrix4F());

	/**
	 * Information about this context implementation.
	 */
	protected class Info11 extends Info
	{
		/**
		 * Creates a new info object.
		 */
		protected Info11()
		{
			this.vendor = glGetString(GL_VENDOR);
			this.version = glGetString(GL_VERSION);
			this.renderer = glGetString(GL_RENDERER);
			this.extensions = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);

			addExtensions(this.extensions);
			refreshExtensionFlags();

			String rend = new String(renderer.toLowerCase());
			this.isNVidia = rend.contains("nvidia");
			this.isAMD = rend.contains("amd");
			this.isATi = rend.contains("ati"); 
			this.isS3 = rend.contains("s3"); 
			this.isMatrox = rend.contains("matrox");
			this.isIntel = rend.contains("intel");
			
			this.maxLights = getInt(GL_MAX_LIGHTS);
			this.maxTextureSize = getInt(GL_MAX_TEXTURE_SIZE);

			float[] FLOAT_STATE = new float[2];
			getFloats(GL_POINT_SIZE_RANGE, FLOAT_STATE);
			this.minPointSize = FLOAT_STATE[0];
			this.maxPointSize = FLOAT_STATE[1];
			getFloats(GL_LINE_WIDTH_RANGE, FLOAT_STATE);
			this.minLineWidth = FLOAT_STATE[0];
			this.maxLineWidth = FLOAT_STATE[1];
		}
		
		/**
		 * Fetches extensions for this graphics instance.
		 * @param set the set to add it to.
		 */
		protected void addExtensions(Set<String> set)
		{
			set.addAll(Arrays.asList(glGetString(GL_EXTENSIONS).split("\\s+")));
		}
		
		/**
		 * Refreshes the extension-based fields.
		 */
		protected void refreshExtensionFlags()
		{
			this.occlusionQueryExtensionPresent = extensionIsPresent("gl_arb_occlusion_query");
			this.vertexShaderExtensionPresent = extensionIsPresent("gl_arb_vertex_program");
			this.fragmentShaderExtensionPresent = extensionIsPresent("gl_arb_fragment_program");
			this.geometryShaderExtensionPresent = 
				extensionIsPresent("gl_ext_geometry_program4") || 
				extensionIsPresent("gl_nv_geometry_shader4") || 
				extensionIsPresent("gl_arb_geometry_shader4");
			this.renderBufferExtensionPresent = extensionIsPresent("gl_ext_framebuffer_object");
			this.vertexBufferExtensionPresent = extensionIsPresent("gl_arb_vertex_buffer_object");
			this.nonPowerOfTwoTextures =
				extensionIsPresent("GL_ARB_texture_non_power_of_two") ||
				extensionIsPresent("GL_texture_rectangle_ext") ||
				extensionIsPresent("GL_texture_rectangle_nv") ||
				extensionIsPresent("GL_texture_rectangle_arb");
			this.pointSmoothingPresent = extensionIsPresent("gl_arb_point_smooth");
			this.pointSpritesPresent = extensionIsPresent("gl_arb_point_sprite");
			this.textureAnisotropyPresent = extensionIsPresent("gl_ext_texture_filter_anisotropic");

			if (textureAnisotropyPresent)
				this.maxTextureAnisotropy = getFloat(0x084FF);			
		}
		
	}

	/**
	 * Texture builder used for OpenGL 1.1.
	 */
	private static class OGL11TextureBuilder extends TextureBuilder.Abstract<OGL11Graphics>
	{
		protected OGL11TextureBuilder(OGL11Graphics gl)
		{
			super(gl);
		}

		@Override
		public TextureBuilder setCompressed(boolean enabled)
		{
			throw new UnsupportedOperationException("Texture compression is not supported in this implementation.");
		}
		
		@Override
		public TextureBuilder setAutoGenerateMipMaps(boolean autoGenerateMipMaps)
		{
			throw new UnsupportedOperationException("Mipmap auto-generation is not supported in this implementation.");
		}

		@Override
		public TextureBuilder setWrapping(TextureWrapType wrapS, TextureWrapType wrapT, TextureWrapType wrapR)
		{
			throw new UnsupportedOperationException("Three-dimensional wrapping is not supported in this implementation.");
		}
		
		@Override
		public OGLTexture create()
		{
			OGLTexture out = gl.createTexture();
			try {
				
				if (imageLevels.isEmpty())
					throw new GraphicsException("No data to store for texture.");
				
				// No 3D support, no compression, no auto mipmapgen, force RGBA.
				
				gl.setTexture(targetType, out);
				gl.setTextureFiltering(targetType, minFilter, magFilter, anisotropy);
				
				switch (targetType)
				{
					case TEXTURE_1D:
					{
						int i = 0;
						gl.setTextureWrapping(targetType, wrapS);
						for (BufferedImage[] imageArray : imageLevels)
						{
							gl.setTextureData(
								targetType, 
								TextureUtils.getRGBAByteData(imageArray[0]), 
								ColorFormat.RGBA, 
								TextureFormat.RGBA, 
								i, imageArray[0].getWidth(), border
							);
							i++;
						}
					}
					break;

					case TEXTURE_2D:
					{
						int i = 0;
						gl.setTextureWrapping(targetType, wrapS, wrapT);
						for (BufferedImage[] imageArray : imageLevels)
						{
							gl.setTextureData(
								targetType, 
								TextureUtils.getRGBAByteData(imageArray[0]), 
								ColorFormat.RGBA, 
								TextureFormat.RGBA, 
								i, imageArray[0].getWidth(), imageArray[0].getHeight(), border
							);
							i++;
						}
					}
					break;
					
					default:
						throw new GraphicsException("Unsupported texture target: " + targetType.name());
				}
				
			} catch (Exception e) {
				gl.destroyTexture(out);
				throw e;
			} finally {
				gl.unsetTexture(targetType);
			}
			
			return out;
		}
	}
	
	/** Current matrix id. */
	private Integer currentMatrixId;
	/** Current matrix stack. */
	private MatrixStack currentMatrixStack;
	/** Current matrix stacks per mode. */
	private Map<Integer, MatrixStack> currentMatrixStacks;
	/** Current bound textures per unit. */
	private Map<Integer, Map<Integer, OGLTexture>> currentTextures;
	
	// Create OpenGL 1.1 context.
	public OGL11Graphics(Options options, boolean core)
	{
		super(options, core);
		this.currentMatrixId = null;
		this.currentMatrixStack = null;
		this.currentMatrixStacks = new TreeMap<>();
		this.currentTextures = null;
	}
	
	@Override
	public OGLVersion getVersion()
	{
		return OGLVersion.GL11;
	}

	/**
	 * Gets the current texture state.
	 * Uses the current texture unit.
	 * @param targetId the texture target id.
	 * @return the current texture, or null if no current.
	 * @see #getCurrentActiveTextureUnitState()
	 */
	protected OGLTexture getCurrentActiveTextureState(int targetId)
	{
		int unit = getCurrentActiveTextureUnitState();
		Map<Integer, OGLTexture> stateMap;
		if (currentTextures == null)
			return null;
		else if ((stateMap = currentTextures.get(unit)) == null)
			return null;
		else
			return stateMap.get(targetId);
	}

	/**
	 * Sets the current texture state.
	 * Uses the current texture unit.
	 * @param targetId the texture target id.
	 * @param texture the texture to set.
	 * @see #getCurrentActiveTextureUnitState()
	 */
	protected void setCurrentActiveTextureState(int targetId, OGLTexture texture)
	{
		int unit = getCurrentActiveTextureUnitState();
		if (currentTextures == null)
			currentTextures = new TreeMap<>();
		
		Map<Integer, OGLTexture> stateMap;
		if ((stateMap = currentTextures.get(unit)) == null)
			currentTextures.put(unit, stateMap = new TreeMap<>());
		
		if (texture != null)
			stateMap.put(targetId, texture);
		else
			stateMap.remove(targetId);
	}

	/**
	 * @return the current "active" texture unit.
	 */
	protected int getCurrentActiveTextureUnitState()
	{
		return 0;
	}
	
	/**
	 * Sets the current texture unit state.
	 * @param unit the current "active" texture unit to set.
	 */
	protected void setCurrentActiveTextureUnitState(int unit)
	{
		// Do nothing.
	}
	
	/**
	 * @return the current matrix mode index.
	 */
	protected Integer getCurrentMatrixId()
	{
		return currentMatrixId;
	}
	
	/**
	 * Sets the current matrix id.
	 * @param currentMatrixId the new current matrix id.
	 */
	protected void setCurrentMatrixId(Integer currentMatrixId)
	{
		this.currentMatrixId = currentMatrixId;
		this.currentMatrixStack = resolveCurrentMatrixStack();
	}
	
	/**
	 * @return the current matrix stack, or null if no current matrix.
	 */
	protected MatrixStack getCurrentMatrixStack()
	{
		if (currentMatrixId == null)
			return null;
		return getCurrentMatrixStack(currentMatrixId);
	}
	
	/**
	 * @param id the matrix id.
	 * @return the current matrix stack, or null if no current matrix.
	 */
	protected MatrixStack getCurrentMatrixStack(int id)
	{
		if (currentMatrixStacks == null)
			currentMatrixStacks = new TreeMap<>();
		
		MatrixStack stack;
		if ((stack = currentMatrixStacks.get(id)) == null)
			currentMatrixStacks.put(id, (stack = new MatrixStack(64)));
		return stack;
	}
	
	/**
	 * @return the current matrix stack.
	 * @throws GraphicsException if no current matrix is set. 
	 */
	protected MatrixStack resolveCurrentMatrixStack()
	{
		MatrixStack stack;
		if ((stack = getCurrentMatrixStack()) == null)
			throw new GraphicsException("No current matrix.");
		return stack;
	}
	
	@Override
	protected Info createInfo()
	{
		return new Info11();
	}

	@Override
	protected void endFrame()
	{
		// Clean up abandoned objects.
		OGLTexture.destroyUndeleted();
	}

	/**
	 * Clears a bunch of the current framebuffers.
	 * <p> This is the non-core clear function, since it includes the accumulation buffer, optionally.
	 * @param clearColorBuffer if true, clear the color buffer.
	 * @param clearDepthBuffer if true, clear the depth buffer.
	 * @param clearAccumulationBuffer if true, clear the accumulation buffer.
	 * @param clearStencilBuffer if true, clear the stencil buffer.
	 */
	public void clear(boolean clearColorBuffer, boolean clearDepthBuffer, boolean clearAccumulationBuffer, boolean clearStencilBuffer)
	{
		verifyNonCore();
		glClear(
			(clearColorBuffer ? GL_COLOR_BUFFER_BIT : 0)
			| (clearDepthBuffer ? GL_DEPTH_BUFFER_BIT : 0)
			| (clearAccumulationBuffer ? GL_ACCUM_BUFFER_BIT : 0)
			| (clearStencilBuffer ? GL_STENCIL_BUFFER_BIT : 0)
		);
	}

	/**
	 * Sets the current color used for drawing polygons and other geometry.
	 * @param c the color to use.
	 */
	public void setColor(Color c)
	{
		setColorARGB(c.getRGB());
	}

	/**
	 * Sets the current color used for drawing polygons and other geometry.
	 * @param red the red component of the color to use (0 to 1).
	 * @param green the green component of the color to use (0 to 1).
	 * @param blue the blue component of the color to use (0 to 1).
	 * @param alpha the alpha component of the color to use (0 to 1).
	 */
	public void setColor(float red, float green, float blue, float alpha)
	{
		verifyNonCore();
		glColor4f(red, green, blue, alpha);
	}

	/**
	 * Sets the current color used for drawing polygons and other geometry using an ARGB integer.
	 * @param argb the 32-bit color as an integer.
	 */
	public void setColorARGB(int argb)
	{
		verifyNonCore();
		glColor4ub(
			(byte)((argb >>> 16) & 0x0ff),
			(byte)((argb >>> 8) & 0x0ff),
			(byte)(argb & 0x0ff),
			(byte)((argb >>> 24) & 0x0ff)
		);
	}

	/**
	 * Sets if lighting is enabled.
	 * @param enable true to enable, false to disable.
	 */
	public void setLightingEnabled(boolean enable)
	{
		verifyNonCore();
		setFlag(GL_LIGHTING, enable);
	}

	/**
	 * Sets the light shading type.
	 * @param shade the shading type. 
	 */
	public void setLightShadeType(LightShadeType shade)
	{
		verifyNonCore();
		glShadeModel(shade.glValue);
	}

	/**
	 * Verifies that the light source id is valid.
	 * @param sourceId the light source id.
	 * @throws IllegalArgumentException if the specified sourceId is not a valid one.
	 */
	protected void checkLightId(int sourceId)
	{
		verifyNonCore();
		if (sourceId < 0 || getInfo().getMaxLights() >= sourceId)
			throw new IllegalArgumentException("Light id is invalid: Must be " + 0 + " to " + getInfo().getMaxLights());
	}

	/**
	 * Sets if certain lights are enabled.
	 * @param sourceId the light source id.
	 * @param enable true to enable, false to disable.
	 * @throws IllegalArgumentException if the specified sourceId is not a valid one.
	 */
	public void setLightEnabled(int sourceId, boolean enable)
	{
		checkLightId(sourceId);
		setFlag(GL_LIGHT0 + sourceId, enable);
		checkError();
	}

	/**
	 * Sets the current light used for illuminating polygons and other geometry.
	 * This light will set all properties.
	 * @param sourceId the light source id. this cannot exceed the maximum number of lights
	 * that OpenGL can handle.
	 * @param light the Light to use.
	 * @throws IllegalArgumentException if the specified sourceId is not a valid one.
	 */
	public void setLight(int sourceId, OGLLight light)
	{
		checkLightId(sourceId);
		setLightPosition(sourceId, light.getXPosition(), light.getYPosition(), light.getZPosition(), light.getWPosition());
		setLightAmbientColor(sourceId, light.getAmbientColor());
		setLightDiffuseColor(sourceId, light.getDiffuseColor());
		setLightSpecularColor(sourceId, light.getSpecularColor());
		setLightAttenuation(sourceId, light.getConstantAttenuation(), light.getLinearAttenuation(), light.getQuadraticAttenuation());
	}

	/**
	 * Sets the current light attenuation used for illuminating polygons and other geometry.
	 * This alters light intensity at varying distances from the light.
	 * @param sourceId the light source id. this cannot exceed the maximum number of lights
	 * that OpenGL can handle.
	 * @param constant the constant coefficient.
	 * @param linear the linear coefficient.
	 * @param quadratic the quadratic coefficient.
	 * @throws IllegalArgumentException if the specified sourceId is not a valid one.
	 */
	public void setLightAttenuation(int sourceId, float constant, float linear, float quadratic)
	{
		checkLightId(sourceId);
		glLightf(GL_LIGHT0 + sourceId, GL_CONSTANT_ATTENUATION, constant);
		checkError();
		glLightf(GL_LIGHT0 + sourceId, GL_LINEAR_ATTENUATION, linear);
		checkError();
		glLightf(GL_LIGHT0 + sourceId, GL_QUADRATIC_ATTENUATION, quadratic);
		checkError();
	}

	/**
	 * Sets the color for a ambient component for a light. 
	 * @param sourceId the light source id.
	 * @param color the color to use.
	 * @throws IllegalArgumentException if the specified sourceId is not a valid one.
	 */
	public void setLightAmbientColor(int sourceId, Color color)
	{
		setLightAmbientColor(sourceId, color.getRGB());
	}

	/**
	 * Sets the color for a ambient component for a light. 
	 * @param sourceId the light source id.
	 * @param argbColor the ARGB color to set.
	 * @throws IllegalArgumentException if the specified sourceId is not a valid one.
	 */
	public void setLightAmbientColor(int sourceId, int argbColor)
	{
		setLightAmbientColor(sourceId, 			
			((0x00ff0000 & argbColor) >>> 16) / 255f,
			((0x0000ff00 & argbColor) >>> 8) / 255f,
			(0x000000ff & argbColor) / 255f,
			((0xff000000 & argbColor) >>> 24) / 255f
		);
	}

	/**
	 * Sets the color for a ambient component for a light. 
	 * @param sourceId the light source id.
	 * @param red the red component of the color to use (0 to 1).
	 * @param green the green component of the color to use (0 to 1).
	 * @param blue the blue component of the color to use (0 to 1).
	 * @param alpha the alpha component of the color to use (0 to 1).
	 * @throws IllegalArgumentException if the specified sourceId is not a valid one.
	 */
	public void setLightAmbientColor(int sourceId, float red, float green, float blue, float alpha)
	{
		checkLightId(sourceId);
		try (MemoryStack stack = MemoryStack.stackPush())
		{
			FloatBuffer fbuf = stack.mallocFloat(4);
			fbuf.put(0, red);
			fbuf.put(1, green);
			fbuf.put(2, blue);
			fbuf.put(3, alpha);
			glLightfv(GL_LIGHT0 + sourceId, GL_AMBIENT, fbuf);
		}
		checkError();
	}

	/**
	 * Sets the color for a diffuse component for a light. 
	 * @param sourceId the light source id.
	 * @param color the color to use.
	 * @throws IllegalArgumentException if the specified sourceId is not a valid one.
	 */
	public void setLightDiffuseColor(int sourceId, Color color)
	{
		setLightDiffuseColor(sourceId, color.getRGB());
	}

	/**
	 * Sets the color for a diffuse component for a light. 
	 * @param sourceId the light source id.
	 * @param argbColor the ARGB color to set.
	 * @throws IllegalArgumentException if the specified sourceId is not a valid one.
	 */
	public void setLightDiffuseColor(int sourceId, int argbColor)
	{
		setLightDiffuseColor(sourceId, 			
			((0x00ff0000 & argbColor) >>> 16) / 255f,
			((0x0000ff00 & argbColor) >>> 8) / 255f,
			(0x000000ff & argbColor) / 255f,
			((0xff000000 & argbColor) >>> 24) / 255f
		);
	}

	/**
	 * Sets the color for a diffuse component for a light. 
	 * @param sourceId the light source id.
	 * @param red the red component of the color to use (0 to 1).
	 * @param green the green component of the color to use (0 to 1).
	 * @param blue the blue component of the color to use (0 to 1).
	 * @param alpha the alpha component of the color to use (0 to 1).
	 * @throws IllegalArgumentException if the specified sourceId is not a valid one.
	 */
	public void setLightDiffuseColor(int sourceId, float red, float green, float blue, float alpha)
	{
		checkLightId(sourceId);
		try (MemoryStack stack = MemoryStack.stackPush())
		{
			FloatBuffer fbuf = stack.mallocFloat(4);
			fbuf.put(0, red);
			fbuf.put(1, green);
			fbuf.put(2, blue);
			fbuf.put(3, alpha);
			glLightfv(GL_LIGHT0 + sourceId, GL_DIFFUSE, fbuf);
		}
		checkError();
	}

	/**
	 * Sets the color for a specular component for a light. 
	 * @param sourceId the light source id.
	 * @param color the color to use.
	 * @throws IllegalArgumentException if the specified sourceId is not a valid one.
	 */
	public void setLightSpecularColor(int sourceId, Color color)
	{
		setLightSpecularColor(sourceId, color.getRGB());
	}

	/**
	 * Sets the color for a specular component for a light. 
	 * @param sourceId the light source id.
	 * @param argbColor the ARGB color to set.
	 * @throws IllegalArgumentException if the specified sourceId is not a valid one.
	 */
	public void setLightSpecularColor(int sourceId, int argbColor)
	{
		setLightSpecularColor(sourceId, 			
			((0x00ff0000 & argbColor) >>> 16) / 255f,
			((0x0000ff00 & argbColor) >>> 8) / 255f,
			(0x000000ff & argbColor) / 255f,
			((0xff000000 & argbColor) >>> 24) / 255f
		);
	}

	/**
	 * Sets the color for a specular component for a light. 
	 * @param sourceId the light source id.
	 * @param red the red component of the color to use (0 to 1).
	 * @param green the green component of the color to use (0 to 1).
	 * @param blue the blue component of the color to use (0 to 1).
	 * @param alpha the alpha component of the color to use (0 to 1).
	 * @throws IllegalArgumentException if the specified sourceId is not a valid one.
	 */
	public void setLightSpecularColor(int sourceId, float red, float green, float blue, float alpha)
	{
		checkLightId(sourceId);
		try (MemoryStack stack = MemoryStack.stackPush())
		{
			FloatBuffer fbuf = stack.mallocFloat(4);
			fbuf.put(0, red);
			fbuf.put(1, green);
			fbuf.put(2, blue);
			fbuf.put(3, alpha);
			glLightfv(GL_LIGHT0 + sourceId, GL_SPECULAR, fbuf);
		}
		checkError();
	}

	/**
	 * Sets the position of a light source. 
	 * @param sourceId the light source id.
	 * @param x the x-axis position.
	 * @param y the y-axis position.
	 * @param z the z-axis position.
	 * @param w if 0, the light is a directional one. If nonzero, positional.
	 * @throws IllegalArgumentException if the specified sourceId is not a valid one.
	 */
	public void setLightPosition(int sourceId, float x, float y, float z, float w)
	{
		checkLightId(sourceId);
		try (MemoryStack stack = MemoryStack.stackPush())
		{
			FloatBuffer fbuf = stack.mallocFloat(4);
			fbuf.put(0, x);
			fbuf.put(1, y);
			fbuf.put(2, z);
			fbuf.put(3, w);
			glLightfv(GL_LIGHT0 + sourceId, GL_POSITION, fbuf);
		}
		checkError();
	}

	/**
	 * Sets the current material used for drawing polygons and other geometry.
	 * Depending on what colors are set on the Material object, not all of the
	 * material calls will be made. This applies the Material properties to both
	 * polygon sides, and will remain doing so until this is changed.
	 * @param material the material to use.
	 */
	public void setMaterial(OGLMaterial material)
	{
		setMaterial(FaceSide.FRONT_AND_BACK, material);
	}

	/**
	 * Sets the current material used for drawing polygons and other geometry,
	 * and will remain doing so until this is changed.
	 * Depending on what colors are set on the Material object, not all of the
	 * material calls will be made. 
	 * @param faceside the face side to apply these properties to.
	 * @param material the material to use.
	 */
	public void setMaterial(FaceSide faceside, OGLMaterial material)
	{
		setMaterialAmbientColor(faceside, material.getAmbientColor());
		setMaterialDiffuseColor(faceside, material.getDiffuseColor());
		setMaterialSpecularColor(faceside, material.getSpecularColor());
		setMaterialEmissionColor(faceside, material.getEmissionColor());
		setMaterialShininessFactor(faceside, material.getShininess());
	}

	/**
	 * Sets the current material ambient color used for drawing polygons and other geometry.
	 * @param faceside the face side to apply these properties to.
	 * @param color the color to use.
	 */
	public void setMaterialAmbientColor(FaceSide faceside, Color color)
	{
		setMaterialAmbientColor(faceside, color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
	}

	/**
	 * Sets the current material ambient color used for drawing polygons and other geometry.
	 * @param faceside the face side to apply these properties to.
	 * @param argbColor the ARGB color to set.
	 */
	public void setMaterialAmbientColor(FaceSide faceside, int argbColor)
	{
		setMaterialAmbientColor(faceside,
			((0x00ff0000 & argbColor) >>> 16) / 255f,
			((0x0000ff00 & argbColor) >>> 8) / 255f,
			(0x000000ff & argbColor) / 255f,
			((0xff000000 & argbColor) >>> 24) / 255f
		);
	}

	/**
	 * Sets the current material ambient color used for drawing polygons and other geometry.
	 * @param faceside the face side to apply these properties to.
	 * @param red the red component of the color to use (0 to 1).
	 * @param green the green component of the color to use (0 to 1).
	 * @param blue the blue component of the color to use (0 to 1).
	 * @param alpha the alpha component of the color to use (0 to 1).
	 */
	public void setMaterialAmbientColor(FaceSide faceside, float red, float green, float blue, float alpha)
	{
		verifyNonCore();
		try (MemoryStack stack = MemoryStack.stackPush())
		{
			FloatBuffer fbuf = stack.mallocFloat(4);
			fbuf.put(0, red);
			fbuf.put(1, green);
			fbuf.put(2, blue);
			fbuf.put(3, alpha);
			glMaterialfv(faceside.glValue, GL_AMBIENT, fbuf);
		}
		checkError();
	}

	/**
	 * Sets the current material diffuse color used for drawing polygons and other geometry.
	 * @param faceside the face side to apply these properties to.
	 * @param color the color to use.
	 */
	public void setMaterialDiffuseColor(FaceSide faceside, Color color)
	{
		setMaterialDiffuseColor(faceside, color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
	}

	/**
	 * Sets the current material diffuse color used for drawing polygons and other geometry.
	 * @param faceside the face side to apply these properties to.
	 * @param argbColor the ARGB color to set.
	 */
	public void setMaterialDiffuseColor(FaceSide faceside, int argbColor)
	{
		setMaterialDiffuseColor(faceside,
			((0x00ff0000 & argbColor) >>> 16) / 255f,
			((0x0000ff00 & argbColor) >>> 8) / 255f,
			(0x000000ff & argbColor) / 255f,
			((0xff000000 & argbColor) >>> 24) / 255f
		);
	}

	/**
	 * Sets the current material diffuse color used for drawing polygons and other geometry.
	 * @param faceside the face side to apply these properties to.
	 * @param red the red component of the color to use (0 to 1).
	 * @param green the green component of the color to use (0 to 1).
	 * @param blue the blue component of the color to use (0 to 1).
	 * @param alpha the alpha component of the color to use (0 to 1).
	 */
	public void setMaterialDiffuseColor(FaceSide faceside, float red, float green, float blue, float alpha)
	{
		verifyNonCore();
		try (MemoryStack stack = MemoryStack.stackPush())
		{
			FloatBuffer fbuf = stack.mallocFloat(4);
			fbuf.put(0, red);
			fbuf.put(1, green);
			fbuf.put(2, blue);
			fbuf.put(3, alpha);
			glMaterialfv(faceside.glValue, GL_DIFFUSE, fbuf);
		}
		checkError();
	}

	/**
	 * Sets the current material specular color used for drawing polygons and other geometry.
	 * @param faceside the face side to apply these properties to.
	 * @param color	the color to use.
	 */
	public void setMaterialSpecularColor(FaceSide faceside, Color color)
	{
		setMaterialSpecularColor(faceside, color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
	}

	/**
	 * Sets the current material specular color used for drawing polygons and other geometry.
	 * @param faceside the face side to apply these properties to.
	 * @param argbColor the ARGB color to set.
	 */
	public void setMaterialSpecularColor(FaceSide faceside, int argbColor)
	{
		setMaterialSpecularColor(faceside,
			((0x00ff0000 & argbColor) >>> 16) / 255f,
			((0x0000ff00 & argbColor) >>> 8) / 255f,
			(0x000000ff & argbColor) / 255f,
			((0xff000000 & argbColor) >>> 24) / 255f
		);
	}

	/**
	 * Sets the current material specular color used for drawing polygons and other geometry.
	 * @param faceside the face side to apply these properties to.
	 * @param red the red component of the color to use (0 to 1).
	 * @param green the green component of the color to use (0 to 1).
	 * @param blue the blue component of the color to use (0 to 1).
	 * @param alpha the alpha component of the color to use (0 to 1).
	 */
	public void setMaterialSpecularColor(FaceSide faceside, float red, float green, float blue, float alpha)
	{
		verifyNonCore();
		try (MemoryStack stack = MemoryStack.stackPush())
		{
			FloatBuffer fbuf = stack.mallocFloat(4);
			fbuf.put(0, red);
			fbuf.put(1, green);
			fbuf.put(2, blue);
			fbuf.put(3, alpha);
			glMaterialfv(faceside.glValue, GL_SPECULAR, fbuf);
		}
		checkError();
	}

	/**
	 * Sets the current material emission color used for drawing polygons and other geometry.
	 * @param faceside	the face side to apply these properties to.
	 * @param color			the color to use.
	 */
	public void setMaterialEmissionColor(FaceSide faceside, Color color)
	{
		setMaterialEmissionColor(faceside, color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
	}

	/**
	 * Sets the current material emission color used for drawing polygons and other geometry.
	 * @param faceside the face side to apply these properties to.
	 * @param argbColor the ARGB color to set.
	 */
	public void setMaterialEmissionColor(FaceSide faceside, int argbColor)
	{
		setMaterialEmissionColor(faceside,
			((0x00ff0000 & argbColor) >>> 16) / 255f,
			((0x0000ff00 & argbColor) >>> 8) / 255f,
			(0x000000ff & argbColor) / 255f,
			((0xff000000 & argbColor) >>> 24) / 255f
		);
	}

	/**
	 * Sets the current material emission color used for drawing polygons and other geometry.
	 * @param faceside	the face side to apply these properties to.
	 * @param red the red component of the color to use (0 to 1).
	 * @param green the green component of the color to use (0 to 1).
	 * @param blue the blue component of the color to use (0 to 1).
	 * @param alpha the alpha component of the color to use (0 to 1).
	 */
	public void setMaterialEmissionColor(FaceSide faceside, float red, float green, float blue, float alpha)
	{
		verifyNonCore();
		try (MemoryStack stack = MemoryStack.stackPush())
		{
			FloatBuffer fbuf = stack.mallocFloat(4);
			fbuf.put(0, red);
			fbuf.put(1, green);
			fbuf.put(2, blue);
			fbuf.put(3, alpha);
			glMaterialfv(faceside.glValue, GL_EMISSION, fbuf);
		}
		checkError();
	}

	/**
	 * Sets the current material shininess factor used for drawing polygons and other geometry.
	 * As this number gets higher,
	 * @param faceside the face side to apply these properties to.
	 * @param f the factor.
	 */
	public void setMaterialShininessFactor(FaceSide faceside, float f)
	{
		verifyNonCore();
		glMaterialf(faceside.glValue, GL_SHININESS, f);		
	}

	/**
	 * Sets if fog rendering is enabled or disabled. 
	 * @param enabled true to enable, false to disable. 
	 */
	public void setFogEnabled(boolean enabled)
	{
		verifyNonCore();
		setFlag(GL_FOG, enabled);
	}

	/**
	 * Sets most fog attributes at once for linear fog.
	 * @param color the color of the fog.
	 * @param start the unit of space for the fog start (before that is no fog).
	 * @param end the unit of space for the fog end (after that is solid color).
	 * @see #setFogColor(Color)
	 * @see #setFogFormula(FogFormulaType)
	 * @see #setFogStart(float)
	 * @see #setFogEnd(float)
	 */
	public void setFogLinear(Color color, float start, float end)
	{
		setFogFormula(FogFormulaType.LINEAR);
		setFogColor(color);
		setFogStart(start);
		setFogEnd(end);
	}

	/**
	 * Sets most fog attributes at once for exponent fog.
	 * @param color the color of the fog.
	 * @param density the density factor to use.
	 * @see #setFogColor(Color)
	 * @see #setFogFormula(FogFormulaType)
	 * @see #setFogDensity(float)
	 */
	public void setFogExponent(Color color, float density)
	{
		setFogFormula(FogFormulaType.EXPONENT);
		setFogColor(color);
		setFogDensity(density);
	}

	/**
	 * Sets most fog attributes at once for exponent squared fog.
	 * @param color the color of the fog.
	 * @param density the density factor to use.
	 * @see #setFogColor(Color)
	 * @see #setFogFormula(FogFormulaType)
	 * @see #setFogDensity(float)
	 */
	public void setFogExponentSquared(Color color, float density)
	{
		setFogFormula(FogFormulaType.EXPONENT_SQUARED);
		setFogColor(color);
		setFogDensity(density);
	}

	/**
	 * Sets the color of the fog.
	 * @param color the color of the fog.
	 */
	public void setFogColor(Color color)
	{
		setFogColor(color.getRGB());
	}

	/**
	 * Sets the current color used for fog as an ARGB integer.
	 * @param argb the 32-bit color as an integer.
	 */
	public void setFogColor(int argb)
	{
		float a = ((argb & 0xFF000000) >>> 24) / 255f; 
		float r = ((argb & 0x00FF0000) >>> 16) / 255f; 
		float g = ((argb & 0x0000FF00) >>> 8) / 255f; 
		float b = (argb & 0x000000FF) / 255f;
		setFogColor(r, g, b, a);
	}

	/**
	 * Sets the color of the fog.
	 * @param red the red component of the color to use (0 to 1).
	 * @param green the green component of the color to use (0 to 1).
	 * @param blue the blue component of the color to use (0 to 1).
	 * @param alpha the alpha component of the color to use (0 to 1).
	 */
	public void setFogColor(float red, float green, float blue, float alpha)
	{
		verifyNonCore();
		try (MemoryStack stack = MemoryStack.stackPush())
		{
			FloatBuffer fbuf = stack.mallocFloat(4);
			fbuf.put(0, red);
			fbuf.put(1, green);
			fbuf.put(2, blue);
			fbuf.put(3, alpha);
			glFogfv(GL_FOG_COLOR, fbuf);
		}
		checkError();
	}

	/**
	 * Sets the distance calculation formula for calculating fog cover. 
	 * @param formula the formula to use.
	 */
	public void setFogFormula(FogFormulaType formula)
	{
		verifyNonCore();
		glFogi(GL_FOG_MODE, formula.glValue);
	}

	/**
	 * Sets the density factor for calculating fog.
	 * Only works for the exponential formulas.
	 * @param density the density factor to use.
	 */
	public void setFogDensity(float density)
	{
		verifyNonCore();
		glFogf(GL_FOG_DENSITY, density);
	}

	/**
	 * Sets the starting point for calculating fog.
	 * The value passed in is from the eye.
	 * @param start the unit of space for the fog start (before that is no fog).
	 */
	public void setFogStart(float start)
	{
		verifyNonCore();
		glFogf(GL_FOG_START, start);
	}

	/**
	 * Sets the starting point for calculating fog.
	 * The value passed in is from the eye.
	 * @param end the unit of space for the fog end (after that is solid color).
	 */
	public void setFogEnd(float end)
	{
		verifyNonCore();
		glFogf(GL_FOG_END, end);
	}

	/**
	 * Sets if a texture target is enabled for rendering or not.
	 * @param target the texture target.
	 * @param enabled true to enable, false to disable.
	 */
	public void setTextureEnabled(TextureTargetType target, boolean enabled)
	{
		verifyNonCore();
		verifyFeatureSupport(target);
		setFlag(target.glValue, enabled);
	}
	
	/**
	 * Sets the texture environment mode to use for texel fragment coloring.
	 * This is usually REPLACE, by default. Only viable in the fixed pipeline.
	 * @param mode the texture mode.
	 */
	public void setTextureEnvironment(TextureMode mode)
	{
		verifyNonCore();
		glTexEnvi(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, mode.glValue);
	}

	/**
	 * Sets if texture coordinates are to be automatically generated
	 * for the S coordinate axis (usually width).
	 * @param enabled true to enable, false to disable.
	 */
	public void setTexGenSEnabled(boolean enabled)
	{
		verifyNonCore();
		setFlag(GL_TEXTURE_GEN_S, enabled);
	}

	/**
	 * Sets if texture coordinates are to be automatically generated
	 * for the T coordinate axis (usually height).
	 * @param enabled true to enable, false to disable.
	 */
	public void setTexGenTEnabled(boolean enabled)
	{
		verifyNonCore();
		setFlag(GL_TEXTURE_GEN_T, enabled);
	}

	/**
	 * Sets if texture coordinates are to be automatically generated
	 * for the R coordinate axis (usually depth).
	 * @param enabled true to enable, false to disable.
	 */
	public void setTexGenREnabled(boolean enabled)
	{
		verifyNonCore();
		setFlag(GL_TEXTURE_GEN_R, enabled);
	}

	/**
	 * Sets if texture coordinates are to be automatically generated
	 * for the Q coordinate axis (I have no idea what the hell this could be).
	 * @param enabled true to enable, false to disable.
	 */
	public void setTexGenQEnabled(boolean enabled)
	{
		verifyNonCore();
		setFlag(GL_TEXTURE_GEN_Q, enabled);
	}

	/**
	 * Sets how texture coordinates are to be automatically generated.
	 * @param coord the texture coordinate to set the mode for.
	 * @param mode the generation function.
	 */
	public void setTexGenMode(TextureCoordType coord, TextureGenMode mode)
	{
		verifyNonCore();
		glTexGeni(coord.glValue, GL_TEXTURE_GEN_MODE, mode.glValue);
	}

	/**
	 * Sets the eye plane equation for generating coordinates using the eye method.
	 * @param coord	the texture coordinate to set the mode for.
	 * @param a	the plane A coordinate coefficient.
	 * @param b the plane B coordinate coefficient.
	 * @param c the plane C coordinate coefficient.
	 * @param d the plane D coordinate coefficient.
	 */
	public void setTexGenEyePlane(TextureCoordType coord, float a, float b, float c, float d)
	{
		verifyNonCore();
		try (MemoryStack stack = MemoryStack.stackPush())
		{
			FloatBuffer fbuf = stack.mallocFloat(4);
			fbuf.put(0, a);
			fbuf.put(1, b);
			fbuf.put(2, c);
			fbuf.put(3, d);
			glTexGenfv(coord.glValue, GL_EYE_PLANE, fbuf);
		}
		checkError();
	}

	/**
	 * Sets the object plane equation for generating coordinates using the object method.
	 * @param coord	the texture coordinate to set the mode for.
	 * @param a		the plane A coordinate coefficient.
	 * @param b		the plane B coordinate coefficient.
	 * @param c		the plane C coordinate coefficient.
	 * @param d		the plane D coordinate coefficient.
	 */
	public void setTexGenObjectPlane(TextureCoordType coord, float a, float b, float c, float d)
	{
		verifyNonCore();
		try (MemoryStack stack = MemoryStack.stackPush())
		{
			FloatBuffer fbuf = stack.mallocFloat(4);
			fbuf.put(0, a);
			fbuf.put(1, b);
			fbuf.put(2, c);
			fbuf.put(3, d);
			glTexGenfv(coord.glValue, GL_OBJECT_PLANE, fbuf);
		}
		checkError();
	}

	/**
	 * Sets if normal vectors are generated automatically when geometry is submitted to
	 * the OpenGL geometry pipeline.
	 * @param enabled true to enable, false to disable.
	 */
	public void setAutoNormalGen(boolean enabled)
	{
		verifyNonCore();
		setFlag(GL_AUTO_NORMAL, enabled);
	}

	/**
	 * Pushes an array of attributes onto the attribute stack.
	 * @param attribs the list of attributes to preserve.
	 */
	public void attribPush(AttribType ... attribs)
	{
		verifyNonCore();
		int bits = 0;
		for (AttribType at : attribs)
			bits |= at.glValue;
		glPushAttrib(bits);
	}

	/**
	 * Restores attributes from the attribute stack.
	 */
	public void attribPop()
	{
		verifyNonCore();
		glPopAttrib();
	}

	/**
	 * Pushes a series of attributes onto the client attribute stack.
	 * @param attribs the list of attributes to preserve.
	 */
	public void clientAttribPush(ClientAttribType ... attribs)
	{
		verifyNonCore();
		int bits = 0;
		for (ClientAttribType cat : attribs)
			bits |= cat.glValue;
		glPushClientAttrib(bits);
	}

	/**
	 * Restores attributes from the client attribute stack.
	 */
	public void clientAttribPop()
	{
		verifyNonCore();
		glPopClientAttrib();
	}

	/**
	 * Sets the clear color for the accumulation buffer.
	 * The accumulation buffer is filled with this color upon clear.
	 * @param clearRed the red component of the color to use (0 to 1).
	 * @param clearGreen the green component of the color to use (0 to 1).
	 * @param clearBlue the blue component of the color to use (0 to 1).
	 * @param clearAlpha the alpha component of the color to use (0 to 1).
	 */
	public void setClearAccum(float clearRed, float clearGreen, float clearBlue, float clearAlpha)
	{
		verifyNonCore();
		glClearAccum(clearRed, clearGreen, clearBlue, clearAlpha);
	}

	/**
	 * Performs an accumulation buffer operation.
	 * What happens with accum buffer contents differs based on the 
	 * current color buffers for reading and writing, and the desired operation.
	 * @param operation the accum buffer operation.
	 * @param value the value scalar for the operation.
	 * @see AccumOperation
	 */
	public void accumulate(AccumOperation operation, float value)
	{
		verifyNonCore();
		glAccum(operation.glValue, value);
	}

	/**
	 * Sets the next raster position for drawing bitmaps.
	 * Remember, (0,0) is the lower left edge of the window.
	 * @param x	the screen x-coordinate.
	 * @param y	the screen y-coordinate.
	 * @param z	the screen z-coordinate.
	 */
	public void setRasterPosition(int x, int y, float z)
	{
		verifyNonCore();
		glRasterPos3f(x, y, z);
	}

	/**
	 * Draws a Bitmap at the current raster position and increments the raster position.
	 * @param b	the Bitmap to draw ((0,0) is the lower-left).
	 * @param offsetX the offset from the current raster position, x-coordinate.
	 * @param offsetY the offset from the current raster position, y-coordinate.
	 * @param incX what to increment the raster position x-coordinate by after the draw.
	 * @param incY what to increment the raster position y-coordinate by after the draw.
	 */
	public void drawBitmap(OGLBitmap b, float offsetX, float offsetY, float incX, float incY)
	{
		verifyNonCore();
		try (MemoryStack stack = MemoryStack.stackPush())
		{
			byte[] bytes = b.getBytes();
			ByteBuffer buffer = stack.malloc(bytes.length);
			buffer.put(bytes);
			buffer.rewind();
			glBitmap(b.getWidth(), b.getHeight(), offsetX, offsetY, incX, incY, buffer);
		}
	}

	/**
	 * Enables or disables the processing of bound vertex arrays and/or buffers.
	 * @param enable true to enable, false to disable.
	 */
	public void setVertexArrayEnabled(boolean enable)
	{
		setClientFlag(GL_VERTEX_ARRAY, enable);
	}

	/**
	 * Sets what positions in the current {@link BufferTargetType#GEOMETRY}-bound buffer or array are used to draw polygonal information:
	 * This sets the attribute pointer for vertices.
	 * @param dataType the data type contained in the buffer that will be read (calculates actual sizes of data).
	 * @param width the width of a full set of coordinates (3-dimensional vertices = 3).
	 * @param stride the distance (in elements) between each vertex.    
	 * @param offset the offset in each stride where each vertex starts.  
	 * @see #setVertexArrayEnabled(boolean)   
	 */
	public void setVertexArrayPointer(DataType dataType, int width, int stride, int offset)
	{
		verifyNonCore();
		glVertexPointer(width, dataType.glValue, stride * dataType.size, offset * dataType.size);
		checkError();
	}

	/**
	 * Enables or disables the processing of bound texture coordinate arrays.
	 * @param enable true to enable, false to disable.
	 */
	public void setTextureCoordinateArrayEnabled(boolean enable)
	{
		setClientFlag(GL_TEXTURE_COORD_ARRAY, enable);
	}

	/**
	 * Sets what positions in the current {@link BufferTargetType#GEOMETRY}-bound buffer or array are used to draw polygonal information:
	 * This sets the attribute pointer for texture coordinates.
	 * @param dataType the data type contained in the buffer that will be read (calculates actual sizes of data).
	 * @param width the width of a full set of coordinates (2-dimensional coords = 2).
	 * @param stride the distance (in elements) between each coordinate group.     
	 * @param offset the offset in each stride where each coordinate starts.     
	 * @see #setTextureCoordinateArrayEnabled(boolean)   
	 */
	public void setTextureCoordinateArrayPointer(DataType dataType, int width, int stride, int offset)
	{
		verifyNonCore();
		glTexCoordPointer(width, dataType.glValue, stride * dataType.size, offset * dataType.size);
		checkError();
	}

	/**
	 * Enables or disables the processing of bound vertex color arrays.
	 * @param enable true to enable, false to disable.
	 */
	public void setColorArrayEnabled(boolean enable)
	{
		setClientFlag(GL_COLOR_ARRAY, enable);
	}

	/**
	 * Sets what positions in the current {@link BufferTargetType#GEOMETRY}-bound buffer or array are used to draw polygonal information:
	 * This sets the attribute pointer for colors.
	 * @param dataType the data type contained in the buffer that will be read (calculates actual sizes of data).
	 * @param width the width of a full set of color components (4-component color = 4).
	 * @param stride the distance (in elements) between each color.   
	 * @param offset the offset in each stride where each color starts.     
	 * @see #setColorArrayEnabled(boolean)   
	 */
	public void setColorArrayPointer(DataType dataType, int width, int stride, int offset)
	{
		verifyNonCore();
		glColorPointer(width, dataType.glValue, stride * dataType.size, offset * dataType.size);
		checkError();
	}

	/**
	 * Enables or disables the processing of bound surface normal arrays.
	 * @param enable true to enable, false to disable.
	 */
	public void setNormalArrayEnabled(boolean enable)
	{
		setClientFlag(GL_NORMAL_ARRAY, enable);
	}

	/**
	 * Sets what positions in the current {@link BufferTargetType#GEOMETRY}-bound buffer or array are used to draw polygonal information:
	 * This sets the attribute pointer for normal vectors. Always assumes 3-dimensional vectors.
	 * @param dataType the data type contained in the buffer that will be read (calculates actual sizes of data).
	 * @param stride the distance (in elements) between each normal.     
	 * @param offset the offset in each stride where each normal starts.     
	 * @see #setNormalArrayEnabled(boolean)   
	 */
	public void setNormalArrayPointer(DataType dataType, int stride, int offset)
	{
		verifyNonCore();
		glNormalPointer(dataType.glValue, stride * dataType.size, offset * dataType.size);
		checkError();
	}
	
	/* ==================================================================== */
	/*        VVVVVVVVVVVV Core Re-implementation Below VVVVVVVVVVVV        */
	/* ==================================================================== */

	/**
	 * Sets the current matrix for matrix operations.
	 * @param mode the matrix mode to set.
	 * @throws UnsupportedOperationException if matrix modes are unavailable in this version (core implementation).
	 * @throws NullPointerException if the mode is null.
	 */
	public void matrixMode(MatrixMode mode)
	{
		Objects.requireNonNull(mode);
		if (isCore())
			setCurrentMatrixId(mode.ordinal());
		else
			glMatrixMode(mode.glValue);
	}

	/**
	 * Loads the identity matrix into the current selected matrix.
	 * <p>This is technically not available in Core OpenGL, but is instead 
	 * implemented using {@link MatrixStack} for core implementations.  
	 */
	public void matrixReset()
	{
		if (isCore())
			currentMatrixStack.identity();
		else
			glLoadIdentity();
	}

	/**
	 * Pushes a copy of the current matrix onto the current selected stack.
	 * <p>This is technically not available in Core OpenGL, but is instead 
	 * implemented using {@link MatrixStack} for core implementations.  
	 */
	public void matrixPush()
	{
		if (isCore())
			currentMatrixStack.push();
		else
			glPushMatrix();
	}

	/**
	 * Pops the current matrix off of the current selected stack.
	 * <p>This is technically not available in Core OpenGL, but is instead 
	 * implemented using {@link MatrixStack} for core implementations.  
	 */
	public void matrixPop()
	{
		if (isCore())
			currentMatrixStack.pop();
		else
			glPopMatrix();
	}

	/**
	 * Reads a current matrix into an array.
	 * <p>This is technically not available in Core OpenGL, but is instead 
	 * implemented using {@link MatrixStack} for core implementations.  
	 * @param matrixType the type of matrix to load.
	 * @param outArray the output array. Must be length 16 or greater.
	 * @throws ArrayIndexOutOfBoundsException if the array length is less than 16.
	 * @throws UnsupportedOperationException if matrix modes are unavailable in this version (core implementation).
	 */
	public void matrixGet(MatrixMode matrixType, float[] outArray)
	{
		if (isCore())
			getCurrentMatrixStack(matrixType.ordinal()).peek().getFloats(outArray);
		else
			glGetFloatv(matrixType.glReadValue, outArray);
	}

	/**
	 * Reads a current matrix into a matrix.
	 * <p>This is technically not available in Core OpenGL, but is instead 
	 * implemented using {@link MatrixStack} for core implementations.  
	 * @param matrixType the type of matrix to load.
	 * @param matrix the output matrix.
	 * @throws UnsupportedOperationException if matrix modes are unavailable in this version (core implementation).
	 */
	public void matrixGet(MatrixMode matrixType, Matrix4F matrix)
	{
		if (isCore())
			matrix.set(getCurrentMatrixStack(matrixType.ordinal()).peek());
		else
			glGetFloatv(matrixType.glReadValue, matrix.getArray());
	}

	/**
	 * Loads a matrix's contents from a column-major array into the current selected matrix.
	 * <p>This is technically not available in Core OpenGL, but is instead 
	 * implemented using {@link MatrixStack} for core implementations.  
	 * @param matrixArray the column-major cells of a matrix.
	 */
	public void matrixSet(float[] matrixArray)
	{
		if (isCore())
			currentMatrixStack.set(matrixArray);
		else
		{
			if (matrixArray.length < 16)
				throw new GraphicsException("The array is less than 16 components.");
			glLoadMatrixf(matrixArray);
		}
	}

	/**
	 * Loads a matrix's contents into the current selected matrix.
	 * <p>This is technically not available in Core OpenGL, but is instead 
	 * implemented using {@link MatrixStack} for core implementations.  
	 * @param matrix the matrix to read from.
	 */
	public void matrixSet(Matrix4F matrix)
	{
		matrixSet(matrix.getArray());
	}

	/**
	 * Multiplies a matrix into the current selected matrix from a column-major array into.
	 * <p>This is technically not available in Core OpenGL, but is instead 
	 * implemented using {@link MatrixStack} for core implementations.  
	 * @param matrixArray the column-major cells of a matrix.
	 */
	public void matrixMultiply(float[] matrixArray)
	{
		if (isCore())
			currentMatrixStack.multiply(matrixArray);
		else
		{
			if (matrixArray.length < 16)
				throw new GraphicsException("The array is less than 16 components.");
			glMultMatrixf(matrixArray);
		}
	}

	/**
	 * Multiplies a matrix into the current selected matrix.
	 * <p>This is technically not available in Core OpenGL, but is instead 
	 * implemented using {@link MatrixStack} for core implementations.  
	 * @param matrix the matrix to read from.
	 */
	public void matrixMultiply(Matrix4F matrix)
	{
		matrixMultiply(matrix.getArray());
	}

	/**
	 * Translates the current matrix by a set of units.
	 * This is applied via multiplication with the current matrix.
	 * <p>This is technically not available in Core OpenGL, but is instead 
	 * implemented using {@link MatrixStack} for core implementations.  
	 * @param x the x-axis translation.
	 * @param y the y-axis translation.
	 * @param z the z-axis translation.
	 */
	public void matrixTranslate(float x, float y, float z)
	{
		if (isCore())
			currentMatrixStack.translate(x, y, z);
		else
			glTranslatef(x, y, z);
	}

	/**
	 * Rotates the current matrix by an amount of DEGREES around the X-Axis.
	 * This is applied via multiplication with the current matrix.
	 * <p>This is technically not available in Core OpenGL, but is instead 
	 * implemented using {@link MatrixStack} for core implementations.  
	 * @param degrees the amount of degrees.
	 */
	public void matrixRotateX(float degrees)
	{
		if (isCore())
			currentMatrixStack.rotateX(degrees);
		else
			glRotatef(degrees, 1, 0, 0);
	}

	/**
	 * Rotates the current matrix by an amount of DEGREES around the Y-Axis.
	 * This is applied via multiplication with the current matrix.
	 * <p>This is technically not available in Core OpenGL, but is instead 
	 * implemented using {@link MatrixStack} for core implementations.  
	 * @param degrees the amount of degrees.
	 */
	public void matrixRotateY(float degrees)
	{
		if (isCore())
			currentMatrixStack.rotateY(degrees);
		else
			glRotatef(degrees, 0, 1, 0);
	}

	/**
	 * Rotates the current matrix by an amount of DEGREES around the Z-Axis.
	 * This is applied via multiplication with the current matrix.
	 * <p>This is technically not available in Core OpenGL, but is instead 
	 * implemented using {@link MatrixStack} for core implementations.  
	 * @param degrees the amount of degrees.
	 */
	public void matrixRotateZ(float degrees)
	{
		if (isCore())
			currentMatrixStack.rotateZ(degrees);
		else
			glRotatef(degrees, 0, 0, 1);
	}

	/**
	 * Scales the current matrix by a set of scalars that 
	 * correspond to each axis.
	 * This is applied via multiplication with the current matrix.
	 * <p>This is technically not available in Core OpenGL, but is instead 
	 * implemented using {@link MatrixStack} for core implementations.  
	 * @param x the x-axis scalar.
	 * @param y the y-axis scalar.
	 * @param z the z-axis scalar.
	 */
	public void matrixScale(float x, float y, float z)
	{
		if (isCore())
			currentMatrixStack.scale(x, y, z);
		else
			glScalef(x, y, z);
	}

	/**
	 * Multiplies the current matrix by a symmetric perspective projection matrix.
	 * <p>This is technically not available in Core OpenGL, but is instead 
	 * implemented using {@link MatrixStack} for core implementations.  
	 * @param fov front of view angle in degrees.
	 * @param aspect the aspect ratio, usually view width over view height.
	 * @param near the near clipping plane on the Z-Axis.
	 * @param far the far clipping plane on the Z-Axis.
	 * @throws GraphicsException if <code>fov == 0 || aspect == 0 || near == far</code>.
	 */
	public void matrixPerspective(float fov, float aspect, float near, float far)
	{
		if (isCore())
			currentMatrixStack.perspective(fov, aspect, near, far);
		else
		{
			Matrix4F matrix = MATRIX.get();
			matrix.setPerspective(fov, aspect, near, far);
			matrixMultiply(matrix);
			checkError();
		}
	}

	/**
	 * Multiplies the current matrix by a frustum projection matrix.
	 * <p>This is technically not available in Core OpenGL, but is instead 
	 * implemented using {@link MatrixStack} for core implementations.  
	 * @param left the left clipping plane on the X-Axis.
	 * @param right the right clipping plane on the X-Axis.
	 * @param bottom the bottom clipping plane on the Y-Axis.
	 * @param top the upper clipping plane on the Y-Axis.
	 * @param near the near clipping plane on the Z-Axis.
	 * @param far the far clipping plane on the Z-Axis.
	 * @throws GraphicsException if <code>left == right || bottom == top || near == far</code>.
	 */
	public void matrixFrustum(float left, float right, float bottom, float top, float near, float far)
	{
		if (isCore())
			currentMatrixStack.frustum(left, right, bottom, top, near, far);
		else
		{
			glFrustum(left, right, bottom, top, near, far);
			checkError();
		}
	}

	/**
	 * Multiplies the current matrix by an orthographic projection matrix.
	 * <p>This is technically not available in Core OpenGL, but is instead 
	 * implemented using {@link MatrixStack} for core implementations.  
	 * @param left the left clipping plane on the X-Axis.
	 * @param right the right clipping plane on the X-Axis.
	 * @param bottom the bottom clipping plane on the Y-Axis.
	 * @param top the upper clipping plane on the Y-Axis.
	 * @param near the near clipping plane on the Z-Axis.
	 * @param far the far clipping plane on the Z-Axis.
	 * @throws GraphicsException if <code>left == right || bottom == top || near == far</code>.
	 */
	public void matrixOrtho(float left, float right, float bottom, float top, float near, float far)
	{
		if (isCore())
			currentMatrixStack.ortho(left, right, bottom, top, near, far);
		else
		{
			glOrtho(left, right, bottom, top, near, far);
			checkError();
		}
	}

	/**
	 * Multiplies the current matrix by an aspect-adjusted orthographic projection matrix using the canvas dimensions.
	 * <p>This is technically not available in Core OpenGL, but is instead 
	 * implemented using {@link MatrixStack} for core implementations.  
	 * @param targetAspect the target orthographic 
	 * @param left the left clipping plane on the X-Axis.
	 * @param right the right clipping plane on the X-Axis.
	 * @param bottom the bottom clipping plane on the Y-Axis.
	 * @param top the upper clipping plane on the Y-Axis.
	 * @param near the near clipping plane on the Z-Axis.
	 * @param far the far clipping plane on the Z-Axis.
	 * @throws GraphicsException if <code>left == right || bottom == top || near == far</code>.
	 */
	public void matrixAspectOrtho(float targetAspect, float left, float right, float bottom, float top, float near, float far)
	{
		if (isCore())
			currentMatrixStack.aspectOrtho(targetAspect, left, right, bottom, top, near, far);
		else
		{
			Matrix4F matrix = MATRIX.get();
			matrix.setAspectOrtho(targetAspect, left, right, bottom, top, near, far);
			matrixMultiply(matrix);
			checkError();
		}
	}

	/**
	 * Multiplies a "look at" matrix to the current matrix.
	 * This sets up the matrix to look at a place in the world (if modelview).
	 * <p>This is technically not available in Core OpenGL, but is instead 
	 * implemented using {@link MatrixStack} for core implementations.  
	 * @param eyeX the point to look at, X-coordinate.
	 * @param eyeY the point to look at, Y-coordinate.
	 * @param eyeZ the point to look at, Z-coordinate.
	 * @param centerX the reference point to look from, X-coordinate.
	 * @param centerY the reference point to look from, Y-coordinate.
	 * @param centerZ the reference point to look from, Z-coordinate.
	 * @param upX the up vector of the viewpoint, X-coordinate.
	 * @param upY the up vector of the viewpoint, Y-coordinate.
	 * @param upZ the up vector of the viewpoint, Z-coordinate.
	 */
	public void matrixLookAt(float eyeX, float eyeY, float eyeZ, float centerX, float centerY, float centerZ, float upX, float upY, float upZ)
	{
		if (isCore())
			currentMatrixStack.lookAt(eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX, upY, upZ);
		else
		{
			Matrix4F matrix = MATRIX.get();
			matrix.setLookAt(eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX, upY, upZ);
			matrixMultiply(matrix);
			checkError();
		}
	}

	/* ==================================================================== */
	/*                 VVVVVVVVVVVV Core Below VVVVVVVVVVVV                 */
	/* ==================================================================== */
	
	/**
	 * Tells the OpenGL implementation to finish all pending commands in finite time.
	 * This ensures that the next commands are executed immediately.
	 * Has little to no effect on a double-buffered setup.
	 * Not to be confused with {@link #finish()}.
	 * @see #finish()
	 */
	public void flush()
	{
		glFlush();
	}

	/**
	 * Tells the OpenGL implementation to finish all pending commands, and waits for it to do so.
	 * OpenGL commands are usually pipelined for performance reasons. This ensures
	 * that OpenGL finishes all pending commands so that what you expect in the target writable buffers
	 * is the last command executed, then resumes this thread.
	 * <p> NOTE: This best called right before a screenshot is taken.
	 */
	public void finish()
	{
		glFinish();
	}

	/**
	 * Sets an OpenGL hint.
	 * @param type the hint type to set.
	 * @param value the value to set for the provided hint.
	 */
	public void setHint(HintType type, HintValue value)
	{
		glHint(type.glValue, value.glValue);
	}

	/**
	 * Set polygon fill mode.
	 * @param mode the fill mode.
	 */
	public void setFillMode(FillMode mode)
	{
		glPolygonMode(FaceSide.FRONT_AND_BACK.glValue, mode.glValue);
	}

	/**
	 * Set front polygon fill mode.
	 * @param mode the fill mode.
	 */
	public void setFrontFillMode(FillMode mode)
	{
	   	glPolygonMode(FaceSide.FRONT.glValue, mode.glValue);
	}

	/**
	 * Set back polygon fill mode.
	 * @param mode the fill mode.
	 */
	public void setBackFillMode(FillMode mode)
	{
	   	glPolygonMode(FaceSide.BACK.glValue, mode.glValue);
	}

	/**
	 * Sets the OpenGL viewport (Note: (0,0) is the lower-left corner).
	 * If any value is below zero, it is clamped to zero.
	 * @param x x-coordinate origin of the screen.
	 * @param y y-coordinate origin of the screen.
	 * @param width	the width of the viewport in pixels.
	 * @param height the height of the viewport in pixels.
	 */
	public void setViewport(int x, int y, int width, int height)
	{
		glViewport(Math.max(0,x), Math.max(0,y), Math.max(0,width), Math.max(0,height));
	}

	/**
	 * Sets face winding to determine the front face.
	 * @param faceFront the front side.
	 */
	public void setFaceFront(FaceSide.Direction faceFront)
	{
		glFrontFace(faceFront.glValue);
	}

	/**
	 * Sets the clear color.
	 * The color buffer is filled with this color upon clear.
	 * @param clearRed the red component of the color to use (0 to 1).
	 * @param clearGreen the green component of the color to use (0 to 1).
	 * @param clearBlue the blue component of the color to use (0 to 1).
	 * @param clearAlpha the alpha component of the color to use (0 to 1).
	 */
	public void setClearColor(float clearRed, float clearGreen, float clearBlue, float clearAlpha)
	{
		glClearColor(clearRed, clearGreen, clearBlue, clearAlpha);
	}

	/**
	 * Sets depth clear value.
	 * If the depth buffer gets cleared, this is the value that is written to all of the pixels in the buffer.
	 * @param depthValue the depth value to set on clear.
	 */
	public void setClearDepth(double depthValue)
	{
		glClearDepth(depthValue);
	}

	/** 
	 * Sets the stencil mask clear value. 
	 * @param mask the mask bits.
	 */
	public void setClearStencil(int mask)
	{
		glClearStencil(mask);
	}

	/**
	 * Clears a bunch of current framebuffers.
	 * @param clearColorBuffer if true, clear the color buffer.
	 * @param clearDepthBuffer if true, clear the depth buffer.
	 * @param clearStencilBuffer if true, clear the stencil buffer.
	 */
	public void clear(boolean clearColorBuffer, boolean clearDepthBuffer, boolean clearStencilBuffer)
	{
		glClear(
			(clearColorBuffer ? GL_COLOR_BUFFER_BIT : 0)
			| (clearDepthBuffer ? GL_DEPTH_BUFFER_BIT : 0)
			| (clearStencilBuffer ? GL_STENCIL_BUFFER_BIT : 0)
		);
	}

	/**
	 * Sets if each color component gets written to the color buffer.
	 * @param red will the red component be written to the buffer?
	 * @param green	will the green component be written to the buffer?
	 * @param blue will the blue component be written to the buffer?
	 * @param alpha	will the alpha component be written to the buffer?
	 */
	public void setColorMask(boolean red, boolean green, boolean blue, boolean alpha)
	{
		glColorMask(red, green, blue, alpha);
	}

	/**
	 * Sets if all of the components of the color buffer get written to.
	 * @param enabled true to enable all components, false to disable.
	 */
	public void setColorMask(boolean enabled)
	{
		setColorMask(enabled, enabled, enabled, enabled);
	}

	/**
	 * Sets the reference unit size for the diameter of Point geometry.
	 * @param size the minimum size.
	 */
	public void setPointSize(float size)
	{
		glPointSize(size);
	}

	/**
	 * Enables/Disables smooth point geometry.
	 * @param enabled true to enable, false to disable.
	 */
	public void setPointSmoothingEnabled(boolean enabled)
	{
		setFlag(GL_POINT_SMOOTH, enabled);
	}

	/**
	 * Sets the width of line geometry.
	 * @param width the width of the line in pixels.
	 */
	public void setLineWidth(float width)
	{
		glLineWidth(width);
	}

	/**
	 * Enables/Disables line smoothing.
	 * "Line smoothing" is a fancy term for anti-aliasing.
	 * @param enabled true to enable, false to disable.
	 */
	public void setLineSmoothingEnabled(boolean enabled)
	{
		setFlag(GL_LINE_SMOOTH, enabled);
	}

	/**
	 * Sets the current buffer to read from for pixel read/copy operations.
	 * By default, this is the BACK buffer in double-buffered contexts.
	 * @param type the buffer to read from from now on.
	 * @throws IllegalArgumentException if type is NONE or FRONT_AND_BACK.
	 */
	public void setFrameBufferRead(FrameBufferType type)
	{
		if (type == FrameBufferType.FRONT_AND_BACK || type == FrameBufferType.NONE)
			throw new IllegalArgumentException("The read buffer can't be NONE nor FRONT AND BACK");
		glReadBuffer(type.glValue);
	}

	/**
	 * Sets the current buffer to write to for pixel drawing/rasterizing operations.
	 * By default, this is the BACK buffer in double-buffered contexts.
	 * @param type	the buffer to write to from now on.
	 * @throws IllegalArgumentException if type is NONE or FRONT_AND_BACK.
	 */
	public void setFrameBufferWrite(FrameBufferType type)
	{
		if (type == FrameBufferType.FRONT_AND_BACK || type == FrameBufferType.NONE)
			throw new IllegalArgumentException("The read buffer can't be NONE nor FRONT AND BACK");
		glDrawBuffer(type.glValue);
	}

	/**
	 * Reads from the current-bound frame buffer into a target buffer.
	 * @param imageData	the buffer to write the pixel data to (must be direct).
	 * @param colorFormat the color format to write to the buffer.
	 * @param x the starting screen offset, x-coordinate (0 is left).
	 * @param y the starting screen offset, y-coordinate (0 is bottom).
	 * @param width the capture width in pixels.
	 * @param height the capture height in pixels.
	 * @throws GraphicsException if the buffer provided is not direct.
	 */
	public void readFrameBuffer(ByteBuffer imageData, ColorFormat colorFormat, int x, int y, int width, int height)
	{
		if (!imageData.isDirect())
			throw new GraphicsException("Data must be a direct buffer.");
		verifyFeatureSupport(colorFormat);
		glReadPixels(x, y, width, height, colorFormat.glValue, GL_UNSIGNED_BYTE, imageData);
	}

	/**
	 * Sets the current pixel packing alignment value (GL-to-application).
	 * This is used for pulling pixel data from an OpenGL buffer into a format
	 * that the application can recognize/manipulate.
	 * @param alignment the alignment in bytes.
	 */
	public void setPixelPackAlignment(int alignment)
	{
		glPixelStorei(GL_PACK_ALIGNMENT, alignment);
	}

	/**
	 * Gets the current pixel packing alignment value (GL-to-application).
	 * This is used for pulling pixel data from an OpenGL buffer into a format
	 * that the application can recognize/manipulate.
	 * @return the packing alignment in bytes.
	 */
	public int getPixelPackAlignment()
	{
		return getInt(GL_PACK_ALIGNMENT);
	}

	/**
	 * Sets the current pixel unpacking alignment value (application-to-GL).
	 * This is used for pulling pixel data from an OpenGL buffer into a format
	 * that the application can recognize/manipulate.
	 * @param alignment the alignment in bytes.
	 */
	public void setPixelUnpackAlignment(int alignment)
	{
		glPixelStorei(GL_UNPACK_ALIGNMENT, alignment);
	}

	/**
	 * Gets the current pixel unpacking alignment value (application-to-GL).
	 * This is used for pulling pixel data from an OpenGL buffer into a format
	 * that the application can recognize/manipulate.
	 * @return the unpacking alignment in bytes.
	 */
	public int getPixelUnpackAlignment()
	{
		return getInt(GL_UNPACK_ALIGNMENT);
	}

	/**
	 * Sets if the depth test is enabled or not for incoming fragments.
	 * @param enabled true to enable, false to disable.
	 */
	public void setDepthTestEnabled(boolean enabled)
	{
		setFlag(GL_DEPTH_TEST, enabled);
	}

	/**
	 * Set depth comparison function.
	 * @param func the function to set.
	 */
	public void setDepthFunc(LogicFunc func)
	{
		glDepthFunc(func.glValue);
	}

	/** 
	 * Sets if the depth buffer is enabled for writing. 
	 * @param enabled true to enable, false to disable.
	 */
	public void setDepthMask(boolean enabled)
	{
		glDepthMask(enabled);
	}

	/** 
	 * Sets the stencil mask. 
	 * @param mask the mask bits.
	 */
	public void setStencilMask(int mask)
	{
		glStencilMask(mask);
	}

	/**
	 * Sets if the stencil test is enabled or not for incoming fragments.
	 * @param enabled true to enable, false to disable.
	 */
	public void setStencilTestEnabled(boolean enabled)
	{
		setFlag(GL_STENCIL_TEST, enabled);
	}

	/**
	 * Sets the stencil function for the stencil test.
	 * @param func the comparison function to use.
	 * @param ref the reference value.
	 * @param refmask the stencil mask bits.
	 */
	public void setStencilTestFunc(LogicFunc func, int ref, int refmask)
	{
		glStencilFunc(func.glValue, ref, refmask);
	}

	/**
	 * Sets the functions for what to do for each incoming fragment.
	 * @param stencilFail the function to perform if the stencil test fails.
	 * @param stencilDepthFail the function to perform if the stencil test passes, but the depth test fails (if enabled).
	 * @param stencilDepthPass the function to perform if the fragment passes, both the depth and stencil test.
	 */
	public void setStencilTestOperations(StencilTestFunc stencilFail, StencilTestFunc stencilDepthFail, StencilTestFunc stencilDepthPass)
	{
		glStencilOp(stencilFail.glValue, stencilDepthFail.glValue, stencilDepthPass.glValue);
	}

	/**
	 * Sets if the scissor test is enabled.
	 * @param enabled true to enable, false to disable.
	 */
	public void setScissorTestEnabled(boolean enabled)
	{
		setFlag(GL_SCISSOR_TEST, enabled);
	}

	/**
	 * Sets the bounds of the scissor test area.
	 * @param x	the lower left corner, x-coordinate.
	 * @param y the lower left corner, y-coordinate.
	 * @param width the width of scissor area from the lower-left corner.
	 * @param height the height of scissor area from the lower-left corner.
	 */
	public void setScissorBounds(int x, int y, int width, int height)
	{
		glScissor(x, y, width, height);
	}

	/**
	 * Sets if blending is enabled.
	 * @param enabled true to enable, false to disable.
	 */
	public void setBlendingEnabled(boolean enabled)
	{
		setFlag(GL_BLEND, enabled);
	}

	/**
	 * Sets the current blending function.
	 * @param source the source fragment argument.
	 * @param destination the destination fragment argument.
	 */
	public void setBlendingFunc(BlendArg source, BlendArg destination)
	{
		glBlendFunc(source.glValue, destination.glValue);
	}

	/**
	 * Sets the current blending function.
	 * @param func the function to set the fragment arguments.
	 */
	public void setBlendingFunc(BlendFunc func)
	{
		setBlendingFunc(func.argsrc, func.argdst);
	}

	/**
	 * Sets if face culling is enabled. 
	 * @param enabled true to enable, false to disable.
	 */
	public void setFaceCullingEnabled(boolean enabled)
	{
		setFlag(GL_CULL_FACE, enabled);
	}

	/**
	 * Sets the face side(s) that are culled if face culling is enabled.
	 * @param side the side to cull.
	 */
	public void setFaceCullingSide(FaceSide side)
	{
		glCullFace(side.glValue);
	}

	/**
	 * Creates a texture builder.
	 * <p> This texture builder aids in building texture objects, and its
	 * {@link TextureBuilder#create()} method will bind a new texture to its required target,
	 * send the data, set the filtering and build mipmaps, unbind the target, and return the new object.
	 * <p> Limitations on this implementation version are: No 3D support, no compression, no auto mipmapgen, force RGBA.
	 * @return a new texture builder.
	 */
	public TextureBuilder createTextureBuilder()
	{
		return new OGL11TextureBuilder(this);
	}
	
	/**
	 * Creates a new texture object.
	 * @return a new, uninitialized texture object.
	 * @throws GraphicsException if the object could not be created.
	 */
	public OGLTexture createTexture()
	{
		return new OGLTexture();
	}
	
	/**
	 * Destroys a texture object.
	 * @param texture the texture to destroy.
	 */
	public void destroyTexture(OGLTexture texture)
	{
		destroyObject(texture);
		checkError();
	}
	
	/**
	 * Gets a texture currently bound to a target. 
	 * @param target the texture target.
	 * @return the texture, or null if no bound texture.
	 * @throws UnsupportedOperationException if the target type is unavailable in this version.
	 */
	public OGLTexture getTexture(TextureTargetType target)
	{
		verifyFeatureSupport(target);
		return getCurrentActiveTextureState(target.glValue);
	}
	
	/**
	 * Binds a texture object to the current active texture unit on the specified target.
	 * This also sets the texture's target identity: if this texture from this point forward
	 * is bound to any other target type, an exception will be thrown.
	 * @param target the texture target.
	 * @param texture the texture to bind.
	 * @throws UnsupportedOperationException if the target type is unavailable in this version.
	 * @throws GraphicsException if the provided texture was previously bound to a different target.
	 */
	public void setTexture(TextureTargetType target, OGLTexture texture)
	{
		verifyFeatureSupport(target);
		Objects.requireNonNull(texture);
		texture.setUsedTarget(target);
		glBindTexture(target.glValue, texture.getName());
		setCurrentActiveTextureState(target.glValue, texture);
	}
	
	/**
	 * Sets the filtering for the current texture bound to the specified target.
	 * Assumes an anisotropy value of <code>1.0f</code> (not set if not supported).
	 * @param target the texture target.
	 * @param minFilter the minification filter.
	 * @param magFilter the magnification filter.
	 */
	public void setTextureFiltering(TextureTargetType target, TextureMinFilter minFilter, TextureMagFilter magFilter)
	{
		setTextureFiltering(target, minFilter, magFilter, 1.0f);
	}

	/**
	 * Sets the filtering for the current texture bound to the specified target.
	 * If anisotropic filtering is unsupported, the anisotropy filtering constant is ignored.
	 * @param target the texture target.
	 * @param minFilter the minification filter.
	 * @param magFilter the magnification filter.
	 * @param anisotropy the anisotropic filtering (2.0 or greater to enable, 1.0 is "off").
	 * @throws UnsupportedOperationException if the target type is unavailable in this version.
	 */
	public void setTextureFiltering(TextureTargetType target, TextureMinFilter minFilter, TextureMagFilter magFilter, float anisotropy)
	{
		verifyFeatureSupport(target);
		glTexParameteri(target.glValue, GL_TEXTURE_MAG_FILTER, magFilter.glid);
		glTexParameteri(target.glValue, GL_TEXTURE_MIN_FILTER, minFilter.glid);
		
		if (getInfo().supportsTextureAnisotropy())
		{
			anisotropy = Math.max(1.0f, Math.min(getInfo().getMaxTextureAnisotropy(), anisotropy));
			glTexParameterf(target.glValue, 0x084FE, anisotropy);
		}
	}

	/**
	 * Sets the current wrapping for the current texture bound to the specified target.
	 * @param target the texture target.
	 * @param wrapS the wrapping mode, S-axis.
	 * @throws UnsupportedOperationException if either provided type is unavailable in this version.
	 * @throws GraphicsException if the target is not a one-dimensionally-sampled target.
	 */
	public void setTextureWrapping(TextureTargetType target, TextureWrapType wrapS)
	{
		verifyFeatureSupport(target);
		verifyFeatureSupport(wrapS);
		target.checkSampleDimensions(1);
		glTexParameteri(target.glValue, GL_TEXTURE_WRAP_S, wrapS.glValue);
	}
	
	/**
	 * Sets the current wrapping for the current texture bound to the specified target.
	 * @param target the texture target.
	 * @param wrapS the wrapping mode, S-axis.
	 * @param wrapT the wrapping mode, T-axis.
	 * @throws UnsupportedOperationException if any provided type is unavailable in this version.
	 * @throws GraphicsException if the target is not a two-dimensionally-sampled target.
	 */
	public void setTextureWrapping(TextureTargetType target, TextureWrapType wrapS, TextureWrapType wrapT)
	{
		verifyFeatureSupport(target);
		verifyFeatureSupport(wrapS);
		verifyFeatureSupport(wrapT);
		target.checkSampleDimensions(2);
		glTexParameteri(target.glValue, GL_TEXTURE_WRAP_S, wrapS.glValue);
		glTexParameteri(target.glValue, GL_TEXTURE_WRAP_T, wrapT.glValue);
	}
	
	/**
	 * Sends a texture to OpenGL's memory for the current texture bound to the specified target.
	 * @param target the texture target.
	 * @param imageData the image to send.
	 * @param colorFormat the pixel storage format of the buffer data.
	 * @param format the internal texture format.
	 * @param texlevel the mipmapping level to copy this into (0 is topmost).
	 * @param width the texture width in texels.
	 * @param border the texel border to add, if any.
	 * @throws UnsupportedOperationException if any provided type or format is unavailable in this version.
	 * @throws GraphicsException if the buffer provided is not direct, or if the target is not stored one-dimensionally.
	 */
	public void setTextureData(TextureTargetType target, ByteBuffer imageData, ColorFormat colorFormat, TextureFormat format, int texlevel, int width, int border)
	{
		verifyFeatureSupport(target);
		verifyFeatureSupport(colorFormat);
		verifyFeatureSupport(format);
		target.checkStorageDimensions(1);

		if (width > getInfo().getMaxTextureSize())
			throw new GraphicsException("Texture is too large. Maximum width is "+ getInfo().getMaxTextureSize() + " pixels.");
		
		if (!imageData.isDirect())
			throw new GraphicsException("Data must be a direct buffer."); 

		clearError();
		glTexImage1D(
			target.glValue,
			texlevel,
			format.glValue, 
			width,
			border,
			colorFormat.glValue,
			GL_UNSIGNED_BYTE,
			imageData
		);
		checkError();
	}

	/**
	 * Sends a texture into OpenGL's memory for the current texture bound to the specified target.
	 * @param target the texture target.
	 * @param imageData the image to send.
	 * @param colorFormat the pixel storage format of the buffer data.
	 * @param format the internal format.
	 * @param texlevel the mipmapping level to copy this into (0 is topmost).
	 * @param width the texture width in texels.
	 * @param height the texture height in texels.
	 * @param border the texel border to add, if any.
	 * @throws UnsupportedOperationException if any provided type or format is unavailable in this version.
	 * @throws GraphicsException if the buffer provided is not direct, or if the target is not stored two-dimensionally.
	 */
	public void setTextureData(TextureTargetType target, ByteBuffer imageData, ColorFormat colorFormat, TextureFormat format, int texlevel, int width, int height, int border)
	{
		verifyFeatureSupport(target);
		verifyFeatureSupport(colorFormat);
		verifyFeatureSupport(format);
		target.checkStorageDimensions(2);

		if (width > getInfo().getMaxTextureSize() || height > getInfo().getMaxTextureSize())
			throw new GraphicsException("Texture is too large. Maximum size is " + getInfo().getMaxTextureSize() + " pixels.");
	
		if (!imageData.isDirect())
			throw new GraphicsException("Data must be a direct buffer."); 
		
		clearError();
		glTexImage2D(
			target.glValue,
			texlevel,
			format.glValue, 
			width,
			height,
			border,
			colorFormat.glValue,
			GL_UNSIGNED_BYTE,
			imageData
		);
		checkError();
	}

	/**
	 * Sends a subset of data to the current texture bound to the specified target already in OpenGL's memory.
	 * @param target the texture target.
	 * @param imageData the image to send.
	 * @param colorFormat the pixel storage format of the buffer data.
	 * @param texlevel the mipmapping level to copy this into (0 is topmost).
	 * @param width the texture width in texels.
	 * @param xoffs the texel offset.
	 * @throws UnsupportedOperationException if any provided type or format is unavailable in this version.
	 * @throws GraphicsException if the buffer provided is not direct, or if the target is not stored one-dimensionally.
	 */
	public void setTextureSubData(TextureTargetType target, ByteBuffer imageData, ColorFormat colorFormat, int texlevel, int width, int xoffs)
	{
		verifyFeatureSupport(target);
		verifyFeatureSupport(colorFormat);
		target.checkStorageDimensions(1);
		
		if (!imageData.isDirect())
			throw new GraphicsException("Data must be a direct buffer."); 

		clearError();
		glTexSubImage1D(
			target.glValue,
			texlevel,
			xoffs,
			width,
			colorFormat.glValue,
			GL_UNSIGNED_BYTE,
			imageData
		);
		checkError();
	}

	/**
	 * Sends a subset of data to the current texture bound to the specified target already in OpenGL's memory.
	 * @param target the texture target.
	 * @param imageData the image to send.
	 * @param colorFormat the pixel storage format of the buffer data.
	 * @param texlevel	the mipmapping level to copy this into (0 is topmost).
	 * @param width the texture width in texels.
	 * @param height the texture height in texels.
	 * @param xoffs the texel offset.
	 * @param yoffs the texel offset.
	 * @throws UnsupportedOperationException if any provided type or format is unavailable in this version.
	 * @throws GraphicsException if the buffer provided is not direct, or if the target is not stored two-dimensionally.
	 */
	public void setTextureSubData(TextureTargetType target, ByteBuffer imageData, ColorFormat colorFormat, int texlevel, int width, int height, int xoffs, int yoffs)
	{
		verifyFeatureSupport(target);
		verifyFeatureSupport(colorFormat);
		target.checkStorageDimensions(2);
		
		if (!imageData.isDirect())
			throw new GraphicsException("Data must be a direct buffer."); 
	
		clearError();
		glTexSubImage2D(
			target.glValue,
			texlevel,
			xoffs,
			yoffs,
			width,
			height,
			colorFormat.glValue,
			GL_UNSIGNED_BYTE,
			imageData
		);
		checkError();
	}

	/**
	 * Copies the contents of the current read frame buffer into the current texture bound to the specified target.
	 * @param target the texture target.
	 * @param format the internal texture format.
	 * @param texlevel the mipmapping level to copy this into (0 is topmost level).
	 * @param srcX the screen-aligned x-coordinate of what to grab from the buffer (0 is the left side of the screen).
	 * @param srcY the screen-aligned y-coordinate of what to grab from the buffer (0 is the bottom of the screen).
	 * @param width	the width of the screen in pixels to grab.
	 * @param border the texel border to add, if any.
	 * @throws UnsupportedOperationException if any provided type or format is unavailable in this version.
	 * @throws GraphicsException if the target is not stored one-dimensionally.
	 */
	public void setTextureDataFromReadBuffer(TextureTargetType target, TextureFormat format, int texlevel, int srcX, int srcY, int width, int border)
	{
		verifyFeatureSupport(target);
		verifyFeatureSupport(format);
		target.checkStorageDimensions(1);
		glCopyTexImage1D(target.glValue, texlevel, format.glValue, srcX, srcY, width, border);
	}

	/**
	 * Copies the contents of the current read frame buffer into the current texture bound to the specified target.
	 * @param target the texture target.
	 * @param format the internal format.
	 * @param texlevel the mipmapping level to copy this into (0 is topmost level).
	 * @param srcX the screen-aligned x-coordinate of what to grab from the buffer (0 is the left side of the screen).
	 * @param srcY the screen-aligned y-coordinate of what to grab from the buffer (0 is the bottom of the screen).
	 * @param width the width of the screen in pixels to grab.
	 * @param height the height of the screen in pixels to grab.
	 * @param border the texel border to add, if any.
	 * @throws UnsupportedOperationException if any provided type or format is unavailable in this version.
	 * @throws GraphicsException if the target is not stored two-dimensionally.
	 */
	public void setTextureDataFromReadBuffer(TextureTargetType target, TextureFormat format, int texlevel, int srcX, int srcY, int width, int height, int border)
	{
		verifyFeatureSupport(target);
		verifyFeatureSupport(format);
		target.checkStorageDimensions(2);
		glCopyTexImage2D(target.glValue, texlevel, format.glValue, srcX, srcY, width, height, border);
	}

	/**
	 * Copies the contents of the current read frame buffer into the current texture bound to the specified target already in OpenGL's memory.
	 * @param target the texture target.
	 * @param texlevel the mipmapping level to copy this into (0 is topmost level).
	 * @param xoffset the offset in pixels on this texture (x-coordinate) to put this texture data.
	 * @param srcX the screen-aligned x-coordinate of what to grab from the buffer (0 is the left side of the screen).
	 * @param srcY the screen-aligned y-coordinate of what to grab from the buffer (0 is the bottom of the screen).
	 * @param width the width of the screen in pixels to grab.
	 * @throws UnsupportedOperationException if the target type is unavailable in this version.
	 * @throws GraphicsException if the target is not stored one-dimensionally.
	 */
	public void setTextureSubDataFromReadBuffer(TextureTargetType target, int texlevel, int xoffset, int srcX, int srcY, int width)
	{
		verifyFeatureSupport(target);
		target.checkStorageDimensions(1);
		glCopyTexSubImage1D(target.glValue, texlevel, xoffset, srcX, srcY, width);
	}

	/**
	 * Copies the contents of the current read frame buffer into the current texture bound to the specified target already in OpenGL's memory.
	 * @param target the texture target.
	 * @param texlevel the mipmapping level to copy this into (0 is topmost level).
	 * @param xoffset the offset in pixels on this texture (x-coordinate) to put this texture data.
	 * @param yoffset the offset in pixels on this texture (y-coordinate) to put this texture data.
	 * @param srcX the screen-aligned x-coordinate of what to grab from the buffer (0 is the left side of the screen).
	 * @param srcY the screen-aligned y-coordinate of what to grab from the buffer (0 is the bottom of the screen).
	 * @param width the width of the screen in pixels to grab.
	 * @param height the height of the screen in pixels to grab.
	 * @throws UnsupportedOperationException if the target type is unavailable in this version.
	 * @throws GraphicsException if the target is not stored two-dimensionally.
	 */
	public void setTextureSubDataFromReadBuffer(TextureTargetType target, int texlevel, int xoffset, int yoffset, int srcX, int srcY, int width, int height)
	{
		verifyFeatureSupport(target);
		target.checkStorageDimensions(2);
		glCopyTexSubImage2D(target.glValue, texlevel, xoffset, yoffset, srcX, srcY, width, height);
	}

	/**
	 * Unbinds a texture currently bound to a target.
	 * @param target the texture target.
	 * @throws UnsupportedOperationException if the target type is unavailable in this version.
	 */
	public void unsetTexture(TextureTargetType target)
	{
		verifyFeatureSupport(target);
		glBindTexture(target.glValue, 0);
		setCurrentActiveTextureState(target.glValue, null);
	}
	
	/**
	 * Draws geometry using the current bound, enabled coordinate arrays/buffers as data.
	 * @param geometryType the geometry type - tells how to interpret the data.
	 * @param offset the starting offset in the bound buffers (in elements).
	 * @param elementCount the number of elements to draw using bound buffers.
	 * NOTE: an element is in terms of array elements, so if the bound buffers describe the coordinates of 4 vertices,
	 * <code>elementCount</code> should be 4.
	 */
	public void drawGeometryArray(GeometryType geometryType, int offset, int elementCount)
	{
		glDrawArrays(geometryType.glValue, offset, elementCount);
		checkError();
	}
	
	/**
	 * Draws geometry using the current bound, enabled coordinate arrays/buffers as data, plus
	 * an element buffer to describe the ordering.
	 * @param geometryType the geometry type - tells how to interpret the data.
	 * @param dataType the data type of the indices in the {@link BufferTargetType#INDICES}-bound buffer (must be an unsigned type).
	 * @param count the amount of element indices to interpret in the {@link BufferTargetType#INDICES}-bound buffer.
	 * @param offset the starting offset in the index buffer (in elements).
	 */
	public void drawGeometryElements(GeometryType geometryType, DataType dataType, int count, int offset)
	{
		glDrawElements(geometryType.glValue, count, dataType.glValue, dataType.size * offset);
		checkError();
	}

}
