/**
 * 
 */
package com.daoman.task;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author parox
 * 
 */
public class DemoCornTaskTest extends TestCase {

	public DemoCornTaskTest(String testName) {
		super(testName);
	}

	public static Test suite() {
		return new TestSuite(DemoCornTaskTest.class);
	}
	
	public void testDemoExecute(){
		assertTrue(true);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
	}

}
