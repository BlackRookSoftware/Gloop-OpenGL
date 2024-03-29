/*******************************************************************************
 * Copyright (c) 2021-2022 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at 
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gloop.opengl.gl1;

/**
 * Bitmap object for doing bitmap stuff.
 * A bitmap contains a bunch of bits in a particular order 
 * in such a way that is useful somehow to the user. I guess.
 * IT'S A BITMAP!
 * @author Matthew Tropiano
 */
public class OGLBitmap
{
	/** Width of the bitmap. */
	private int width;
	/** Height of the bitmap. */
	private int height;

	/** Width bytes of the bitmap. */
	private int widthBytes;

	/** The bitmap. */
	private byte[] bits;
	
	/**
	 * Makes a new blank Bitmap of specific width and height. 
	 * @param width		the width in bits of the map.
	 * @param height	the height in bits of the map.
	 */
	OGLBitmap(int width, int height)
	{
		this.width = width;
		this.height = height;
		this.widthBytes = (width/8)+(((width%8) > 0)? 1 : 0);
		this.bits = new byte[widthBytes * height];
	}
	
	OGLBitmap(int width, int height, byte[] source)
	{
		this(width,height);
		System.arraycopy(source, 0, bits, 0, Math.min(source.length, bits.length));
	}
	
	OGLBitmap(OGLBitmap b)
	{
		this(b.width, b.height, b.bits);
	}
	
	/**
	 * Gets if a bit in this bitmap is set.
	 * @param x	the x-coordinate.
	 * @param y	the y-coordinate.
	 * @return	true if set, false if not.
	 */
	public boolean getBit(int x, int y)
	{
		return (bits[getBitIndex(x,y)] & (1<<(x%8))) != 0;
	}
	
	protected final int getBitIndex(int x, int y)
	{
		return (y*widthBytes)+(x/8);
	}
	
	/**
	 * Sets a bit in this bitmap.
	 * @param x the x-coordinate.
	 * @param y the y-coordinate.
	 * @param set true to set, false to clear.
	 */
	public void setBit(int x, int y, boolean set)
	{
		bits[getBitIndex(x,y)] |= (byte)(1<<(x%8));
	}

	/**
	 * XORs this Bitmap with another, changing the contents of this one.
	 * @param b	the Bitmap to XOR this with.
	 */
	public void xor(OGLBitmap b)
	{
		xor(this, b, this);
	}
	
	/**
	 * ANDs this Bitmap with another, changing the contents of this one.
	 * @param b	the Bitmap to AND this with.
	 */
	public void and(OGLBitmap b)
	{
		and(this, b, this);
	}
	
	/**
	 * ORs this Bitmap with another, changing the contents of this one.
	 * @param b	the Bitmap to OR this with.
	 */
	public void or(OGLBitmap b)
	{
		or(this, b, this);
	}
	
	/**
	 * Sets this Bitmap to the One's Compliment of itself.
	 */
	public void not()
	{
		not(this, this);
	}
	
	/**
	 * XORs this Bitmap with another, changing the contents of out.
	 * @param a the first bitmap.
	 * @param b the second bitmap.
	 * @param out the output bitmap.
	 */
	public static void xor(OGLBitmap a, OGLBitmap b, OGLBitmap out)
	{
		int min = Math.min(Math.min(a.bits.length, b.bits.length),out.bits.length);
		for (int i = 0; i < min; i++)
			out.bits[i] = (byte)(a.bits[i] ^ b.bits[i]);
	}
	
	/**
	 * ANDs this Bitmap with another, changing the contents of out.
	 * @param a the first bitmap.
	 * @param b the second bitmap.
	 * @param out the output bitmap.
	 */
	public static void and(OGLBitmap a, OGLBitmap b, OGLBitmap out)
	{
		int min = Math.min(Math.min(a.bits.length, b.bits.length),out.bits.length);
		for (int i = 0; i < min; i++)
			out.bits[i] = (byte)(a.bits[i] & b.bits[i]);
	}

	/**
	 * ORs this Bitmap with another, changing the contents of out.
	 * @param a the first bitmap.
	 * @param b the second bitmap.
	 * @param out the output bitmap.
	 */
	public static void or(OGLBitmap a, OGLBitmap b, OGLBitmap out)
	{
		int min = Math.min(Math.min(a.bits.length, b.bits.length), out.bits.length);
		for (int i = 0; i < min; i++)
			out.bits[i] = (byte)(a.bits[i] | b.bits[i]);
	}

	/**
	 * NOTs this Bitmap with another, changing the contents of out.
	 * @param a the input bitmap.
	 * @param out the output bitmap.
	 */
	public static void not(OGLBitmap a, OGLBitmap out)
	{
		int min = Math.min(a.bits.length, out.bits.length);
		for (int i = 0; i < min; i++)
			out.bits[i] = (byte)(~a.bits[i]);
	}

	/**
	 * @return the length of this Bitmap in bytes.
	 */
	public int getByteLength()
	{
		return bits.length;
	}
	
	/**
	 * @return the byte representation of this Bitmap (internal byte array).
	 */
	public byte[] getBytes()
	{
		return bits;
	}

	/**
	 * @return this bitmap's width.
	 */
	public final int getWidth()
	{
		return width;
	}

	/**
	 * @return this bitmap's height.
	 */
	public final int getHeight()
	{
		return height;
	}
	
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < height; i++)
		{
			for (int j = 0; j < width; j++)
				sb.append(getBit(j,i)?'1':'0');
			sb.append('\n');
		}
		return sb.toString();
	}
	
}
