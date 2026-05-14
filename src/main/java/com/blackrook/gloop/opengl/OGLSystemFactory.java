package com.blackrook.gloop.opengl;

import com.blackrook.gloop.opengl.OGLGraphics.ErrorHandlingType;
import com.blackrook.gloop.opengl.OGLGraphics.Options;
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
 * A factory class for creating OGLSystems.
 */
public final class OGLSystemFactory
{
	/**
	 * Options that disable error checking.
	 */
	public static final Options NO_ERROR_CHECKING = new Options()
	{
		@Override
		public ErrorHandlingType handleErrorChecking() 
		{
			return ErrorHandlingType.IGNORE;
		}

		@Override
		public ErrorHandlingType handleVersionChecking()
		{
			return ErrorHandlingType.IGNORE;
		}

		@Override
		public ErrorHandlingType handleUndeletedObjects()
		{
			return ErrorHandlingType.IGNORE;
		}
	};
	
	/**
	 * Default options.
	 */
	public static final Options DEFAULT_OPTIONS = new Options()
	{
		@Override
		public ErrorHandlingType handleErrorChecking() 
		{
			return ErrorHandlingType.EXCEPTION;
		}

		@Override
		public ErrorHandlingType handleVersionChecking() 
		{
			return ErrorHandlingType.EXCEPTION;
		}

		@Override
		public ErrorHandlingType handleUndeletedObjects() 
		{
			return ErrorHandlingType.EXCEPTION;
		}
	};
	
	/**
	 * Creates an OpenGL 1.1 implementation system.
	 * @return an OGLSystem using the desired graphics implementation.
	 * @throws GraphicsException if the given implementation could not be created. 
	 */
	public static OGLSystem<OGL11Graphics> getOpenGL11()
	{
		return getOpenGL11(DEFAULT_OPTIONS);
	}
	
	/**
	 * Creates an OpenGL 1.2 implementation system.
	 * @return an OGLSystem using the desired graphics implementation.
	 * @throws GraphicsException if the given implementation could not be created. 
	 */
	public static OGLSystem<OGL12Graphics> getOpenGL12()
	{
		return getOpenGL12(DEFAULT_OPTIONS);
	}
	
	/**
	 * Creates an OpenGL 1.3 implementation system.
	 * @return an OGLSystem using the desired graphics implementation.
	 * @throws GraphicsException if the given implementation could not be created. 
	 */
	public static OGLSystem<OGL13Graphics> getOpenGL13()
	{
		return getOpenGL13(DEFAULT_OPTIONS);
	}
	
	/**
	 * Creates an OpenGL 1.4 implementation system.
	 * @return an OGLSystem using the desired graphics implementation.
	 * @throws GraphicsException if the given implementation could not be created. 
	 */
	public static OGLSystem<OGL14Graphics> getOpenGL14()
	{
		return getOpenGL14(DEFAULT_OPTIONS);
	}
	
	/**
	 * Creates an OpenGL 1.5 implementation system.
	 * @return an OGLSystem using the desired graphics implementation.
	 * @throws GraphicsException if the given implementation could not be created. 
	 */
	public static OGLSystem<OGL15Graphics> getOpenGL15()
	{
		return getOpenGL15(DEFAULT_OPTIONS);
	}
	
	/**
	 * Creates an OpenGL 2.0 implementation system.
	 * @return an OGLSystem using the desired graphics implementation.
	 * @throws GraphicsException if the given implementation could not be created. 
	 */
	public static OGLSystem<OGL20Graphics> getOpenGL20()
	{
		return getOpenGL20(DEFAULT_OPTIONS);
	}
	
	/**
	 * Creates an OpenGL 2.1 implementation system.
	 * @return an OGLSystem using the desired graphics implementation.
	 * @throws GraphicsException if the given implementation could not be created. 
	 */
	public static OGLSystem<OGL21Graphics> getOpenGL21()
	{
		return getOpenGL21(DEFAULT_OPTIONS);
	}
	
	/**
	 * Creates an OpenGL 3.0 implementation system.
	 * @return an OGLSystem using the desired graphics implementation.
	 * @throws GraphicsException if the given implementation could not be created. 
	 */
	public static OGLSystem<OGL30Graphics> getOpenGL30()
	{
		return getOpenGL30(DEFAULT_OPTIONS);
	}
	
	/**
	 * Creates an OpenGL 3.0 Core implementation system.
	 * @return an OGLSystem using the desired graphics implementation.
	 * @throws GraphicsException if the given implementation could not be created. 
	 */
	public static OGLSystem<OGL30Graphics> getOpenGL30Core()
	{
		return getOpenGL30Core(DEFAULT_OPTIONS);
	}
	
	/**
	 * Creates an OpenGL 3.1 implementation system.
	 * @return an OGLSystem using the desired graphics implementation.
	 * @throws GraphicsException if the given implementation could not be created. 
	 */
	public static OGLSystem<OGL31Graphics> getOpenGL31()
	{
		return getOpenGL31(DEFAULT_OPTIONS);
	}
	
	/**
	 * Creates an OpenGL 3.1 Core implementation system.
	 * @return an OGLSystem using the desired graphics implementation.
	 * @throws GraphicsException if the given implementation could not be created. 
	 */
	public static OGLSystem<OGL31Graphics> getOpenGL31Core()
	{
		return getOpenGL31Core(DEFAULT_OPTIONS);
	}
	
	/**
	 * Creates an OpenGL 3.2 implementation system.
	 * @return an OGLSystem using the desired graphics implementation.
	 * @throws GraphicsException if the given implementation could not be created. 
	 */
	public static OGLSystem<OGL32Graphics> getOpenGL32()
	{
		return getOpenGL32(DEFAULT_OPTIONS);
	}
	
	/**
	 * Creates an OpenGL 3.2 Core implementation system.
	 * @return an OGLSystem using the desired graphics implementation.
	 * @throws GraphicsException if the given implementation could not be created. 
	 */
	public static OGLSystem<OGL32Graphics> getOpenGL32Core()
	{
		return getOpenGL32Core(DEFAULT_OPTIONS);
	}
	
	/**
	 * Creates an OpenGL 3.3 implementation system.
	 * @return an OGLSystem using the desired graphics implementation.
	 * @throws GraphicsException if the given implementation could not be created. 
	 */
	public static OGLSystem<OGL33Graphics> getOpenGL33()
	{
		return getOpenGL33(DEFAULT_OPTIONS);
	}
	
	/**
	 * Creates an OpenGL 3.3 Core implementation system.
	 * @return an OGLSystem using the desired graphics implementation.
	 * @throws GraphicsException if the given implementation could not be created. 
	 */
	public static OGLSystem<OGL33Graphics> getOpenGL33Core()
	{
		return getOpenGL33Core(DEFAULT_OPTIONS);
	}
	
	/**
	 * Creates an OpenGL 4.0 Core implementation system.
	 * @return an OGLSystem using the desired graphics implementation.
	 * @throws GraphicsException if the given implementation could not be created. 
	 */
	public static OGLSystem<OGL40Graphics> getOpenGL40Core()
	{
		return getOpenGL40Core(DEFAULT_OPTIONS);
	}

	/**
	 * Creates an OpenGL 4.1 Core implementation system.
	 * @return an OGLSystem using the desired graphics implementation.
	 * @throws GraphicsException if the given implementation could not be created. 
	 */
	public static OGLSystem<OGL41Graphics> getOpenGL41Core()
	{
		return getOpenGL41Core(DEFAULT_OPTIONS);
	}

	/**
	 * Creates an OpenGL 4.2 Core implementation system.
	 * @return an OGLSystem using the desired graphics implementation.
	 * @throws GraphicsException if the given implementation could not be created. 
	 */
	public static OGLSystem<OGL42Graphics> getOpenGL42Core()
	{
		return getOpenGL42Core(DEFAULT_OPTIONS);
	}

	/**
	 * Creates an OpenGL 4.3 Core implementation system.
	 * @return an OGLSystem using the desired graphics implementation.
	 * @throws GraphicsException if the given implementation could not be created. 
	 */
	public static OGLSystem<OGL43Graphics> getOpenGL43Core()
	{
		return getOpenGL43Core(DEFAULT_OPTIONS);
	}

	/**
	 * Creates an OpenGL 4.4 Core implementation system.
	 * @return an OGLSystem using the desired graphics implementation.
	 * @throws GraphicsException if the given implementation could not be created. 
	 */
	public static OGLSystem<OGL44Graphics> getOpenGL44Core()
	{
		return getOpenGL44Core(DEFAULT_OPTIONS);
	}

	/**
	 * Creates an OpenGL 4.5 Core implementation system.
	 * @return an OGLSystem using the desired graphics implementation.
	 * @throws GraphicsException if the given implementation could not be created. 
	 */
	public static OGLSystem<OGL45Graphics> getOpenGL45Core()
	{
		return getOpenGL45Core(DEFAULT_OPTIONS);
	}

	/**
	 * Creates an OpenGL 4.6 Core implementation system.
	 * @return an OGLSystem using the desired graphics implementation.
	 * @throws GraphicsException if the given implementation could not be created. 
	 */
	public static OGLSystem<OGL46Graphics> getOpenGL46Core()
	{
		return getOpenGL46Core(DEFAULT_OPTIONS);
	}

	/**
	 * Creates an OpenGL 1.1 implementation system.
	 * @param options the system options reference to pass to the graphics instance.
	 * @return an OGLSystem using the desired graphics implementation.
	 * @throws GraphicsException if the given implementation could not be created. 
	 */
	public static OGLSystem<OGL11Graphics> getOpenGL11(Options options)
	{
		return new OGLSystem<>(new OGL11Graphics(options, false));
	}
	
	/**
	 * Creates an OpenGL 1.2 implementation system.
	 * @param options the system options reference to pass to the graphics instance.
	 * @return an OGLSystem using the desired graphics implementation.
	 * @throws GraphicsException if the given implementation could not be created. 
	 */
	public static OGLSystem<OGL12Graphics> getOpenGL12(Options options)
	{
		return new OGLSystem<>(new OGL12Graphics(options, false));
	}
	
	/**
	 * Creates an OpenGL 1.3 implementation system.
	 * @param options the system options reference to pass to the graphics instance.
	 * @return an OGLSystem using the desired graphics implementation.
	 * @throws GraphicsException if the given implementation could not be created. 
	 */
	public static OGLSystem<OGL13Graphics> getOpenGL13(Options options)
	{
		return new OGLSystem<>(new OGL13Graphics(options, false));
	}
	
	/**
	 * Creates an OpenGL 1.4 implementation system.
	 * @param options the system options reference to pass to the graphics instance.
	 * @return an OGLSystem using the desired graphics implementation.
	 * @throws GraphicsException if the given implementation could not be created. 
	 */
	public static OGLSystem<OGL14Graphics> getOpenGL14(Options options)
	{
		return new OGLSystem<>(new OGL14Graphics(options, false));
	}
	
	/**
	 * Creates an OpenGL 1.5 implementation system.
	 * @param options the system options reference to pass to the graphics instance.
	 * @return an OGLSystem using the desired graphics implementation.
	 * @throws GraphicsException if the given implementation could not be created. 
	 */
	public static OGLSystem<OGL15Graphics> getOpenGL15(Options options)
	{
		return new OGLSystem<>(new OGL15Graphics(options, false));
	}
	
	/**
	 * Creates an OpenGL 2.0 implementation system.
	 * @param options the system options reference to pass to the graphics instance.
	 * @return an OGLSystem using the desired graphics implementation.
	 * @throws GraphicsException if the given implementation could not be created. 
	 */
	public static OGLSystem<OGL20Graphics> getOpenGL20(Options options)
	{
		return new OGLSystem<>(new OGL20Graphics(options, false));
	}
	
	/**
	 * Creates an OpenGL 2.1 implementation system.
	 * @param options the system options reference to pass to the graphics instance.
	 * @return an OGLSystem using the desired graphics implementation.
	 * @throws GraphicsException if the given implementation could not be created. 
	 */
	public static OGLSystem<OGL21Graphics> getOpenGL21(Options options)
	{
		return new OGLSystem<>(new OGL21Graphics(options, false));
	}
	
	/**
	 * Creates an OpenGL 3.0 implementation system.
	 * @param options the system options reference to pass to the graphics instance.
	 * @return an OGLSystem using the desired graphics implementation.
	 * @throws GraphicsException if the given implementation could not be created. 
	 */
	public static OGLSystem<OGL30Graphics> getOpenGL30(Options options)
	{
		return new OGLSystem<>(new OGL30Graphics(options, false));
	}
	
	/**
	 * Creates an OpenGL 3.0 Core implementation system.
	 * @param options the system options reference to pass to the graphics instance.
	 * @return an OGLSystem using the desired graphics implementation.
	 * @throws GraphicsException if the given implementation could not be created. 
	 */
	public static OGLSystem<OGL30Graphics> getOpenGL30Core(Options options)
	{
		return new OGLSystem<>(new OGL30Graphics(options, true));
	}
	
	/**
	 * Creates an OpenGL 3.1 implementation system.
	 * @param options the system options reference to pass to the graphics instance.
	 * @return an OGLSystem using the desired graphics implementation.
	 * @throws GraphicsException if the given implementation could not be created. 
	 */
	public static OGLSystem<OGL31Graphics> getOpenGL31(Options options)
	{
		return new OGLSystem<>(new OGL31Graphics(options, false));
	}
	
	/**
	 * Creates an OpenGL 3.1 Core implementation system.
	 * @param options the system options reference to pass to the graphics instance.
	 * @return an OGLSystem using the desired graphics implementation.
	 * @throws GraphicsException if the given implementation could not be created. 
	 */
	public static OGLSystem<OGL31Graphics> getOpenGL31Core(Options options)
	{
		return new OGLSystem<>(new OGL31Graphics(options, true));
	}
	
	/**
	 * Creates an OpenGL 3.2 implementation system.
	 * @param options the system options reference to pass to the graphics instance.
	 * @return an OGLSystem using the desired graphics implementation.
	 * @throws GraphicsException if the given implementation could not be created. 
	 */
	public static OGLSystem<OGL32Graphics> getOpenGL32(Options options)
	{
		return new OGLSystem<>(new OGL32Graphics(options, false));
	}
	
	/**
	 * Creates an OpenGL 3.2 Core implementation system.
	 * @param options the system options reference to pass to the graphics instance.
	 * @return an OGLSystem using the desired graphics implementation.
	 * @throws GraphicsException if the given implementation could not be created. 
	 */
	public static OGLSystem<OGL32Graphics> getOpenGL32Core(Options options)
	{
		return new OGLSystem<>(new OGL32Graphics(options, true));
	}
	
	/**
	 * Creates an OpenGL 3.3 implementation system.
	 * @param options the system options reference to pass to the graphics instance.
	 * @return an OGLSystem using the desired graphics implementation.
	 * @throws GraphicsException if the given implementation could not be created. 
	 */
	public static OGLSystem<OGL33Graphics> getOpenGL33(Options options)
	{
		return new OGLSystem<>(new OGL33Graphics(options, false));
	}
	
	/**
	 * Creates an OpenGL 3.3 Core implementation system.
	 * @param options the system options reference to pass to the graphics instance.
	 * @return an OGLSystem using the desired graphics implementation.
	 * @throws GraphicsException if the given implementation could not be created. 
	 */
	public static OGLSystem<OGL33Graphics> getOpenGL33Core(Options options)
	{
		return new OGLSystem<>(new OGL33Graphics(options, true));
	}
	
	/**
	 * Creates an OpenGL 4.0 Core implementation system.
	 * @param options the system options reference to pass to the graphics instance.
	 * @return an OGLSystem using the desired graphics implementation.
	 * @throws GraphicsException if the given implementation could not be created. 
	 */
	public static OGLSystem<OGL40Graphics> getOpenGL40Core(Options options)
	{
		return new OGLSystem<>(new OGL40Graphics(options, true));
	}

	/**
	 * Creates an OpenGL 4.1 Core implementation system.
	 * @param options the system options reference to pass to the graphics instance.
	 * @return an OGLSystem using the desired graphics implementation.
	 * @throws GraphicsException if the given implementation could not be created. 
	 */
	public static OGLSystem<OGL41Graphics> getOpenGL41Core(Options options)
	{
		return new OGLSystem<>(new OGL41Graphics(options, true));
	}

	/**
	 * Creates an OpenGL 4.2 Core implementation system.
	 * @param options the system options reference to pass to the graphics instance.
	 * @return an OGLSystem using the desired graphics implementation.
	 * @throws GraphicsException if the given implementation could not be created. 
	 */
	public static OGLSystem<OGL42Graphics> getOpenGL42Core(Options options)
	{
		return new OGLSystem<>(new OGL42Graphics(options, true));
	}

	/**
	 * Creates an OpenGL 4.3 Core implementation system.
	 * @param options the system options reference to pass to the graphics instance.
	 * @return an OGLSystem using the desired graphics implementation.
	 * @throws GraphicsException if the given implementation could not be created. 
	 */
	public static OGLSystem<OGL43Graphics> getOpenGL43Core(Options options)
	{
		return new OGLSystem<>(new OGL43Graphics(options, true));
	}

	/**
	 * Creates an OpenGL 4.4 Core implementation system.
	 * @param options the system options reference to pass to the graphics instance.
	 * @return an OGLSystem using the desired graphics implementation.
	 * @throws GraphicsException if the given implementation could not be created. 
	 */
	public static OGLSystem<OGL44Graphics> getOpenGL44Core(Options options)
	{
		return new OGLSystem<>(new OGL44Graphics(options, true));
	}

	/**
	 * Creates an OpenGL 4.5 Core implementation system.
	 * @param options the system options reference to pass to the graphics instance.
	 * @return an OGLSystem using the desired graphics implementation.
	 * @throws GraphicsException if the given implementation could not be created. 
	 */
	public static OGLSystem<OGL45Graphics> getOpenGL45Core(Options options)
	{
		return new OGLSystem<>(new OGL45Graphics(options, true));
	}

	/**
	 * Creates an OpenGL 4.6 Core implementation system.
	 * @param options the system options reference to pass to the graphics instance.
	 * @return an OGLSystem using the desired graphics implementation.
	 * @throws GraphicsException if the given implementation could not be created. 
	 */
	public static OGLSystem<OGL46Graphics> getOpenGL46Core(Options options)
	{
		return new OGLSystem<>(new OGL46Graphics(options, true));
	}

}
