package crawler;
import java.util.concurrent.Callable;

public class Task implements Callable<Object>{
	
	private CrawlingTask task;
	
	public Task(CrawlingTask task){
		this.task = task;
	}
	
	public Object call(){
		task.run();
		return new Object();
	}
}
