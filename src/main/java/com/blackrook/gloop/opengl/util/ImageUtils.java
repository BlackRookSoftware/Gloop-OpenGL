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
	 * Resizes an image using nearest filtering.
	 * @param source	the source image.
	 * @param newWidth	the new image width.
	 * @param newHeight	the new image height.
	 * @return the resized image.
	 */
	public static BufferedImage performResize(BufferedImage source, int newWidth, int newHeight)
	{
		BufferedImage out = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = out.createGraphics();
		g2d.setComposite(AlphaComposite.Src);
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
		g2d.setRenderingHint(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_QUALITY);
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_OFF);
		g2d.drawImage(source, 0, 0, newWidth, newHeight, null);
		g2d.dispose();
		return out;
	}

	/**
	 * Resizes an image using bilinear filtering.
	 * @param source	the source image.
	 * @param newWidth	the new image width.
	 * @param newHeight	the new image height.
	 * @return the resized image.
	 */
	public static BufferedImage performResizeBilinear(BufferedImage source, int newWidth, int newHeight)
	{
		BufferedImage out = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = out.createGraphics();
		g2d.setComposite(AlphaComposite.Src);
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g2d.setRenderingHint(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_QUALITY);
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.drawImage(source, 0, 0, newWidth, newHeight, null);
		g2d.dispose();
		return out;
	}

	/**
	 * Resizes an image using trilinear filtering.
	 * @param source	the source image.
	 * @param newWidth	the new image width.
	 * @param newHeight	the new image height.
	 * @return the resized image.
	 */
	public static BufferedImage performResizeTrilinear(BufferedImage source, int newWidth, int newHeight)
	{
		BufferedImage out = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = out.createGraphics();
		g2d.setComposite(AlphaComposite.Src);
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		g2d.setRenderingHint(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_QUALITY);
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
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

}
