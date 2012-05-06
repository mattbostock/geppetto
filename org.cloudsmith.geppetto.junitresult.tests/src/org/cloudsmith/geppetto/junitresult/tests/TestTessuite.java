/**
 * Copyright (c) 2012 Cloudsmith Inc. and other contributors, as listed below.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *   Cloudsmith
 * 
 */
package org.cloudsmith.geppetto.junitresult.tests;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import junit.framework.TestCase;

import org.cloudsmith.geppetto.junitresult.Failure;
import org.cloudsmith.geppetto.junitresult.JunitResult;
import org.cloudsmith.geppetto.junitresult.Property;
import org.cloudsmith.geppetto.junitresult.Testcase;
import org.cloudsmith.geppetto.junitresult.Testsuite;
import org.cloudsmith.geppetto.junitresult.util.JunitresultLoader;
import org.eclipse.core.runtime.Path;

/**
 * Tests the testsuite form of junit result.
 * 
 */
public class TestTessuite extends TestCase {

	public void testLoad_Bougie_testsuite() throws IOException {
		File f = TestDataProvider.getTestFile(new Path("testData/BougieTest_testsuite.xml"));
		JunitResult result = JunitresultLoader.loadFromXML(f);

		assertTrue("should be a Testsuite instance", result instanceof Testsuite);
		Testsuite rootsuite = (Testsuite) result;
		assertEquals("net.cars.engine.BougieTest", rootsuite.getName());
		assertEquals(2, rootsuite.getTests());
		assertEquals(0, rootsuite.getFailures());
		assertEquals(1, rootsuite.getErrors());
		assertEquals("hazelnut.osuosl.org", rootsuite.getHostname());
		Calendar calendar = javax.xml.bind.DatatypeConverter.parseDateTime("2007-11-02T23:13:50");
		Date expected = calendar.getTime();

		assertEquals(expected, rootsuite.getTimestamp());

		Testsuite testsuite = rootsuite;
		assertEquals("net.cars.engine.BougieTest", testsuite.getName());
		assertEquals("0.017", testsuite.getTime());

		assertEquals("There should be two testcases", 2, testsuite.getTestcases().size());
		Testcase tc1 = testsuite.getTestcases().get(0);
		Testcase tc2 = testsuite.getTestcases().get(1);

		assertEquals("sparkDry", tc1.getName());
		assertEquals("net.cars.engine.BougieTest", tc1.getClassname());
		assertEquals("0.0010", tc1.getTime());

		assertEquals("sparkHumid", tc2.getName());
		assertEquals("net.cars.engine.BougieTest", tc2.getClassname());
		assertEquals("0.0050", tc2.getTime());

		org.cloudsmith.geppetto.junitresult.Error error = tc2.getError();
		assertEquals("java.lang.RuntimeException: humidity level too high\n" + //
				"\tat net.cars.engine.Bougie.spark(Unknown Source)\n" + //
				"\tat net.cars.engine.BougieTest.sparkHumid(BougieTest.java:36)\n", error.getValue());

		assertEquals(74, rootsuite.getProperties().size());
		Property p = rootsuite.getProperties().get(0);
		assertEquals("java.vendor", p.getName());
		assertEquals("IBM Corporation", p.getValue());
	}

	public void testLoad_CarborateurTest() throws IOException {
		File f = TestDataProvider.getTestFile(new Path("testData/CarborateurTest_testsuite.xml"));
		JunitResult result = JunitresultLoader.loadFromXML(f);
		assertTrue("should be a Testsuite instance", result instanceof Testsuite);
		Testsuite rootsuite = (Testsuite) result;
		assertEquals("net.cars.engine.CarburateurTest", rootsuite.getName());

		// should have one testcase with a failure
		assertEquals("There should be one testcase", 1, rootsuite.getTestcases().size());
		Testcase tc = rootsuite.getTestcases().get(0);
		Failure failure = tc.getFailure();
		assertNotNull(failure);
		assertNull(tc.getError());
		assertEquals("Mix should be exactly 25. expected:<25> but was:<20>", failure.getMessage());
		assertEquals("junit.framework.AssertionFailedError: Mix should be exactly 25. expected:<25> but was:<20>\n" + //
				"\tat net.cars.engine.CarburateurTest.mix(CarburateurTest.java:34)\n", failure.getValue());

	}
}
