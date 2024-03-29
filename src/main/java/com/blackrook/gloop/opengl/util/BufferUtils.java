/*******************************************************************************
 * Copyright (c) 2021-2022 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at 
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gloop.opengl.util;

import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;

/**
 * Simple utility functions around buffers.
 * @author Matthew Tropiano
 */
public final class BufferUtils
{
	/** The size of a character in bytes. */
	public static final int SIZEOF_CHAR = Character.SIZE/Byte.SIZE;
	/** The size of an int in bytes. */
	public static final int SIZEOF_INT = Integer.SIZE/Byte.SIZE;
	/** The size of a float in bytes. */
	public static final int SIZEOF_FLOAT = Float.SIZE/Byte.SIZE;
	/** The size of a byte in bytes (This should always be 1, or Sun screwed up).*/
	public static final int SIZEOF_BYTE = Byte.SIZE/Byte.SIZE;
	/** The size of a short in bytes. */
	public static final int SIZEOF_SHORT = Short.SIZE/Byte.SIZE;
	/** The size of a long in bytes. */
	public static final int SIZEOF_LONG = Long.SIZE/Byte.SIZE;
	/** The size of a double in bytes. */
	public static final int SIZEOF_DOUBLE = Double.SIZE/Byte.SIZE;

	private BufferUtils() {}

	/**
	 * Allocates space for a DIRECT ByteBuffer in native byte order
	 * (which really doesn't matter).
	 * @param len the length (IN BYTES) of the buffer. 
	 * @return a direct buffer that can hold <code>len</code> items.
	 */
	public static ByteBuffer allocDirectByteBuffer(int len)
	{
		ByteBuffer b = ByteBuffer.allocateDirect(len*SIZEOF_BYTE);
		b.order(ByteOrder.nativeOrder());
		return b;
	}

	/**
	 * Allocates space for a DIRECT IntBuffer in native byte order.
	 * @param len the length (IN INTS) of the buffer. 
	 * @return a direct buffer that can hold <code>len</code> items.
	 */
	public static IntBuffer allocDirectIntBuffer(int len)
	{
		ByteBuffer b = ByteBuffer.allocateDirect(len*SIZEOF_INT);
		b.order(ByteOrder.nativeOrder());
		return b.asIntBuffer();
	}

	/**
	 * Allocates space for a DIRECT FloatBuffer in native byte order.
	 * @param len the length (IN FLOATS) of the buffer. 
	 * @return a direct buffer that can hold <code>len</code> items.
	 */
	public static FloatBuffer allocDirectFloatBuffer(int len)
	{
		ByteBuffer b = ByteBuffer.allocateDirect(len*SIZEOF_FLOAT);
		b.order(ByteOrder.nativeOrder());
		return b.asFloatBuffer();
	}

	/**
	 * Allocates space for a DIRECT LongBuffer in native byte order
	 * @param len the length (IN LONGS) of the buffer. 
	 * @return a direct buffer that can hold <code>len</code> items.
	 */
	public static LongBuffer allocDirectLongBuffer(int len)
	{
		ByteBuffer b = ByteBuffer.allocateDirect(len*SIZEOF_LONG);
		b.order(ByteOrder.nativeOrder());
		return b.asLongBuffer();
	}

	/**
	 * Allocates space for a DIRECT ShortBuffer in native byte order
	 * @param len the length (IN SHORTS) of the buffer. 
	 * @return a direct buffer that can hold <code>len</code> items.
	 */
	public static ShortBuffer allocDirectShortBuffer(int len)
	{
		ByteBuffer b = ByteBuffer.allocateDirect(len*SIZEOF_SHORT);
		b.order(ByteOrder.nativeOrder());
		return b.asShortBuffer();
	}

	/**
	 * Allocates space for a DIRECT CharBuffer in native byte order
	 * @param len the length (IN CHARS) of the buffer. 
	 * @return a direct buffer that can hold <code>len</code> items.
	 */
	public static CharBuffer allocDirectCharBuffer(int len)
	{
		ByteBuffer b = ByteBuffer.allocateDirect(len*SIZEOF_CHAR);
		b.order(ByteOrder.nativeOrder());
		return b.asCharBuffer();
	}

	/**
	 * Allocates space for a DIRECT DoubleBuffer in native byte order
	 * @param len the length (IN DOUBLES) of the buffer. 
	 * @return a direct buffer that can hold <code>len</code> items.
	 */
	public static DoubleBuffer allocDirectDoubleBuffer(int len)
	{
		ByteBuffer b = ByteBuffer.allocateDirect(len*SIZEOF_DOUBLE);
		b.order(ByteOrder.nativeOrder());
		return b.asDoubleBuffer();
	}

	/**
	 * Allocates a DIRECT ByteBuffer using byte array data.
	 * Useful for native wrappers that require direct ByteBuffers.
	 * @param b	the byte array to wrap. 
	 * @return a direct buffer that holds <code>len</code> items.
	 */
	public static ByteBuffer wrapDirectBuffer(byte[] b)
	{
		ByteBuffer buf = allocDirectByteBuffer(b.length);
		buf.put(b);
		buf.flip();
		return buf;
	}

	/**
	 * Allocates a DIRECT ShortBuffer using short array data.
	 * Useful for native wrappers that require direct ShortBuffers.
	 * @param s	the short array to wrap. 
	 * @return a direct buffer that holds <code>len</code> items.
	 */
	public static ShortBuffer wrapDirectBuffer(short[] s)
	{
		ShortBuffer buf = allocDirectShortBuffer(s.length);
		buf.put(s);
		buf.flip();
		return buf;
	}

	/**
	 * Allocates a DIRECT IntBuffer using int array data.
	 * Useful for native wrappers that require direct IntBuffers.
	 * @param i	the int array to wrap. 
	 * @return a direct buffer that holds <code>len</code> items.
	 */
	public static IntBuffer wrapDirectBuffer(int[] i)
	{
		IntBuffer buf = allocDirectIntBuffer(i.length);
		buf.put(i);
		buf.flip();
		return buf;
	}

	/**
	 * Allocates a DIRECT FloatBuffer using float array data.
	 * Useful for native wrappers that require direct FloatBuffers.
	 * @param f	the float array to wrap. 
	 * @return a direct buffer that holds <code>len</code> items.
	 */
	public static FloatBuffer wrapDirectBuffer(float[] f)
	{
		FloatBuffer buf = allocDirectFloatBuffer(f.length);
		buf.put(f);
		buf.flip();
		return buf;
	}

	/**
	 * Allocates a DIRECT LongBuffer using long array data.
	 * Useful for native wrappers that require direct LongBuffers.
	 * @param l	the long array to wrap. 
	 * @return a direct buffer that holds <code>len</code> items.
	 */
	public static LongBuffer wrapDirectBuffer(long[] l)
	{
		LongBuffer buf = allocDirectLongBuffer(l.length);
		buf.put(l);
		buf.flip();
		return buf;
	}

	/**
	 * Allocates a DIRECT DoubleBuffer using double array data.
	 * Useful for native wrappers that require direct DoubleBuffers.
	 * @param d	the double array to wrap. 
	 * @return a direct buffer that holds <code>len</code> items.
	 */
	public static DoubleBuffer wrapDirectBuffer(double[] d)
	{
		DoubleBuffer buf = allocDirectDoubleBuffer(d.length);
		buf.put(d);
		buf.flip();
		return buf;
	}

	/**
	 * Prints the contents of a buffer to an output print stream.
	 * @param buffer the buffer to print.
	 * @param out the {@link PrintStream} to output the dump to.
	 */
	public static void printBuffer(ByteBuffer buffer, PrintStream out)
	{
		int len = buffer.capacity();
		out.print("[");
		for (int i = 0; i < len; i++)
		{
			out.print(buffer.get(i));
			if (i + 1 < len)
				out.print(", ");
		}
		out.println("]");
	}

	/**
	 * Prints the contents of a buffer to an output print stream.
	 * @param buffer the buffer to print.
	 * @param out the {@link PrintStream} to output the dump to.
	 */
	public static void printBuffer(CharBuffer buffer, PrintStream out)
	{
		int len = buffer.capacity();
		out.print("[");
		for (int i = 0; i < len; i++)
		{
			out.print(buffer.get(i));
			if (i + 1 < len)
				out.print(", ");
		}
		out.println("]");
	}

	/**
	 * Prints the contents of a buffer to an output print stream.
	 * @param buffer the buffer to print.
	 * @param out the {@link PrintStream} to output the dump to.
	 */
	public static void printBuffer(ShortBuffer buffer, PrintStream out)
	{
		int len = buffer.capacity();
		out.print("[");
		for (int i = 0; i < len; i++)
		{
			out.print(buffer.get(i));
			if (i + 1 < len)
				out.print(", ");
		}
		out.println("]");
	}

	/**
	 * Prints the contents of a buffer to an output print stream.
	 * @param buffer the buffer to print.
	 * @param out the {@link PrintStream} to output the dump to.
	 */
	public static void printBuffer(IntBuffer buffer, PrintStream out)
	{
		int len = buffer.capacity();
		out.print("[");
		for (int i = 0; i < len; i++)
		{
			out.print(buffer.get(i));
			if (i + 1 < len)
				out.print(", ");
		}
		out.println("]");
	}

	/**
	 * Prints the contents of a buffer to an output print stream.
	 * @param buffer the buffer to print.
	 * @param out the {@link PrintStream} to output the dump to.
	 */
	public static void printBuffer(FloatBuffer buffer, PrintStream out)
	{
		int len = buffer.capacity();
		out.print("[");
		for (int i = 0; i < len; i++)
		{
			out.print(buffer.get(i));
			if (i + 1 < len)
				out.print(", ");
		}
		out.println("]");
	}

	/**
	 * Prints the contents of a buffer to an output print stream.
	 * @param buffer the buffer to print.
	 * @param out the {@link PrintStream} to output the dump to.
	 */
	public static void printBuffer(LongBuffer buffer, PrintStream out)
	{
		int len = buffer.capacity();
		out.print("[");
		for (int i = 0; i < len; i++)
		{
			out.print(buffer.get(i));
			if (i + 1 < len)
				out.print(", ");
		}
		out.println("]");
	}

	/**
	 * Prints the contents of a buffer to an output print stream.
	 * @param buffer the buffer to print.
	 * @param out the {@link PrintStream} to output the dump to.
	 */
	public static void printBuffer(DoubleBuffer buffer, PrintStream out)
	{
		int len = buffer.capacity();
		out.print("[");
		for (int i = 0; i < len; i++)
		{
			out.print(buffer.get(i));
			if (i + 1 < len)
				out.print(", ");
		}
		out.println("]");
	}
}
