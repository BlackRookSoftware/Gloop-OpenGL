/*******************************************************************************
 * Copyright (c) 2021-2022 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at 
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/

package com.blackrook.gloop.opengl;

import com.blackrook.gloop.glfw.GLFWContext;
import com.blackrook.gloop.glfw.GLFWInputSystem;
import com.blackrook.gloop.glfw.GLFWWindow;
import com.blackrook.gloop.glfw.GLFWWindow.WindowHints;
import com.blackrook.gloop.glfw.GLFWWindow.WindowHints.OpenGLProfile;
import com.blackrook.gloop.glfw.input.annotation.OnKeyAction;
import com.blackrook.gloop.glfw.input.enums.KeyType;
import com.blackrook.gloop.opengl.OGLGraphics.Info;
import com.blackrook.gloop.opengl.enums.SyncResultType;
import com.blackrook.gloop.opengl.enums.TextureWrapType;
import com.blackrook.gloop.opengl.gl4.OGL40Graphics;
import com.blackrook.gloop.opengl.node.OGLNode;
import com.blackrook.gloop.opengl.node.OGLTriggeredNode;

@SuppressWarnings("unused")
public final class OGLTest2 
{
	private GLFWWindow window;
	private OGLSystem<OGL40Graphics> oglSystem;
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
			.setContextVersion(4, 0)
			.setOpenGLProfile(OpenGLProfile.CORE_PROFILE);
		
		GLFWInputSystem inputSystem = new GLFWInputSystem();
		window = new GLFWWindow(hints, "Hello World!", 640, 480);
		inputSystem.attachToWindow(window);
		inputSystem.addInputObject(new Keyboard());
		window.setVisible(true);
		
		oglSystem = OGLSystemFactory.getOpenGL40Core(window);
		oglSystem.addNode(new DrawNode());
		oglSystem.setFPS(null);
		
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
	
	public static class DrawNode implements OGLNode<OGL40Graphics>
	{
		@Override
		public void onDisplay(OGL40Graphics gl)
		{
			Info info = gl.getInfo();
		}
	}
	
	public static void main(String[] args)
	{
		(new OGLTest2()).run();
	}
	
}
