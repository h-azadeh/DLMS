package dlms.comp.udp.util;

import dlms.comp.common.protocol.UDPProtocol;

public interface UDPNotifierIF
{
public void notifyMessage(UDPProtocol message);
}
