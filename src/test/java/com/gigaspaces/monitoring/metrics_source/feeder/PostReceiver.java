package com.gigaspaces.monitoring.metrics_source.feeder;

import java.util.Map;

public interface PostReceiver {
	/**
	 * Posts this post to the blog.
	 * The semantics for propertyNames and propertyValues are determined by the implementor.
	 *  
	 * @param post a Map of propertyName to propertyValue
	 * @throws Exception as determined by the implementor
	 */
	void postToBlog(Map<String, String> post) throws Exception;

}
