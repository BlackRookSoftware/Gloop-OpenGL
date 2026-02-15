/*******************************************************************************
 * Copyright (c) 2021-2022 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at 
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gloop.opengl.gl1;

import com.blackrook.gloop.opengl.OGLObject;
import com.blackrook.gloop.opengl.enums.TextureTargetType;
import com.blackrook.gloop.opengl.exception.GraphicsException;

import static org.lwjgl.opengl.GL11.*;

/**
 * Standard texture class.
 * @author Matthew Tropiano
 */
public class OGLTexture extends OGLObject
{
	/** List of OpenGL object ids that were not deleted properly. */
	protected static int[] UNDELETED_IDS;
	/** Amount of OpenGL object ids that were not deleted properly. */
	protected static int UNDELETED_LENGTH;
	
	static
	{
		UNDELETED_IDS = new int[32];
		UNDELETED_LENGTH = 0;
	}

	/** The target that this texture was first bound to. */
	private TextureTargetType usedtarget;
	
	/**
	 * Creates a new blank texture object.
	 */
	OGLTexture()
	{
		setName(glGenTextures());
		this.usedtarget = null;
	}
	
	/**
	 * Sets the used target for this texture, or throws an exception if
	 * the used target is not null and a different target was already set.
	 * @param target the target to set.
	 */
	void setUsedTarget(TextureTargetType target)
	{
		if (this.usedtarget != null && this.usedtarget != target)
			throw new GraphicsException("Texture was already bound to a different target: " + this.usedtarget.name());
		this.usedtarget = target;
	}
	
	/**
	 * Gets the texture target that this texture object was first bound to.
	 * OpenGL disallows a texture to be bound to a different target after it was first bound to one.
	 * @return the target it was first bound to, or null if not bound yet.
	 */
	public TextureTargetType getUsedtarget()
	{
		return usedtarget;
	}
	
	@Override
	protected void free()
	{
		glDeleteTextures(getName());
	}
	
	/**
	 * Destroys undeleted texture objects abandoned from destroyed Java objects.
	 * <p><b>This is automatically called by OGLSystem after every frame and should NEVER be called manually!</b>
	 * @return the amount of objects deleted.
	 */
	public static int destroyUndeleted()
	{
		if (UNDELETED_LENGTH > 0)
		{
			int out = UNDELETED_LENGTH;
			for (int i = 0; i < UNDELETED_LENGTH; i++)
				glDeleteTextures(UNDELETED_IDS[i]);
			UNDELETED_LENGTH = 0;
			return out;
		}
		return 0;
	}

	// adds the OpenGL Id to the UNDELETED_IDS list.
	private static void finalizeAddId(int id)
	{
		if (UNDELETED_LENGTH == UNDELETED_IDS.length)
			UNDELETED_IDS = expand(UNDELETED_IDS, UNDELETED_IDS.length * 2);
		UNDELETED_IDS[UNDELETED_LENGTH++] = id;
	}

	@Override
	public void finalize() throws Throwable
	{
		if (isAllocated())
			finalizeAddId(getName());
		super.finalize();
	}

}
