package com.blackrook.gloop.opengl;

import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.nio.IntBuffer;
import java.util.Objects;

import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWNativeWin32;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLCapabilities;
import org.lwjgl.opengl.GLX;
import org.lwjgl.opengl.GLX13;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.Platform;
import org.lwjgl.system.jawt.JAWT;
import org.lwjgl.system.jawt.JAWTDrawingSurface;
import org.lwjgl.system.jawt.JAWTDrawingSurfaceInfo;
import org.lwjgl.system.jawt.JAWTWin32DrawingSurfaceInfo;
import org.lwjgl.system.jawt.JAWTX11DrawingSurfaceInfo;
import org.lwjgl.system.linux.X11;
import org.lwjgl.system.linux.XVisualInfo;

import com.blackrook.gloop.glfw.GLFWWindowHints;

import static org.lwjgl.system.jawt.JAWTFunctions.JAWT_DrawingSurface_FreeDrawingSurfaceInfo;
import static org.lwjgl.system.jawt.JAWTFunctions.JAWT_DrawingSurface_GetDrawingSurfaceInfo;
import static org.lwjgl.system.jawt.JAWTFunctions.JAWT_DrawingSurface_Lock;
import static org.lwjgl.system.jawt.JAWTFunctions.JAWT_DrawingSurface_Unlock;
import static org.lwjgl.system.jawt.JAWTFunctions.JAWT_FreeDrawingSurface;
import static org.lwjgl.system.jawt.JAWTFunctions.JAWT_GetAWT;
import static org.lwjgl.system.jawt.JAWTFunctions.JAWT_GetDrawingSurface;
import static org.lwjgl.system.jawt.JAWTFunctions.JAWT_LOCK_ERROR;
import static org.lwjgl.system.jawt.JAWTFunctions.JAWT_VERSION_1_4;

/**
 * A common OpenGL Canvas.
 * @author Matthew Tropiano
 * @param <OGL> the OGLGraphics type.
 */
public abstract class OGLCanvas<OGL extends OGLGraphics> extends Canvas 
{
	private static final long serialVersionUID = -5154698808503798624L;
	
	/**
	 * Creates a new canvas suitable for rendering to.
	 * @param <OGL> the {@link OGLGraphics} type that matches the context type in the provided {@link GLFWWindowHints}.
	 * @param hints the hints for this context's creation.
	 * @param system the rendering system to use for rendering content.
	 * @return a canvas for rendering.
	 */
	public static final <OGL extends OGLGraphics> OGLCanvas<OGL> createCanvas(GLFWWindowHints hints, OGLSystem<OGL> system)
	{
		switch (Platform.get())
		{
			default:
				throw new UnsupportedOperationException(Platform.get().name() + " is not supported yet!");
			case LINUX:
				return new LinuxOGLCanvas<>(hints, system);
			case WINDOWS:
				return new WindowsOGLCanvas<>(hints, system);
		}
	}
	
	private OGLSystem<OGL> system;

    private final JAWT awt;
    private JAWTDrawingSurface drawingSurface;
    
    protected final GLFWWindowHints hints;
    
    protected GLCapabilities caps;
    protected long context;
    
    /**
     * Creates a new WindowsOpenGLCanvas.
     * @param hints the window hints used to create a GL context.
     * @param system the root OGLSystem to use for rendering.
     */
    protected OGLCanvas(GLFWWindowHints hints, OGLSystem<OGL> system)
    {
    	this.hints = hints;
    	this.system = system;
        
    	this.awt = JAWT.calloc();
        this.awt.version(JAWT_VERSION_1_4);
        if (!JAWT_GetAWT(awt)) 
            throw new IllegalStateException("GetAWT failed");

        addComponentListener(new ComponentAdapter()
        {
            @Override 
            public void componentResized(ComponentEvent e)
            {
                if (context != MemoryUtil.NULL) 
                    jawtRender();
            }
        });
    }
    
    /**
     * @return the {@link GLFWWindowHints} used to make this canvas.
     */
    public GLFWWindowHints getHints()
    {
		return hints;
	}

    @Override
    public void update(Graphics g)
    {
    	// Skip AWT buffer clear.
        paint(g);
    }

    @Override
    public void paint(Graphics g)
    {
        jawtRender();
    }

    private void jawtRender()
    {
        if (drawingSurface == null)
        {
            // Get the drawing surface
            drawingSurface = JAWT_GetDrawingSurface(this, awt.GetDrawingSurface());
            if (drawingSurface == null)
                throw new IllegalStateException("awt->GetDrawingSurface() failed");
        }

        int lock = JAWT_DrawingSurface_Lock(drawingSurface, drawingSurface.Lock());
        if ((lock & JAWT_LOCK_ERROR) != 0)
            throw new IllegalStateException("ds->Lock() failed");

        try {
            JAWTDrawingSurfaceInfo dsi = JAWT_DrawingSurface_GetDrawingSurfaceInfo(drawingSurface, drawingSurface.GetDrawingSurfaceInfo());
            if (dsi == null)
                throw new IllegalStateException("ds->GetDrawingSurfaceInfo() failed");

            try {
            	doPlatformSpecificRender(dsi);
            } finally {
                JAWT_DrawingSurface_FreeDrawingSurfaceInfo(dsi, drawingSurface.FreeDrawingSurfaceInfo());
            }
        } finally {
            JAWT_DrawingSurface_Unlock(drawingSurface, drawingSurface.Unlock());
        }
    }

    /**
     * Called when the rendering surface is secured so that rendering can be done to via a rendering system.
     * @param width the width of the framebuffer.
     * @param height the height of the framebuffer.
     */
    protected void renderSystem(int width, int height)
    {
	    system.renderFrame(width, height);
    }
    
    /**
     * Performs the tasks necessary to secure a rendering surface for OpenGL calls.
     * @param dsi the AWT surface info to use.
     */
    protected abstract void doPlatformSpecificRender(JAWTDrawingSurfaceInfo dsi);
    
	/**
     * Destroys this canvas.
     */
    public void destroy()
    {
        JAWT_FreeDrawingSurface(drawingSurface, awt.FreeDrawingSurface());
        awt.free();
        if (context != MemoryUtil.NULL)
            GLFW.glfwDestroyWindow(context);
    }

    @Override
    protected void finalize() throws Throwable 
    {
    	destroy();
    	super.finalize();
    }

    /**
     * Windows implementation.
     * @param <OGL> the OGLGraphics type.
     */
    private static class WindowsOGLCanvas<OGL extends OGLGraphics> extends OGLCanvas<OGL>
    {
		private static final long serialVersionUID = -7082706492428229355L;

		private WindowsOGLCanvas(GLFWWindowHints hints, OGLSystem<OGL> system)
    	{
			super(hints, system);
		}

		@Override
		protected void doPlatformSpecificRender(JAWTDrawingSurfaceInfo dsi)
    	{
    		JAWTWin32DrawingSurfaceInfo dsiWin = JAWTWin32DrawingSurfaceInfo.create(dsi.platformInfo());

    		long hdc = dsiWin.hdc();
    		if (hdc == MemoryUtil.NULL)
    		    return;

    		if (context == MemoryUtil.NULL)
    		{
    			GLFW.glfwInit();
    			hints.callHints();
    			
    			context = GLFWNativeWin32.glfwAttachWin32Window(dsiWin.hwnd(), MemoryUtil.NULL);
    			if (context == MemoryUtil.NULL)
    			    throw new IllegalStateException("Failed to attach win32 window.");

    			GLFW.glfwMakeContextCurrent(context);
    		    caps = GL.createCapabilities();
    		} 
    		else 
    		{
    			GLFW.glfwMakeContextCurrent(context);
    		    GL.setCapabilities(caps);
    		}

    		try (MemoryStack stack = MemoryStack.stackPush())
    		{
    		    IntBuffer pw = stack.mallocInt(1);
    		    IntBuffer ph = stack.mallocInt(1);
    		    GLFW.glfwGetFramebufferSize(context, pw, ph);
    		    renderSystem(pw.get(0), ph.get(0));
    		}
    		
    		GLFW.glfwSwapBuffers(context);
    		
    		GLFW.glfwMakeContextCurrent(MemoryUtil.NULL);
    		GL.setCapabilities(null);
    	}
    }
    
    /**
     * Linux implementation.
     * @param <OGL> the OGLGraphics type.
     */
	private static class LinuxOGLCanvas<OGL extends OGLGraphics> extends OGLCanvas<OGL>
    {
		private static final long serialVersionUID = -2071880816459982897L;

		private LinuxOGLCanvas(GLFWWindowHints hints, OGLSystem<OGL> system)
    	{
			super(hints, system);
		}

		@Override
		protected void doPlatformSpecificRender(JAWTDrawingSurfaceInfo dsi)
    	{
            JAWTX11DrawingSurfaceInfo dsiX11 = JAWTX11DrawingSurfaceInfo.create(dsi.platformInfo());

            long drawable = dsiX11.drawable();
            if (drawable == MemoryUtil.NULL)
                return;

            if (context == MemoryUtil.NULL)
            {
    	        long display = dsiX11.display();

    	        PointerBuffer configs = Objects.requireNonNull(GLX13.glXGetFBConfigs(display, 0));

    	        long config = MemoryUtil.NULL;
    	        for (int i = 0; i < configs.remaining(); i++)
    	        {
    	            XVisualInfo vi = GLX13.glXGetVisualFromFBConfig(display, configs.get(i));
    	            if (vi == null)
    	            {
    	                continue;
    	            }
    	            try {
    	                if (vi.visualid() == dsiX11.visualID())
    	                {
    	                    config = configs.get(i);
    	                    break;
    	                }
    	            } finally {
    	            	X11.nXFree(vi.address());
    	            }
    	        }
    	        X11.XFree(configs);

    	        if (config == MemoryUtil.NULL)
    	            throw new IllegalStateException("Failed to find a compatible GLXFBConfig");

    	        context = GLX13.glXCreateNewContext(display, config, GLX13.GLX_RGBA_TYPE, MemoryUtil.NULL, true);
    	        if (context == MemoryUtil.NULL) {
    	            throw new IllegalStateException("glXCreateContext() failed");
    	        }

    	        if (!GLX.glXMakeCurrent(display, drawable, context))
    	            throw new IllegalStateException("glXMakeCurrent() failed");

    	        caps = GL.createCapabilities();
            } 
            else 
            {
                if (!GLX.glXMakeCurrent(dsiX11.display(), drawable, context))
                    throw new IllegalStateException("glXMakeCurrent() failed");
                
                GL.setCapabilities(caps);
            }

            renderSystem(getWidth(), getHeight());
            GLX.glXSwapBuffers(dsiX11.display(), drawable);

            GLX.glXMakeCurrent(dsiX11.display(), MemoryUtil.NULL, MemoryUtil.NULL);
            GL.setCapabilities(null);
    	}
    }
    
}
