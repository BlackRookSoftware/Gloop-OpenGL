/*******************************************************************************
 * Copyright (c) 2021 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at 
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/

package com.blackrook.gloop.opengl;

import java.nio.IntBuffer;

import com.blackrook.gloop.glfw.GLFWContext;
import com.blackrook.gloop.glfw.GLFWInputSystem;
import com.blackrook.gloop.glfw.GLFWWindow;
import com.blackrook.gloop.glfw.GLFWWindow.WindowHints;
import com.blackrook.gloop.glfw.GLFWWindow.WindowHints.OpenGLProfile;
import com.blackrook.gloop.glfw.input.annotation.OnKeyAction;
import com.blackrook.gloop.glfw.input.enums.KeyType;
import com.blackrook.gloop.opengl.enums.BufferTargetType;
import com.blackrook.gloop.opengl.enums.CachingHint;
import com.blackrook.gloop.opengl.enums.MatrixMode;
import com.blackrook.gloop.opengl.gl1.OGLBuffer;
import com.blackrook.gloop.opengl.gl1.OGLTexture;
import com.blackrook.gloop.opengl.gl2.OGLShader;
import com.blackrook.gloop.opengl.gl3.OGL32Graphics;
import com.blackrook.gloop.opengl.node.OGLNodeAdapter;
import com.blackrook.gloop.opengl.struct.BufferUtils;
import com.blackrook.gloop.opengl.util.GeometryBuilder;

public final class OGLTest 
{
	private GLFWWindow window;
	private OGLSystem<OGL32Graphics> oglSystem;
	private GLFWContext.MainLoop mainLoop;
	
	public void run() 
	{
		// Setup an error callback. The default implementation
		// will print the error message in System.err.
		GLFWContext.setErrorStream(System.err);
		
		// Configure GLFW
		WindowHints hints = (new WindowHints())
			.setVisible(false)
			.setResizable(true)
			.setContextVersion(3, 2)
			.setOpenGLProfile(OpenGLProfile.CORE_PROFILE);
		
		GLFWInputSystem inputSystem = new GLFWInputSystem();
		window = new GLFWWindow(hints, "Hello World!", 640, 480);
		inputSystem.attachToWindow(window);
		inputSystem.addInputObject(new Keyboard());
		window.setVisible(true);
		
		oglSystem = OGLSystem.getOpenGL32Core(window);
		oglSystem.addNode(new DrawNode());
		oglSystem.setFPS(60);
		
		mainLoop = GLFWContext.createLoop(window, inputSystem);
		mainLoop.setShutDownOnExit(true);
		mainLoop.run();
	}
	
	public class Keyboard
	{
		@OnKeyAction
		public void onKey(KeyType type, boolean pressed)
		{
			if (type == KeyType.ESCAPE && !pressed)
				window.setClosing(true);
			else if (type == KeyType.R && pressed)
				oglSystem.display();
		}
	}
	
	public static class DrawNode extends OGLNodeAdapter<OGL32Graphics>
	{
		private static final int VERTEX = 0;
		private static final int COLOR = 1;
		private static final int TEXCOORD = 2;

		private OGLTexture texture;
		private OGLBuffer geometry;
		private OGLBuffer indices;
		private OGLShader shader;
		private boolean once;
		
		@Override
		public void onDisplay(OGL32Graphics gl)
		{
			if (!once)
			{
				gl.setClearColor(0, 0, 0, 1);
				gl.setClearDepth(-1);
				
				GeometryBuilder builder = GeometryBuilder.start(4, 3, 4, 2)
					.add(VERTEX,   -1,  1, 0)
					.add(COLOR,     1,  1, 1, 1)
					.add(TEXCOORD,  0,  0)
	
					.add(VERTEX,   -1, -1, 0)
					.add(COLOR,     1,  1, 1, 1)
					.add(TEXCOORD,  0,  1)
					
					.add(VERTEX,    1, -1, 0)
					.add(COLOR,     1,  1, 1, 1)
					.add(TEXCOORD,  1,  1)
					
					.add(VERTEX,   -1,  1, 0)
					.add(COLOR,     1,  1, 1, 1)
					.add(TEXCOORD,  1,  0)
				;
				
				IntBuffer idx = BufferUtils.allocDirectIntBuffer(4)
					.put(0)
					.put(1)
					.put(2)
					.put(3)
				;
				
				geometry = gl.createBuffer();
				gl.setBuffer(BufferTargetType.GEOMETRY, geometry);
				gl.setBufferData(BufferTargetType.GEOMETRY, CachingHint.STATIC_DRAW, builder.getBuffer());
				gl.unsetBuffer(BufferTargetType.GEOMETRY);

				indices = gl.createBuffer();
				gl.setBuffer(BufferTargetType.INDICES, indices);
				gl.setBufferData(BufferTargetType.INDICES, CachingHint.STATIC_DRAW, idx);
				gl.unsetBuffer(BufferTargetType.INDICES);
				
			}
			
			gl.clear(true, true, false, false);
			gl.setColorMask(true);
			gl.setDepthTestEnabled(false);
			
			gl.matrixMode(MatrixMode.TEXTURE);
			gl.matrixReset();

			gl.matrixMode(MatrixMode.PROJECTION);
			gl.matrixReset();
			gl.matrixOrtho(-2f, 2f, -1.5f, 1.5f, -1f, 1f);
			
			gl.matrixMode(MatrixMode.MODELVIEW);
			gl.matrixReset();
			
			// TODO: Finish.
			
		}
	}
	
	public static void main(String[] args)
	{
		(new OGLTest()).run();
	}
	
}
