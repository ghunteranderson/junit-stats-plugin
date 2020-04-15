package junitstats.stats;

import java.util.List;

import junitstats.data.TestSet;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class SampleSubset<T extends TestSet> extends SampleSet<T> {
	
	private double percentByCount;
	private double percentByDuration;

	public SampleSubset(List<T> subset, SampleSet<T> parent) {
		super(subset);
		this.percentByCount = this.getSamples().size() / (double) parent.getSamples().size() * 100;
		this.percentByDuration = this.getDuration() / (double) parent.getDuration() * 100;
	}

	
}
