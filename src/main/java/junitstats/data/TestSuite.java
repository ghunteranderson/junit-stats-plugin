package junitstats.data;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class TestSuite implements TestSet {
	@ToString.Include
	@EqualsAndHashCode.Include
	private long duration;
	@ToString.Include
	@EqualsAndHashCode.Include
	private final List<TestPackage> packages = new ArrayList<>();
	private int testCount;
}
