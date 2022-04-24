/*******************************************************************************
 * Copyright (c) 2021-2022 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at 
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gloop.opengl.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Supplier;

import com.blackrook.gloop.opengl.OGLGraphics;
import com.blackrook.gloop.opengl.enums.ShaderType;
import com.blackrook.gloop.opengl.exception.GraphicsException;
import com.blackrook.gloop.opengl.gl2.OGLProgram;

/**
 * Shader program pipeline builder utility class.
 * <p>
 * This class is used to generate linked shader programs in a "builder" way.
 * Mostly useful for small applications and test applications, not necessarily for
 * large enterprise applications that may employ better methods for shader loading and assembly.
 * <p>
 * All of these methods can be called outside of the graphics thread except {@link #create()}.
 * <p>
 * Depending on implementation version, some methods may not be supported or available,
 * and will throw {@link UnsupportedOperationException} if so.
 */
public interface ProgramBuilder
{
	@FunctionalInterface
	public interface Listener
	{
		/**
		 * Called when a shader is compiled, and the log built.
		 * @param type the shader type built.
		 * @param log the log content.
		 */
		void onShaderLog(ShaderType type, String log);
	}
	
	/**
	 * Binds an attribute name to a specific location index.
	 * @param attributeName the attribute name.
	 * @param locationId the location id.
	 * @return this builder.
	 */
	ProgramBuilder attributeLocation(String attributeName, int locationId);
	
	/**
	 * Binds a fragment output attribute name to a specific output color index.
	 * @param attributeName the attribute name.
	 * @param index the index.
	 * @return this builder.
	 */
	ProgramBuilder fragmentDataLocation(String attributeName, int index);
	
	/**
	 * Sets a shader program and a shader source. 
	 * @param type the shader type.
	 * @param file the source file.
	 * @return this builder.
	 */
	ProgramBuilder setShader(ShaderType type, final File file);
	
	/**
	 * Sets a shader program and a shader source. 
	 * @param type the shader type.
	 * @param in the input stream to read from.
	 * @return this builder.
	 */
	ProgramBuilder setShader(ShaderType type, final InputStream in);
	
	/**
	 * Sets a shader program and a shader source. 
	 * @param type the shader type.
	 * @param reader the reader to read from.
	 * @return this builder.
	 */
	ProgramBuilder setShader(ShaderType type, final Reader reader);
	
	/**
	 * Sets a shader program and a shader source. 
	 * @param type the shader type.
	 * @param source the string that contains the source code.
	 * @return this builder.
	 */
	ProgramBuilder setShader(ShaderType type, final String source);
	
	/**
	 * Sets a shader program and a shader source. 
	 * @param type the shader type.
	 * @param source the source code supplier.
	 * @return this builder.
	 */
	ProgramBuilder setShader(ShaderType type, Supplier<String> source);

	/**
	 * Sets a log listener to listen for builder events.
	 * @param listener the listener to set.
	 * @return this builder.
	 */
	ProgramBuilder setListener(Listener listener);
	
	/**
	 * Creates the program.
	 * @return the shader program created.
	 * @throws GraphicsException if the program could not be created.
	 */
	OGLProgram create();

	/**
	 * Shader builder utility class.
	 * @param <GL> the graphics implementation that this executes on.
	 */
	public static abstract class Abstract<GL extends OGLGraphics> implements ProgramBuilder
	{
		protected GL gl;
		protected Map<String, Integer> attributeLocationBindings;
		protected Map<String, Integer> fragmentDataBindings;
		protected Map<ShaderType, Supplier<String>> shaderPrograms;
		protected ProgramBuilder.Listener builderListener;

		/**
		 * Creates a new Shader Builder with defaults set.
		 * @param gl the graphics implementation that created this (and will execute this).
		 */
		protected Abstract(GL gl)
		{
			this.gl = gl;
			this.attributeLocationBindings = new TreeMap<>();
			this.fragmentDataBindings = new TreeMap<>();
			this.shaderPrograms = new TreeMap<>();
			this.builderListener = null;
		}
		
		// Reads source from a reader.
		private static String readSource(Reader reader) throws IOException
		{
			int c = 0;
			char[] cbuf = new char[4096];
			StringBuilder sb = new StringBuilder();
			while ((c = reader.read(cbuf)) > 0)
				sb.append(cbuf, 0, c);
			return sb.toString();
		}

		@Override
		public ProgramBuilder attributeLocation(String attributeName, int locationId)
		{
			attributeLocationBindings.put(attributeName, locationId);
			return this;
		}
		
		@Override
		public ProgramBuilder fragmentDataLocation(String attributeName, int index)
		{
			fragmentDataBindings.put(attributeName, index);
			return this;
		}
		
		@Override
		public ProgramBuilder setShader(ShaderType type, final File file)
		{
			return setShader(type, () ->
			{
				try (Reader reader = new InputStreamReader(new FileInputStream(file)))
				{
					return readSource(reader);
				}
				catch (FileNotFoundException e)
				{
					throw new GraphicsException("Shader source for " + type.name() + " could not be found.", e);
				}
				catch (IOException e)
				{
					throw new GraphicsException("Shader source for " + type.name() + " could not be read.", e);
				}
			});
		}
		
		@Override
		public ProgramBuilder setShader(ShaderType type, final InputStream in)
		{
			return setShader(type, () ->
			{
				try (Reader reader = new InputStreamReader(in))
				{
					return readSource(reader);
				}
				catch (FileNotFoundException e)
				{
					throw new GraphicsException("Shader source for " + type.name() + " could not be found.", e);
				}
				catch (IOException e)
				{
					throw new GraphicsException("Shader source for " + type.name() + " could not be read.", e);
				}
			});
		}
		
		@Override
		public ProgramBuilder setShader(ShaderType type, final Reader reader)
		{
			return setShader(type, () ->
			{
				try
				{
					return readSource(reader);
				}
				catch (FileNotFoundException e)
				{
					throw new GraphicsException("Shader source for " + type.name() + " could not be found.", e);
				}
				catch (IOException e)
				{
					throw new GraphicsException("Shader source for " + type.name() + " could not be read.", e);
				}
			});
		}
		
		@Override
		public ProgramBuilder setShader(ShaderType type, final String source)
		{
			return setShader(type, ()->source);
		}
		
		@Override
		public ProgramBuilder setShader(ShaderType type, Supplier<String> source)
		{
			shaderPrograms.put(type, source);
			return this;
		}

		@Override
		public ProgramBuilder setListener(ProgramBuilder.Listener listener)
		{
			builderListener = listener;
			return this;
		}
		
		/**
		 * Fires a log event to the listener, if attached.
		 * @param type the shader type.
		 * @param log the shader log.
		 */
		protected final void fireShaderLog(ShaderType type, String log)
		{
			if (builderListener != null)
				builderListener.onShaderLog(type, log);
		}
		
	}
	
}
