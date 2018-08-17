package se306group8.scheduleoptimizer.visualisation;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.scene.chart.XYChart.Data;
import se306group8.scheduleoptimizer.algorithm.RuntimeMonitor;
import se306group8.scheduleoptimizer.algorithm.TreeSchedule;
import se306group8.scheduleoptimizer.taskgraph.Schedule;

public class ObservableRuntimeMonitor implements RuntimeMonitor, Observable {

	private volatile boolean started;
	private volatile boolean finished;
	private volatile TreeSchedule bestSchedule;
	private volatile Queue<String> messages;

	private volatile int schedulesExplored;
	private volatile int schedulesInArray;
	private volatile int scheduleInArrayStorageSize;
	private volatile int schedulesInQueue;
	private volatile int scheduleInQueueStorageSize;
	private volatile int schedulesOnDisk;
	private volatile int scheduleOnDiskStorageSize;

	private volatile String algorithmName;
	
	private volatile int numberOfProcessors;
	
	//Used for the histogram
	private volatile int[] histogramData = new int[0];
	private volatile int slots = 0;
	private volatile int granularity = 0;
	
	private final List<InvalidationListener> listeners;
	
	public ObservableRuntimeMonitor() {

		started = false;
		finished = false;
		bestSchedule = null;
		messages = new LinkedBlockingQueue<>();

		schedulesExplored = 0;
		schedulesInArray = 0;
		scheduleInArrayStorageSize = 0;
		schedulesInQueue = 0;
		scheduleInQueueStorageSize = 0;
		schedulesOnDisk = 0;
		scheduleOnDiskStorageSize = 0;
		
		numberOfProcessors = 0;

		listeners = new ArrayList<>();
	}

	private void invalidateListeners() {
		Platform.runLater(() -> {
			for (InvalidationListener listener : listeners) {
				listener.invalidated(this);
			}
		});
	}

	@Override
	public void updateBestSchedule(TreeSchedule optimalSchedule) {
		bestSchedule = optimalSchedule;
	}

	@Override
	public void start() {
		started = true;
		invalidateListeners();
	}

	@Override
	public void finish(Schedule solution) {
		finished = true;
		invalidateListeners();
	}

	@Override
	public void logMessage(String message) {
		LocalDateTime now = LocalDateTime.now();
		messages.add("["+new SimpleDateFormat("HH:mm:ss").format(new Date())+"]: "+message);
		invalidateListeners();
	}

	@Override
	public void setSchedulesExplored(int number) {
		schedulesExplored = number;
	}

	public int getSchedulesExplored() {
		return schedulesExplored;
	}

	@Override
	public void setSchedulesInArray(int number) {
		schedulesInArray = number;
	}

	public int getSchedulesInArray() {
		return schedulesInArray;
	}

	@Override
	public void setScheduleInArrayStorageSize(int bytes) {
		scheduleInArrayStorageSize = bytes;
	}

	public int getScheduleInArrayStorageSize() {
		return scheduleInArrayStorageSize;
	}

	@Override
	public void setSchedulesInQueue(int number) {
		schedulesInQueue = number;
	}

	public int getSchedulesInQueue() {
		return schedulesInQueue;
	}

	@Override
	public void setScheduleInQueueStorageSize(int bytes) {
		scheduleInQueueStorageSize = bytes;
	}

	public int getScheduleInQueueStorageSize() {
		return scheduleInQueueStorageSize;
	}

	@Override
	public void setSchedulesOnDisk(int number) {
		schedulesOnDisk = number;
	}

	public int getSchedulesOnDisk() {
		return schedulesOnDisk;
	}
	
	@Override
	public void setScheduleOnDiskStorageSize(int bytes) {
		scheduleOnDiskStorageSize = bytes;
	}

	public int getScheduleOnDiskStorageSize() {
		return scheduleOnDiskStorageSize;
	}

	public boolean hasStarted() {
		return started;
	}

	public boolean hasFinished() {
		return finished;
	}

	public String nextMessage() {
		return messages.poll();
	}

	public TreeSchedule getBestSchedule() {
		return bestSchedule;
	}
	
	public int getNumberOfProcessors() {
		return numberOfProcessors;
	}
	
	public void setNumberOfProcessors(int processors) {
		numberOfProcessors = processors;
	}

	@Override
	public void setAlgorithmName(String name) {
		algorithmName = name;
	}

	@Override
	public void setParallelized(int cores) {

	}

	@Override
	public void addListener(InvalidationListener listener) {
		listeners.add(listener);
	}

	@Override
	public void setScheduleDistribution(int[] histogramData, int limit) {
		this.histogramData = histogramData;
		slots = limit;
	}
	
	@Override
	public void removeListener(InvalidationListener listener) {
		listeners.remove(listener);
	}

	public Collection<Data<String, Number>> getHistogramData() {
		Collection<Data<String, Number>> col = new ArrayList<>();
		
		boolean added = false;
		for(int i = 0; i < slots; i++) {
			if(added || histogramData[i] != 0) {
				added = true;
				col.add(new Data<>(getName(i), histogramData[i]));
			}
		}
		
		return col;
	}

	@Override
	public void setBucketSize(int granularity) {
		this.granularity = granularity;
	}
	
	private String getName(int i) {
		if(granularity == 1) {
			return Integer.toString(i);
		} else {
			return Integer.toString(i * granularity) + " - " + Integer.toString((i + 1) * granularity - 1);
		}
	}
}
