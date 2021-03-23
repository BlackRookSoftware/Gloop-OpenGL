/*******************************************************************************
 * Copyright (c) 2021 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at 
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gloop.opengl.gl3.enums;

import static org.lwjgl.opengl.GL31.*;

/**
 * Describes a list of possible framebuffer attachments.
 * @author Matthew Tropiano
 */
public enum AttachPoint
{
	COLOR0(GL_COLOR),
	COLOR1(GL_COLOR_ATTACHMENT1),
	COLOR2(GL_COLOR_ATTACHMENT2),
	COLOR3(GL_COLOR_ATTACHMENT3),
	COLOR4(GL_COLOR_ATTACHMENT4),
	COLOR5(GL_COLOR_ATTACHMENT5),
	COLOR6(GL_COLOR_ATTACHMENT6),
	COLOR7(GL_COLOR_ATTACHMENT7),
	COLOR8(GL_COLOR_ATTACHMENT8),
	COLOR9(GL_COLOR_ATTACHMENT9),
	COLOR10(GL_COLOR_ATTACHMENT10),
	COLOR11(GL_COLOR_ATTACHMENT11),
	COLOR12(GL_COLOR_ATTACHMENT12),
	COLOR13(GL_COLOR_ATTACHMENT13),
	COLOR14(GL_COLOR_ATTACHMENT14),
	COLOR15(GL_COLOR_ATTACHMENT15),
	DEPTH(GL_DEPTH_ATTACHMENT),
	STENCIL(GL_STENCIL_ATTACHMENT),
	DEPTH_STENCIL(GL_DEPTH_STENCIL_ATTACHMENT);
	
	public final int glVal;
	AttachPoint(int gltype) {glVal = gltype;}
	
}
