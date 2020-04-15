package junitstats.stats;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import junitstats.data.TestSet;
import lombok.Data;
import lombok.ToString;

@Data
public class SampleSet<T extends TestSet> {
	@ToString.Exclude
	private List<T> samples;
	private long duration;
	private long averageDuration;
	private long min;
	private long max;
	private long standardDeviation;

	public SampleSet(List<T> source) {
		this.samples = Collections.unmodifiableList(source);
		if(samples.isEmpty())
			return;
		
		List<Long> durations = source
				.stream()
				.map(TestSet::getDuration)
				.sorted(Long::compare)
				.collect(Collectors.toList());
		
		this.min = durations.get(0);
		this.max = durations.get(durations.size()-1);
		
		this.duration = source.stream().mapToLong(TestSet::getDuration).sum();
		this.averageDuration = duration / source.size();
		
		double sumOfSquaredDeviations = durations
				.stream()
				.map(x -> Math.pow(x-(double)averageDuration, 2))
				.reduce((a, b) -> a+b).orElse(0.0);
		
		this.standardDeviation = (long)Math.sqrt(sumOfSquaredDeviations / durations.size());
	}
	
}
