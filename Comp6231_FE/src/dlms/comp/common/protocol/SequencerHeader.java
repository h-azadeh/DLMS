package dlms.comp.common.protocol;

import java.io.Serializable;

public class SequencerHeader implements Serializable
{
	private static final long serialVersionUID = 6494490524556404172L;
	private int UUID = -1;

	public SequencerHeader(int uuid)
	{
		UUID = uuid;
	}

	public int getUUID()
	{
		return UUID;
	}
}
