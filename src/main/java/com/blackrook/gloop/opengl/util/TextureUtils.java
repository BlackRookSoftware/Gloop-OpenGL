/*******************************************************************************
 * Copyright (c) 2021-2022 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at 
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gloop.opengl.util;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

/**
 * Texture utility class.
 * <p> 
 * All of these methods can be called outside of the graphics thread.
 * @author Matthew Tropiano
 */
public final class TextureUtils
{

	/**
	 * Finds the closest power of two to an integer value, larger than the initial value.
	 * <p>Examples:</p>
	 * <ul>
	 * <li>If x is 19, this returns 32.</li>
	 * <li>If x is 4, this returns 4.</li>
	 * <li>If x is 99, this returns 128.</li>
	 * <li>If x is 129, this returns 256.</li>
	 * </ul>
	 * @param x	the input value.
	 * @return the closest power of two.
	 */
	public static int closestPowerOfTwo(int x)
	{
		if (x <= 1)
			return 1;
		if (x == 2)
			return x;
		int out = 2;
		while (x > 1)
		{
			out <<= 1;
			x >>= 1;
		}
		return out;
	}

	/**
	 * Checks if an integer is a valid power of two.
	 * @param x the input value.
	 * @return true if it is, false if not.
	 */
	public static boolean isPowerOfTwo(int x)
	{
		return (x & (x-1)) == 0;
	}

	/**
	 * Checks if a texture has power-of-two dimensions.
	 * @param image the image to check.
	 * @return true if so, false if not.
	 */
	public static boolean hasPowerOfTwoDimensions(BufferedImage image)
	{
		return isPowerOfTwo(image.getWidth()) && isPowerOfTwo(image.getHeight());
	}
	
	/**
	 * Returns the raw size in bytes that this image will need for byte
	 * buffer/array storage.
	 * @param image the image to inspect.
	 * @return the size in pixels for the image.
	 */
	public static int getRawSize(BufferedImage image)
	{
		int imageWidth = image.getWidth();
		int imageHeight = image.getHeight();
		return imageWidth * imageHeight * 4;
	}

	/**
	 * Gets the byte data for a texture in BGRA color information per pixel.
	 * @param image the input image.
	 * @return a new direct {@link ByteBuffer} of the image's byte data.
	 */
	public static ByteBuffer getBGRAByteData(BufferedImage image)
	{
		ByteBuffer out = BufferUtils.allocDirectByteBuffer(getRawSize(image));
		out.order(ByteOrder.LITTLE_ENDIAN);
		convertImageData(image, out.asIntBuffer());
		return out;
	}

	/**
	 * Gets the byte data for a contiguous series of textures in BGRA color information per pixel.
	 * Works best if all of the textures are the same size.
	 * @param image the image list.
	 * @return a new direct {@link ByteBuffer} of the image's byte data.
	 */
	public static ByteBuffer getBGRAByteData(BufferedImage ... image)
	{
		int size = 0;
		for (int i = 0; i < image.length; i++)
			size += getRawSize(image[i]);
		
		ByteBuffer out = BufferUtils.allocDirectByteBuffer(size);
		out.order(ByteOrder.LITTLE_ENDIAN);
		IntBuffer ibuf = out.asIntBuffer();
		for (int i = 0; i < image.length; i++)
			convertImageData(image[i], ibuf);
		return out;
	}

	/**
	 * Gets the byte data for a texture in ARGB color information per pixel.
	 * @param image the input image.
	 * @return a new direct {@link ByteBuffer} of the image's byte data.
	 */
	public static ByteBuffer getARGBByteData(BufferedImage image)
	{
		ByteBuffer out = BufferUtils.allocDirectByteBuffer(getRawSize(image));
		out.order(ByteOrder.BIG_ENDIAN);
		convertImageData(image, out.asIntBuffer());
		return out;
	}

	/**
	 * Gets the byte data for a contiguous series of textures in ARGB color information per pixel.
	 * Works best if all of the textures are the same size.
	 * @param image the image list.
	 * @return a new direct {@link ByteBuffer} of the image's byte data.
	 */
	public static ByteBuffer getARGBByteData(BufferedImage ... image)
	{
		int size = 0;
		for (int i = 0; i < image.length; i++)
			size += getRawSize(image[i]);
		
		ByteBuffer out = BufferUtils.allocDirectByteBuffer(size);
		out.order(ByteOrder.BIG_ENDIAN);
		IntBuffer ibuf = out.asIntBuffer();
		for (int i = 0; i < image.length; i++)
			convertImageData(image[i], ibuf);
		return out;
	}

	/**
	 * Gets the byte data for a texture in ARGB color information per pixel.
	 * @param image the input image.
	 * @return a new direct {@link ByteBuffer} of the image's byte data.
	 */
	public static ByteBuffer getRGBAByteData(BufferedImage image)
	{
		ByteBuffer out = BufferUtils.allocDirectByteBuffer(getRawSize(image));
		out.order(ByteOrder.BIG_ENDIAN);
		convertRGBAImageData(image, out.asIntBuffer());
		return out;
	}

	/**
	 * Gets the byte data for a texture in ARGB color information per pixel.
	 * @param image the input image.
	 * @return a new direct {@link ByteBuffer} of the image's byte data.
	 */
	public static ByteBuffer getRGBAByteData(BufferedImage ... image)
	{
		int size = 0;
		for (int i = 0; i < image.length; i++)
			size += getRawSize(image[i]);
		
		ByteBuffer out = BufferUtils.allocDirectByteBuffer(size);
		out.order(ByteOrder.BIG_ENDIAN);
		IntBuffer ibuf = out.asIntBuffer();
		for (int i = 0; i < image.length; i++)
			convertRGBAImageData(image[i], ibuf);
		return out;
	}

	/**
	 * Converts color byte data to a BufferedImage.
	 * @param imageBGRAData the input BGRA byte data.
	 * @param width the width of the resultant image.
	 * @param height the height of the resultant image.
	 * @return a new direct {@link ByteBuffer} of the image's byte data.
	 */
	public static BufferedImage setImageData(ByteBuffer imageBGRAData, int width, int height)
	{
		IntBuffer imageData = imageBGRAData.asIntBuffer();
		BufferedImage out = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		int[] data = new int[imageData.capacity()];
		imageData.get(data);
		out.setRGB(0, 0, width, height, data, 0, width);
		return out;
	}

	// Puts image data into an IntBuffer.
	private static void convertImageData(BufferedImage image, IntBuffer intout)
	{
		int imageWidth = image.getWidth();
		int imageHeight = image.getHeight();
		int[] data = new int[imageWidth * imageHeight];
		image.getRGB(0, 0, imageWidth, imageHeight, data, 0, imageWidth);
		intout.put(data);
	}

	// Puts image data into an IntBuffer.
	private static void convertRGBAImageData(BufferedImage image, IntBuffer intout)
	{
		int imageWidth = image.getWidth();
		int imageHeight = image.getHeight();
		int[] data = new int[imageWidth * imageHeight];
		image.getRGB(0, 0, imageWidth, imageHeight, data, 0, imageWidth);
		for (int i = 0; i < data.length; i++)
			data[i] = (data[i] << 8) | (((data[i] & 0xff000000) >> 24) & 0x0ff);
		intout.put(data);
	}

}
