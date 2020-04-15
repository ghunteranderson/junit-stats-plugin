package junitstats.data;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class TestPackage implements TestSet {
	@ToString.Include
	@EqualsAndHashCode.Include
	private String packageName;
	@ToString.Include
	@EqualsAndHashCode.Include
	private long duration;
	@ToString.Include
	@EqualsAndHashCode.Include
	private final List<TestClass> classes = new ArrayList<>();
	private int testCount;
	private TestSuite parent;
}