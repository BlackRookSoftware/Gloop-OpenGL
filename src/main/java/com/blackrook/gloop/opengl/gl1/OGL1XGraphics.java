package com.blackrook.gloop.opengl.gl1;

import com.blackrook.gloop.opengl.OGLGraphics;
import com.blackrook.gloop.opengl.gl1.enums.FillMode;
import com.blackrook.gloop.opengl.gl1.enums.HintType;
import com.blackrook.gloop.opengl.gl1.enums.HintValue;
import com.blackrook.gloop.opengl.gl1.enums.MatrixType;

/**
 * OpenGL 1.X Graphics.
 * @author Matthew Tropiano
 */
public interface OGL1XGraphics extends OGLGraphics
{
	// TODO: Finish all of this.
	
	/**
	 * Sets the current matrix mode.
	 * @param mode the new current mode.
	 */
	void matrixType(MatrixType mode);

	/**
	 * Sets a frustum matrix for the current matrix mode. 
	 * @param left the left plane coefficient.
	 * @param right the right plane coefficient.
	 * @param top the top plane coefficient.
	 * @param bottom the bottom plane coefficient.
	 * @param near the near plane coefficient.
	 * @param far the far plane coefficient.
	 */
	void matrixFrustum(double left, double right, double top, double bottom, double near, double far);

	/**
	 * Sets the clear color.
	 * The color buffer is filled with this color upon clear.
	 * @param red the red component of the color to use (0 to 1).
	 * @param green the green component of the color to use (0 to 1).
	 * @param blue the blue component of the color to use (0 to 1).
	 * @param alpha the alpha component of the color to use (0 to 1).
	 */
	void setClearColor(float red, float green, float blue, float alpha);

	/**
	 * Sets depth clear value.
	 * If the depth buffer gets cleared, this is the value that is written to all of the pixels in the buffer.
	 * @param depthValue the depth value to set on clear.
	 */
	void setDepthClear(double depthValue);

	/**
	 * Clears a bunch of framebuffers.
	 * @param clearColorBuffer clear the color buffer?
	 * @param clearDepthBuffer clear the depth buffer?
	 * @param clearAccumulationBuffer clear the accumulation buffer?
	 * @param clearStencilBuffer clear the stencil buffer?
	 */
	void clearFrameBuffers(boolean clearColorBuffer, boolean clearDepthBuffer, boolean clearAccumulationBuffer, boolean clearStencilBuffer);

	/**
	 * Sets an OpenGL hint.
	 * @param type the hint type to set.
	 * @param value the value to set for the provided hint.
	 */
	void setHint(HintType type, HintValue value);

	/**
	 * Set polygon fill mode.
	 * @param mode the fill mode.
	 */
	void setFillMode(FillMode mode);

	/**
	 * Set front polygon fill mode.
	 * @param mode the fill mode.
	 */
	public void setFrontFillMode(FillMode mode);

	/**
	 * Set back polygon fill mode.
	 * @param mode the fill mode.
	 */
	public void setBackFillMode(FillMode mode);

}
