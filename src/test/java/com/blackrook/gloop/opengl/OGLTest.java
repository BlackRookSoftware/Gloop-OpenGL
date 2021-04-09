/*******************************************************************************
 * Copyright (c) 2021 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at 
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/

package com.blackrook.gloop.opengl;

import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

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
import com.blackrook.gloop.opengl.enums.TextureMagFilter;
import com.blackrook.gloop.opengl.enums.TextureMinFilter;
import com.blackrook.gloop.opengl.enums.TextureTargetType;
import com.blackrook.gloop.opengl.exception.GraphicsException;
import com.blackrook.gloop.opengl.gl1.OGLBuffer;
import com.blackrook.gloop.opengl.gl1.OGLTexture;
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
		oglSystem.setFPS(0);
		
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
		private OGLProgram program;
		private OGLBuffer geometry;
		private OGLTexture texture;
		private OGLVertexArrayState vstate;
		private boolean once;
		
		private boolean viewportChange;
		private int viewportWidth;
		private int viewportHeight;
		
		public DrawNode()
		{
			program = null;
			geometry = null;
			texture = null;
			vstate = null;
			once = false;
			
			viewportChange = false;
			viewportWidth = 640;
			viewportHeight = 480;
		}
		
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
			if (!once)
			{
				final int VERTEX = 0;
				final int COLOR = 1;
				final int TEXCOORD = 2;

				program = gl.createProgramBuilder()
					.setShader(ShaderType.VERTEX, (new StringBuilder())
						.append("#version 330\n")
						.append("in vec3 position;\n")
						.append("in vec4 color;\n")
						.append("in vec2 texcoord;\n")
						.append("\n")
						.append("uniform mat4 modelview;\n")
						.append("uniform mat4 projection;\n")
						.append("uniform mat4 textureTransform;\n")
						.append("\n")
						.append("out vec4 varyingColor;\n")
						.append("out vec2 varyingTexcoord;\n")
						.append("\n")
						.append("void main()\n")
						.append("{\n")
						.append("    varyingColor = color;\n")
						.append("    varyingTexcoord = (textureTransform * vec4(texcoord.st, 1.0, 1.0)).st;\n")
						.append("    gl_Position = projection * modelview * vec4(position.xyz, 1.0);\n") // order matters!
						.append("}\n")
					.toString())
					.setShader(ShaderType.FRAGMENT, (new StringBuilder())
						.append("#version 330\n")
						.append("in vec4 varyingColor;\n")
						.append("in vec2 varyingTexcoord;\n")
						.append("\n")
						.append("uniform sampler2D texture0;")
						.append("\n")
						.append("out vec4 outColor;\n")
						.append("\n")
						.append("void main()\n")
						.append("{\n")
						.append("    outColor = varyingColor * texture(texture0, varyingTexcoord);\n")
						.append("}\n")
					.toString())
					.attributeLocation("position", VERTEX)
					.attributeLocation("color", COLOR)
					.attributeLocation("texcoord", TEXCOORD)
					.fragmentDataLocation("outColor", 0)
					.setListener((type, log) -> {
						System.out.println(type.name() + " Shader Log:\n" + log);
					})
				.create();
				
				try (InputStream in = openResource("example/textures/earth.png"))
				{
					texture = gl.createTextureBuilder()
						.setFiltering(TextureMinFilter.NEAREST, TextureMagFilter.NEAREST)
						.setTargetType(TextureTargetType.TEXTURE_2D)
						.addTextureImage(ImageIO.read(in))
						.create()
					;
				} 
				catch (IOException e) 
				{
					throw new GraphicsException("Could not read texture file.", e);
				}
				
				// Geometry Buffer
				GeometryBuilder gbuilder = gl.createGeometryBuilder(4, 3, 4, 2) 
					.add(VERTEX,   -0.5f,  0.5f, 0.0f)
					.add(COLOR,     1,     0,    0,   1)
					.add(TEXCOORD,  0,     0)
					.add(VERTEX,   -0.5f, -0.5f, 0.0f)
					.add(COLOR,     0,     1,    0,   1)
					.add(TEXCOORD,  0,     1)
					.add(VERTEX,    0.5f,  0.5f, 0.0f)
					.add(COLOR,     0,     0,    1,   1)
					.add(TEXCOORD,  1,     0)
					.add(VERTEX,    0.5f, -0.5f, 0.0f)
					.add(COLOR,     1,     1,    1,   1)
					.add(TEXCOORD,  1,     1)
				;
				
				geometry = gbuilder.create();
				vstate = gl.createVertexArrayState(geometry, gbuilder);

				gl.setClearColor(0, 0, 0, 1);
				gl.setClearDepth(-1);
				
				once = true;
			}
			
			if (viewportChange)
			{
				gl.setViewport(0, 0, viewportWidth, viewportHeight);
				viewportChange = false;
			}
			
			gl.clear(true, true, false, false);
			gl.setColorMask(true);
			gl.setDepthTestEnabled(false);
			
			gl.matrixMode(MatrixMode.TEXTURE);
			gl.matrixReset();
			gl.matrixScale(2f, 2f, 2f);
			gl.matrixMode(MatrixMode.PROJECTION);
			gl.matrixReset();
			gl.matrixOrtho(-2.0f, 2.0f, -1.5f, 1.5f, -1.0f, 1.0f);
			gl.matrixMode(MatrixMode.MODELVIEW);
			gl.matrixReset();
			gl.matrixRotateZ(gl.currentFrame() % 360f);
			
			gl.setProgram(program);
			gl.setProgramUniformMatrix4(program.getUniform("modelview").getIndex(), MatrixMode.MODELVIEW);
			gl.setProgramUniformMatrix4(program.getUniform("projection").getIndex(), MatrixMode.PROJECTION);
			gl.setProgramUniformMatrix4(program.getUniform("textureTransform").getIndex(), MatrixMode.TEXTURE);
			gl.setProgramUniformInt(program.getUniform("texture0").getIndex(), 0);

			gl.setTextureEnabled(TextureTargetType.TEXTURE_2D, true);
			gl.setTextureUnit(0);
			gl.setTexture(TextureTargetType.TEXTURE_2D, texture);
			
			gl.setVertexArrayState(vstate);
			gl.drawGeometryArray(GeometryType.TRIANGLE_STRIP, 0, 4);
			
			gl.unsetTexture(TextureTargetType.TEXTURE_2D);
			gl.setTextureEnabled(TextureTargetType.TEXTURE_2D, false);

			gl.unsetVertexArrayState();
			gl.unsetProgram();
		}
	}
	
	public static void main(String[] args)
	{
		(new OGLTest()).run();
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
