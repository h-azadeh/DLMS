package BankServer;

/** 
 * Helper class for : AccountsMap
 *  
 * @author OpenORB Compiler
 */ 
public class AccountsMapHelper
{
    private static final boolean HAS_OPENORB;
    static
    {
        boolean hasOpenORB = false;
        try
        {
            Thread.currentThread().getContextClassLoader().loadClass( "org.openorb.orb.core.Any" );
            hasOpenORB = true;
        }
        catch ( ClassNotFoundException ex )
        {
            // do nothing
        }
        HAS_OPENORB = hasOpenORB;
    }
    /**
     * Insert AccountsMap into an any
     * @param a an any
     * @param t AccountsMap value
     */
    public static void insert(org.omg.CORBA.Any a, BankServer.CustomerAccount[] t)
    {
        a.insert_Streamable(new BankServer.AccountsMapHolder(t));
    }

    /**
     * Extract AccountsMap from an any
     *
     * @param a an any
     * @return the extracted AccountsMap value
     */
    public static BankServer.CustomerAccount[] extract( org.omg.CORBA.Any a )
    {
        if ( !a.type().equivalent( type() ) )
        {
            throw new org.omg.CORBA.MARSHAL();
        }
        if ( HAS_OPENORB && a instanceof org.openorb.orb.core.Any )
        {
            // streamable extraction. The jdk stubs incorrectly define the Any stub
            org.openorb.orb.core.Any any = ( org.openorb.orb.core.Any ) a;
            try
            {
                org.omg.CORBA.portable.Streamable s = any.extract_Streamable();
                if ( s instanceof BankServer.AccountsMapHolder )
                {
                    return ( ( BankServer.AccountsMapHolder ) s ).value;
                }
            }
            catch ( org.omg.CORBA.BAD_INV_ORDER ex )
            {
            }
            BankServer.AccountsMapHolder h = new BankServer.AccountsMapHolder( read( a.create_input_stream() ) );
            a.insert_Streamable( h );
            return h.value;
        }
        return read( a.create_input_stream() );
    }

    //
    // Internal TypeCode value
    //
    private static org.omg.CORBA.TypeCode _tc = null;

    /**
     * Return the AccountsMap TypeCode
     * @return a TypeCode
     */
    public static org.omg.CORBA.TypeCode type()
    {
        if (_tc == null) {
            org.omg.CORBA.ORB orb = org.omg.CORBA.ORB.init();
            _tc = orb.create_alias_tc( id(), "AccountsMap", orb.create_sequence_tc( 0, BankServer.CustomerAccountHelper.type() ) );
        }
        return _tc;
    }

    /**
     * Return the AccountsMap IDL ID
     * @return an ID
     */
    public static String id()
    {
        return _id;
    }

    private final static String _id = "IDL:BankServer/AccountsMap:1.0";

    /**
     * Read AccountsMap from a marshalled stream
     * @param istream the input stream
     * @return the readed AccountsMap value
     */
    public static BankServer.CustomerAccount[] read(org.omg.CORBA.portable.InputStream istream)
    {
        BankServer.CustomerAccount[] new_one;
        {
        int size7 = istream.read_ulong();
        new_one = new BankServer.CustomerAccount[size7];
        for (int i7=0; i7<new_one.length; i7++)
         {
            new_one[i7] = BankServer.CustomerAccountHelper.read(istream);

         }
        }

        return new_one;
    }

    /**
     * Write AccountsMap into a marshalled stream
     * @param ostream the output stream
     * @param value AccountsMap value
     */
    public static void write(org.omg.CORBA.portable.OutputStream ostream, BankServer.CustomerAccount[] value)
    {
        ostream.write_ulong( value.length );
        for ( int i7 = 0; i7 < value.length; i7++ )
        {
            BankServer.CustomerAccountHelper.write( ostream, value[ i7 ] );

        }
    }

}
