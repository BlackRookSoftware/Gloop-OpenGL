package com.blackrook.gloop.opengl.util;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

/**
 * Image utility class. 
 * <p> 
 * All of these methods can be called outside of the graphics thread.
 * @author Matthew Tropiano
 */
public final class ImageUtils
{
	private ImageUtils() {}
	
	/**
	 * Enumeration of image resizing hints 
	 */
	public enum ResizeQuality
	{
		NEAREST
		{
			@Override
			public void setHints(Graphics2D g2d)
			{
				g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
				g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
			}
		},
		
		LINEAR
		{
			@Override
			public void setHints(Graphics2D g2d)
			{
				g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
				g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			}
		},
		
		BILINEAR
		{
			@Override
			public void setHints(Graphics2D g2d)
			{
				g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
				g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			}
		},
		
		BICUBIC
		{
			@Override
			public void setHints(Graphics2D g2d)
			{
				g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
				g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			}
		};

		/**
		 * Sets the appropriate resize quality hints on the graphic instance.
		 * @param g2d the Graphics2D context to change.
		 */
		public abstract void setHints(Graphics2D g2d);
	}
	
	/**
	 * Resizes an image using nearest filtering.
	 * @param source the source image.
	 * @param quality the rendering quality to use.
	 * @param newWidth the new image width.
	 * @param newHeight	the new image height.
	 * @return the resized image.
	 */
	public static BufferedImage performResize(BufferedImage source, ResizeQuality quality, int newWidth, int newHeight)
	{
		BufferedImage out = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = out.createGraphics();
		g2d.setComposite(AlphaComposite.Src);
		quality.setHints(g2d);
		g2d.drawImage(source, 0, 0, newWidth, newHeight, null);
		g2d.dispose();
		return out;
	}

	/**
	 * Flips an image across one or two axes.
	 * @param source the source image.
	 * @param flipX if true, flips horizontally.
	 * @param flipY if true, flips vertically.
	 * @return an output image where the contents are flipped according to parameters.
	 */
	public static BufferedImage performFlip(BufferedImage source, boolean flipX, boolean flipY)
	{
		int width = source.getWidth();
		int height = source.getHeight();
		BufferedImage out = new BufferedImage(width, source.getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = out.createGraphics();
		g2d.setComposite(AlphaComposite.Src);
		AffineTransform transform = g2d.getTransform();
		transform.concatenate(AffineTransform.getScaleInstance(flipX ? -1.0 : 1.0, flipY ? -1.0 : 1.0));
		transform.concatenate(AffineTransform.getTranslateInstance(flipX ? -width : 0, flipY ? -height : 0));
		g2d.setTransform(transform);
		g2d.drawImage(source, 0, 0, width, height, null);
		g2d.dispose();
		return out;
	}

	// Calculates the amount of mipmap levels in an image.
	private static int calculateLevels(int referenceWidth)
	{
		int levels = 1;
		while ((referenceWidth = referenceWidth >>> 1) > 0)
			levels++;
		return levels;
	}
	
	/**
	 * Creates a series of texture mipmaps from an original image.
	 * The first image returned is the original, and each subsequent image is a smaller map.
	 * Best used with square images whose width and height are powers of two.
	 * @param source the source image.
	 * @param quality the resize quality.
	 * @return the array of images in texture mipmap level ordering (largest to smallest).
	 */
	public static BufferedImage[] createMipMaps(BufferedImage source, ResizeQuality quality)
	{
		BufferedImage[] out = new BufferedImage[calculateLevels(Math.max(source.getWidth(), source.getHeight()))];
		
		out[0] = new BufferedImage(source.getWidth(), source.getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = out[0].createGraphics();
		g.drawImage(source, 0, 0, null);
		g.dispose();
		
		for (int i = 1; i < out.length; i++)
		{
			out[i] = new BufferedImage(Math.max(out[i - 1].getWidth() / 2, 1), Math.max(out[i - 1].getHeight() / 2, 1), BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2d = out[i].createGraphics();
			quality.setHints(g2d);
			g2d.drawImage(source, 0, 0, out[i].getWidth(), out[i].getHeight(), null);
			g2d.dispose();
		}
		
		return out;
	}
	
	/**
	 * Creates an image that contains the source image and all of its mipmap levels.
	 * The first image returned is the original, and each subsequent image is a smaller map.
	 * Best used with square images whose width and height are powers of two.
	 * @param source the source image.
	 * @param quality the resize quality.
	 * @return the resultant image.
	 * @see #createMipMaps(BufferedImage, ResizeQuality)
	 */
	public static BufferedImage createMipMapImage(BufferedImage source, ResizeQuality quality)
	{
		BufferedImage[] images = createMipMaps(source, quality);
		BufferedImage out = new BufferedImage(source.getWidth() + (source.getWidth() / 2), source.getHeight(), BufferedImage.TYPE_INT_ARGB);

		Graphics2D g2d = out.createGraphics();
		g2d.drawImage(images[0], 0, 0, null);
		
		int offset = 0;
		for (int i = 1; i < images.length; i++)
		{
			g2d.drawImage(images[i], source.getWidth(), offset, null);
			offset += images[i].getHeight();
		}
		
		g2d.dispose();
		return out;
	}
	
	/**
	 * Creates a series of images from an image that contains a primary image and all of its mipmap levels.
	 * <p>This assumes a composition pattern as though it were created with {@link #createMipMapImage(BufferedImage, ResizeQuality)}. 
	 * @param source the source image.
	 * @return the resultant image.
	 */
	public static BufferedImage[] splitMipMapImage(BufferedImage source)
	{
		BufferedImage[] out = new BufferedImage[calculateLevels(source.getHeight())];
		out[0] = new BufferedImage(source.getWidth() * 2 / 3, source.getHeight(), BufferedImage.TYPE_INT_ARGB);

		Graphics2D g = out[0].createGraphics();
		g.drawImage(source.getSubimage(0, 0, out[0].getWidth(), out[0].getHeight()), 0, 0, null);
		g.dispose();
		
		int offsetX = out[0].getWidth();
		int offsetY = 0;
		int width = out[0].getWidth() / 2;
		int height = out[0].getHeight() / 2;
		for (int i = 1; i < out.length; i++)
		{
			out[i] = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2d = out[i].createGraphics();
			g2d.drawImage(source.getSubimage(offsetX, offsetY, width, height), 0, 0, null);
			g2d.dispose();
			offsetY += height;
			width += width / 2;
			height += height / 2;
		}
		
		return out;
	}
	
	/**
	 * Creates a series of images from a larger image, such that the resultant images 
	 * are made from dividing up the source image into a grid of equally-sized images.
	 * @param source the source image.
	 * @param rows the amount of grid rows.
	 * @param columns the amount of grid columns.
	 * @return the resultant images, in row-major order.
	 */
	public static BufferedImage[] splitTextureGrid(BufferedImage source, int rows, int columns)
	{
		BufferedImage[] out = new BufferedImage[rows * columns];

		int width = source.getWidth() / columns;
		int height = source.getHeight() / rows;

		for (int i = 0; i < out.length; i++)
		{
			int x = i % columns;
			int y = i / columns;
			
			out[i] = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2d = out[i].createGraphics();
			g2d.drawImage(source.getSubimage(x * width, y * height, width, height), 0, 0, null);
			g2d.dispose();
		}
		
		return out;
	}
	
}
