package junitstats.stats;

import java.util.List;
import java.util.stream.Collectors;

import junitstats.data.TestSet;
import junitstats.data.Test;
import junitstats.data.TestClass;
import junitstats.data.TestPackage;
import junitstats.data.TestSuite;

public class StatisticsCalculator {

	public Statistics calculate(TestSuite suite) {
		
		List<TestPackage> packages = suite.getPackages();
		
		List<TestClass> classes = packages.stream()
				.flatMap(p -> p.getClasses().stream())
				.collect(Collectors.toList());
		
		List<Test> tests = classes.stream()
				.flatMap(c -> c.getTests().stream())
				.collect(Collectors.toList());
		
		Statistics stats = new Statistics();
		stats.setAllTests(new SampleSet<Test>(tests));
		stats.setSlowTests(buildSlowSubset(stats.getAllTests()));
		
		return stats;
	}
	
	private <T extends TestSet> SampleSubset<T> buildSlowSubset(SampleSet<T> parent){
		long stdDevThreshold = parent.getStandardDeviation() * 2;
		
		List<T> slowSamples = parent.getSamples().stream()
				.filter(t -> {
					long threshold = Math.max(stdDevThreshold, t.getTestCount() * 10L /*ms*/);
					return t.getDuration() >= threshold;
				})
				.sorted((s1, s2) -> Long.compare(s1.getDuration(), s2.getDuration())*-1)
				.collect(Collectors.toList());
		
		return new SampleSubset<>(slowSamples, parent);
	}

}
