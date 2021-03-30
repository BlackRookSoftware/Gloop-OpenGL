/*******************************************************************************
 * Copyright (c) 2014, 2015 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 *
 * Contributors:
 *     Matt Tropiano - initial API and implementation
 *******************************************************************************/
package com.blackrook.gloop.opengl.gl2;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.opengl.GL21;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL31;
import org.lwjgl.opengl.GL32;
import org.lwjgl.opengl.GL40;
import org.lwjgl.opengl.GL42;
import org.lwjgl.system.MemoryStack;

import com.blackrook.gloop.opengl.OGLObject;
import com.blackrook.gloop.opengl.exception.GraphicsException;

import static org.lwjgl.opengl.GL20.*;

/**
 * The main shader program class.
 * @author Matthew Tropiano
 */
public class OGLShader extends OGLObject
{
	/** List of OpenGL object ids that were not deleted properly. */
	protected static int[] UNDELETED_IDS;
	/** Amount of OpenGL object ids that were not deleted properly. */
	protected static int UNDELETED_LENGTH;
	
	/** Map type names. */
	private static final Map<Integer, String> TYPENAMES = new HashMap<Integer, String>() 
	{
		private static final long serialVersionUID = -6869578990794785352L;
		{
			put(GL_FLOAT, "float");
			put(GL_FLOAT_VEC2, "vec2");
			put(GL_FLOAT_VEC3, "vec3");
			put(GL_FLOAT_VEC4, "vec4");
			put(GL_DOUBLE, "double");
			put(GL_INT, "int");
			put(GL_INT_VEC2, "ivec2");
			put(GL_INT_VEC3, "ivec3");
			put(GL_INT_VEC4, "ivec4");
			put(GL_UNSIGNED_INT, "unsigned int");
			
			put(GL_BOOL, "bool");
			put(GL_BOOL_VEC2, "bvec2");
			put(GL_BOOL_VEC3, "bvec3");
			put(GL_BOOL_VEC4, "bvec4");
			put(GL_FLOAT_MAT2, "mat2");
			put(GL_FLOAT_MAT3, "mat3");
			put(GL_FLOAT_MAT4, "mat4");
	
			put(GL_SAMPLER_1D, "sampler1D");
			put(GL_SAMPLER_2D, "sampler2D");
			put(GL_SAMPLER_3D, "sampler3D");
			put(GL_SAMPLER_CUBE, "samplerCube");
			put(GL_SAMPLER_1D_SHADOW, "sampler1DShadow");
			put(GL_SAMPLER_2D_SHADOW, "sampler2DShadow");
	
			put(GL21.GL_FLOAT_MAT2x3, "mat2x3");
			put(GL21.GL_FLOAT_MAT2x4, "mat2x4");
			put(GL21.GL_FLOAT_MAT3x2, "mat3x2");
			put(GL21.GL_FLOAT_MAT3x4, "mat3x4");
			put(GL21.GL_FLOAT_MAT4x2, "mat4x2");
			put(GL21.GL_FLOAT_MAT4x3, "mat4x3");
	
			put(GL30.GL_UNSIGNED_INT_VEC2, "uvec2");
			put(GL30.GL_UNSIGNED_INT_VEC3, "uvec3");
			put(GL30.GL_UNSIGNED_INT_VEC4, "uvec4");
			put(GL30.GL_SAMPLER_1D_ARRAY, "sampler1DArray");
			put(GL30.GL_SAMPLER_2D_ARRAY, "sampler2DArray");
			put(GL30.GL_SAMPLER_1D_ARRAY_SHADOW, "sampler1DArrayShadow");
			put(GL30.GL_SAMPLER_2D_ARRAY_SHADOW, "sampler2DArrayShadow");
			put(GL30.GL_SAMPLER_CUBE_SHADOW, "samplerCubeShadow");
			put(GL30.GL_INT_SAMPLER_1D, "isampler1D");
			put(GL30.GL_INT_SAMPLER_2D, "isampler2D");
			put(GL30.GL_INT_SAMPLER_3D, "isampler3D");
			put(GL30.GL_INT_SAMPLER_CUBE, "isamplerCube");
			put(GL30.GL_INT_SAMPLER_1D_ARRAY, "isampler1DArray");
			put(GL30.GL_INT_SAMPLER_2D_ARRAY, "isampler2DArray");
			put(GL30.GL_UNSIGNED_INT_SAMPLER_1D, "usampler1D");
			put(GL30.GL_UNSIGNED_INT_SAMPLER_2D, "usampler2D");
			put(GL30.GL_UNSIGNED_INT_SAMPLER_3D, "usampler3D");
			put(GL30.GL_UNSIGNED_INT_SAMPLER_CUBE, "usamplerCube");
			put(GL30.GL_UNSIGNED_INT_SAMPLER_1D_ARRAY, "usampler2DArray");
			put(GL30.GL_UNSIGNED_INT_SAMPLER_2D_ARRAY, "usampler2DArray");
	
			put(GL31.GL_INT_SAMPLER_BUFFER, "isamplerBuffer");
			put(GL31.GL_INT_SAMPLER_2D_RECT, "isampler2DRect");
			put(GL31.GL_UNSIGNED_INT_SAMPLER_BUFFER, "usamplerBuffer");
			put(GL31.GL_UNSIGNED_INT_SAMPLER_2D_RECT, "usampler2DRect");
	
			put(GL32.GL_SAMPLER_2D_MULTISAMPLE, "sampler2DMS");
			put(GL32.GL_SAMPLER_2D_MULTISAMPLE_ARRAY, "sampler2DMSArray");
			put(GL32.GL_SAMPLER_BUFFER, "samplerBuffer");
			put(GL32.GL_SAMPLER_2D_RECT, "sampler2DRect");
			put(GL32.GL_SAMPLER_2D_RECT_SHADOW, "sampler2DRectShadow");
			put(GL32.GL_INT_SAMPLER_2D_MULTISAMPLE, "isampler2DMS");
			put(GL32.GL_INT_SAMPLER_2D_MULTISAMPLE_ARRAY, "isampler2DMSArray");
			put(GL32.GL_UNSIGNED_INT_SAMPLER_2D_MULTISAMPLE, "usampler2DMS");
			put(GL32.GL_UNSIGNED_INT_SAMPLER_2D_MULTISAMPLE_ARRAY, "usampler2DMSArray");
	
			put(GL40.GL_DOUBLE_VEC2, "dvec2");
			put(GL40.GL_DOUBLE_VEC3, "dvec3");
			put(GL40.GL_DOUBLE_VEC4, "dvec4");
			put(GL40.GL_DOUBLE_MAT2, "dmat2");
			put(GL40.GL_DOUBLE_MAT3, "dmat3");
			put(GL40.GL_DOUBLE_MAT4, "dmat4");
			put(GL40.GL_DOUBLE_MAT2x3, "dmat2x3");
			put(GL40.GL_DOUBLE_MAT2x4, "dmat2x4");
			put(GL40.GL_DOUBLE_MAT3x2, "dmat3x2");
			put(GL40.GL_DOUBLE_MAT3x4, "dmat3x4");
			put(GL40.GL_DOUBLE_MAT4x2, "dmat4x2");
			put(GL40.GL_DOUBLE_MAT4x3, "dmat4x3");
			
			put(GL42.GL_IMAGE_1D, "image1D");
			put(GL42.GL_IMAGE_2D, "image2D");
			put(GL42.GL_IMAGE_3D, "image3D");
			put(GL42.GL_IMAGE_2D_RECT, "image2DRect");
			put(GL42.GL_IMAGE_CUBE, "imageCube");
			put(GL42.GL_IMAGE_BUFFER, "imageBuffer");
			put(GL42.GL_IMAGE_1D_ARRAY, "image1DArray");
			put(GL42.GL_IMAGE_2D_ARRAY, "image2DArray");
			put(GL42.GL_IMAGE_2D_MULTISAMPLE, "image2DMS");
			put(GL42.GL_IMAGE_2D_MULTISAMPLE_ARRAY, "image2DMSArray");
			put(GL42.GL_INT_IMAGE_1D, "iimage1D");
			put(GL42.GL_INT_IMAGE_2D, "iimage2D");
			put(GL42.GL_INT_IMAGE_3D, "iimage3D");
			put(GL42.GL_INT_IMAGE_2D_RECT, "iimage2DRect");
			put(GL42.GL_INT_IMAGE_CUBE, "iimageCube");
			put(GL42.GL_INT_IMAGE_BUFFER, "iimageBuffer");
			put(GL42.GL_INT_IMAGE_1D_ARRAY, "iimage1DArray");
			put(GL42.GL_INT_IMAGE_2D_ARRAY, "iimage2DArray");
			put(GL42.GL_INT_IMAGE_2D_MULTISAMPLE, "iimage2DMS");
			put(GL42.GL_INT_IMAGE_2D_MULTISAMPLE_ARRAY, "iimage2DMSArray");
			put(GL42.GL_UNSIGNED_INT_IMAGE_1D, "uimage1D");
			put(GL42.GL_UNSIGNED_INT_IMAGE_2D, "uimage2D");
			put(GL42.GL_UNSIGNED_INT_IMAGE_3D, "uimage3D");
			put(GL42.GL_UNSIGNED_INT_IMAGE_2D_RECT, "uimage2DRect");
			put(GL42.GL_UNSIGNED_INT_IMAGE_CUBE, "uimageCube");
			put(GL42.GL_UNSIGNED_INT_IMAGE_BUFFER, "uimageBuffer");
			put(GL42.GL_UNSIGNED_INT_IMAGE_1D_ARRAY, "uimage1DArray");
			put(GL42.GL_UNSIGNED_INT_IMAGE_2D_ARRAY, "uimage2DArray");
			put(GL42.GL_UNSIGNED_INT_IMAGE_2D_MULTISAMPLE, "uimage2DMS");
			put(GL42.GL_UNSIGNED_INT_IMAGE_2D_MULTISAMPLE_ARRAY, "uimage2DMSArray");
			put(GL42.GL_UNSIGNED_INT_ATOMIC_COUNTER, "atomic_uint");
		}
	};
	
	static
	{
		UNDELETED_IDS = new int[32];
		UNDELETED_LENGTH = 0;
	}
	
	/** Vertex program. */
	private OGLShaderProgram vertexProgram;
	/** Tessellation control program. */
	private OGLShaderProgram tessellationControlProgram;
	/** Tessellation evaluation program. */
	private OGLShaderProgram tessellationEvaluationProgram;
	/** Geometry program. */
	private OGLShaderProgram geometryProgram;
	/** Fragment program. */
	private OGLShaderProgram fragmentProgram;
	
	/* == After link == */
	
	/** The shader log. */
	private String log;
	/** Uniform location hash. */
	private Uniform[] uniformLocationList;
	/** Uniform hash. */
	private Map<String, Uniform> uniformMap;
	
	/**
	 * Creates a new Shader. 
	 * Each program can be null and is just left absent in the complete program.
	 * @param programs the programs to attach.
	 */
	OGLShader(OGLShaderProgram ... programs)
	{
		super();
		this.vertexProgram = null;
		this.tessellationControlProgram = null;
		this.tessellationEvaluationProgram = null;
		this.geometryProgram = null;
		this.fragmentProgram = null;

		// Get programs.
		for (OGLShaderProgram program : programs)
		{
			if (program != null) switch (program.getType())
			{
				case VERTEX:
					if (vertexProgram != null)
						throw new GraphicsException("Vertex program already provided.");
					else
						vertexProgram = program;
					break;
				case TESSELLATION_CONTROL:
					if (tessellationControlProgram != null)
						throw new GraphicsException("Tessellation control program already provided.");
					else
						tessellationControlProgram = program;
					break;
				case TESSELLATION_EVALUATION:
					if (tessellationEvaluationProgram != null)
						throw new GraphicsException("Tessellation evaluation program already provided.");
					else
						tessellationEvaluationProgram = program;
					break;
				case GEOMETRY:
					if (geometryProgram != null)
						throw new GraphicsException("Geometry program already provided.");
					else
						geometryProgram = program;
					break;
				case FRAGMENT:
					if (fragmentProgram != null)
						throw new GraphicsException("Fragment program already provided.");
					else
						fragmentProgram = program;
					break;
				case COMPUTE:
					throw new GraphicsException("Compute shaders are not created this way."); 
				default:
					throw new GraphicsException("Unexpected program type."); 
			}
		}
		
		if (vertexProgram == null 
			&& tessellationControlProgram == null 
			&& tessellationEvaluationProgram == null 
			&& geometryProgram == null
			&& fragmentProgram == null 
		)
			throw new GraphicsException("All provided programs are null!");
		
		// attach the programs.
		if (vertexProgram != null)
			glAttachShader(getName(), vertexProgram.getName());
		if (tessellationControlProgram != null)
			glAttachShader(getName(), tessellationControlProgram.getName());
		if (tessellationEvaluationProgram != null)
			glAttachShader(getName(), tessellationEvaluationProgram.getName());
		if (geometryProgram != null)
			glAttachShader(getName(), geometryProgram.getName());
		if (fragmentProgram != null)
			glAttachShader(getName(), fragmentProgram.getName());

		// link programs.
		glLinkProgram(getName());
	    this.log = glGetProgramInfoLog(getName());
	    if (glGetProgrami(getName(), GL_LINK_STATUS) == 0)
	    	throw new GraphicsException("Failed to link together program " + getName() + ".\n"+log);
		
	    refreshUniforms();
	}

	// Gets the uniform data.
	private void refreshUniforms()
	{
		int uniformCount = glGetProgrami(getName(), GL_ACTIVE_UNIFORMS);
		
		this.uniformLocationList = new Uniform[uniformCount];
		this.uniformMap = new HashMap<String, Uniform>(uniformCount, 1.0f);
		
		try (MemoryStack stack = MemoryStack.stackPush())
		{
			final int MAXBUFFER = 512;
			int[] length = new int[1];
			int[] size = new int[1];
			int[] type = new int[1];
			byte[] namebytes = new byte[MAXBUFFER];
			ByteBuffer name = stack.malloc(MAXBUFFER);
			
			for (int i = 0; i < uniformCount; i++)
			{
				glGetActiveUniform(getName(), i, length, size, type, name);

				name.rewind();
				name.get(namebytes, 0, length[0]);
				name.rewind();
				
				uniformLocationList[i] = new Uniform();
				uniformLocationList[i].locationId = i;
				uniformLocationList[i].length = length[0];
				uniformLocationList[i].name = new String(namebytes, 0, length[0]);
				uniformLocationList[i].size = size[0];
				uniformLocationList[i].type = type[0];
				uniformLocationList[i].typeName = TYPENAMES.get(type[0]);
				
				uniformMap.put(uniformLocationList[i].name, uniformLocationList[i]);
			}
		}
	}

	@Override
	protected int allocate()
	{
		return glCreateProgram();
	}

	@Override
	protected void free()
	{
		glDeleteProgram(getName());
	}
	
	/**
	 * @return the log from this program's linking.
	 */
	public String getLog()
	{
		return log;
	}
	
	/**
	 * @return the number of uniforms on this shader.
	 */
	public int getUniformCount()
	{
		return uniformLocationList.length;
	}

	/**
	 * Gets a {@link Uniform} by its location id.
	 * @param locationId the location id.
	 * @return the corresponding uniform or null if not found.
	 */
	public Uniform getUniform(int locationId)
	{
		if (locationId < 0 || locationId >= uniformLocationList.length)
			return null;
		return uniformLocationList[locationId];
	}
	
	/**
	 * Gets a {@link Uniform} by uniform name.
	 * @param name the uniform name.
	 * @return the corresponding uniform or null if not found.
	 */
	public Uniform getUniform(String name)
	{
		return uniformMap.get(name);
	}
	
	/**
	 * Destroys undeleted shader programs abandoned from destroyed Java objects.
	 */
	public static void destroyUndeleted()
	{
		if (UNDELETED_LENGTH > 0)
		{
			for (int i = 0; i < UNDELETED_LENGTH; i++)
				glDeleteProgram(UNDELETED_IDS[i]);
			UNDELETED_LENGTH = 0;
		}
	}

	// adds the OpenGL Id to the UNDELETED_IDS list.
	private static void finalizeAddId(int id)
	{
		if (UNDELETED_LENGTH == UNDELETED_IDS.length)
			UNDELETED_IDS = expand(UNDELETED_IDS, UNDELETED_IDS.length * 2);
		UNDELETED_IDS[UNDELETED_LENGTH++] = id;
	}
	
	@Override
	public void finalize() throws Throwable
	{
		if (isAllocated())
			finalizeAddId(getName());
		super.finalize();
	}
	
	/**
	 * Uniform for a shader. 
	 */
	public static class Uniform
	{
		/** Location id. */
		private int locationId;
		/** Uniform name. */
		private String name;
		/** Uniform character length. */
		private int length;
		/** Uniform size (in positions). */
		private int size;
		/** Uniform type (GL value). */
		private int type;
		/** Uniform type. */
		private String typeName;
		
		private Uniform() {}
		
		/** @return the uniform location id. */
		public int getLocationId() 
		{
			return locationId;
		}
		
		/** @return the uniform name. */
		public String getName() 
		{
			return name;
		}
		
		/** @return the length of the uniform. */
		public int getLength()
		{
			return length;
		}
		
		/** @return the size of the uniform. */
		public int getSize() 
		{
			return size;
		}
		
		/** @return the GL type id. */
		public int getType() 
		{
			return type;
		}
		
		/** @return the type name. */
		public String getTypeName() 
		{
			return typeName;
		}
		
		@Override
		public String toString() 
		{
			return "uniform "+typeName+" "+name+" (location "+locationId+")";
		}
		
	}

}

