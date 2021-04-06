/*******************************************************************************
 * Copyright (c) 2021 Black Rook Software
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
import com.blackrook.gloop.opengl.enums.GeometryType;
import com.blackrook.gloop.opengl.enums.MatrixMode;
import com.blackrook.gloop.opengl.enums.ShaderType;
import com.blackrook.gloop.opengl.gl1.OGLBuffer;
import com.blackrook.gloop.opengl.gl2.OGLProgram;
import com.blackrook.gloop.opengl.gl3.OGL33Graphics;
import com.blackrook.gloop.opengl.gl3.OGLVertexArrayState;
import com.blackrook.gloop.opengl.node.OGLNodeAdapter;
import com.blackrook.gloop.opengl.util.GeometryBuilder;

public final class OGLTest 
{
	private GLFWWindow window;
	private OGLSystem<OGL33Graphics> oglSystem;
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
			.setContextVersion(3, 3)
			.setOpenGLProfile(OpenGLProfile.COMPAT_PROFILE);
		
		GLFWInputSystem inputSystem = new GLFWInputSystem();
		window = new GLFWWindow(hints, "Hello World!", 640, 480);
		inputSystem.attachToWindow(window);
		inputSystem.addInputObject(new Keyboard());
		window.setVisible(true);
		
		oglSystem = OGLSystem.getOpenGL33(window);
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
	
	public static class DrawNode extends OGLNodeAdapter<OGL33Graphics>
	{
		private static final int VERTEX = 0;
		private static final int COLOR = 1;
		// private static final int TEXCOORD = 2;

		private OGLProgram program;
		private OGLBuffer geometry;
		// private OGLTexture texture;
		private OGLVertexArrayState vstate;
		private boolean once;
		
		private boolean viewportChange;
		private int viewportWidth;
		private int viewportHeight;
		
		@Override
		public void onFramebufferResize(int newWidth, int newHeight)
		{
			viewportWidth = newWidth;
			viewportHeight = newHeight;
			viewportChange = true;
		}
		
		@Override
		public void onDisplay(OGL33Graphics gl)
		{
			if (viewportChange)
			{
				gl.setViewport(0, 0, viewportWidth, viewportHeight);
				viewportChange = false;
			}
			
			if (!once)
			{
				program = gl.createProgramBuilder()
					.setShader(ShaderType.VERTEX, (new StringBuilder())
						.append("#version 330\n")
						.append("in vec3 position;\n")
						.append("in vec4 color;\n")
						.append("\n")
						.append("out vec4 varyingColor;\n")
						.append("\n")
						.append("void main()\n")
						.append("{\n")
						.append("    varyingColor = color;\n")
						.append("    gl_Position = vec4(position.xyz, 1.0);\n")
						.append("}\n")
					.toString())
					.setShader(ShaderType.FRAGMENT, (new StringBuilder())
						.append("#version 330\n")
						.append("in vec4 varyingColor;\n")
						.append("\n")
						.append("out vec4 outColor;\n")
						.append("\n")
						.append("void main()\n")
						.append("{\n")
						.append("    outColor = varyingColor;\n")
						.append("}\n")
					.toString())
					.attributeLocation("position", VERTEX)
					.attributeLocation("color", COLOR)
					.fragmentDataLocation("outColor", 0)
				.create();
				
				// Geometry Buffer
				GeometryBuilder gbuilder = gl.createGeometryBuilder(4, 3, 4) 
					.add(VERTEX,   -0.5f,  0.5f, 0.0f)
					.add(COLOR,     1,     0,    0,   1)
//					.add(TEXCOORD,  0,     0)
	
					.add(VERTEX,   -0.5f, -0.5f, 0.0f)
					.add(COLOR,     0,     1,    0,   1)
//					.add(TEXCOORD,  0,     1)
					
					.add(VERTEX,    0.5f,  0.5f, 0.0f)
					.add(COLOR,     0,     0,    1,   1)
//					.add(TEXCOORD,  1,     0)

					.add(VERTEX,    0.5f, -0.5f, 0.0f)
					.add(COLOR,     1,     1,    1,   1)
//					.add(TEXCOORD,  1,     1)
				;
				
				geometry = gbuilder.create();
				vstate = gl.createVertexArrayState(geometry, gbuilder);

				gl.setClearColor(0, 0, 0, 1);
				gl.setClearDepth(-1);
				
				once = true;
			}
			
			gl.clear(true, true, false, false);
			gl.setColorMask(true);
			gl.setDepthTestEnabled(false);
			
			gl.matrixMode(MatrixMode.TEXTURE);
			gl.matrixReset();
			gl.matrixMode(MatrixMode.PROJECTION);
			gl.matrixReset();
			gl.matrixOrtho(-2.0f, 2.0f, -1.5f, 1.5f, -1.0f, 1.0f);
			gl.matrixMode(MatrixMode.MODELVIEW);
			gl.matrixReset();
			
			gl.setProgram(program);
			gl.setVertexArrayState(vstate);
			gl.drawGeometryArray(GeometryType.TRIANGLE_STRIP, 0, 4);
			gl.unsetVertexArrayState();
			gl.unsetProgram();
		}
	}
	
	public static void main(String[] args)
	{
		(new OGLTest()).run();
	}
	
}
