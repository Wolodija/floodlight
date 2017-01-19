package pl.edu.agh.kt;

import io.netty.util.Timer;
import net.floodlightcontroller.core.*;
import net.floodlightcontroller.core.internal.Controller;
import net.floodlightcontroller.core.internal.RoleManager;
import net.floodlightcontroller.core.module.FloodlightModuleContext;
import net.floodlightcontroller.core.module.FloodlightModuleException;
import net.floodlightcontroller.core.module.IFloodlightModule;
import net.floodlightcontroller.core.module.IFloodlightService;
import org.projectfloodlight.openflow.protocol.OFMessage;
import org.projectfloodlight.openflow.protocol.OFPacketIn;
import org.projectfloodlight.openflow.protocol.OFType;
import org.projectfloodlight.openflow.protocol.action.OFActionPopMpls;
import org.projectfloodlight.openflow.types.IPv4Address;
import org.projectfloodlight.openflow.types.OFPort;
import org.projectfloodlight.openflow.types.TransportPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by wolodija on 25.12.16.
 */
public class SdnLabListener implements IFloodlightModule, IOFMessageListener{
    protected IFloodlightProviderService floodlightProvider;
    protected static Logger logger;

    @Override
    public String getName() {
        return SdnLabListener.class.getSimpleName();
    }

    @Override
    public boolean isCallbackOrderingPrereq(OFType type, String name) {
        return false;
    }

    @Override
    public boolean isCallbackOrderingPostreq(OFType type, String name) {
        return false;
    }

    @Override
    public Command receive(IOFSwitch sw, OFMessage msg, FloodlightContext cntx) {
        logger.info("************* NEW PACKET IN *************");
        PacketExtractor extractor = new PacketExtractor(cntx);

        OFPacketIn pin = (OFPacketIn) msg;
        OFPort outPort = OFPort.of(0);
        if (pin.getInPort() == OFPort.of(1)){
            outPort = OFPort.of(2);
        }
        else{
            outPort = OFPort.of(1);
        }

        Flows.simpleAdd(sw, pin, cntx, outPort);
        return Command.CONTINUE;
    }

    @Override
    public Collection<Class<? extends IFloodlightService>> getModuleServices() {
        return null;
    }

    @Override
    public Map<Class<? extends IFloodlightService>, IFloodlightService> getServiceImpls() {
        return null;
    }

    @Override
    public Collection<Class<? extends IFloodlightService>> getModuleDependencies() {
        Collection<Class<? extends IFloodlightService>> l = new ArrayList<Class<? extends
                        IFloodlightService>>();
        l.add(IFloodlightProviderService.class);
        return l;
    }

    @Override
    public void init(FloodlightModuleContext context) throws FloodlightModuleException {
        floodlightProvider = context.getServiceImpl(IFloodlightProviderService.class);
        logger = LoggerFactory.getLogger(SdnLabListener.class);
    }

    @Override
    public void startUp(FloodlightModuleContext context) throws FloodlightModuleException {
        floodlightProvider.addOFMessageListener(OFType.PACKET_IN, this);
        logger.info("******************* START **************************");
    }
}
