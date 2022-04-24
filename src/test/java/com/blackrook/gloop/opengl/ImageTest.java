/*******************************************************************************
 * Copyright (c) 2021-2022 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at 
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/

package com.blackrook.gloop.opengl;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;

import javax.imageio.ImageIO;

import com.blackrook.gloop.opengl.util.ImageUtils;

public final class ImageTest 
{
	public static void main(String[] args) throws Exception
	{
		try (InputStream in = openResource("example/textures/earth.png"))
		{
			ImageIO.write(ImageUtils.createMipMapImage(ImageIO.read(in), ImageUtils.ResizeQuality.BICUBIC), "png", new File("junk.png"));
		}
		try (InputStream in = openResource("example/textures/earth.png"))
		{
			BufferedImage[] images = ImageUtils.splitTextureGrid(ImageIO.read(in), 4, 3); 
			for (int i = 0; i < images.length; i++)
				ImageIO.write(images[i], "png", new File("junk"+i+".png"));
		}
	}

	/**
	 * Opens an {@link InputStream} to a resource using the current thread's {@link ClassLoader}.
	 * @param pathString the resource pathname.
	 * @return an open {@link InputStream} for reading the resource or null if not found.
	 * @see ClassLoader#getResourceAsStream(String)
	 */
	public static InputStream openResource(String pathString)
	{
		return Thread.currentThread().getContextClassLoader().getResourceAsStream(pathString);
	}

}
