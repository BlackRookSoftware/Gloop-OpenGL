/*******************************************************************************
 * Copyright (c) 2021 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at 
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/

package com.blackrook.gloop.opengl;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import com.blackrook.gloop.opengl.util.ImageUtils;

public final class ImageTest 
{
	public static void main(String[] args) throws Exception
	{
		BufferedImage image = ImageIO.read(new File("E:\\Users\\Matt\\Desktop\\Untitled.png"));
		ImageIO.write(ImageUtils.createMipMapImage(image, ImageUtils.ResizeQuality.BICUBIC), "png", new File("junk.png"));
	}
	
}
