/*******************************************************************************
 * Copyright (c) 2021 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at 
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gloop.opengl.gl1;

import java.awt.Color;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

import org.lwjgl.system.MemoryStack;

import com.blackrook.gloop.opengl.OGLGraphics;
import com.blackrook.gloop.opengl.OGLVersion;
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
import com.blackrook.gloop.opengl.enums.TextureWrapType;
import com.blackrook.gloop.opengl.exception.GraphicsException;
import com.blackrook.gloop.opengl.math.Matrix4F;

import static org.lwjgl.opengl.GL11.*;

/**
 * OpenGL 1.1 Graphics Implementation.
 * @author Matthew Tropiano
 */
public class OGL11Graphics extends OGLGraphics
{
	private static ThreadLocal<Matrix4F> MATRIX = ThreadLocal.withInitial(()->new Matrix4F());
	
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

			Set<String> set = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
			set.addAll(Arrays.asList(glGetString(GL_EXTENSIONS).split("\\s+")));
			this.extensions = set;

			String rend = new String(renderer.toLowerCase());
			this.isNVidia = rend.contains("nvidia");
			this.isAMD = rend.contains("amd");
			this.isATi = rend.contains("ati"); 
			this.isS3 = rend.contains("s3"); 
			this.isMatrox = rend.contains("matrox");
			this.isIntel = rend.contains("intel");
			
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
			this.textureAnisotropyPresent = extensionIsPresent("ext_texture_filter_anisotropic");

			this.maxLights = getInt(GL_MAX_LIGHTS);
			this.maxTextureSize = getInt(GL_MAX_TEXTURE_SIZE);

			float[] FLOAT_STATE = new float[2];
			getFloats(GL_POINT_SIZE_RANGE, FLOAT_STATE);
			this.minPointSize = FLOAT_STATE[0];
			this.maxPointSize = FLOAT_STATE[1];
			getFloats(GL_LINE_WIDTH_RANGE, FLOAT_STATE);
			this.minLineWidth = FLOAT_STATE[0];
			this.maxLineWidth = FLOAT_STATE[1];

			if (textureAnisotropyPresent)
				this.maxTextureAnisotropy = getFloat(0x084FF);
		}
		
	}

	/** Current 1D texture binding. */
	private OGLTexture currentTexture1D;
	/** Current 2D texture binding. */
	private OGLTexture currentTexture2D;

	// Create OpenGL 1.1 context.
	public OGL11Graphics()
	{
		this.currentTexture1D = null;
		this.currentTexture2D = null;
	}
	
	@Override
	public OGLVersion getVersion()
	{
		return OGLVersion.GL11;
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
	 * Clears a bunch of fixed framebuffers.
	 * @param clearColorBuffer if true, clear the color buffer.
	 * @param clearDepthBuffer if true, clear the depth buffer.
	 * @param clearAccumulationBuffer if true, clear the accumulation buffer.
	 * @param clearStencilBuffer if true, clear the stencil buffer.
	 */
	public void clearFrameBuffers(boolean clearColorBuffer, boolean clearDepthBuffer, boolean clearAccumulationBuffer, boolean clearStencilBuffer)
	{
		glClear(
			(clearColorBuffer ? GL_COLOR_BUFFER_BIT : 0)
			| (clearDepthBuffer ? GL_DEPTH_BUFFER_BIT : 0)
			| (clearAccumulationBuffer ? GL_ACCUM_BUFFER_BIT : 0)
			| (clearStencilBuffer ? GL_STENCIL_BUFFER_BIT : 0)
		);
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
	 * Sets the light shading type.
	 * @param shade the shading type. 
	 */
	public void setShadeType(LightShadeType shade)
	{
		glShadeModel(shade.glValue);
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
	 * Sets the reference unit size for the diameter of Point geometry.
	 * @param size the minimum size.
	 */
	public void setPointSize(float size)
	{
		glPointSize(size);
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
	 * Tells the OpenGL implementation to finish all pending commands.
	 * OpenGL commands are usually pipelined for performance reasons. This ensures
	 * that OpenGL finishes all pending commands so that what you expect in the framebuffer
	 * is the last command executed, then resumes this thread.
	 * <p> NOTE: This best called right before a screenshot is taken.
	 */
	public void finish()
	{
		glFinish();
	}

	/**
	 * Sets the current matrix for matrix operations.
	 * Note that other commands may change this mode automatically.
	 * @param mode the matrix mode to set.
	 */
	public void matrixMode(MatrixMode mode)
	{
		glMatrixMode(mode.glValue);
	}

	/**
	 * Loads the identity matrix into the current selected matrix.
	 */
	public void matrixReset()
	{
		glLoadIdentity();
	}

	/**
	 * Pushes a copy of the current matrix onto the current selected stack.
	 */
	public void matrixPush()
	{
		glPushMatrix();
	}

	/**
	 * Pops the current matrix off of the current selected stack.
	 */
	public void matrixPop()
	{
		glPopMatrix();
	}

	/**
	 * Reads a current matrix into an array.
	 * @param matrixType the type of matrix to load.
	 * @param outArray the output array. Must be length 16 or greater.
	 */
	public void matrixGet(MatrixMode matrixType, float[] outArray)
	{
		glGetFloatv(matrixType.glReadValue, outArray);
	}

	/**
	 * Reads a current matrix into a matrix.
	 * @param matrixType the type of matrix to load.
	 * @param matrix the output matrix.
	 */
	public void matrixGet(MatrixMode matrixType, Matrix4F matrix)
	{
		glGetFloatv(matrixType.glReadValue, matrix.getArray());
	}

	/**
	 * Loads a matrix's contents from a column-major array into the current selected matrix.
	 * @param matrixArray the column-major cells of a matrix.
	 */
	public void matrixSet(float[] matrixArray)
	{
		if (matrixArray.length < 16)
			throw new GraphicsException("The array is less than 16 components.");
		glLoadMatrixf(matrixArray);
	}

	/**
	 * Loads a matrix's contents into the current selected matrix.
	 * @param matrix the matrix to read from.
	 */
	public void matrixSet(Matrix4F matrix)
	{
		matrixSet(matrix.getArray());
	}

	/**
	 * Multiplies a matrix into the current selected matrix from a column-major array into.
	 * @param matrixArray the column-major cells of a matrix.
	 */
	public void matrixMultiply(float[] matrixArray)
	{
		if (matrixArray.length < 16)
			throw new GraphicsException("The array is less than 16 components.");
		glMultMatrixf(matrixArray);
	}

	/**
	 * Multiplies a matrix into the current selected matrix.
	 * @param matrix the matrix to read from.
	 */
	public void matrixMultiply(Matrix4F matrix)
	{
		matrixMultiply(matrix.getArray());
	}

	/**
	 * Translates the current matrix by a set of units.
	 * This is applied via multiplication with the current matrix.
	 * @param x the x-axis translation.
	 * @param y the y-axis translation.
	 * @param z the z-axis translation.
	 */
	public void matrixTranslate(float x, float y, float z)
	{
		glTranslatef(x, y, z);
	}

	/**
	 * Rotates the current matrix by an amount of DEGREES around the X-Axis.
	 * This is applied via multiplication with the current matrix.
	 * @param degrees the amount of degrees.
	 */
	public void matrixRotateX(float degrees)
	{
		glRotatef(degrees, 1, 0, 0);
	}

	/**
	 * Rotates the current matrix by an amount of DEGREES around the Y-Axis.
	 * This is applied via multiplication with the current matrix.
	 * @param degrees the amount of degrees.
	 */
	public void matrixRotateY(float degrees)
	{
		glRotatef(degrees, 0, 1, 0);
	}

	/**
	 * Rotates the current matrix by an amount of DEGREES around the Z-Axis.
	 * This is applied via multiplication with the current matrix.
	 * @param degrees the amount of degrees.
	 */
	public void matrixRotateZ(float degrees)
	{
		glRotatef(degrees, 0, 0, 1);
	}

	/**
	 * Scales the current matrix by a set of scalars that 
	 * correspond to each axis.
	 * This is applied via multiplication with the current matrix.
	 * @param x the x-axis scalar.
	 * @param y the y-axis scalar.
	 * @param z the z-axis scalar.
	 */
	public void matrixScale(float x, float y, float z)
	{
		glScalef(x, y, z);
	}

	/**
	 * Multiplies the current matrix by a symmetric perspective projection matrix.
	 * @param fov front of view angle in degrees.
	 * @param aspect the aspect ratio, usually view width over view height.
	 * @param near the near clipping plane on the Z-Axis.
	 * @param far the far clipping plane on the Z-Axis.
	 * @throws GraphicsException if <code>fov == 0 || aspect == 0 || near == far</code>.
	 */
	public void matrixPerpective(float fov, float aspect, float near, float far)
	{
		Matrix4F matrix = MATRIX.get();
		matrix.setPerspective(fov, aspect, near, far);
		matrixMultiply(matrix);
		getError();
	}

	/**
	 * Multiplies the current matrix by a frustum projection matrix.
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
		glFrustum(left, right, bottom, top, near, far);
		getError();
	}

	/**
	 * Multiplies the current matrix by an orthographic projection matrix.
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
		glOrtho(left, right, bottom, top, near, far);
		getError();
	}

	/**
	 * Multiplies the current matrix by an aspect-adjusted orthographic projection matrix using the canvas dimensions.
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
		float viewWidth = Math.max(left, right) - Math.min(left, right);
		float viewHeight = Math.max(bottom, top) - Math.min(bottom, top);
		float viewAspect = viewWidth / viewHeight;
	    
	    if (targetAspect >= viewAspect)
	    {
	        float axis = targetAspect * viewHeight;
	        float widthDiff = (axis - viewWidth) / 2f;
	        right = left + viewWidth + widthDiff;
	        left = left - widthDiff;
	    }
	    else
	    {
	        float axis = (1.0f / targetAspect) * viewWidth;
	        float heightDiff = (axis - viewHeight) / 2f;
	        top = bottom + viewHeight + heightDiff;
	    	bottom = bottom - heightDiff;
	    }
		
	    matrixOrtho(left, right, bottom, top, near, far);	
	}

	/**
	 * Multiplies a "look at" matrix to the current matrix.
	 * This sets up the matrix to look at a place in the world (if modelview).
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
		Matrix4F matrix = MATRIX.get();
		matrix.setLookAt(eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX, upY, upZ);
		matrixMultiply(matrix);
		getError();
	}

	/**
	 * Pushes an array of attributes onto the attribute stack.
	 * @param attribs the list of attributes to preserve.
	 */
	public void attribPush(AttribType ... attribs)
	{
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
		glPopAttrib();
	}

	/**
	 * Pushes a series of attributes onto the client attribute stack.
	 * @param attribs the list of attributes to preserve.
	 */
	public void clientAttribPush(ClientAttribType ... attribs)
	{
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
		glPopClientAttrib();
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
		glColor4f(red, green, blue, alpha);
	}

	/**
	 * Sets the current color used for drawing polygons and other geometry using an ARGB integer.
	 * @param argb the 32-bit color as an integer.
	 */
	public void setColorARGB(int argb)
	{
		glColor4ub(
			(byte)((argb >>> 16) & 0x0ff),
			(byte)((argb >>> 8) & 0x0ff),
			(byte)(argb & 0x0ff),
			(byte)((argb >>> 24) & 0x0ff)
		);
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
	 * Sets if lighting is enabled.
	 * @param enable true to enable, false to disable.
	 */
	public void setLightingEnabled(boolean enable)
	{
		setFlag(GL_LIGHTING, enable);
	}

	/**
	 * Verifies that the light source id is valid.
	 * @param sourceId the light source id.
	 * @throws IllegalArgumentException if the specified sourceId is not a valid one.
	 */
	protected void checkLightId(int sourceId)
	{
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
		getError();
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
		getError();
		glLightf(GL_LIGHT0 + sourceId, GL_LINEAR_ATTENUATION, linear);
		getError();
		glLightf(GL_LIGHT0 + sourceId, GL_QUADRATIC_ATTENUATION, quadratic);
		getError();
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
		getError();
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
		getError();
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
		getError();
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
		getError();
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
		try (MemoryStack stack = MemoryStack.stackPush())
		{
			FloatBuffer fbuf = stack.mallocFloat(4);
			fbuf.put(0, red);
			fbuf.put(1, green);
			fbuf.put(2, blue);
			fbuf.put(3, alpha);
			glMaterialfv(faceside.glValue, GL_AMBIENT, fbuf);
		}
		getError();
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
		try (MemoryStack stack = MemoryStack.stackPush())
		{
			FloatBuffer fbuf = stack.mallocFloat(4);
			fbuf.put(0, red);
			fbuf.put(1, green);
			fbuf.put(2, blue);
			fbuf.put(3, alpha);
			glMaterialfv(faceside.glValue, GL_DIFFUSE, fbuf);
		}
		getError();
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
		try (MemoryStack stack = MemoryStack.stackPush())
		{
			FloatBuffer fbuf = stack.mallocFloat(4);
			fbuf.put(0, red);
			fbuf.put(1, green);
			fbuf.put(2, blue);
			fbuf.put(3, alpha);
			glMaterialfv(faceside.glValue, GL_SPECULAR, fbuf);
		}
		getError();
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
		try (MemoryStack stack = MemoryStack.stackPush())
		{
			FloatBuffer fbuf = stack.mallocFloat(4);
			fbuf.put(0, red);
			fbuf.put(1, green);
			fbuf.put(2, blue);
			fbuf.put(3, alpha);
			glMaterialfv(faceside.glValue, GL_EMISSION, fbuf);
		}
		getError();
	}

	/**
	 * Sets the current material shininess factor used for drawing polygons and other geometry.
	 * As this number gets higher,
	 * @param faceside the face side to apply these properties to.
	 * @param f the factor.
	 */
	public void setMaterialShininessFactor(FaceSide faceside, float f)
	{
		glMaterialf(faceside.glValue, GL_SHININESS, f);		
	}

	/**
	 * Sets if fog rendering is enabled or disabled. 
	 * @param enabled true to enable, false to disable. 
	 */
	public void setFogEnabled(boolean enabled)
	{
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
		try (MemoryStack stack = MemoryStack.stackPush())
		{
			FloatBuffer fbuf = stack.mallocFloat(4);
			fbuf.put(0, red);
			fbuf.put(1, green);
			fbuf.put(2, blue);
			fbuf.put(3, alpha);
			glFogfv(GL_FOG_COLOR, fbuf);
		}
		getError();
	}

	/**
	 * Sets the distance calculation formula for calculating fog cover. 
	 * @param formula the formula to use.
	 */
	public void setFogFormula(FogFormulaType formula)
	{
		glFogi(GL_FOG_MODE, formula.glValue);
	}

	/**
	 * Sets the density factor for calculating fog.
	 * Only works for the exponential formulas.
	 * @param density the density factor to use.
	 */
	public void setFogDensity(float density)
	{
		glFogf(GL_FOG_DENSITY, density);
	}

	/**
	 * Sets the starting point for calculating fog.
	 * The value passed in is from the eye.
	 * @param start the unit of space for the fog start (before that is no fog).
	 */
	public void setFogStart(float start)
	{
		glFogf(GL_FOG_START, start);
	}

	/**
	 * Sets the starting point for calculating fog.
	 * The value passed in is from the eye.
	 * @param end the unit of space for the fog end (after that is solid color).
	 */
	public void setFogEnd(float end)
	{
		glFogf(GL_FOG_END, end);
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
	public void setColorBufferWriteEnabled(boolean enabled)
	{
		setColorMask(enabled, enabled, enabled, enabled);
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
		checkFeatureVersion(colorFormat);
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
	 * Sets depth clear value.
	 * If the depth buffer gets cleared, this is the value that is written to all of the pixels in the buffer.
	 * @param depthValue the depth value to set on clear.
	 */
	public void setDepthClear(double depthValue)
	{
		glClearDepth(depthValue);
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
	 s*/
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
	 * Sets the next raster position for drawing bitmaps.
	 * Remember, (0,0) is the lower left edge of the window.
	 * @param x	the screen x-coordinate.
	 * @param y	the screen y-coordinate.
	 * @param z	the screen z-coordinate.
	 */
	public void setRasterPosition(int x, int y, float z)
	{
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
	 * Sets the texture environment mode to use for texel fragment coloring.
	 * This is usually REPLACE, by default.
	 * @param mode the texture mode.
	 */
	public void setTextureEnvironment(TextureMode mode)
	{
		glTexEnvi(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, mode.glValue);
	}

	/**
	 * Sets if texture coordinates are to be automatically generated
	 * for the S coordinate axis (usually width).
	 * @param enabled true to enable, false to disable.
	 */
	public void setTexGenSEnabled(boolean enabled)
	{
		setFlag(GL_TEXTURE_GEN_S, enabled);
	}

	/**
	 * Sets if texture coordinates are to be automatically generated
	 * for the T coordinate axis (usually height).
	 * @param enabled true to enable, false to disable.
	 */
	public void setTexGenTEnabled(boolean enabled)
	{
		setFlag(GL_TEXTURE_GEN_T, enabled);
	}

	/**
	 * Sets if texture coordinates are to be automatically generated
	 * for the R coordinate axis (usually depth).
	 * @param enabled true to enable, false to disable.
	 */
	public void setTexGenREnabled(boolean enabled)
	{
		setFlag(GL_TEXTURE_GEN_R, enabled);
	}

	/**
	 * Sets if texture coordinates are to be automatically generated
	 * for the Q coordinate axis (I have no idea what the hell this could be).
	 * @param enabled true to enable, false to disable.
	 */
	public void setTexGenQEnabled(boolean enabled)
	{
		setFlag(GL_TEXTURE_GEN_Q, enabled);
	}

	/**
	 * Sets how texture coordinates are to be automatically generated.
	 * @param coord the texture coordinate to set the mode for.
	 * @param mode the generation function.
	 */
	public void setTexGenMode(TextureCoordType coord, TextureGenMode mode)
	{
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
		try (MemoryStack stack = MemoryStack.stackPush())
		{
			FloatBuffer fbuf = stack.mallocFloat(4);
			fbuf.put(0, a);
			fbuf.put(1, b);
			fbuf.put(2, c);
			fbuf.put(3, d);
			glTexGenfv(coord.glValue, GL_EYE_PLANE, fbuf);
		}
		getError();
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
		try (MemoryStack stack = MemoryStack.stackPush())
		{
			FloatBuffer fbuf = stack.mallocFloat(4);
			fbuf.put(0, a);
			fbuf.put(1, b);
			fbuf.put(2, c);
			fbuf.put(3, d);
			glTexGenfv(coord.glValue, GL_OBJECT_PLANE, fbuf);
		}
		getError();
	}

	/**
	 * Sets if normal vectors are generated automatically when geometry is submitted to
	 * the OpenGL geometry pipeline.
	 * @param enabled true to enable, false to disable.
	 */
	public void setAutoNormalGen(boolean enabled)
	{
		setFlag(GL_AUTO_NORMAL, enabled);
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
	 * Sets if 1D texturing is enabled or not.
	 * @param enabled true to enable, false to disable.
	 */
	public void setTexture1DEnabled(boolean enabled)
	{
		setFlag(GL_TEXTURE_1D, enabled);
	}

	/**
	 * Gets the currently bound 1D texture. 
	 * @return the texture, or null if no bound texture.
	 */
	public OGLTexture getTexture1D()
	{
		return currentTexture1D;
	}
	
	/**
	 * Binds a 1D texture object to the current active texture unit.
	 * @param texture the texture to bind.
	 */
	public void setTexture1D(OGLTexture texture)
	{
		Objects.requireNonNull(texture);
		glBindTexture(GL_TEXTURE_1D, texture.getName());
		currentTexture1D = texture;
	}

	/**
	 * Sets the current filtering for the current 1D texture.
	 * @param minFilter the minification filter.
	 * @param magFilter the magnification filter.
	 */
	public void setTexture1DFiltering(TextureMinFilter minFilter, TextureMagFilter magFilter)
	{
		glTexParameteri(GL_TEXTURE_1D, GL_TEXTURE_MAG_FILTER, magFilter.glid);
		glTexParameteri(GL_TEXTURE_1D, GL_TEXTURE_MIN_FILTER, minFilter.glid);
	}

	/**
	 * Sets the current filtering for the current 1D texture.
	 * If anisotropy is not supported, 
	 * @param minFilter the minification filter.
	 * @param magFilter the magnification filter.
	 * @param anisotropy the anisotropic filtering (2.0 or greater to enable, 1.0 is "off").
	 */
	public void setTexture1DFiltering(TextureMinFilter minFilter, TextureMagFilter magFilter, float anisotropy)
	{
		glTexParameteri(GL_TEXTURE_1D, GL_TEXTURE_MAG_FILTER, magFilter.glid);
		glTexParameteri(GL_TEXTURE_1D, GL_TEXTURE_MIN_FILTER, minFilter.glid);
		
		if (getInfo().supportsTextureAnisotropy())
		{
			anisotropy = Math.max(1.0f, Math.min(getInfo().getMaxTextureAnisotropy(), anisotropy));
			glTexParameterf(GL_TEXTURE_1D, 0x084FE, anisotropy);
		}
	}

	/**
	 * Sets the current wrapping for the current 1D texture.
	 * @param wrapS the wrapping mode, S-axis.
	 */
	public void setTexture1DWrapping(TextureWrapType wrapS)
	{
		checkFeatureVersion(wrapS);
		glTexParameteri(GL_TEXTURE_1D, GL_TEXTURE_WRAP_S, wrapS.glValue);
	}

	/**
	 * Sends a texture into OpenGL's memory for the current 1D texture at the topmost mipmap level.
	 * @param imageData the image to send.
	 * @param colorFormat the pixel storage format of the buffer data.
	 * @param format the internal texture format.
	 * @param width the texture width in texels.
	 * @param border the texel border to add, if any.
	 * @throws GraphicsException if the buffer provided is not direct.
	 */
	public void setTexture1DData(ByteBuffer imageData, ColorFormat colorFormat, TextureFormat format, int width, int border)
	{
		setTexture1DData(imageData, colorFormat, format, 0, width, border);
	}

	/**
	 * Sends a texture into OpenGL's memory for the current 1D texture at the topmost mipmap level.
	 * @param imageData the image to send.
	 * @param colorFormat the pixel storage format of the buffer data.
	 * @param format the internal texture format.
	 * @param texlevel	the mipmapping level to copy this into (0 is topmost).
	 * @param width the texture width in texels.
	 * @param border the texel border to add, if any.
	 * @throws GraphicsException if the buffer provided is not direct.
	 */
	public void setTexture1DData(ByteBuffer imageData, ColorFormat colorFormat, TextureFormat format, int texlevel, int width, int border)
	{
		if (width > getInfo().getMaxTextureSize())
			throw new GraphicsException("Texture is too large. Maximum width is "+ getInfo().getMaxTextureSize() + " pixels.");
		
		if (!imageData.isDirect())
			throw new GraphicsException("Data must be a direct buffer."); 

		checkFeatureVersion(colorFormat);
		checkFeatureVersion(format);

		clearError();
		glTexImage1D(
			GL_TEXTURE_1D,
			texlevel,
			format.glValue, 
			width,
			border,
			colorFormat.glValue,
			GL_UNSIGNED_BYTE,
			imageData
		);
		getError();
	}

	/**
	 * Copies the contents of the current read frame buffer into the current 1D texture target already at the topmost mipmap level.
	 * @param format    the internal texture format.
	 * @param srcX		the screen-aligned x-coordinate of what to grab from the buffer (0 is the left side of the screen).
	 * @param srcY		the screen-aligned y-coordinate of what to grab from the buffer (0 is the bottom of the screen).
	 * @param width		the width of the screen in pixels to grab.
	 * @param border the texel border to add, if any.
	 */
	public void setTexture1DDataFromBuffer(TextureFormat format, int srcX, int srcY, int width, int border)
	{
		setTexture1DDataFromBuffer(format, 0, srcX, srcY, width, border);
	}

	/**
	 * Copies the contents of the current read frame buffer into the current 1D texture target.
	 * @param format    the internal texture format.
	 * @param texlevel	the mipmapping level to copy this into (0 is topmost level).
	 * @param srcX		the screen-aligned x-coordinate of what to grab from the buffer (0 is the left side of the screen).
	 * @param srcY		the screen-aligned y-coordinate of what to grab from the buffer (0 is the bottom of the screen).
	 * @param width		the width of the screen in pixels to grab.
	 * @param border the texel border to add, if any.
	 */
	public void setTexture1DDataFromBuffer(TextureFormat format, int texlevel, int srcX, int srcY, int width, int border)
	{
		checkFeatureVersion(format);
		glCopyTexImage1D(GL_TEXTURE_1D, texlevel, format.glValue, srcX, srcY, width, border);
	}

	/**
	 * Sends a subset of data to the currently-bound 1D texture already 
	 * in OpenGL's memory at the topmost mipmap level.
	 * @param imageData the image to send.
	 * @param colorFormat the pixel storage format of the buffer data.
	 * @param width the texture width in texels.
	 * @param xoffs the texel offset.
	 * @throws GraphicsException if the buffer provided is not direct.
	 */
	public void setTexture1DSubData(ByteBuffer imageData, ColorFormat colorFormat, int width, int xoffs)
	{
		setTexture1DSubData(imageData, colorFormat, 0, width, xoffs);
	}

	/**
	 * Sends a subset of data to the currently-bound 1D texture already in OpenGL's memory.
	 * @param imageData the image to send.
	 * @param colorFormat the pixel storage format of the buffer data.
	 * @param texlevel	the mipmapping level to copy this into (0 is topmost).
	 * @param width the texture width in texels.
	 * @param xoffs the texel offset.
	 * @throws GraphicsException if the buffer provided is not direct.
	 */
	public void setTexture1DSubData(ByteBuffer imageData, ColorFormat colorFormat, int texlevel, int width, int xoffs)
	{
		if (!imageData.isDirect())
			throw new GraphicsException("Data must be a direct buffer."); 
	
		checkFeatureVersion(colorFormat);

		clearError();
		glTexSubImage1D(
			GL_TEXTURE_1D,
			texlevel,
			xoffs,
			width,
			colorFormat.glValue,
			GL_UNSIGNED_BYTE,
			imageData
		);
		getError();
	}

	/**
	 * Copies the contents of the current read frame buffer into the current 1D texture target already at the topmost mipmap level.
	 * @param xoffset	the offset in pixels on this texture (x-coordinate) to put this texture data.
	 * @param srcX		the screen-aligned x-coordinate of what to grab from the buffer (0 is the left side of the screen).
	 * @param srcY		the screen-aligned y-coordinate of what to grab from the buffer (0 is the bottom of the screen).
	 * @param width		the width of the screen in pixels to grab.
	 */
	public void setTexture1DSubDataFromBuffer(int xoffset, int srcX, int srcY, int width)
	{
		setTexture1DSubDataFromBuffer(0, xoffset, srcX, srcY, width);
	}

	/**
	 * Copies the contents of the current read frame buffer into the current 1D texture target.
	 * @param texlevel	the mipmapping level to copy this into (0 is topmost level).
	 * @param xoffset	the offset in pixels on this texture (x-coordinate) to put this texture data.
	 * @param srcX		the screen-aligned x-coordinate of what to grab from the buffer (0 is the left side of the screen).
	 * @param srcY		the screen-aligned y-coordinate of what to grab from the buffer (0 is the bottom of the screen).
	 * @param width		the width of the screen in pixels to grab.
	 */
	public void setTexture1DSubDataFromBuffer(int texlevel, int xoffset, int srcX, int srcY, int width)
	{
		glCopyTexSubImage1D(GL_TEXTURE_1D, texlevel, xoffset, srcX, srcY, width);
	}

	/**
	 * Unbinds a texture from the current 1D target.
	 */
	public void unsetTexture1D()
	{
		glBindTexture(GL_TEXTURE_1D, 0);
		currentTexture1D = null;
	}

	/**
	 * Sets if 2D texturing is enabled or not.
	 * @param enabled true to enable, false to disable.
	 */
	public void setTexture2DEnabled(boolean enabled)
	{
		setFlag(GL_TEXTURE_2D, enabled);
	}

	/**
	 * Gets the currently bound 2D texture. 
	 * @return the texture, or null if no bound texture.
	 */
	public OGLTexture getTexture2D()
	{
		return currentTexture2D;
	}
	
	/**
	 * Binds a 2D texture object to the current active texture unit.
	 * @param texture the texture to bind.
	 */
	public void setTexture2D(OGLTexture texture)
	{
		Objects.requireNonNull(texture);
		glBindTexture(GL_TEXTURE_2D, texture.getName());
		currentTexture2D = texture;
	}

	/**
	 * Sets the current filtering for the current 2D texture.
	 * @param minFilter the minification filter.
	 * @param magFilter the magnification filter.
	 */
	public void setTexture2DFiltering(TextureMinFilter minFilter, TextureMagFilter magFilter)
	{
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, magFilter.glid);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, minFilter.glid);
	}

	/**
	 * Sets the current filtering for the current 2D texture.
	 * @param minFilter the minification filter.
	 * @param magFilter the magnification filter.
	 * @param anisotropy the anisotropic filtering (2.0 or greater to enable, 1.0 is "off").
	 */
	public void setTexture2DFiltering(TextureMinFilter minFilter, TextureMagFilter magFilter, float anisotropy)
	{
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, magFilter.glid);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, minFilter.glid);
		
		if (getInfo().supportsTextureAnisotropy())
		{
			anisotropy = Math.max(1.0f, Math.min(getInfo().getMaxTextureAnisotropy(), anisotropy));
			glTexParameterf(GL_TEXTURE_2D, 0x084FE, anisotropy);
		}
	}

	/**
	 * Sets the current wrapping for the current 2D texture.
	 * @param wrapS the wrapping mode, S-axis.
	 * @param wrapT the wrapping mode, T-axis.
	 */
	public void setTexture2DWrapping(TextureWrapType wrapS, TextureWrapType wrapT)
	{
		checkFeatureVersion(wrapS);
		checkFeatureVersion(wrapT);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, wrapS.glValue);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, wrapT.glValue);
	}

	/**
	 * Sends a texture into OpenGL's memory for the current 2D texture at the topmost mipmap level.
	 * @param imageData the image to send.
	 * @param colorFormat the pixel storage format of the buffer data.
	 * @param format the internal format.
	 * @param width the texture width in texels.
	 * @param height the texture height in texels.
	 * @param border the texel border to add, if any.
	 * @throws GraphicsException if the buffer provided is not direct.
	 */
	public void setTexture2DData(ByteBuffer imageData, ColorFormat colorFormat, TextureFormat format, int width, int height, int border)
	{
		setTexture2DData(imageData, colorFormat, format, 0, width, height, border);
	}

	/**
	 * Sends a texture into OpenGL's memory for the current 2D texture.
	 * @param imageData the image to send.
	 * @param colorFormat the pixel storage format of the buffer data.
	 * @param format the internal format.
	 * @param texlevel the mipmapping level to copy this into (0 is topmost).
	 * @param width the texture width in texels.
	 * @param height the texture height in texels.
	 * @param border the texel border to add, if any.
	 * @throws GraphicsException if the buffer provided is not direct.
	 */
	public void setTexture2DData(ByteBuffer imageData, ColorFormat colorFormat, TextureFormat format, int texlevel, int width, int height, int border)
	{
		if (width > getInfo().getMaxTextureSize() || height > getInfo().getMaxTextureSize())
			throw new GraphicsException("Texture is too large. Maximum size is " + getInfo().getMaxTextureSize() + " pixels.");
	
		if (!imageData.isDirect())
			throw new GraphicsException("Data must be a direct buffer."); 
		
		checkFeatureVersion(colorFormat);
		checkFeatureVersion(format);

		clearError();
		glTexImage2D(
			GL_TEXTURE_2D,
			texlevel,
			format.glValue, 
			width,
			height,
			border,
			colorFormat.glValue,
			GL_UNSIGNED_BYTE,
			imageData
		);
		getError();
	}

	/**
	 * Copies the contents of the current read frame buffer into the current 2D texture target at the topmost mipmap level.
	 * @param format    the internal format.
	 * @param srcX		the screen-aligned x-coordinate of what to grab from the buffer (0 is the left side of the screen).
	 * @param srcY		the screen-aligned y-coordinate of what to grab from the buffer (0 is the bottom of the screen).
	 * @param width		the width of the screen in pixels to grab.
	 * @param height	the height of the screen in pixels to grab.
	 * @param border    the texel border to add, if any.
	 */
	public void setTexture2DDataFromBuffer(TextureFormat format, int srcX, int srcY, int width, int height, int border)
	{
		setTexture2DDataFromBuffer(format, 0, srcX, srcY, width, height, border);
	}

	/**
	 * Copies the contents of the current read frame buffer into the current 2D texture target.
	 * @param format    the internal format.
	 * @param texlevel	the mipmapping level to copy this into (0 is topmost level).
	 * @param srcX		the screen-aligned x-coordinate of what to grab from the buffer (0 is the left side of the screen).
	 * @param srcY		the screen-aligned y-coordinate of what to grab from the buffer (0 is the bottom of the screen).
	 * @param width		the width of the screen in pixels to grab.
	 * @param height	the height of the screen in pixels to grab.
	 * @param border    the texel border to add, if any.
	 */
	public void setTexture2DDataFromBuffer(TextureFormat format, int texlevel, int srcX, int srcY, int width, int height, int border)
	{
		checkFeatureVersion(format);
		glCopyTexImage2D(GL_TEXTURE_2D, format.glValue, texlevel, srcX, srcY, width, height, border);
	}

	/**
	 * Sends a subset of data to the currently-bound 2D texture 
	 * already in OpenGL's memory at the topmost mipmap level.
	 * @param imageData the image to send.
	 * @param colorFormat the pixel storage format of the buffer data.
	 * @param width the texture width in texels.
	 * @param height the texture height in texels.
	 * @param xoffs the texel offset.
	 * @param yoffs the texel offset.
	 * @throws GraphicsException if the buffer provided is not direct.
	 */
	public void setTexture2DSubData(ByteBuffer imageData, ColorFormat colorFormat, int width, int height, int xoffs, int yoffs)
	{
		setTexture2DSubData(imageData, colorFormat, 0, width, height, xoffs, yoffs);
	}

	/**
	 * Sends a subset of data to the currently-bound 2D texture already in OpenGL's memory.
	 * @param imageData the image to send.
	 * @param colorFormat the pixel storage format of the buffer data.
	 * @param texlevel	the mipmapping level to copy this into (0 is topmost).
	 * @param width the texture width in texels.
	 * @param height the texture height in texels.
	 * @param xoffs the texel offset.
	 * @param yoffs the texel offset.
	 * @throws GraphicsException if the buffer provided is not direct.
	 */
	public void setTexture2DSubData(ByteBuffer imageData, ColorFormat colorFormat, int texlevel, int width, int height, int xoffs, int yoffs)
	{
		if (!imageData.isDirect())
			throw new GraphicsException("Data must be a direct buffer."); 
	
		checkFeatureVersion(colorFormat);

		clearError();
		glTexSubImage2D(
			GL_TEXTURE_2D,
			texlevel,
			xoffs,
			yoffs,
			width,
			height,
			colorFormat.glValue,
			GL_UNSIGNED_BYTE,
			imageData
		);
		getError();
	}

	/**
	 * Copies the contents of the current read frame buffer into the 
	 * current 2D texture target already in OpenGL's memory at the topmost mipmap level.
	 * @param xoffset	the offset in pixels on this texture (x-coordinate) to put this texture data.
	 * @param yoffset	the offset in pixels on this texture (y-coordinate) to put this texture data.
	 * @param srcX		the screen-aligned x-coordinate of what to grab from the buffer (0 is the left side of the screen).
	 * @param srcY		the screen-aligned y-coordinate of what to grab from the buffer (0 is the bottom of the screen).
	 * @param width		the width of the screen in pixels to grab.
	 * @param height	the height of the screen in pixels to grab.
	 */
	public void setTexture2DSubDataFromBuffer(int xoffset, int yoffset, int srcX, int srcY, int width, int height)
	{
		setTexture2DSubDataFromBuffer(0, xoffset, yoffset, srcX, srcY, width, height);
	}

	/**
	 * Copies the contents of the current read frame buffer into the 
	 * current 2D texture target already in OpenGL's memory.
	 * @param texlevel	the mipmapping level to copy this into (0 is topmost level).
	 * @param xoffset	the offset in pixels on this texture (x-coordinate) to put this texture data.
	 * @param yoffset	the offset in pixels on this texture (y-coordinate) to put this texture data.
	 * @param srcX		the screen-aligned x-coordinate of what to grab from the buffer (0 is the left side of the screen).
	 * @param srcY		the screen-aligned y-coordinate of what to grab from the buffer (0 is the bottom of the screen).
	 * @param width		the width of the screen in pixels to grab.
	 * @param height	the height of the screen in pixels to grab.
	 */
	public void setTexture2DSubDataFromBuffer(int texlevel, int xoffset, int yoffset, int srcX, int srcY, int width, int height)
	{
		glCopyTexSubImage2D(GL_TEXTURE_2D, texlevel, xoffset, yoffset, srcX, srcY, width, height);
	}

	/**
	 * Unbinds a texture from the current 2D target.
	 */
	public void unsetTexture2D()
	{
		glBindTexture(GL_TEXTURE_2D, 0);
		currentTexture2D = null;
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
	 * Enables or disables the processing of bound texture coordinate arrays.
	 * @param enable true to enable, false to disable.
	 */
	public void setTextureCoordArrayEnabled(boolean enable)
	{
		setClientFlag(GL_TEXTURE_COORD_ARRAY, enable);
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
	 * Enables or disables the processing of bound surface normal arrays.
	 * @param enable true to enable, false to disable.
	 */
	public void setNormalArrayEnabled(boolean enable)
	{
		setClientFlag(GL_NORMAL_ARRAY, enable);
	}

	/**
	 * Sets what positions in the current {@link BufferTargetType#GEOMETRY}-bound buffer or array are used to draw polygonal information:
	 * This sets the vertex pointers.
	 * @param dataType the data type contained in the buffer that will be read (calculates actual sizes of data).
	 * @param width the width of a full set of coordinates (3-dimensional vertices = 3).
	 * @param stride the distance (in elements) between each vertex.    
	 * @param offset the offset in each stride where each vertex starts.  
	 * @see #setVertexArrayEnabled(boolean)   
	 */
	public void setPointerVertex(DataType dataType, int width, int stride, int offset)
	{
		glVertexPointer(width, dataType.glValue, stride * dataType.size, offset * dataType.size);
		getError();
	}
	
	/**
	 * Sets what positions in the current {@link BufferTargetType#GEOMETRY}-bound buffer or array are used to draw polygonal information:
	 * This sets the texture coordinate pointers.
	 * @param dataType the data type contained in the buffer that will be read (calculates actual sizes of data).
	 * @param width the width of a full set of coordinates (2-dimensional coords = 2).
	 * @param stride the distance (in elements) between each coordinate group.     
	 * @param offset the offset in each stride where each coordinate starts.     
	 * @see #setTextureCoordArrayEnabled(boolean)   
	 */
	public void setPointerTextureCoordinate(DataType dataType, int width, int stride, int offset)
	{
		glTexCoordPointer(width, dataType.glValue, stride * dataType.size, offset * dataType.size);
		getError();
	}
	
	/**
	 * Sets what positions in the current {@link BufferTargetType#GEOMETRY}-bound buffer or array are used to draw polygonal information:
	 * This sets the normal vector pointers. Always assumes 3-dimensional vectors.
	 * @param dataType the data type contained in the buffer that will be read (calculates actual sizes of data).
	 * @param stride the distance (in elements) between each normal.     
	 * @param offset the offset in each stride where each normal starts.     
	 * @see #setNormalArrayEnabled(boolean)   
	 */
	public void setPointerNormal(DataType dataType, int stride, int offset)
	{
		glNormalPointer(dataType.glValue, stride * dataType.size, offset * dataType.size);
		getError();
	}
	
	/**
	 * Sets what positions in the current {@link BufferTargetType#GEOMETRY}-bound buffer or array are used to draw polygonal information:
	 * This sets the color pointers.
	 * @param dataType the data type contained in the buffer that will be read (calculates actual sizes of data).
	 * @param width the width of a full set of color components (4-component color = 4).
	 * @param stride the distance (in elements) between each color.   
	 * @param offset the offset in each stride where each color starts.     
	 * @see #setColorArrayEnabled(boolean)   
	 */
	public void setPointerColor(DataType dataType, int width, int stride, int offset)
	{
		glColorPointer(width, dataType.glValue, stride * dataType.size, offset * dataType.size);
		getError();
	}
	
	/**
	 * Draws geometry using the current bound, enabled coordinate arrays/buffers as data.
	 * @param geometryType the geometry type - tells how to interpret the data.
	 * @param offset the starting offset in the bound buffers (in elements).
	 * @param elementCount the number of elements to draw using bound buffers.
	 * NOTE: an element is in terms of array elements, so if the bound buffers describe the coordinates of 4 vertices,
	 * <code>elementCount</code> should be 4.
	 * @see #setVertexArrayEnabled(boolean)
	 * @see #setTextureCoordArrayEnabled(boolean)
	 * @see #setNormalArrayEnabled(boolean)
	 * @see #setColorArrayEnabled(boolean)
	 * @see #setPointerVertex(DataType, int, int, int)
	 * @see #setPointerTextureCoordinate(DataType, int, int, int)
	 * @see #setPointerNormal(DataType, int, int)
	 * @see #setPointerColor(DataType, int, int, int)
	 */
	public void drawGeometryArray(GeometryType geometryType, int offset, int elementCount)
	{
		glDrawArrays(geometryType.glValue, offset, elementCount);
		getError();
	}
	
	/**
	 * Draws geometry using the current bound, enabled coordinate arrays/buffers as data, plus
	 * an element buffer to describe the ordering.
	 * @param geometryType the geometry type - tells how to interpret the data.
	 * @param dataType the data type of the indices in the {@link BufferTargetType#INDICES}-bound buffer (must be an unsigned type).
	 * @param count the amount of element indices to interpret in the {@link BufferTargetType#INDICES}-bound buffer.
	 * @param offset the starting offset in the index buffer (in elements).
	 * @see #setVertexArrayEnabled(boolean)
	 * @see #setTextureCoordArrayEnabled(boolean)
	 * @see #setNormalArrayEnabled(boolean)
	 * @see #setColorArrayEnabled(boolean)
	 * @see #setPointerVertex(DataType, int, int, int)
	 * @see #setPointerTextureCoordinate(DataType, int, int, int)
	 * @see #setPointerNormal(DataType, int, int)
	 * @see #setPointerColor(DataType, int, int, int)
	 */
	public void drawGeometryElements(GeometryType geometryType, DataType dataType, int count, int offset)
	{
		glDrawElements(geometryType.glValue, count, dataType.glValue, dataType.size * offset);
		getError();
	}

}
