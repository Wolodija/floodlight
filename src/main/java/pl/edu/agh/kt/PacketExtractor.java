package pl.edu.agh.kt;

import net.floodlightcontroller.core.FloodlightContext;
import net.floodlightcontroller.core.IFloodlightProviderService;
import net.floodlightcontroller.packet.*;
import org.projectfloodlight.openflow.protocol.OFMessage;
import org.projectfloodlight.openflow.types.EthType;
import org.projectfloodlight.openflow.types.IpProtocol;
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

    public String extractEth() {
        eth = IFloodlightProviderService.bcStore.get(cntx,
                IFloodlightProviderService.CONTEXT_PI_PAYLOAD);

        logger.info("Frame: src mac {}", eth.getSourceMACAddress());
        logger.info("Frame: dst mac {}", eth.getDestinationMACAddress());
        logger.info("Frame: ether_type {}", eth.getEtherType());

        if (eth.getEtherType() == EthType.ARP) {
            arp = (ARP) eth.getPayload();
            return extractArp(arp);
        }
        if (eth.getEtherType() == EthType.IPv4) {
            ipv4 = (IPv4) eth.getPayload();
            if (ipv4.getProtocol() == IpProtocol.TCP) {
                return extractTCP(ipv4);
            }
            else if (ipv4.getProtocol() == IpProtocol.ICMP) {
                return extractICMP(ipv4, eth);
            }
        }
        return "";

    }

    private String extractArp(ARP arp) {
        logger.info("ARP: {}", arp.toString());
        return "ARP" + arp.getSenderHardwareAddress().toString() + ";" + arp.getTargetHardwareAddress() +
                ";" + arp.getSenderProtocolAddress().toString() + ";" + arp.getTargetProtocolAddress().toString();
    }

    private String extractTCP(IPv4 ipv4) {
        logger.info("IPv4: {}", ipv4.getSourceAddress().toString());
        return "IP4" + ipv4.getSourceAddress().toString() + ";" + ipv4.getDestinationAddress().toString() + ";"
                + ((TCP)ipv4.getPayload()).getSourcePort().toString() + ";"
                + ((TCP)ipv4.getPayload()).getDestinationPort().toString();
    }

    private String extractICMP(IPv4 ipv4, Ethernet eth) {
        logger.info("IPv4: {}", ipv4.getSourceAddress().toString());
        return "ICM" + eth.getSourceMACAddress().toString() + ";" + eth.getDestinationMACAddress().toString() + ";"
                + ipv4.getSourceAddress().toString() + ";" + ipv4.getDestinationAddress().toString();
    }
}
