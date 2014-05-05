/*
 * Copyright 2006-2007 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.gigaspaces.monitoring.metrics_source.feeder;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;

/**
 * This feeder class connects to the space started in the hello-processor module.
 */
public class Feeder {

    Logger logger = Logger.getLogger(this.getClass().getName());

    private String postsLocation;

    protected PostReceiver receiver;

    protected List<Map<String, String>> allPosts;

    private int interval, maxIncrement, maxSimultaneous;

    private long runDuration;

    public void startFeed() {
        String[] feederParams = new String[]{"3", "20", "2", "20"};
        try {
            parsePosts();
            feed();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	protected void parsePosts()
			throws Exception {
        //TODO can we simplify this?
        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(postsLocation);
        this.allPosts = parsePosts(new InputSource(new BufferedReader(new InputStreamReader(inputStream))));
	}
	
	protected List<Map<String,String>> parsePosts(InputSource source)
			throws Exception {
		List<Map<String, String>> result = new ArrayList<Map<String, String>>();

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(source);
		doc.getDocumentElement().normalize();

		// Assume that there is an outer tag containing inner elements
		// and we only want the inner ones.
		NodeList nodes = doc.getDocumentElement().getChildNodes();

		Map<String, String> propertyObj = null;
		final int nodesLength = nodes.getLength();
		for (int i = 0; i < nodesLength; i++) {
			Node node = nodes.item(i);
			propertyObj = null;
			// Assume only one level of elements.
			if (Node.ELEMENT_NODE == node.getNodeType()) {
				propertyObj = new TreeMap<String, String>();
				// Assume that each property of the object is in
				// the format: <propertyName>propertyValue</propertyName>
				Element element = (Element) node;
				NodeList propertyNodes = element.getChildNodes();
				final int propertiesLength = propertyNodes.getLength();
				for (int j = 0; j < propertiesLength; j++) {
					Node propertyNode = propertyNodes.item(j);
					if (Node.ELEMENT_NODE == propertyNode.getNodeType()) {
						Element subElement = (Element) propertyNode;
						NodeList valueNodes = subElement.getChildNodes();
						String value = "";
						final int valueLength = valueNodes.getLength();
						for (int k = 0; k < valueLength; k++) {
							Node valueNode = valueNodes.item(k);
							//Assumes there's only one text node
							if (Node.TEXT_NODE == valueNode.getNodeType()) {
								value = valueNode.getNodeValue();
							}
						}
						propertyObj.put(subElement.getNodeName(), value);
					}
				}
			}
			if (propertyObj != null) {
				result.add(propertyObj);
			}
		}

		return result;
	}

    protected Feeder() {
    }

    
    public void setReceiver(PostReceiver receiver) {
    	this.receiver = receiver;
    }

    public void setPostsLocation(String postsLocation) {
        this.postsLocation = postsLocation;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public void setMaxIncrement(int maxIncrement) {
        this.maxIncrement = maxIncrement;
    }

    public void setMaxSimultaneous(int maxSimultaneous) {
        this.maxSimultaneous = maxSimultaneous;
    }

    public void setRunDuration(int runDuration) {
        this.runDuration = runDuration;
    }

    /**
     * Feeds the space with messages
     *
     *
     */
    public void feed() {
    	//This just runs until duration (in seconds) has expired.
    	long started = System.currentTimeMillis();
    	final int sizeOfMessages = this.allPosts.size();
    	final long duration = runDuration * 1000L;
    	int numberOfMessages = 1;
    	while (System.currentTimeMillis() - started < duration) {
        	try {
        	receiver.postToBlog(allPosts.get((int)Math.floor(Math.random()*sizeOfMessages)));
        	numberOfMessages++;
        	} catch(Exception e) {
        		logger.warning("Exception " + e.getMessage());
        		e.printStackTrace(System.err);
        	}
        	try {
        		//We don't want to submit on the same millisecond
				Thread.sleep(2);
			} catch (InterruptedException e) {
				//do nothing
			}
        }
        logger.info("Feeder WROTE " + numberOfMessages + " messages");
    }
}
