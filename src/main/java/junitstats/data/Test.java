package junitstats.data;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Test implements TestSet {
	@ToString.Include
	@EqualsAndHashCode.Include
	private String testName;
	@ToString.Include
	@EqualsAndHashCode.Include
	private long duration;
	private TestClass parent;
	
	@Override
	public int getTestCount() {
		return 1;
	}
}
