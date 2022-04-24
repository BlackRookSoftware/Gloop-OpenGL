/*******************************************************************************
 * Copyright (c) 2021-2022 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at 
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gloop.opengl.math;

/**
 * A matrix stack for the core implementations.
 * @author Matthew Tropiano
 */
public class MatrixStack
{
	private static final ThreadLocal<Matrix4F> MATRIX = ThreadLocal.withInitial(()->new Matrix4F());

	/** The stack. */
	private Matrix4F[] stack;
	/** The top index. */
	private int top;
	
	/**
	 * Creates a new matrix stack.
	 * The topmost matrix is initialized with the identity matrix.
	 * @param depth the matrix stack depth in matrices.
	 */
	public MatrixStack(int depth)
	{
		this.top = 0;
		this.stack = new Matrix4F[depth];
		for (int i = 0; i < this.stack.length; i++)
			this.stack[i] = new Matrix4F();
		identity();
	}
	
	/**
	 * Pushes a copy of the topmost matrix onto the stack.
	 * @return the topmost matrix.
	 * @throws ArrayIndexOutOfBoundsException if there's no more room to push a new matrix.
	 */
	public Matrix4F push()
	{
		// If an exception occurs, top is not incremented.
		stack[top + 1].set(stack[top]);
		top++;
		return stack[top];
	}
	
	/**
	 * Pops the topmost matrix off the stack.
	 * @return the topmost matrix.
	 * @throws ArrayIndexOutOfBoundsException if there's no matrices left to pop.
	 */
	public Matrix4F pop()
	{
		// If an exception occurs, top is not decremented.
		Matrix4F out = stack[top - 1];
		top--;
		return out;
	}
	
	/**
	 * @return the topmost matrix.
	 */
	public Matrix4F peek()
	{
		return stack[top];
	}
	
	/**
	 * Sets the topmost matrix to another matrix.
	 * @param values the column-major values for the matrix.
	 * @return the topmost matrix.
	 */
	public Matrix4F set(float[] values)
	{
		peek().set(values);
		return peek();
	}
	
	/**
	 * Sets the topmost matrix to another matrix.
	 * @param matrix the matrix to multiply.
	 * @return the topmost matrix.
	 */
	public Matrix4F set(Matrix4F matrix)
	{
		peek().set(matrix);
		return peek();
	}
	
	/**
	 * Sets the topmost matrix to the identity matrix.
	 * @return the topmost matrix.
	 */
	public Matrix4F identity()
	{
		peek().setIdentity();
		return peek();
	}
	
	/**
	 * Multiplies the topmost matrix with another matrix.
	 * @param values the column-major values for the matrix.
	 * @return the topmost matrix.
	 */
	public Matrix4F multiply(float[] values)
	{
		Matrix4F matrix = MATRIX.get();
		matrix.set(values);
		peek().multiplyRight(matrix);
		return peek();
	}
	
	/**
	 * Multiplies the topmost matrix with another matrix.
	 * @param matrix the matrix to multiply.
	 * @return the topmost matrix.
	 */
	public Matrix4F multiply(Matrix4F matrix)
	{
		peek().multiplyRight(matrix);
		return peek();
	}
	
	/**
	 * Translates the current matrix by a set of units.
	 * This is applied via multiplication with the current matrix.
	 * @param x the x-axis translation.
	 * @param y the y-axis translation.
	 * @param z the z-axis translation.
	 * @return the topmost matrix.
	 */
	public Matrix4F translate(float x, float y, float z)
	{
		Matrix4F matrix = MATRIX.get();
		matrix.setTranslation(x, y, z);
		return multiply(matrix);
	}

	/**
	 * Rotates the current matrix by an amount of DEGREES around the X-Axis.
	 * This is applied via multiplication with the current matrix.
	 * @param degrees the amount of degrees.
	 * @return the topmost matrix.
	 */
	public Matrix4F rotateX(float degrees)
	{
		Matrix4F matrix = MATRIX.get();
		matrix.setRotateX(degrees);
		return multiply(matrix);
	}

	/**
	 * Rotates the current matrix by an amount of DEGREES around the Y-Axis.
	 * This is applied via multiplication with the current matrix.
	 * @param degrees the amount of degrees.
	 * @return the topmost matrix.
	 */
	public Matrix4F rotateY(float degrees)
	{
		Matrix4F matrix = MATRIX.get();
		matrix.setRotateY(degrees);
		return multiply(matrix);
	}

	/**
	 * Rotates the current matrix by an amount of DEGREES around the Z-Axis.
	 * This is applied via multiplication with the current matrix.
	 * @param degrees the amount of degrees.
	 * @return the topmost matrix.
	 */
	public Matrix4F rotateZ(float degrees)
	{
		Matrix4F matrix = MATRIX.get();
		matrix.setRotateZ(degrees);
		return multiply(matrix);
	}

	/**
	 * Scales the current matrix by a set of scalars that 
	 * correspond to each axis.
	 * This is applied via multiplication with the current matrix.
	 * @param x the x-axis scalar.
	 * @param y the y-axis scalar.
	 * @param z the z-axis scalar.
	 * @return the topmost matrix.
	 */
	public Matrix4F scale(float x, float y, float z)
	{
		Matrix4F matrix = MATRIX.get();
		matrix.setScale(x, y, z);
		return multiply(matrix);
	}

	/**
	 * Multiplies the current matrix by a symmetric perspective projection matrix.
	 * @param fov front of view angle in degrees.
	 * @param aspect the aspect ratio, usually view width over view height.
	 * @param near the near clipping plane on the Z-Axis.
	 * @param far the far clipping plane on the Z-Axis.
	 * @throws ArithmeticException if <code>fov == 0 || aspect == 0 || near == far</code>.
	 * @return the topmost matrix.
	 */
	public Matrix4F perspective(float fov, float aspect, float near, float far)
	{
		Matrix4F matrix = MATRIX.get();
		matrix.setPerspective(fov, aspect, near, far);
		return multiply(matrix);
	}

	/**
	 * Multiplies the current matrix by a frustum projection matrix.
	 * @param left the left clipping plane on the X-Axis.
	 * @param right the right clipping plane on the X-Axis.
	 * @param bottom the bottom clipping plane on the Y-Axis.
	 * @param top the upper clipping plane on the Y-Axis.
	 * @param near the near clipping plane on the Z-Axis.
	 * @param far the far clipping plane on the Z-Axis.
	 * @throws ArithmeticException if <code>left == right || bottom == top || near == far</code>.
	 * @return the topmost matrix.
	 */
	public Matrix4F frustum(float left, float right, float bottom, float top, float near, float far)
	{
		Matrix4F matrix = MATRIX.get();
		matrix.setFrustum(left, right, bottom, top, near, far);
		return multiply(matrix);
	}

	/**
	 * Multiplies the current matrix by an orthographic projection matrix.
	 * @param left the left clipping plane on the X-Axis.
	 * @param right the right clipping plane on the X-Axis.
	 * @param bottom the bottom clipping plane on the Y-Axis.
	 * @param top the upper clipping plane on the Y-Axis.
	 * @param near the near clipping plane on the Z-Axis.
	 * @param far the far clipping plane on the Z-Axis.
	 * @throws ArithmeticException if <code>left == right || bottom == top || near == far</code>.
	 * @return the topmost matrix.
	 */
	public Matrix4F ortho(float left, float right, float bottom, float top, float near, float far)
	{
		Matrix4F matrix = MATRIX.get();
		matrix.setOrtho(left, right, bottom, top, near, far);
		return multiply(matrix);
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
	 * @throws ArithmeticException if <code>left == right || bottom == top || near == far</code>.
	 * @return the topmost matrix.
	 */
	public Matrix4F aspectOrtho(float targetAspect, float left, float right, float bottom, float top, float near, float far)
	{
		Matrix4F matrix = MATRIX.get();
		matrix.setAspectOrtho(targetAspect, left, right, bottom, top, near, far);
		return multiply(matrix);
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
	 * @return the topmost matrix.
	 */
	public Matrix4F lookAt(float eyeX, float eyeY, float eyeZ, float centerX, float centerY, float centerZ, float upX, float upY, float upZ)
	{
		Matrix4F matrix = MATRIX.get();
		matrix.setLookAt(eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX, upY, upZ);
		return multiply(matrix);
	}

}
