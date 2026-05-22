package com.blackrook.gloop.opengl;

import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferStrategy;
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
 * @param <G> the OGLGraphics type.
 */
public class OGLCanvas<G extends OGLGraphics> extends Canvas 
{
	private static final long serialVersionUID = -5154698808503798624L;
	
	private OGLSystem<G> system;

    private final JAWT awt;
    private JAWTDrawingSurface drawingSurface;
    
    protected final GLFWWindowHints hints;
    
    protected GLCapabilities caps;
    protected long context;
    protected BufferStrategy bufferStrategy;
    
    private static void verifySupportedPlatform(Platform platform)
    {
		switch (Platform.get())
		{
			default:
				throw new UnsupportedOperationException(Platform.get().name() + " is not supported yet!");
			case LINUX:
			case WINDOWS:
				break;
		}
    }
    
	/**
	 * Creates a new canvas suitable for rendering to.
	 * @param hints the hints for this context's creation. Note that some hints for creating the window itself will have no effect, here.
	 * @param system the rendering system to use for rendering content.
	 * @throws UnsupportedOperationException if this canvas can't be created for this platform.
	 */
    public OGLCanvas(GLFWWindowHints hints, OGLSystem<G> system)
    {
    	verifySupportedPlatform(Platform.get());
		if (!GLFW.glfwInit())
			throw new IllegalStateException("Could not initialize GLFW!");

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

            @Override
            public void componentShown(ComponentEvent e)
            {
                if (context != MemoryUtil.NULL) 
                    jawtRender();
            }
        });
    }
    
    /**
     * @return the {@link GLFWWindowHints} used to make this canvas.
     */
    public final GLFWWindowHints getHints()
    {
		return hints;
	}

    @Override
    public final void update(Graphics g)
    {
    	// Skip AWT buffer clear.
        paint(g);
    }

    @Override
    public final void paint(Graphics g)
    {
		jawtRender();
    }

    /**
     * Gets a reference to this canvas's underlying system.
     * @return the system reference.
     */
    public OGLSystem<G> getSystem() 
    {
		return system;
	}
    
    /**
     * Performs the system draw using a JAWT surface.
     */
    protected final void jawtRender()
    {
        if (drawingSurface == null)
        {
            drawingSurface = JAWT_GetDrawingSurface(this, awt.GetDrawingSurface());
            if (drawingSurface == null)
                throw new IllegalStateException("awt.GetDrawingSurface() failed");
        }

        int lock = JAWT_DrawingSurface_Lock(drawingSurface, drawingSurface.Lock());
        if ((lock & JAWT_LOCK_ERROR) != 0)
            throw new IllegalStateException("ds.Lock() failed");

        try {
            JAWTDrawingSurfaceInfo dsi = JAWT_DrawingSurface_GetDrawingSurfaceInfo(drawingSurface, drawingSurface.GetDrawingSurfaceInfo());
            if (dsi == null)
                throw new IllegalStateException("ds.GetDrawingSurfaceInfo() failed");

            try {
        		switch (Platform.get())
        		{
        			case LINUX:
        				doLinuxRender(dsi);
        				break;
        			case WINDOWS:
        				doWindowsRender(dsi);
        				break;
					default:
						throw new UnsupportedOperationException(Platform.get().name() + " is not supported yet!");
        		}
            } finally {
                JAWT_DrawingSurface_FreeDrawingSurfaceInfo(dsi, drawingSurface.FreeDrawingSurfaceInfo());
            }
        } finally {
            JAWT_DrawingSurface_Unlock(drawingSurface, drawingSurface.Unlock());
        }
    }
    
    private void doWindowsRender(JAWTDrawingSurfaceInfo dsi)
    {
		JAWTWin32DrawingSurfaceInfo dsiWin = JAWTWin32DrawingSurfaceInfo.create(dsi.platformInfo());

		long hdc = dsiWin.hdc();
		if (hdc == MemoryUtil.NULL)
		    return;

		if (context == MemoryUtil.NULL)
		{
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

    private void doLinuxRender(JAWTDrawingSurfaceInfo dsi)
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

    /**
     * Called when the rendering surface is secured so that rendering can be done to via a rendering system.
     * Called by {@link #jawtRender()}.
     * @param width the width of the framebuffer.
     * @param height the height of the framebuffer.
     */
    protected void renderSystem(int width, int height)
    {
	    system.renderFrame(width, height);
    }
    
	/**
     * Destroys this canvas.
     */
    public void dispose()
    {
        JAWT_FreeDrawingSurface(drawingSurface, awt.FreeDrawingSurface());
        awt.free();
        if (context != MemoryUtil.NULL)
            GLFW.glfwDestroyWindow(context);
    }

    @Override
    protected void finalize() throws Throwable 
    {
    	dispose();
    	super.finalize();
    }

}
