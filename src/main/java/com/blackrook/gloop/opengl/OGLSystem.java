package com.blackrook.gloop.opengl;

import java.util.ArrayList;
import java.util.List;

import com.blackrook.gloop.opengl.exception.GraphicsException;
import com.blackrook.gloop.opengl.gl1.OGL15Graphics;
import com.blackrook.gloop.opengl.gl2.OGL21Graphics;
import com.blackrook.gloop.opengl.node.OGLNode;

/**
 * The starting point: the OpenGL subsystem. 
 * @param <GL> the graphics object to call.
 * @author Matthew Tropiano
 */
public class OGLSystem<GL extends OGLGraphicsAbstract>
{
	/** OpenGL graphics context. */
	private GL graphics;
	/** All scene nodes. */
	private List<OGLNode<? super GL>> nodes;
	
	/** Nano time of the previous frame rendered. */
	private long previousFrameNanos;
	/** Total time it took to render each individual node. */
	private long renderTimeNanos;
	/** Frame render time nanos. */
	private long frameRenderTimeNanos;
	/** Polygon count. */
	private int polygonCount;

	/** Frame buffer width. */
	private int framebufferWidth;
	/** Frame buffer height. */
	private int framebufferHeight;
	
	/**
	 * Creates an OpenGL 1.5 implementation system.
	 * @return an OpenGL context entry.
	 * @throws GraphicsException if the given implementation could not be created. 
	 */
	public static OGLSystem<OGL15Graphics> getOpenGL15()
	{
		return new OGLSystem<OGL15Graphics>(new OGL15Graphics());
	}
	
	/**
	 * Creates an OpenGL 2.1 implementation system.
	 * @return an OpenGL context entry.
	 * @throws GraphicsException if the given implementation could not be created. 
	 */
	public static OGLSystem<OGL21Graphics> getOpenGL21()
	{
		return new OGLSystem<OGL21Graphics>(new OGL21Graphics());
	}
	
	// Creates the system.
	private OGLSystem(GL graphics)
	{
		this.graphics = graphics;
		this.nodes = new ArrayList<>();

		this.previousFrameNanos = -1L;
		this.frameRenderTimeNanos = -1L;
		this.polygonCount = 0;
	}

	/**
	 * Refreshes the display by displaying all of the added nodes.
	 * <p><b>Should be called by the thread attached to the OpenGL context.</b>
	 */
	public void display()
	{
		long rendertime = 0L;
		int polys = 0;
	
		graphics.beginFrame();
		
	    for (int i = 0; i < nodes.size(); i++)
	    {
	    	OGLNode<? super GL> node = nodes.get(i);
    		node.onDisplay(graphics);
    		rendertime += node.getRenderTimeNanos();
    		polys += node.getPolygonsRendered();
	    }
	    
	    frameRenderTimeNanos = System.nanoTime() - previousFrameNanos;
	    previousFrameNanos = System.nanoTime();
	
	    renderTimeNanos = rendertime;
	    polygonCount = polys;
	    
	    graphics.endFrame();
	}

	/**
	 * Tells all attached nodes to resize themselves.
	 * @param width the new framebuffer width.
	 * @param height the new framebuffer height.
	 */
	public void resize(int width, int height)
	{
		framebufferWidth = width;
		framebufferHeight = height;
	    for (OGLNode<?> node : nodes)
	    	node.onFramebufferResize(width, height);
	}

	/**
	 * Adds a node to this system.
	 * @param node the node to add.
	 */
	public void addNode(OGLNode<? super GL> node)
	{
		nodes.add(node);
		node.onFramebufferResize(framebufferWidth, framebufferHeight);
	}

	/**
	 * Removes a node from this system.
	 * @param node the node to remove.
	 * @return true if removed, false if not (wasn't added).
	 */
	public boolean removeNode(OGLNode<? super GL> node)
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
	 * @return the estimated frames per second in this context
	 * based on the time to render the visible nodes.
	 */
	public float getFPS()
	{
		double n = (frameRenderTimeNanos / 1000000d);
		return n > 0.0 ? (float)(1000 / n) : 0f;
	}

}
