package com.gigaspaces.monitoring.metrics_source.feeder;

import com.gigaspaces.document.SpaceDocument;
import org.openspaces.core.GigaSpace;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.UUID;

/**
 * Implements a blog as a GigaSpace. Each post can actually have any number of
 * propertNames and values. However, there can't be a property with a reserved
 * key name.
 * 
 * @author tsarver
 * @see #RESERVED_KEYS
 */
public class SpacePostReceiver implements PostReceiver {

	protected static final String PARTITION_ID_KEY = "_partitionId";
	protected static final String DATE_TIME_KEY = "_postedDatetime";
	protected static final String ID_KEY = "_id";
	protected static final String[] RESERVED_KEYS = new String[] {
			ID_KEY, DATE_TIME_KEY, PARTITION_ID_KEY };
	protected static final String DOCUMENT_NAME = "BlogPost";
	
	protected GigaSpace gigaSpace;

	@Override
	public void postToBlog(Map<String, String> post) throws Exception {
		validatePost(post);
		TreeMap<String,Object> newProperties = new TreeMap<String,Object>(post);
		newProperties.put(DATE_TIME_KEY, new Date(System.currentTimeMillis()));
		newProperties.put(PARTITION_ID_KEY, calculatePartitionId(post));
		newProperties.put(ID_KEY, UUID.randomUUID());
		this.gigaSpace.write(new SpaceDocument(DOCUMENT_NAME, newProperties));
	}

	protected void validatePost(Map<String, String> post)
			throws IllegalArgumentException {
		if (post == null) {
			throw new IllegalArgumentException("The post can not be null.");
		}
		for (String reservedKey : RESERVED_KEYS) {
			if (post.containsKey(reservedKey)) {
				throw new IllegalArgumentException(
						"The post cannot contain a key named '" + reservedKey
								+ "'. It is a reserved key.");
			}
		}
	}

	protected int calculatePartitionId(Map<String,String> post) {
		int result = Integer.MIN_VALUE;
		for (Entry<String,String> entry : post.entrySet()) {
			//Don't need to worry about overflow because Java handles that for us
			result += entry.getKey().hashCode();
			result += entry.getValue().hashCode();
		}
		return result;
	}

    @Required
    public void setGigaSpace(GigaSpace gigaSpace) {
        this.gigaSpace = gigaSpace;
    }
}
