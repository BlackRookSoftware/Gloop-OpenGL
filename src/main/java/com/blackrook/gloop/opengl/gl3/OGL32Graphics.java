/*******************************************************************************
 * Copyright (c) 2021 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at 
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gloop.opengl.gl3;

import com.blackrook.gloop.opengl.OGLVersion;
import com.blackrook.gloop.opengl.enums.MatrixMode;
import com.blackrook.gloop.opengl.exception.GraphicsException;
import com.blackrook.gloop.opengl.math.Matrix4F;
import com.blackrook.gloop.opengl.math.MatrixStack;

/**
 * OpenGL 3.2 Graphics Implementation.
 * The implementation of the matrix operations are done using a {@link MatrixStack}.
 * @author Matthew Tropiano
 */
public class OGL32Graphics extends OGL31Graphics
{
	private MatrixMode matrixMode;
	private MatrixStack[] matrixStacks;
	
	public OGL32Graphics(boolean core)
	{
		super(core);
		this.matrixStacks = new MatrixStack[3];
		for (int i = 0; i < this.matrixStacks.length; i++)
			this.matrixStacks[i] = new MatrixStack(64);
	}

	@Override
	public OGLVersion getVersion()
	{
		return OGLVersion.GL32;
	}

	// Gets a matrix stack.
	private MatrixStack getMatrixStack(MatrixMode matrixMode)
	{
		return matrixStacks[matrixMode.ordinal()];
	}

	// Gets the current matrix stack.
	private MatrixStack currentMatrixStack()
	{
		return getMatrixStack(matrixMode);
	}
	
	/**
	 * Sets the current matrix for matrix operations.
	 * Note that other commands may change this mode automatically.
	 * @param mode the matrix mode to set.
	 */
	public void matrixMode(MatrixMode mode)
	{
		matrixMode = mode;	
	}

	/**
	 * Loads the identity matrix into the current selected matrix.
	 */
	public void matrixReset()
	{
		currentMatrixStack().identity();
	}

	/**
	 * Pushes a copy of the current matrix onto the current selected stack.
	 */
	public void matrixPush()
	{
		currentMatrixStack().push();
	}

	/**
	 * Pops the current matrix off of the current selected stack.
	 */
	public void matrixPop()
	{
		currentMatrixStack().pop();
	}

	/**
	 * Reads a current matrix into an array.
	 * @param matrixType the type of matrix to load.
	 * @param outArray the output array. Must be length 16 or greater.
	 */
	public void matrixGet(MatrixMode matrixType, float[] outArray)
	{
		getMatrixStack(matrixType).peek().getFloats(outArray);
	}

	/**
	 * Reads a current matrix into a matrix.
	 * @param matrixType the type of matrix to load.
	 * @param matrix the output matrix.
	 */
	public void matrixGet(MatrixMode matrixType, Matrix4F matrix)
	{
		matrix.set(getMatrixStack(matrixType).peek());
	}

	/**
	 * Loads a matrix's contents from a column-major array into the current selected matrix.
	 * @param matrixArray the column-major cells of a matrix.
	 */
	public void matrixSet(float[] matrixArray)
	{
		currentMatrixStack().set(matrixArray);
	}

	/**
	 * Loads a matrix's contents into the current selected matrix.
	 * @param matrix the matrix to read from.
	 */
	public void matrixSet(Matrix4F matrix)
	{
		currentMatrixStack().set(matrix);
	}

	/**
	 * Multiplies a matrix into the current selected matrix from a column-major array into.
	 * @param matrixArray the column-major cells of a matrix.
	 */
	public void matrixMultiply(float[] matrixArray)
	{
		currentMatrixStack().multiply(matrixArray);
	}

	/**
	 * Multiplies a matrix into the current selected matrix.
	 * @param matrix the matrix to read from.
	 */
	public void matrixMultiply(Matrix4F matrix)
	{
		currentMatrixStack().multiply(matrix);
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
		currentMatrixStack().translate(x, y, z);
	}

	/**
	 * Rotates the current matrix by an amount of DEGREES around the X-Axis.
	 * This is applied via multiplication with the current matrix.
	 * @param degrees the amount of degrees.
	 */
	public void matrixRotateX(float degrees)
	{
		currentMatrixStack().rotateX(degrees);
	}

	/**
	 * Rotates the current matrix by an amount of DEGREES around the Y-Axis.
	 * This is applied via multiplication with the current matrix.
	 * @param degrees the amount of degrees.
	 */
	public void matrixRotateY(float degrees)
	{
		currentMatrixStack().rotateY(degrees);
	}

	/**
	 * Rotates the current matrix by an amount of DEGREES around the Z-Axis.
	 * This is applied via multiplication with the current matrix.
	 * @param degrees the amount of degrees.
	 */
	public void matrixRotateZ(float degrees)
	{
		currentMatrixStack().rotateZ(degrees);
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
		currentMatrixStack().scale(x, y, z);
	}

	/**
	 * Multiplies the current matrix by a symmetric perspective projection matrix.
	 * @param fov front of view angle in degrees.
	 * @param aspect the aspect ratio, usually view width over view height.
	 * @param near the near clipping plane on the Z-Axis.
	 * @param far the far clipping plane on the Z-Axis.
	 * @throws GraphicsException if <code>fov == 0 || aspect == 0 || near == far</code>.
	 */
	public void matrixPerspective(float fov, float aspect, float near, float far)
	{
		currentMatrixStack().perspective(fov, aspect, near, far);
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
		currentMatrixStack().frustum(left, right, bottom, top, near, far);
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
		currentMatrixStack().ortho(left, right, bottom, top, near, far);
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
		currentMatrixStack().aspectOrtho(targetAspect, left, right, bottom, top, near, far);
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
		currentMatrixStack().lookAt(eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX, upY, upZ);
	}

}
