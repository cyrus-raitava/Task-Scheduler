package se306group8.scheduleoptimizer.algorithm;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class GreedyChildScheduleFinder extends BasicChildScheduleFinder {

	public GreedyChildScheduleFinder(int processors) {
		super(processors);
		// TODO Auto-generated constructor stub
	}

	public List<TreeSchedule> getChildSchedules(TreeSchedule schedule) {
		Collection<TreeSchedule> unorderedSet = super.getChildSchedules(schedule);
		List<TreeSchedule> unorderedList = (List<TreeSchedule>)unorderedSet;
		List<TreeSchedule> orderedList = orderByLowerBound(unorderedList);
		return orderedList;
	}
	
	private List<TreeSchedule> orderByLowerBound(List<TreeSchedule> list) {
		Collections.sort(list);
		return list;
	}
	
}
