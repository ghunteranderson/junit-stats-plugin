package junitstats.mojo;

import java.io.File;
import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import junitstats.data.SurefireTestSource;
import junitstats.data.Test;
import junitstats.data.TestSuite;
import junitstats.stats.SampleSubset;
import junitstats.stats.Statistics;
import junitstats.stats.StatisticsCalculator;

@Mojo(name = "log", defaultPhase = LifecyclePhase.TEST)
public class LogStatsMojo extends AbstractMojo {

	@Parameter(defaultValue = "${project.build.directory}", property = "outputDir", required = true)
	private File buildDirectory;

	@Inject
	private SurefireTestSource testSource;
	@Inject
	private StatisticsCalculator statsCalculator;
	
	public void execute() throws MojoExecutionException, MojoFailureException {
		TestSuite suite = testSource.loadTests(buildDirectory);
		Statistics stats = statsCalculator.calculate(suite);
		System.out.println(stats);
		
		print("Unit Test Statistics");
		print();
		print("time:    %s", format(stats.getAllTests().getDuration()));
		print("average: %s", format(stats.getAllTests().getAverageDuration()));
		print("max:     %s", format(stats.getAllTests().getMax()));
		print();
		
		print("Slowest Tests");
		printTestSubset(stats.getSlowTests());
		print();
		
	}
	
	private void printTestSubset(SampleSubset<Test> subset) {
		print("count:    %4.1f%% (%s tests)", subset.getPercentByCount(), subset.getSamples().size());
		print("duration  %4.1f%% (%s)", subset.getPercentByDuration(), format(subset.getDuration()));
		int limit = 20;
		subset.getSamples().stream().limit(limit).forEach(t ->
			print("    %s %s (%s)", format(t.getDuration()), t.getTestName(), t.getParent().getClassName())
		);
		if(subset.getSamples().size() > limit)
			print("    ... and " + (subset.getSamples().size() - limit) + " more");
	}
	
	private void print(String format, Object...args) {
		getLog().info(String.format(format, args));
	}
	
	private void print() {
		print("");
	}
	
	
	private String format(long duration) {
		long millis = duration;
		long hours = TimeUnit.HOURS.convert(millis , TimeUnit.MILLISECONDS);
		millis -= TimeUnit.HOURS.toMillis(hours);
		long minutes = TimeUnit.MINUTES.convert(millis , TimeUnit.MILLISECONDS);
		millis -= TimeUnit.MINUTES.toMillis(minutes);
		long seconds = TimeUnit.SECONDS.convert(millis, TimeUnit.MILLISECONDS);
		millis -= TimeUnit.SECONDS.toMillis(seconds);
		
		StringBuilder bob = new StringBuilder();
		boolean started = false;
		
		if(hours > 0) {
			started = true;
			bob.append(hours).append("h");
		}
		
		if(started || minutes > 0) {
			if(started)
				bob.append(" ");
			else
				started = true;
			bob.append(minutes).append("m");
		}
		
		if(started)
			bob.append(" ");
		
		bob.append(new BigDecimal(seconds)
				.add(new BigDecimal(millis).movePointLeft(3))
				.setScale(3)
				.toString())
			.append("s");
		
		return bob.toString();
	}
	
}
