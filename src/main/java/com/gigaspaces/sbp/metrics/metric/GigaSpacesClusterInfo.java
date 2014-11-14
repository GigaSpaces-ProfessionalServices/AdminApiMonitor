package com.gigaspaces.sbp.metrics.metric;

import com.gigaspaces.lrmi.*;
import com.gigaspaces.sbp.metrics.visitor.StatsVisitor;
import org.openspaces.admin.gsc.GridServiceContainer;
import org.openspaces.admin.space.SpaceInstance;
import org.openspaces.admin.transport.Transport;
import org.openspaces.admin.transport.TransportLRMIMonitoring;

import java.util.*;

public enum GigaSpacesClusterInfo implements NamedMetric {

    SPACE_MODE("space_mode"){
        public void accept(StatsVisitor statsVisitor){
            for (SpaceInstance spaceInstance : statsVisitor.spaceInstance()){
                statsVisitor.saveStat(new FullMetric.FullMetricBuilder().metric(this).metricValue(spaceInstance.getMode().name()).
                        spaceInstanceID(spaceInstance.getSpaceInstanceName()).create());
            }
        }
    },
    GSC_CONNECTED_MACHINES("connected_machines"){
        @Override
        public void accept(StatsVisitor statsVisitor) {
            Map<GridServiceContainer, Set<String>> connections = new HashMap<>();
            for(SpaceInstance spaceInstance : statsVisitor.spaceInstance()){
                Transport transport = spaceInstance.getTransport();
                GridServiceContainer gscPid = transport.getVirtualMachine().getGridServiceContainer();
                if (connections.get(gscPid) == null){
                    connections.put(gscPid, new HashSet<String>());
                }
                TransportLRMIMonitoring lrmiMonitoring = transport.getLRMIMonitoring();
                LRMIMonitoringDetails fetchedMonitoringDetails = lrmiMonitoring.fetchMonitoringDetails();
                //inbound
                LRMIInboundMonitoringDetails inboundMonitoringDetails =
                        fetchedMonitoringDetails.getInboundMonitoringDetails();
                LRMIServiceMonitoringDetails[] inboundServicesMonitoringDetailsArray =
                        inboundMonitoringDetails.getServicesMonitoringDetails();

                //outbound
                LRMIInboundMonitoringDetails outboundMonitoringDetails =
                        fetchedMonitoringDetails.getInboundMonitoringDetails();
                LRMIServiceMonitoringDetails[] outboundServicesMonitoringDetailsArray =
                        outboundMonitoringDetails.getServicesMonitoringDetails();

                List<LRMIServiceMonitoringDetails> servicesMonitoringDetails = new ArrayList<>();
                servicesMonitoringDetails.addAll(Arrays.asList(inboundServicesMonitoringDetailsArray));
                servicesMonitoringDetails.addAll(Arrays.asList(outboundServicesMonitoringDetailsArray));

                for(LRMIServiceMonitoringDetails servicesMonitoringDetail : servicesMonitoringDetails){
                    //check if service is required space instance
                    Map<LRMIServiceClientMonitoringId, LRMIServiceClientMonitoringDetails> clientsTrackingDetails =
                            servicesMonitoringDetail.getClientsTrackingDetails();
                    Set<Map.Entry<LRMIServiceClientMonitoringId, LRMIServiceClientMonitoringDetails>> entrySet =
                            clientsTrackingDetails.entrySet();
                    for( Map.Entry<LRMIServiceClientMonitoringId, LRMIServiceClientMonitoringDetails> entry : entrySet ){
                        LRMIServiceClientMonitoringId key = entry.getKey();
                        connections.get(gscPid).add(key.getRemoteInetAddress().getHostAddress());
                    }
                }
            }
            for (GridServiceContainer gridServiceContainer : connections.keySet()){
                FullMetric fullMetric = new FullMetric.FullMetricBuilder().
                        metric(this).
                        metricValue(String.valueOf(connections.get(gridServiceContainer).size())).
                        hostName(gridServiceContainer.getMachine().getHostName()).
                        pid(gridServiceContainer.getVirtualMachine().getDetails().getPid()).
                        create();
                statsVisitor.saveStat(fullMetric);
            }
        }
    },
    TOTAl_CONNECTED_MACHINES("total_connected_machines"){
        @Override
        public void accept(StatsVisitor statsVisitor) {
            Set<String> connections = new HashSet<>();
            for(SpaceInstance spaceInstance : statsVisitor.spaceInstance()){
                Transport transport = spaceInstance.getTransport();
                GridServiceContainer gscPid = transport.getVirtualMachine().getGridServiceContainer();

                TransportLRMIMonitoring lrmiMonitoring = transport.getLRMIMonitoring();
                LRMIMonitoringDetails fetchedMonitoringDetails = lrmiMonitoring.fetchMonitoringDetails();
                //inbound
                LRMIInboundMonitoringDetails inboundMonitoringDetails =
                        fetchedMonitoringDetails.getInboundMonitoringDetails();
                LRMIServiceMonitoringDetails[] inboundServicesMonitoringDetailsArray =
                        inboundMonitoringDetails.getServicesMonitoringDetails();

                //outbound
                LRMIInboundMonitoringDetails outboundMonitoringDetails =
                        fetchedMonitoringDetails.getInboundMonitoringDetails();
                LRMIServiceMonitoringDetails[] outboundServicesMonitoringDetailsArray =
                        outboundMonitoringDetails.getServicesMonitoringDetails();

                List<LRMIServiceMonitoringDetails> servicesMonitoringDetails = new ArrayList<>();
                servicesMonitoringDetails.addAll(Arrays.asList(inboundServicesMonitoringDetailsArray));
                servicesMonitoringDetails.addAll(Arrays.asList(outboundServicesMonitoringDetailsArray));

                for(LRMIServiceMonitoringDetails servicesMonitoringDetail : servicesMonitoringDetails){
                    //check if service is required space instance
                    Map<LRMIServiceClientMonitoringId, LRMIServiceClientMonitoringDetails> clientsTrackingDetails =
                            servicesMonitoringDetail.getClientsTrackingDetails();
                    Set<Map.Entry<LRMIServiceClientMonitoringId, LRMIServiceClientMonitoringDetails>> entrySet =
                            clientsTrackingDetails.entrySet();
                    for( Map.Entry<LRMIServiceClientMonitoringId, LRMIServiceClientMonitoringDetails> entry : entrySet ){
                        LRMIServiceClientMonitoringId key = entry.getKey();
                        connections.add(key.getRemoteInetAddress().getHostAddress());
                    }
                }
            }
            FullMetric fullMetric = new FullMetric.FullMetricBuilder().
                    metric(this).
                    metricValue(String.valueOf(connections.size())).
                    create();
            statsVisitor.saveStat(fullMetric);
        }
    }
    ;

    private final String displayName;

    GigaSpacesClusterInfo(String displayName){
        this.displayName = displayName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String displayName() {
        return displayName;
    }

}
