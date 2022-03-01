package com.blackrook.gloop.opengl.node;

import java.util.Deque;
import java.util.LinkedList;
import java.util.function.Consumer;

import com.blackrook.gloop.opengl.OGLGraphics;

/**
 * An OpenGL node that contains a set of jobs ({@link Consumer}s) for executing on
 * this node's parent system's thread, the idea being that other non-context threads 
 * may want to execute OpenGL commands somewhere in the pipeline, such as prepping
 * shader programs or loading/assigning texture objects.
 * <p> The enables setting up things to run at the place that this node is
 * added to the system.
 * <p> All public non-OGLNode methods are thread-safe unless otherwise specified.
 * <p> If the queue is empty, this node does nothing.
 * <p> You can guarantee that the following methods will be executed in order by the same thread:
 * <ul>
 * <li>{@link #isWorkAvailable()}</li>
 * <li>{@link #beforeExecute(Consumer)}</li>
 * <li>{@link Consumer#accept(Object)}</li>
 * <li>{@link #afterExecute(Consumer)}</li>
 * </ul>
 * @param <GL> the graphics object to call.
 * @param <J> the "job" type. Must be of type Consumer<GL>.
 * @author Matthew Tropiano
 */
public class OGLWorkerNode<GL extends OGLGraphics, J extends Consumer<GL>> implements OGLNode<GL>
{
	/** The work queue. */
	private Deque<J> workQueue;
	
	/** Time elapsed. */
	private long renderTimeNanos;
	
	/**
	 * Creates a new worker node, with no jobs in its queue.
	 */
	public OGLWorkerNode()
	{
		this.workQueue = new LinkedList<>();
		this.renderTimeNanos = 0L;
	}
	
	@Override
	public final void onDisplay(GL gl)
	{
		long startNanos = System.nanoTime();
		while (isWorkAvailable())
		{
			J job;
			synchronized (workQueue)
			{
				job = workQueue.pollFirst();
			}
			if (job != null)
			{
				beforeExecute(job);
				job.accept(gl);
				afterExecute(job);
			}
		}
		renderTimeNanos = System.nanoTime() - startNanos;
	}
	
	@Override
	public long getRenderTimeNanos()
	{
		return renderTimeNanos;
	}
	
	/**
	 * Enqueues an actionable job in the work queue. 
	 * @param job the job to execute.
	 */
	public final void enqueueJob(J job)
	{
		synchronized (workQueue)
		{
			workQueue.add(job);
		}
	}
	
	/**
	 * Checks if the work queue is empty.
	 * @return true if so, false if not.
	 */
	public final boolean isQueueEmpty()
	{
		synchronized (workQueue)
		{
			return workQueue.isEmpty();
		}
	}
	
	/**
	 * Called by {@link #onDisplay(OGLGraphics)} for determining if there's
	 * work available for processing. 
	 * <P> By default, this just checks if the work queue is not empty.
	 * <p> If this returns false, this node completes, else a job is dequeued and executed.
	 * <p> Do NOT call this method outside of the main context thread.
	 * @return true if work is available, false if not.
	 * @see #isQueueEmpty()
	 */
	protected boolean isWorkAvailable()
	{
		return !isQueueEmpty();
	}

	/**
	 * Called by {@link #onDisplay(OGLGraphics)} when the job is dequeued, but before it is executed.
	 * <p> Do NOT call this method outside of the main context thread.
	 * @param job the dequeued job.
	 */
	protected void beforeExecute(J job)
	{
		// Do nothing by default.
	}
	
	/**
	 * Called by {@link #onDisplay(OGLGraphics)} after the job is executed.
	 * <p> Do NOT call this method outside of the main context thread.
	 * @param job the executed job.
	 */
	protected void afterExecute(J job)
	{
		// Do nothing by default.
	}
	
}
