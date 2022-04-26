/*******************************************************************************
 * Copyright (c) 2021-2022 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at 
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gloop.opengl;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL;

import com.blackrook.gloop.glfw.GLFWContext;
import com.blackrook.gloop.glfw.GLFWWindow;
import com.blackrook.gloop.glfw.GLFWWindow.State;
import com.blackrook.gloop.glfw.GLFWWindow.WindowAdapter;
import com.blackrook.gloop.opengl.exception.GraphicsException;
import com.blackrook.gloop.opengl.node.OGLNode;

/**
 * The starting point: the OpenGL subsystem.
 * <p> An implementation of OpenGL is initialized here, attached to a {@link GLFWWindow}
 * and spawns a thread that makes it responsible for the rendering the context 
 * (see {@link GLFWContext#makeContextCurrent(GLFWWindow)}). The rendering thread 
 * can either fire refreshes at a steady rate or listen for a trigger via {@link #display()}
 * to kick off a frame draw. 
 * @param <G> the graphics object to call.
 * @author Matthew Tropiano
 */
public class OGLSystem<G extends OGLGraphics>
{
	/** The window attached to the rendering thread. */
	private GLFWWindow window;
	/** The rendering thread. */
	private RenderingThread renderingThread;

	/** OpenGL graphics context. */
	private G graphics;
	/** All scene nodes. */
	private List<OGLNode<? super G>> nodes;
	
	/** Nano time of the previous frame rendered. */
	private long previousFrameNanos;
	/** Total time it took to render each individual node. */
	private long renderTimeNanos;
	/** Frame render time nanos. */
	private long frameRenderTimeNanos;
	/** Polygon count. */
	private int polygonCount;

	/** Whether or not to ignore window refresh events. */
	private volatile boolean ignoreRefresh;
	/** Is this redrawing? */
	private volatile boolean redrawing;
	
	// Creates the system.
	OGLSystem(G graphics, GLFWWindow window)
	{
		this.window = window;
		this.renderingThread = new RenderingThread();
		this.graphics = graphics;
		this.nodes = new ArrayList<>();
		
		this.previousFrameNanos = -1L;
		this.renderTimeNanos = -1L;
		this.frameRenderTimeNanos = -1L;
		this.polygonCount = 0;
		
		this.window.addWindowListener(new WindowAdapter()
		{
			@Override
			public void onFramebufferChange(GLFWWindow window, int width, int height)
			{
				resize(width, height);
			}
			
			@Override
			public void onRefresh(GLFWWindow window)
			{
				if (!ignoreRefresh)
					display();
			}
		});
		this.renderingThread.start();
	}

	/**
	 * Tells all attached nodes to resize themselves.
	 * Called from the window listener.
	 * @param width the new framebuffer width.
	 * @param height the new framebuffer height.
	 */
	private void resize(int width, int height)
	{
		for (OGLNode<?> node : nodes)
			node.onFramebufferResize(width, height);
	}

	/**
	 * Refreshes the display by displaying all of the added nodes.
	 * <p><b>Should ONLY be called by the thread attached to the OpenGL context.</b>
	 */
	private void redraw()
	{
		redrawing = true;
		try
		{
			long rendertime = 0L;
			int polys = 0;
		
			State state = window.getState();
			
			graphics.startFrame(state.getWidth(), state.getHeight());
			
			for (int i = 0; i < nodes.size(); i++)
			{
				OGLNode<? super G> node = nodes.get(i);
				node.onDisplay(graphics);
				rendertime += node.getRenderTimeNanos();
				polys += node.getPolygonsRendered();
			}
			
			frameRenderTimeNanos = System.nanoTime() - previousFrameNanos;
			previousFrameNanos = System.nanoTime();
		
			renderTimeNanos = rendertime;
			polygonCount = polys;
			
			graphics.endFrame();
			if (window.isCreated())
				window.swapBuffers();
		} 
		finally 
		{
			// Even if an exception occurs, set this back to false.
			redrawing = false;
		}
	}

	/**
	 * Triggers a display refresh, telling the rendering thread to draw a frame.
	 * If a frame is currently being drawn, this will return false, indicating a dropped frame.
	 * Otherwise, this returns true.
	 * @return true if redrawing, false if not.
	 */
	public boolean display()
	{
		if (redrawing)
			return false;
		renderingThread.trigger();
		return true;
	}

	/**
	 * Adds a node to this system.
	 * @param node the node to add.
	 */
	public void addNode(OGLNode<? super G> node)
	{
		nodes.add(node);
	}

	/**
	 * Removes a node from this system.
	 * @param node the node to remove.
	 * @return true if removed, false if not (wasn't added).
	 */
	public boolean removeNode(OGLNode<? super G> node)
	{
		return nodes.remove(node);
	}

	/**
	 * Returns the length of time it took to render this frame.
	 * This is NOT the same as {@link #getRenderTimeNanos()}, as it takes
	 * time between frames (real time) into consideration. The method
	 * {@link #getFPS()} uses this information.
	 * <p>Results of this call should not be considered accurate until the node 
	 * has had {@link #display()} called on it twice.
	 * @return the length of time in nanoseconds.
	 */
	public long getFrameRenderTimeNanos()
	{
		return frameRenderTimeNanos;
	}

	/**
	 * The results of this call should not be considered accurate until the node 
	 * has had {@link #display()} called on it.
	 * @return the length of time it took to render each individual node
	 * in nanoseconds, accumulated from the displayed nodes.
	 */
	public long getRenderTimeNanos()
	{
		return renderTimeNanos;
	}

	/**
	 * @return the number of polygonal objects rendered in this canvas, 
	 * gathered from the visible nodes.
	 */
	public int getPolygonsRendered()
	{
		return polygonCount;
	}

	/**
	 * @return the estimated frames per second in this context based on the time to render the nodes.
	 */
	public float getFPS()
	{
		double n = (frameRenderTimeNanos / 1000000d);
		return n > 0.0 ? (float)(1000 / n) : 0f;
	}

	/**
	 * Sets the maximum amount of times per second that the rendering thread will 
	 * attempt to automatically redraw the contents of the window.
	 * Depending on the heft of what is being drawn, this maximum may not be reached.
	 * <p> If set to null, no redraws occur unless triggered by the application or the windowing system. 
	 * <p> If set to a number that is 0 or less, this will keep redrawing continuously. 
	 * If greater than 0, it will trigger redraws that many times per second. 
	 * Events that want to redraw the window passively are ignored in both circumstances. 
	 * <p> By default, this is set to null. 
	 * <p> NOTE: If an exception occurs during the rendering thread's execution, continual redraw 
	 * is halted via <code>setFPS(null)</code> until it is started again.
	 * @param fps the new FPS value. Can be null. 
	 */
	public void setFPS(int fps)
	{
		setFPS(Long.valueOf(fps));
	}
	
	/**
	 * Sets the maximum amount of times per second that the rendering thread will 
	 * attempt to automatically redraw the contents of the window.
	 * Depending on the heft of what is being drawn, this maximum may not be reached.
	 * <p> If set to null, no redraws occur unless triggered by the application or the windowing system. 
	 * <p> If set to a number that is 0 or less, this will keep redrawing continuously. 
	 * If greater than 0, it will trigger redraws that many times per second. 
	 * Events that want to redraw the window passively are ignored in both circumstances. 
	 * <p> By default, this is set to null.
	 * <p> NOTE: If an exception occurs during the rendering thread's execution, continual redraw 
	 * is halted via <code>setFPS(null)</code> until it is started again.
	 * @param fps the new FPS value. Can be null. 
	 */
	public void setFPS(Long fps)
	{
		if (fps == null)
		{
			renderingThread.waitMillis = null;
			renderingThread.waitNanos = null;
			ignoreRefresh = false;
		}
		else if (fps <= 0)
		{
			renderingThread.waitMillis = 0L;
			renderingThread.waitNanos = 0;
			ignoreRefresh = true;
			display();
		}
		else
		{
			long npf = 1000000000L / fps;
			renderingThread.waitMillis = npf / 1000000L;
			renderingThread.waitNanos = (int)(npf % 1000000L);
			ignoreRefresh = true;
			display();
		}
	}
	
	/**
	 * An options class for changing runtime behavior for a created system.
	 */
	public interface Options
	{
		/**
		 * Gets whether or not the error checking functions used to detect
		 * OpenGL runtime errors should do anything on call. Turning this off
		 * stops all {@link OGLGraphics#checkError()} calls from going through to OpenGL, saving calls.
		 * <p> It would be unwise to turn this off while developing.
		 * @return true to perform error checking, false to not.
		 */
		boolean performErrorChecking();
		
		/**
		 * Gets whether or not the version checking functions used to detect
		 * mismatched features should do anything on call. Turning this off
		 * turns all internal version checking and verification off.
		 * <p> It would be unwise to turn this off while developing.
		 * @return true to perform feature version checking, false to not.
		 */
		boolean performVersionChecking();
	}
	
	// The rendering thread.
	private class RenderingThread extends Thread
	{
		private Object renderLatch;
		private Long waitMillis;
		private Integer waitNanos;
		
		private RenderingThread()
		{
			super("Gloop-OGL-RenderingThread");
			setDaemon(true);
			this.renderLatch = new Object();
			this.waitMillis = null;
			this.waitNanos = null;
		}
		
		// Triggers another render.
		private void trigger()
		{
			synchronized(renderLatch)
			{
				renderLatch.notify();
			}
		}
		
		@Override
		public void run()
		{
			GLFWContext.makeContextCurrent(window);
			GL.createCapabilities();
			while (true)
			{
				synchronized (renderLatch)
				{
					try {
						if (waitMillis == null || waitNanos == null)
							renderLatch.wait();
						else if (waitMillis != 0 || waitNanos != 0)
							renderLatch.wait(waitMillis, waitNanos);
						redraw();
					} catch (Throwable e) {
						setFPS(null);
						throw new GraphicsException("Graphics thread halted due to exception!", e);
					}
				}
			}
		}
	}

}
