package pl.edu.agh.kt;

import net.floodlightcontroller.core.FloodlightContext;
import net.floodlightcontroller.core.IFloodlightProviderService;
import net.floodlightcontroller.packet.*;
import org.projectfloodlight.openflow.protocol.OFMessage;
import org.projectfloodlight.openflow.types.EthType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by wolodija on 25.12.16.
 */
public class PacketExtractor {

    private static final Logger logger = LoggerFactory.getLogger(PacketExtractor.class);
    private FloodlightContext cntx;
    private OFMessage msg;
    protected IFloodlightProviderService floodlightProvider;
    private Ethernet eth;
    private IPv4 ipv4;
    private ARP arp;
    private TCP tcp;
    private UDP udp;

    public PacketExtractor(FloodlightContext cntx){
        this.cntx = cntx;
        this.extractEth();
    }

    private void extractEth() {
        eth = IFloodlightProviderService.bcStore.get(cntx,
                IFloodlightProviderService.CONTEXT_PI_PAYLOAD);

        logger.info("Frame: src mac {}", eth.getSourceMACAddress());
        logger.info("Frame: dst mac {}", eth.getDestinationMACAddress());
        logger.info("Frame: ether_type {}", eth.getEtherType());

        if (eth.getEtherType() == EthType.ARP) {
            arp = (ARP) eth.getPayload();
            extractArp(arp);
        }
        if (eth.getEtherType() == EthType.IPv4) {
            ipv4 = (IPv4) eth.getPayload();
            extractIp(ipv4);
        }

    }

    private void extractArp(ARP arp) {
        logger.info("ARP: {}", arp.toString());
    }

    private void extractIp(IPv4 ipv4) {
        logger.info("IPv4: {}", ipv4.getSourceAddress().toString());
    }
}
