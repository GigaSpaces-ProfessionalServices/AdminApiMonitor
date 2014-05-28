package com.gigaspaces.monitoring.metrics_source.feeder;

import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.util.*;

public class TestFeeder {

	protected Feeder objectUnderTest = null;

	@Before
	public void setUp() throws Exception {
		objectUnderTest = new Feeder();
	}

	@After
	public void tearDown() throws Exception {
		objectUnderTest = null;
	}

	@Test
	public void testParsePostsInputSource() {
		String testString = "<posts>\n"
				+ "\t<post>\n"
				+ "<text>401k is becoming a 40.1k...</text>"
				+ "<username>Bnewport</username>"
				+ "</post> <post><text>RT @DevNews: Apple introduces new iMac line, new Minis and new Mac Pros: http://bit.ly/jyDJq</text>"
				+ "\n\t\t<username>hamids</username>\n" + "</post>\n"
				+ "</posts>";
		InputSource source = new InputSource(new StringReader(testString));
		List<Map<String, String>> expected = new ArrayList<Map<String, String>>();
		Map<String, String> subExpected = new TreeMap<String, String>();
		subExpected.put("text", "401k is becoming a 40.1k...");
		subExpected.put("username", "Bnewport");
		expected.add(subExpected);
		subExpected = new TreeMap<String, String>();
		subExpected
				.put("text",
						"RT @DevNews: Apple introduces new iMac line, new Minis and new Mac Pros: http://bit.ly/jyDJq");
		subExpected.put("username", "hamids");
		expected.add(subExpected);

		try {
			List<Map<String, String>> actual = objectUnderTest
					.parsePosts(source);
			Assert.assertNotNull("parsePosts returned null.", actual);
			Assert.assertEquals(
					"parsePosts returned the wrong number of Maps.",
					expected.size(), actual.size());
			// The expected and actual have to be in the same order.
			Iterator<Map<String, String>> expectedIterator = expected
					.iterator();
			Iterator<Map<String, String>> actualIterator = actual.iterator();
			while (expectedIterator.hasNext()) {
				Map<String, String> anExpected = expectedIterator.next();
				Map<String, String> anActual = actualIterator.next();
				Set<String> expectedKeys = anExpected.keySet();
				Assert.assertEquals(
						"parsePosts returned the wrong number of keys.",
						expectedKeys.size(), anActual.keySet().size());
				for (String expKey : expectedKeys) {
					Assert.assertTrue(
							"parsePost did not contain key " + expKey,
							anActual.containsKey(expKey));
					Assert.assertEquals("parsePost: unexpected value for key "
							+ expKey, anExpected.get(expKey),
							anActual.get(expKey));
				}
			}
		} catch (Exception e) {
			Assert.fail("parsePosts failed: " + e.getMessage());
		}
	}
	// public static void compareUnorderedCollection(String message, Collection
	// expected, Collection actual, Comparator comper) {
	// if (expected == null && actual != null) {
	// fail(message + ": expected was null, but not actual.");
	// }
	// if (expected != null && actual == null) {
	// fail(message + ": expected was not null, but actual was null.");
	// }
	//
	// Assert.assertEquals(message + ": the size is incorrect.",
	// expected.size(), actual.size());
	// compareSortedCollection(message, expected, actual, )
	// }
	//
	// //Assumes the Map is made up of Comparable key and value
	// public static class MapComparator implements Comparator{
	//
	// @Override
	// public int compare(Object o1, Object o2) {
	// Map map1 = (Map)o1;
	// Map map2 = (Map)o2;
	//
	// return 0;
	// }
	//
	// }
}
