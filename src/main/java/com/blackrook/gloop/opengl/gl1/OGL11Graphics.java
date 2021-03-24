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
import java.util.Set;
import java.util.TreeSet;

import org.lwjgl.system.MemoryStack;

import com.blackrook.gloop.opengl.OGLGraphics;
import com.blackrook.gloop.opengl.exception.GraphicsException;
import com.blackrook.gloop.opengl.gl1.enums.AttribType;
import com.blackrook.gloop.opengl.gl1.enums.BlendArg;
import com.blackrook.gloop.opengl.gl1.enums.BlendFunc;
import com.blackrook.gloop.opengl.gl1.enums.ClientAttribType;
import com.blackrook.gloop.opengl.gl1.enums.FaceSide;
import com.blackrook.gloop.opengl.gl1.enums.FillMode;
import com.blackrook.gloop.opengl.gl1.enums.FogFormulaType;
import com.blackrook.gloop.opengl.gl1.enums.FrameBufferType;
import com.blackrook.gloop.opengl.gl1.enums.HintType;
import com.blackrook.gloop.opengl.gl1.enums.HintValue;
import com.blackrook.gloop.opengl.gl1.enums.LightShadeType;
import com.blackrook.gloop.opengl.gl1.enums.LogicFunc;
import com.blackrook.gloop.opengl.gl1.enums.MatrixMode;
import com.blackrook.gloop.opengl.gl1.enums.StencilTestFunc;
import com.blackrook.gloop.opengl.gl1.enums.TextureCoordType;
import com.blackrook.gloop.opengl.gl1.enums.TextureGenMode;
import com.blackrook.gloop.opengl.gl1.enums.TextureMode;
import com.blackrook.gloop.opengl.gl1.objects.OGLBitmap;
import com.blackrook.gloop.opengl.gl1.objects.OGLLight;
import com.blackrook.gloop.opengl.gl1.objects.OGLMaterial;
import com.blackrook.gloop.opengl.math.Matrix4F;

import static org.lwjgl.opengl.GL11.*;

/**
 * OpenGL 1.1 Graphics Implementation.
 * @author Matthew Tropiano
 */
public class OGL11Graphics extends OGLGraphics
{
	private static ThreadLocal<Matrix4F> MATRIX = ThreadLocal.withInitial(()->new Matrix4F());
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

	/**
	 * Clears a bunch of framebuffers.
	 * @param clearColorBuffer clear the color buffer?
	 * @param clearDepthBuffer clear the depth buffer?
	 * @param clearAccumulationBuffer clear the accumulation buffer?
	 * @param clearStencilBuffer clear the stencil buffer?
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
	 * Sets if certain lights are enabled.
	 * @param sourceId the light source id.
	 * @param enable true to enable, false to disable.
	 * @throws GraphicsException if the specified sourceId is not a valid one.
	 */
	public void setLightEnabled(int sourceId, boolean enable)
	{
		setFlag(GL_LIGHT0 + sourceId, enable);
		getError();
	}

	/**
	 * Sets the current light used for illuminating polygons and other geometry.
	 * This light will set all properties.
	 * @param sourceId the light source id. this cannot exceed the maximum number of lights
	 * that OpenGL can handle.
	 * @param light the Light to use.
	 * @throws GraphicsException if the specified sourceId is not a valid one.
	 */
	public void setLight(int sourceId, OGLLight light)
	{
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
	 * @throws GraphicsException if the specified sourceId is not a valid one.
	 */
	public void setLightAttenuation(int sourceId, float constant, float linear, float quadratic)
	{
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
	 * @throws GraphicsException if the specified sourceId is not a valid one.
	 */
	public void setLightAmbientColor(int sourceId, Color color)
	{
		setLightAmbientColor(sourceId, color.getRGB());
	}

	/**
	 * Sets the color for a ambient component for a light. 
	 * @param sourceId the light source id.
	 * @param argbColor the ARGB color to set.
	 * @throws GraphicsException if the specified sourceId is not a valid one.
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
	 * @throws GraphicsException if the specified sourceId is not a valid one.
	 */
	public void setLightAmbientColor(int sourceId, float red, float green, float blue, float alpha)
	{
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
	 * @throws GraphicsException if the specified sourceId is not a valid one.
	 */
	public void setLightDiffuseColor(int sourceId, Color color)
	{
		setLightDiffuseColor(sourceId, color.getRGB());
	}

	/**
	 * Sets the color for a diffuse component for a light. 
	 * @param sourceId the light source id.
	 * @param argbColor the ARGB color to set.
	 * @throws GraphicsException if the specified sourceId is not a valid one.
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
	 * @throws GraphicsException if the specified sourceId is not a valid one.
	 */
	public void setLightDiffuseColor(int sourceId, float red, float green, float blue, float alpha)
	{
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
	 * @throws GraphicsException if the specified sourceId is not a valid one.
	 */
	public void setLightSpecularColor(int sourceId, Color color)
	{
		setLightSpecularColor(sourceId, color.getRGB());
	}

	/**
	 * Sets the color for a specular component for a light. 
	 * @param sourceId the light source id.
	 * @param argbColor the ARGB color to set.
	 * @throws GraphicsException if the specified sourceId is not a valid one.
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
	 * @throws GraphicsException if the specified sourceId is not a valid one.
	 */
	public void setLightSpecularColor(int sourceId, float red, float green, float blue, float alpha)
	{
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
	 * @throws GraphicsException if the specified sourceId is not a valid one.
	 */
	public void setLightPosition(int sourceId, float x, float y, float z, float w)
	{
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
	 * Sets if 1D texturing is enabled or not.
	 * @param enabled true to enable, false to disable.
	 */
	public void setTexture1DEnabled(boolean enabled)
	{
		setFlag(GL_TEXTURE_1D, enabled);
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

	// TODO: Finish this.
	
}
