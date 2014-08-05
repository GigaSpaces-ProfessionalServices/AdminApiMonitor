package com.gigaspaces.sbp.metrics;

/**
 * Created by IntelliJ IDEA.
 * User: jason
 * Date: 8/5/14
 * Time: 3:04 PM
 * A metric can apply either to a single GigaSpaces process
 * or to a group of processes (like a group of GSCs (a partition) or
 * a Space).
 */
public enum GigaSpaceProcess {
    GSM, GSA, LUS, GSC, ESM, MIRROR, SPACE, PARTITION, WEBUI, PROXY;
}
