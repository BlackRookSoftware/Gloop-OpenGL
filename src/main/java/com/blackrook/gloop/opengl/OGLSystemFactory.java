package com.blackrook.gloop.opengl;

import com.blackrook.gloop.glfw.GLFWWindow;
import com.blackrook.gloop.opengl.exception.GraphicsException;
import com.blackrook.gloop.opengl.gl1.OGL11Graphics;
import com.blackrook.gloop.opengl.gl1.OGL12Graphics;
import com.blackrook.gloop.opengl.gl1.OGL13Graphics;
import com.blackrook.gloop.opengl.gl1.OGL14Graphics;
import com.blackrook.gloop.opengl.gl1.OGL15Graphics;
import com.blackrook.gloop.opengl.gl2.OGL20Graphics;
import com.blackrook.gloop.opengl.gl2.OGL21Graphics;
import com.blackrook.gloop.opengl.gl3.OGL30Graphics;
import com.blackrook.gloop.opengl.gl3.OGL31Graphics;
import com.blackrook.gloop.opengl.gl3.OGL32Graphics;
import com.blackrook.gloop.opengl.gl3.OGL33Graphics;
import com.blackrook.gloop.opengl.gl4.OGL40Graphics;
import com.blackrook.gloop.opengl.gl4.OGL41Graphics;
import com.blackrook.gloop.opengl.gl4.OGL42Graphics;
import com.blackrook.gloop.opengl.gl4.OGL43Graphics;
import com.blackrook.gloop.opengl.gl4.OGL44Graphics;
import com.blackrook.gloop.opengl.gl4.OGL45Graphics;
import com.blackrook.gloop.opengl.gl4.OGL46Graphics;

/**
 * A static factory for building an OpenGL context.
 * @author Matthew Tropiano
 */
public final class OGLSystemFactory
{
	// Not instantiable.
	private OGLSystemFactory() {}
	
	/**
	 * Options that disable error checking.
	 */
	public static final OGLSystem.Options NO_ERROR_CHECKING = new OGLSystem.Options()
	{
		@Override
		public boolean performVersionChecking()
		{
			return false;
		}
		
		@Override
		public boolean performErrorChecking()
		{
			return false;
		}
	};
	
	/**
	 * Default options.
	 */
	public static final OGLSystem.Options DEFAULT_OPTIONS = new OGLSystem.Options()
	{
		@Override
		public boolean performVersionChecking()
		{
			return true;
		}
		
		@Override
		public boolean performErrorChecking()
		{
			return true;
		}
	};
	
	/**
	 * Creates an OpenGL 1.1 implementation system.
	 * @param window the window to render to.
	 * @return an OpenGL context entry.
	 * @throws GraphicsException if the given implementation could not be created. 
	 */
	public static OGLSystem<OGL11Graphics> getOpenGL11(GLFWWindow window)
	{
		return getOpenGL11(DEFAULT_OPTIONS, window);
	}
	
	/**
	 * Creates an OpenGL 1.2 implementation system.
	 * @param window the window to render to.
	 * @return an OpenGL context entry.
	 * @throws GraphicsException if the given implementation could not be created. 
	 */
	public static OGLSystem<OGL12Graphics> getOpenGL12(GLFWWindow window)
	{
		return getOpenGL12(DEFAULT_OPTIONS, window);
	}
	
	/**
	 * Creates an OpenGL 1.3 implementation system.
	 * @param window the window to render to.
	 * @return an OpenGL context entry.
	 * @throws GraphicsException if the given implementation could not be created. 
	 */
	public static OGLSystem<OGL13Graphics> getOpenGL13(GLFWWindow window)
	{
		return getOpenGL13(DEFAULT_OPTIONS, window);
	}
	
	/**
	 * Creates an OpenGL 1.4 implementation system.
	 * @param window the window to render to.
	 * @return an OpenGL context entry.
	 * @throws GraphicsException if the given implementation could not be created. 
	 */
	public static OGLSystem<OGL14Graphics> getOpenGL14(GLFWWindow window)
	{
		return getOpenGL14(DEFAULT_OPTIONS, window);
	}
	
	/**
	 * Creates an OpenGL 1.5 implementation system.
	 * @param window the window to render to.
	 * @return an OpenGL context entry.
	 * @throws GraphicsException if the given implementation could not be created. 
	 */
	public static OGLSystem<OGL15Graphics> getOpenGL15(GLFWWindow window)
	{
		return getOpenGL15(DEFAULT_OPTIONS, window);
	}
	
	/**
	 * Creates an OpenGL 2.0 implementation system.
	 * @param window the window to render to.
	 * @return an OpenGL context entry.
	 * @throws GraphicsException if the given implementation could not be created. 
	 */
	public static OGLSystem<OGL20Graphics> getOpenGL20(GLFWWindow window)
	{
		return getOpenGL20(DEFAULT_OPTIONS, window);
	}
	
	/**
	 * Creates an OpenGL 2.1 implementation system.
	 * @param window the window to render to.
	 * @return an OpenGL context entry.
	 * @throws GraphicsException if the given implementation could not be created. 
	 */
	public static OGLSystem<OGL21Graphics> getOpenGL21(GLFWWindow window)
	{
		return getOpenGL21(DEFAULT_OPTIONS, window);
	}
	
	/**
	 * Creates an OpenGL 3.0 implementation system.
	 * @param window the window to render to.
	 * @return an OpenGL context entry.
	 * @throws GraphicsException if the given implementation could not be created. 
	 */
	public static OGLSystem<OGL30Graphics> getOpenGL30(GLFWWindow window)
	{
		return getOpenGL30(DEFAULT_OPTIONS, window);
	}
	
	/**
	 * Creates an OpenGL 3.0 Core implementation system.
	 * @param window the window to render to.
	 * @return an OpenGL context entry.
	 * @throws GraphicsException if the given implementation could not be created. 
	 */
	public static OGLSystem<OGL30Graphics> getOpenGL30Core(GLFWWindow window)
	{
		return getOpenGL30Core(DEFAULT_OPTIONS, window);
	}
	
	/**
	 * Creates an OpenGL 3.1 implementation system.
	 * @param window the window to render to.
	 * @return an OpenGL context entry.
	 * @throws GraphicsException if the given implementation could not be created. 
	 */
	public static OGLSystem<OGL31Graphics> getOpenGL31(GLFWWindow window)
	{
		return getOpenGL31(DEFAULT_OPTIONS, window);
	}
	
	/**
	 * Creates an OpenGL 3.1 Core implementation system.
	 * @param window the window to render to.
	 * @return an OpenGL context entry.
	 * @throws GraphicsException if the given implementation could not be created. 
	 */
	public static OGLSystem<OGL31Graphics> getOpenGL31Core(GLFWWindow window)
	{
		return getOpenGL31Core(DEFAULT_OPTIONS, window);
	}
	
	/**
	 * Creates an OpenGL 3.2 implementation system.
	 * @param window the window to render to.
	 * @return an OpenGL context entry.
	 * @throws GraphicsException if the given implementation could not be created. 
	 */
	public static OGLSystem<OGL32Graphics> getOpenGL32(GLFWWindow window)
	{
		return getOpenGL32(DEFAULT_OPTIONS, window);
	}
	
	/**
	 * Creates an OpenGL 3.2 Core implementation system.
	 * @param window the window to render to.
	 * @return an OpenGL context entry.
	 * @throws GraphicsException if the given implementation could not be created. 
	 */
	public static OGLSystem<OGL32Graphics> getOpenGL32Core(GLFWWindow window)
	{
		return getOpenGL32Core(DEFAULT_OPTIONS, window);
	}
	
	/**
	 * Creates an OpenGL 3.3 implementation system.
	 * @param window the window to render to.
	 * @return an OpenGL context entry.
	 * @throws GraphicsException if the given implementation could not be created. 
	 */
	public static OGLSystem<OGL33Graphics> getOpenGL33(GLFWWindow window)
	{
		return getOpenGL33(DEFAULT_OPTIONS, window);
	}
	
	/**
	 * Creates an OpenGL 3.3 Core implementation system.
	 * @param window the window to render to.
	 * @return an OpenGL context entry.
	 * @throws GraphicsException if the given implementation could not be created. 
	 */
	public static OGLSystem<OGL33Graphics> getOpenGL33Core(GLFWWindow window)
	{
		return getOpenGL33Core(DEFAULT_OPTIONS, window);
	}
	
	/**
	 * Creates an OpenGL 4.0 Core implementation system.
	 * @param window the window to render to.
	 * @return an OpenGL context entry.
	 * @throws GraphicsException if the given implementation could not be created. 
	 */
	public static OGLSystem<OGL40Graphics> getOpenGL40Core(GLFWWindow window)
	{
		return getOpenGL40Core(DEFAULT_OPTIONS, window);
	}

	/**
	 * Creates an OpenGL 4.1 Core implementation system.
	 * @param window the window to render to.
	 * @return an OpenGL context entry.
	 * @throws GraphicsException if the given implementation could not be created. 
	 */
	public static OGLSystem<OGL41Graphics> getOpenGL41Core(GLFWWindow window)
	{
		return getOpenGL41Core(DEFAULT_OPTIONS, window);
	}

	/**
	 * Creates an OpenGL 4.2 Core implementation system.
	 * @param window the window to render to.
	 * @return an OpenGL context entry.
	 * @throws GraphicsException if the given implementation could not be created. 
	 */
	public static OGLSystem<OGL42Graphics> getOpenGL42Core(GLFWWindow window)
	{
		return getOpenGL42Core(DEFAULT_OPTIONS, window);
	}

	/**
	 * Creates an OpenGL 4.3 Core implementation system.
	 * @param window the window to render to.
	 * @return an OpenGL context entry.
	 * @throws GraphicsException if the given implementation could not be created. 
	 */
	public static OGLSystem<OGL43Graphics> getOpenGL43Core(GLFWWindow window)
	{
		return getOpenGL43Core(DEFAULT_OPTIONS, window);
	}

	/**
	 * Creates an OpenGL 4.4 Core implementation system.
	 * @param window the window to render to.
	 * @return an OpenGL context entry.
	 * @throws GraphicsException if the given implementation could not be created. 
	 */
	public static OGLSystem<OGL44Graphics> getOpenGL44Core(GLFWWindow window)
	{
		return getOpenGL44Core(DEFAULT_OPTIONS, window);
	}

	/**
	 * Creates an OpenGL 4.5 Core implementation system.
	 * @param window the window to render to.
	 * @return an OpenGL context entry.
	 * @throws GraphicsException if the given implementation could not be created. 
	 */
	public static OGLSystem<OGL45Graphics> getOpenGL45Core(GLFWWindow window)
	{
		return getOpenGL45Core(DEFAULT_OPTIONS, window);
	}

	/**
	 * Creates an OpenGL 4.6 Core implementation system.
	 * @param window the window to render to.
	 * @return an OpenGL context entry.
	 * @throws GraphicsException if the given implementation could not be created. 
	 */
	public static OGLSystem<OGL46Graphics> getOpenGL46Core(GLFWWindow window)
	{
		return getOpenGL46Core(DEFAULT_OPTIONS, window);
	}

	/**
	 * Creates an OpenGL 1.1 implementation system.
	 * @param options the system options reference to pass to the graphics instance.
	 * @param window the window to render to.
	 * @return an OpenGL context entry.
	 * @throws GraphicsException if the given implementation could not be created. 
	 */
	public static OGLSystem<OGL11Graphics> getOpenGL11(OGLSystem.Options options, GLFWWindow window)
	{
		return new OGLSystem<OGL11Graphics>(new OGL11Graphics(options, false), window);
	}
	
	/**
	 * Creates an OpenGL 1.2 implementation system.
	 * @param options the system options reference to pass to the graphics instance.
	 * @param window the window to render to.
	 * @return an OpenGL context entry.
	 * @throws GraphicsException if the given implementation could not be created. 
	 */
	public static OGLSystem<OGL12Graphics> getOpenGL12(OGLSystem.Options options, GLFWWindow window)
	{
		return new OGLSystem<OGL12Graphics>(new OGL12Graphics(options, false), window);
	}
	
	/**
	 * Creates an OpenGL 1.3 implementation system.
	 * @param options the system options reference to pass to the graphics instance.
	 * @param window the window to render to.
	 * @return an OpenGL context entry.
	 * @throws GraphicsException if the given implementation could not be created. 
	 */
	public static OGLSystem<OGL13Graphics> getOpenGL13(OGLSystem.Options options, GLFWWindow window)
	{
		return new OGLSystem<OGL13Graphics>(new OGL13Graphics(options, false), window);
	}
	
	/**
	 * Creates an OpenGL 1.4 implementation system.
	 * @param options the system options reference to pass to the graphics instance.
	 * @param window the window to render to.
	 * @return an OpenGL context entry.
	 * @throws GraphicsException if the given implementation could not be created. 
	 */
	public static OGLSystem<OGL14Graphics> getOpenGL14(OGLSystem.Options options, GLFWWindow window)
	{
		return new OGLSystem<OGL14Graphics>(new OGL14Graphics(options, false), window);
	}
	
	/**
	 * Creates an OpenGL 1.5 implementation system.
	 * @param options the system options reference to pass to the graphics instance.
	 * @param window the window to render to.
	 * @return an OpenGL context entry.
	 * @throws GraphicsException if the given implementation could not be created. 
	 */
	public static OGLSystem<OGL15Graphics> getOpenGL15(OGLSystem.Options options, GLFWWindow window)
	{
		return new OGLSystem<OGL15Graphics>(new OGL15Graphics(options, false), window);
	}
	
	/**
	 * Creates an OpenGL 2.0 implementation system.
	 * @param options the system options reference to pass to the graphics instance.
	 * @param window the window to render to.
	 * @return an OpenGL context entry.
	 * @throws GraphicsException if the given implementation could not be created. 
	 */
	public static OGLSystem<OGL20Graphics> getOpenGL20(OGLSystem.Options options, GLFWWindow window)
	{
		return new OGLSystem<OGL20Graphics>(new OGL20Graphics(options, false), window);
	}
	
	/**
	 * Creates an OpenGL 2.1 implementation system.
	 * @param options the system options reference to pass to the graphics instance.
	 * @param window the window to render to.
	 * @return an OpenGL context entry.
	 * @throws GraphicsException if the given implementation could not be created. 
	 */
	public static OGLSystem<OGL21Graphics> getOpenGL21(OGLSystem.Options options, GLFWWindow window)
	{
		return new OGLSystem<OGL21Graphics>(new OGL21Graphics(options, false), window);
	}
	
	/**
	 * Creates an OpenGL 3.0 implementation system.
	 * @param options the system options reference to pass to the graphics instance.
	 * @param window the window to render to.
	 * @return an OpenGL context entry.
	 * @throws GraphicsException if the given implementation could not be created. 
	 */
	public static OGLSystem<OGL30Graphics> getOpenGL30(OGLSystem.Options options, GLFWWindow window)
	{
		return new OGLSystem<OGL30Graphics>(new OGL30Graphics(options, false), window);
	}
	
	/**
	 * Creates an OpenGL 3.0 Core implementation system.
	 * @param options the system options reference to pass to the graphics instance.
	 * @param window the window to render to.
	 * @return an OpenGL context entry.
	 * @throws GraphicsException if the given implementation could not be created. 
	 */
	public static OGLSystem<OGL30Graphics> getOpenGL30Core(OGLSystem.Options options, GLFWWindow window)
	{
		return new OGLSystem<OGL30Graphics>(new OGL30Graphics(options, true), window);
	}
	
	/**
	 * Creates an OpenGL 3.1 implementation system.
	 * @param options the system options reference to pass to the graphics instance.
	 * @param window the window to render to.
	 * @return an OpenGL context entry.
	 * @throws GraphicsException if the given implementation could not be created. 
	 */
	public static OGLSystem<OGL31Graphics> getOpenGL31(OGLSystem.Options options, GLFWWindow window)
	{
		return new OGLSystem<OGL31Graphics>(new OGL31Graphics(options, false), window);
	}
	
	/**
	 * Creates an OpenGL 3.1 Core implementation system.
	 * @param options the system options reference to pass to the graphics instance.
	 * @param window the window to render to.
	 * @return an OpenGL context entry.
	 * @throws GraphicsException if the given implementation could not be created. 
	 */
	public static OGLSystem<OGL31Graphics> getOpenGL31Core(OGLSystem.Options options, GLFWWindow window)
	{
		return new OGLSystem<OGL31Graphics>(new OGL31Graphics(options, true), window);
	}
	
	/**
	 * Creates an OpenGL 3.2 implementation system.
	 * @param options the system options reference to pass to the graphics instance.
	 * @param window the window to render to.
	 * @return an OpenGL context entry.
	 * @throws GraphicsException if the given implementation could not be created. 
	 */
	public static OGLSystem<OGL32Graphics> getOpenGL32(OGLSystem.Options options, GLFWWindow window)
	{
		return new OGLSystem<OGL32Graphics>(new OGL32Graphics(options, false), window);
	}
	
	/**
	 * Creates an OpenGL 3.2 Core implementation system.
	 * @param options the system options reference to pass to the graphics instance.
	 * @param window the window to render to.
	 * @return an OpenGL context entry.
	 * @throws GraphicsException if the given implementation could not be created. 
	 */
	public static OGLSystem<OGL32Graphics> getOpenGL32Core(OGLSystem.Options options, GLFWWindow window)
	{
		return new OGLSystem<OGL32Graphics>(new OGL32Graphics(options, true), window);
	}
	
	/**
	 * Creates an OpenGL 3.3 implementation system.
	 * @param options the system options reference to pass to the graphics instance.
	 * @param window the window to render to.
	 * @return an OpenGL context entry.
	 * @throws GraphicsException if the given implementation could not be created. 
	 */
	public static OGLSystem<OGL33Graphics> getOpenGL33(OGLSystem.Options options, GLFWWindow window)
	{
		return new OGLSystem<OGL33Graphics>(new OGL33Graphics(options, false), window);
	}
	
	/**
	 * Creates an OpenGL 3.3 Core implementation system.
	 * @param options the system options reference to pass to the graphics instance.
	 * @param window the window to render to.
	 * @return an OpenGL context entry.
	 * @throws GraphicsException if the given implementation could not be created. 
	 */
	public static OGLSystem<OGL33Graphics> getOpenGL33Core(OGLSystem.Options options, GLFWWindow window)
	{
		return new OGLSystem<OGL33Graphics>(new OGL33Graphics(options, true), window);
	}
	
	/**
	 * Creates an OpenGL 4.0 Core implementation system.
	 * @param options the system options reference to pass to the graphics instance.
	 * @param window the window to render to.
	 * @return an OpenGL context entry.
	 * @throws GraphicsException if the given implementation could not be created. 
	 */
	public static OGLSystem<OGL40Graphics> getOpenGL40Core(OGLSystem.Options options, GLFWWindow window)
	{
		return new OGLSystem<OGL40Graphics>(new OGL40Graphics(options, true), window);
	}

	/**
	 * Creates an OpenGL 4.1 Core implementation system.
	 * @param options the system options reference to pass to the graphics instance.
	 * @param window the window to render to.
	 * @return an OpenGL context entry.
	 * @throws GraphicsException if the given implementation could not be created. 
	 */
	public static OGLSystem<OGL41Graphics> getOpenGL41Core(OGLSystem.Options options, GLFWWindow window)
	{
		return new OGLSystem<OGL41Graphics>(new OGL41Graphics(options, true), window);
	}

	/**
	 * Creates an OpenGL 4.2 Core implementation system.
	 * @param options the system options reference to pass to the graphics instance.
	 * @param window the window to render to.
	 * @return an OpenGL context entry.
	 * @throws GraphicsException if the given implementation could not be created. 
	 */
	public static OGLSystem<OGL42Graphics> getOpenGL42Core(OGLSystem.Options options, GLFWWindow window)
	{
		return new OGLSystem<OGL42Graphics>(new OGL42Graphics(options, true), window);
	}

	/**
	 * Creates an OpenGL 4.3 Core implementation system.
	 * @param options the system options reference to pass to the graphics instance.
	 * @param window the window to render to.
	 * @return an OpenGL context entry.
	 * @throws GraphicsException if the given implementation could not be created. 
	 */
	public static OGLSystem<OGL43Graphics> getOpenGL43Core(OGLSystem.Options options, GLFWWindow window)
	{
		return new OGLSystem<OGL43Graphics>(new OGL43Graphics(options, true), window);
	}

	/**
	 * Creates an OpenGL 4.4 Core implementation system.
	 * @param options the system options reference to pass to the graphics instance.
	 * @param window the window to render to.
	 * @return an OpenGL context entry.
	 * @throws GraphicsException if the given implementation could not be created. 
	 */
	public static OGLSystem<OGL44Graphics> getOpenGL44Core(OGLSystem.Options options, GLFWWindow window)
	{
		return new OGLSystem<OGL44Graphics>(new OGL44Graphics(options, true), window);
	}

	/**
	 * Creates an OpenGL 4.5 Core implementation system.
	 * @param options the system options reference to pass to the graphics instance.
	 * @param window the window to render to.
	 * @return an OpenGL context entry.
	 * @throws GraphicsException if the given implementation could not be created. 
	 */
	public static OGLSystem<OGL45Graphics> getOpenGL45Core(OGLSystem.Options options, GLFWWindow window)
	{
		return new OGLSystem<OGL45Graphics>(new OGL45Graphics(options, true), window);
	}

	/**
	 * Creates an OpenGL 4.6 Core implementation system.
	 * @param options the system options reference to pass to the graphics instance.
	 * @param window the window to render to.
	 * @return an OpenGL context entry.
	 * @throws GraphicsException if the given implementation could not be created. 
	 */
	public static OGLSystem<OGL46Graphics> getOpenGL46Core(OGLSystem.Options options, GLFWWindow window)
	{
		return new OGLSystem<OGL46Graphics>(new OGL46Graphics(options, true), window);
	}

}
