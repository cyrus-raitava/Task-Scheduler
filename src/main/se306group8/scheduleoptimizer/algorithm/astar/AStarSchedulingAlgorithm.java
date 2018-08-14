package se306group8.scheduleoptimizer.algorithm.astar;

import java.util.List;

import se306group8.scheduleoptimizer.algorithm.Algorithm;
import se306group8.scheduleoptimizer.algorithm.RuntimeMonitor;
import se306group8.scheduleoptimizer.algorithm.TreeSchedule;
import se306group8.scheduleoptimizer.algorithm.childfinder.ChildScheduleFinder;
import se306group8.scheduleoptimizer.algorithm.childfinder.GreedyChildScheduleFinder;
import se306group8.scheduleoptimizer.algorithm.heuristic.MinimumHeuristic;
import se306group8.scheduleoptimizer.algorithm.storage.BlockScheduleStorage;
import se306group8.scheduleoptimizer.algorithm.storage.ScheduleStorage;
import se306group8.scheduleoptimizer.taskgraph.Schedule;
import se306group8.scheduleoptimizer.taskgraph.TaskGraph;

public class AStarSchedulingAlgorithm extends Algorithm {
	
	private final ChildScheduleFinder childGenerator;
	private final MinimumHeuristic heuristic;
	private final ScheduleStorage queue;
	private int explored = 0;
	
	public AStarSchedulingAlgorithm(ChildScheduleFinder childGenerator, MinimumHeuristic heuristic, RuntimeMonitor monitor, ScheduleStorage storage) {
		super(monitor);
		
		this.childGenerator = childGenerator;
		this.heuristic = heuristic;
		this.queue = storage;
	}

	public AStarSchedulingAlgorithm(ChildScheduleFinder childGenerator, MinimumHeuristic heuristic) {
		super();
		
		this.childGenerator = childGenerator;
		this.heuristic = heuristic;
		this.queue = new BlockScheduleStorage();
	}

	@Override
	public Schedule produceCompleteScheduleHook(TaskGraph graph, int numberOfProcessors) throws InterruptedException {
		TreeSchedule best = new TreeSchedule(graph, heuristic, numberOfProcessors);
		queue.signalStorageSizes(getMonitor());
		
		GreedyChildScheduleFinder greedyFinder = new GreedyChildScheduleFinder(numberOfProcessors);
		
		TreeSchedule greedySoln = best;
		while (!greedySoln.isComplete()) {
			greedySoln = greedyFinder.getChildSchedules(greedySoln).get(0);
		}

		queue.put(greedySoln);
		
		while (!best.isComplete()) {
			explore(best);
			best = queue.pop();
			
			getMonitor().setSchedulesExplored(explored);
			queue.signalMonitor(getMonitor());

			if(Thread.interrupted()) {
				throw new InterruptedException();
			}
		}
		
		return best.getFullSchedule();
	}

	TreeSchedule explore(TreeSchedule best) {
		if(best.isComplete()) {
			queue.put(best);
			return best;
		}
		
		List<TreeSchedule> children = childGenerator.getChildSchedules(best);
		
		for(TreeSchedule child : children) {
			explored++;
			
			if(child.getLowerBound() == best.getLowerBound()) {				
				TreeSchedule s = explore(child);
				
				if(s != null) {
					return s;
				}
			} else {
				queue.put(child);
			}
		}
		
		return null;
	}
}
