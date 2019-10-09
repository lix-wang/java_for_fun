## ThreadPoolExecutor 执行流程分析

&emsp;&emsp; 我采用了如下方式创建线程池：
new ThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE_TIME, TIME_UNIT, new LinkedBlockingQueue())。
这里实际上LinkedBlockingQueue，设置了容量为Integer.MAX_VALUE。我通常使用如下方式执行多线程任务：submit(Callable<T> task)。
<br>
&emsp;&emsp; 通过查看submit方法，发现，该方法首先通过new FutureTask<T>(callable) 将 Callable<T> 转换为返回Future<T> 的Runnable任务。
然后通过execute(Runnable)方法，执行该Runnable任务。

<br>
&emsp;&emsp; execute方法，分3步来处理任务，第一步，首先，如果当前线程池中的线程数 < corePoolSize，那么首先会调用addWorker(Runnable, true)创建线程，
首先会增加线程池线程数量，然后new Worker(Runnable)创建工作线程。然后把该worker，添加到当前线程池的worker列表中。此时创建的worker的，首个任务就是当前任务。
然后调用thread.start()方法，执行当前的Worker。此时execute方法结束。第二步，如果没有创建新的Worker，那么将会把当前的任务放入到任务队列workQueue中，
如果能成功放入到任务队列，那么还需要再次确认，是否需要addWorker(command) 或者 reject(command)。第三步，如果没有创建Worker，
并且不能把任务放入到任务队列，那么我们需要再次检查是否能够添加Worker，如果不能那么说明，当前的线程池已经关闭，或者线程池处于饱和状态，此时reject(command)。

<br>
&emsp;&emsp; Worker线程在执行时，采用循环处理任务的方式，如果获取到任务，那么执行任务，执行完任务后，调用processWorkerExit(Worker, boolean)来处理Worker的退出。
如果发生异常，首先更新worker数量。接下来移除worker，尝试停止线程池，正常运行的线程池会忽略这个方法，如果线程池没被关闭，那么获取线程池最小数量。
如果当前线程池数量 >= 最小数量，那么结束当前线程，否则，利用addWorker创建一个新的线程。如果设置了allowCoreThreadTimeOut，
那么在一定时间coreThread没有任务，那么也会退出。

<br>
&emsp;&emsp; 在拉取任务时，采用了无限for循环的方式，如果 allowCoreThreadTimeOut || workerCount > corePoolSize，那么此时在workQueue任务队列中，
使用workQueue.poll(keepAliveTime, TimeUnit.NANOSECONDS)拉取任务，如果超过规定的时间还没拉取到任务，那么超时标识为true，此时会递减workerCount，
并且结束当前线程的任务拉取。Worker线程结束拉取任务，那么就会执行processWorkerExit(Worker, boolean)，执行当前线程的退出。