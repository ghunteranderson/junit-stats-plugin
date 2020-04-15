package junitstats.stats;

import junitstats.data.Test;
import lombok.Data;

@Data
public class Statistics {
	private SampleSet<Test> allTests;
	private SampleSubset<Test> slowTests;
}
