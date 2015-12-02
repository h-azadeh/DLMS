package dlms.comp.udp.util;

import dlms.comp.common.protocol.UDPProtocol;

/**
 * UDP protocol notifier interface
 *
 */
public interface UDPNotifierIF
{
public void notifyMessage(UDPProtocol message);
}
