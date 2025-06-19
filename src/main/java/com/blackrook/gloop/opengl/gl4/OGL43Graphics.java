/*******************************************************************************
 * Copyright (c) 2021-2024 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at 
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gloop.opengl.gl4;

import com.blackrook.gloop.opengl.OGLVersion;
import com.blackrook.gloop.opengl.enums.DebugSeverity;
import com.blackrook.gloop.opengl.enums.DebugControlSeverity;
import com.blackrook.gloop.opengl.enums.DebugControlSource;
import com.blackrook.gloop.opengl.enums.DebugControlType;
import com.blackrook.gloop.opengl.enums.DebugMessageSource;
import com.blackrook.gloop.opengl.enums.DebugSource;
import com.blackrook.gloop.opengl.enums.DebugType;
import com.blackrook.gloop.opengl.OGLSystem.Options;

import static org.lwjgl.opengl.GL43.*;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Iterator;

import org.lwjgl.opengl.GL43;
import org.lwjgl.system.MemoryStack;


/**
 * OpenGL 4.3 Graphics Implementation.
 * @author Matthew Tropiano
 */
public class OGL43Graphics extends OGL42Graphics
{
	private static final int[] NO_IDS = new int[]{};
	
	protected class Info43 extends Info42
	{
		protected Info43()
		{
			super();
			this.maxComputeUniformBlocks = getInt(GL_MAX_COMPUTE_UNIFORM_BLOCKS);
			this.maxComputeTextureImageUnits = getInt(GL_MAX_COMPUTE_TEXTURE_IMAGE_UNITS);
			this.maxComputeImageUniforms = getInt(GL_MAX_COMPUTE_IMAGE_UNIFORMS);
			this.maxComputeSharedMemorySize = getInt(GL_MAX_COMPUTE_SHARED_MEMORY_SIZE);
			this.maxComputeUniformComponents = getInt(GL_MAX_COMPUTE_UNIFORM_BLOCKS);
			this.maxComputeAtomicCounterBuffers = getInt(GL_MAX_COMPUTE_ATOMIC_COUNTER_BUFFERS);
			this.maxComputeAtomicCounters = getInt(GL_MAX_COMPUTE_ATOMIC_COUNTERS);
			this.maxCombinedComputeUniformComponents = getInt(GL_MAX_COMBINED_COMPUTE_UNIFORM_COMPONENTS);
			this.maxComputeWorkGroupInvocations = getInt(GL_MAX_COMPUTE_WORK_GROUP_INVOCATIONS);
			this.maxDebugMessageLength = getInt(GL_MAX_DEBUG_MESSAGE_LENGTH);
			this.maxDebugLoggedMessages = getInt(GL_MAX_DEBUG_LOGGED_MESSAGES);
			this.maxDebugGroupStackDepth = getInt(GL_MAX_DEBUG_GROUP_STACK_DEPTH);
			this.maxLabelLength = getInt(GL_MAX_LABEL_LENGTH);
			this.maxVertexShaderStorageBlocks = getInt(GL_MAX_VERTEX_SHADER_STORAGE_BLOCKS);
			this.maxGeometryShaderStorageBlocks = getInt(GL_MAX_GEOMETRY_SHADER_STORAGE_BLOCKS);
			this.maxTesselationControlShaderStorageBlocks = getInt(GL_MAX_TESS_CONTROL_SHADER_STORAGE_BLOCKS);
			this.maxTesselationEvaluationShaderStorageBlocks = getInt(GL_MAX_TESS_EVALUATION_SHADER_STORAGE_BLOCKS);
			this.maxFragmentShaderStorageBlocks = getInt(GL_MAX_FRAGMENT_SHADER_STORAGE_BLOCKS);
			this.maxComputeShaderStorageBlocks = getInt(GL_MAX_COMPUTE_SHADER_STORAGE_BLOCKS);
			this.maxCombinedShaderStorageBlocks = getInt(GL_MAX_COMBINED_SHADER_STORAGE_BLOCKS);
			this.maxShaderStorageBufferBindings = getInt(GL_MAX_SHADER_STORAGE_BUFFER_BINDINGS);
			this.maxShaderStorageBlockSize = getInt(GL_MAX_SHADER_STORAGE_BLOCK_SIZE);
			this.shaderStorageBufferOffsetAlignment = getInt(GL_SHADER_STORAGE_BUFFER_OFFSET_ALIGNMENT);
		}
	}
	
	public OGL43Graphics(Options options, boolean core)
	{
		super(options, core);
	}

	@Override
	public OGLVersion getVersion()
	{
		return OGLVersion.GL43;
	}

	@Override
	protected Info createInfo()
	{
		return new Info43();
	}
	
	/**
	 * Sets if debug messages are enabled on this context.
	 * @param enabled true if so, false if not.
	 */
	public void setDebugMessagesEnabled(boolean enabled)
	{
		setFlag(GL43.GL_DEBUG_OUTPUT, enabled);
	}
	
	/**
	 * Sets if debug message output is synchronous on this context.
	 * @param enabled true if so, false if not.
	 */
	public void setDebugMessagesSychronous(boolean enabled)
	{
		setFlag(GL43.GL_DEBUG_OUTPUT_SYNCHRONOUS, enabled);
	}
	
	/**
	 * Inserts a debug message into the command list.
	 * @param source the source of the message.
	 * @param type the type of message.
	 * @param id a user-supplied id number. 
	 * @param severity the message severity level.
	 * @param message the message.
	 */
	public void insertDebugMessage(DebugSource source, DebugType type, int id, DebugSeverity severity, String message)
	{
		glDebugMessageInsert(source.glValue, type.glValue, id, severity.glValue, message);
		checkError();
	}
	
	/**
	 * Sets if a set of debug messages get filtered out.
	 * @param source the control source ({@link DebugControlSource#DONT_CARE} for no filter).
	 * @param type the control type ({@link DebugControlType#DONT_CARE} for no filter).
	 * @param severity the control severity ({@link DebugControlSeverity#DONT_CARE} for no filter).
	 * @param ids the list of ids to filter (can be null for no ids - if not null, source and type CANNOT be DONT_CARE).
	 * @param enabled true to enable, false to disable.
	 */
	public void setDebugMessageControl(DebugControlSource source, DebugControlType type, DebugControlSeverity severity, int[] ids, boolean enabled)
	{
		glDebugMessageControl(source.glValue, type.glValue, severity.glValue, ids == null ? NO_IDS : ids, enabled);
		checkError();
	}
	
	/**
	 * Retrieves a debug message log, up to a specific count of messages.
	 * @param count the amount of messages to potentially retrieve.
	 * @param bufferLength the length of the buffer for retrieving the messages. If not enough space, the
	 *     amount of messages retrieved may be less than count.
	 * @return a message log response from the call.
	 */
	public DebugMessageLog getDebugMessageLog(int count, int bufferLength)
	{
		int[] sources = new int[count];
		int[] types = new int[count];
		int[] ids = new int[count];
		int[] severities = new int[count];
		int[] lengths = new int[count];
		byte[] messageData = new byte[bufferLength];
		
		int messages;
		try (MemoryStack stack = MemoryStack.stackPush())
		{
			ByteBuffer buffer = stack.malloc(bufferLength); 
			messages = glGetDebugMessageLog(count, sources, types, ids, severities, lengths, buffer);
			checkError();
			buffer.get(messageData);
		}
		
		DebugMessageLog out = new DebugMessageLog(messages);
		int lengthAccum = 0;
		for (int i = 0; i < messages; i++)
		{
			String messageContent = new String(messageData, lengthAccum, lengths[i] - 1); // - 1 to omit null 
			out.messages[i] = new DebugMessageLog.Message(
				DebugMessageSource.getByGLValue(sources[i]),
				DebugType.getByGLValue(types[i]),
				DebugSeverity.getByGLValue(severities[i]),
				ids[i],
				messageContent
			);
			lengthAccum += lengths[i];
		}
		return out;
	}
	
	// TODO: Finish.

	/**
	 * A representation of the response from a call to {@link OGL43Graphics#getDebugMessageLog(int, int)}.
	 */
	public static class DebugMessageLog implements Iterable<DebugMessageLog.Message>
	{
		private Message[] messages;
		
		private DebugMessageLog(int count)
		{
			this.messages = new Message[count];
		}
		
		/**
		 * @return the amount of messages this contains.
		 */
		public int count()
		{
			return messages.length;
		}

		@Override
		public Iterator<Message> iterator()
		{
			return Arrays.asList(messages).iterator();
		}

		/**
		 * A single debug message.
		 */
		public static class Message
		{
			private DebugMessageSource source;
			private DebugType type;
			private DebugSeverity severity;
			private int id;
			private String content;
			
			private Message(DebugMessageSource source, DebugType type, DebugSeverity severity, int id, String content)
			{
				this.source = source;
				this.type = type;
				this.severity = severity;
				this.id = id;
				this.content = content;
			}

			/**
			 * @return this message's source.
			 */
			public DebugMessageSource getSource() 
			{
				return source;
			}

			/**
			 * @return this message's type.
			 */
			public DebugType getType() 
			{
				return type;
			}

			/**
			 * @return this message's severity level.
			 */
			public DebugSeverity getSeverity() 
			{
				return severity;
			}

			/**
			 * @return this message's id.
			 */
			public int getId() 
			{
				return id;
			}
			
			/**
			 * @return this message's content as a string.
			 */
			public String getContent() 
			{
				return content;
			}
			
			@Override
			public String toString() 
			{
				StringBuilder sb = new StringBuilder();
				sb.append(source.name()).append(" ");
				sb.append(type.name()).append(" ");
				sb.append(severity.name()).append(" ");
				sb.append('(').append(id).append(')').append(" ");
				sb.append(content);
				return sb.toString();
			}
			
		}
		
	}

}
