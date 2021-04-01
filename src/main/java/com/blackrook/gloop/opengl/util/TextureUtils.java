package com.blackrook.gloop.opengl.util;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

import com.blackrook.gloop.opengl.enums.BufferTargetType;
import com.blackrook.gloop.opengl.enums.CachingHint;
import com.blackrook.gloop.opengl.enums.DataType;
import com.blackrook.gloop.opengl.gl1.OGL15Graphics;
import com.blackrook.gloop.opengl.gl1.OGLBuffer;
import com.blackrook.gloop.opengl.struct.BufferUtils;

/**
 * Texture utility class. 
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
	 * Puts pixel data into an {@link OGLBuffer}. 
	 * The buffer's contents are completely replaced.
	 * @param g the graphics instance.
	 * @param data the byte buffer to load into an OGLBuffer. 
	 * @param out the OGL buffer to put the data into.
	 */
	public static void putImageData(OGL15Graphics g, ByteBuffer data, OGLBuffer out)
	{
		g.setBuffer(BufferTargetType.PIXEL, out);
		g.setBufferCapacity(BufferTargetType.PIXEL, DataType.UNSIGNED_BYTE, CachingHint.STREAM_DRAW, data.capacity());
		g.setBufferSubData(BufferTargetType.PIXEL, 0, data);
		g.unsetBuffer(BufferTargetType.PIXEL);
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

}
