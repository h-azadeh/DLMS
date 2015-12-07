package dlms.comp.common;

import java.util.Arrays;

public class Test
{

	public static void main(String[] args)
	{
		 Class<? extends Object> target = args.getClass();
		System.out.println(args.getClass());
		if(target.isArray())
		{
			System.out.println("is array");
			System.out.println(Arrays.deepEquals(args, args));
		}
	}

}
