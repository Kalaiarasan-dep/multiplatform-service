package in.hashconnect.executor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class CustomExecutorService {
	private ExecutorService executorService;

	public CustomExecutorService(int size) {
		executorService = Executors.newFixedThreadPool(size);
	}

	public void submit(Runnable runnable) {
		executorService.submit(runnable);
	}

	public void shutdown() {
		this.executorService.shutdownNow();
	}

	public void awaitTermination() {
		if (executorService == null || executorService.isTerminated())
			return;

		while (!executorService.isTerminated()) {
			try {
				if (executorService.awaitTermination(500, TimeUnit.MICROSECONDS)) {
					return;
				}
			} catch (InterruptedException e) {
			}
		}
	}
}
