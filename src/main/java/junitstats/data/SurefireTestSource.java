package junitstats.data;

import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.maven.plugin.MojoFailureException;

import junitstats.xml.Testsuite;
import junitstats.xml.Testsuite.Testcase;

public class SurefireTestSource {

	public TestSuite loadTests(File buildDirectory) throws MojoFailureException {
		
		TestSuite suite = new TestSuite();

		loadFromXml(buildDirectory)
			.forEach(t -> {
				Test test = new Test();
				test.setDuration(new BigDecimal(t.getTime()).movePointRight(3).longValue());
				
				test.setTestName(t.getName());
				
				TestClass testClass = findTestClass(suite, t.getClassname());
				test.setParent(testClass);
				testClass.getTests().add(test);
			});
		
		// Now we need to go back and populate durations and counts
		
		long suiteDuration = 0;
		int suiteTestCount = 0;
		
		for(TestPackage p : suite.getPackages()) {
			long packgeDuration = 0;
			int packageTestCount = 0;
			
			for(TestClass c : p.getClasses()) {
				long classDuration = 0;
				long classTestCount = 0;
				
				for(Test t : c.getTests())
					classDuration += t.getDuration();
				classTestCount = c.getTests().size();
				
				c.setDuration(classDuration);
				c.setTestCount(c.getTests().size());
				packgeDuration += classDuration;
				packageTestCount += classTestCount;
			}
			
			p.setDuration(packgeDuration);
			p.setTestCount(packageTestCount);
			suiteDuration += packgeDuration;
			suiteTestCount += packageTestCount;
		}
		
		suite.setDuration(suiteDuration);
		suite.setTestCount(suiteTestCount);
		
		
		return suite;
	}
	
	private TestClass findTestClass(TestSuite suite, String className) {
		return suite.getPackages().stream()
			// From all classes
			.flatMap(p -> p.getClasses().stream())
			// Find where class name matches
			.filter(c -> c.getClassName().equals(className))
			.findAny()
			// Or else build a new test class
			.orElseGet(() -> {
				TestClass newClass = new TestClass();
				newClass.setClassName(className);
				
				String packageName = "";
				int lastDot = className.indexOf('.');
				if(lastDot >= 0)
					packageName = className.substring(0, lastDot);
				
				TestPackage testPackage = findTestPackage(suite, packageName);
				newClass.setParent(testPackage);
				testPackage.getClasses().add(newClass);
				
				return newClass;
			});
	}
	
	private TestPackage findTestPackage(TestSuite suite, String packageName) {
		return suite.getPackages().stream()
				.filter(p -> p.getPackageName().equals(packageName))
				.findAny()
				.orElseGet(() -> {
					TestPackage newPackage = new TestPackage();
					newPackage.setPackageName(packageName);
					newPackage.setParent(suite);
					suite.getPackages().add(newPackage);
					return newPackage;
				});
		
	}
	
	private Stream<Testcase> loadFromXml(File buildDirectory) throws MojoFailureException{
		File reportDirectory = Paths.get(buildDirectory.getAbsolutePath(), "surefire-reports").toFile();
		Unmarshaller unmarshaller;
		try {
			JAXBContext context = JAXBContext.newInstance(Testsuite.class);
			unmarshaller = context.createUnmarshaller();
		} catch(JAXBException ex) {
			throw new MojoFailureException("Could not create JAXB unmarshaller.", ex);
		}
		
		return Arrays.asList(reportDirectory.listFiles((dir, name) -> name.endsWith(".xml")))
				.stream()
				.flatMap(f -> {
					try {
						Testsuite suite = (Testsuite) unmarshaller.unmarshal(f);
						return suite.getTestcase().stream();					
					} catch(JAXBException ex) {
						// TODO: Log exception as skipped report
						List<Testcase> empty = Collections.emptyList();
						return empty.stream();
					}
				});
	}
	

}
