package fe.bankserver;

/** 
 * Helper class for : CustomerAccount
 *  
 * @author OpenORB Compiler
 */ 
public class CustomerAccountHelper
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
     * Insert CustomerAccount into an any
     * @param a an any
     * @param t CustomerAccount value
     */
    public static void insert(org.omg.CORBA.Any a, fe.bankserver.CustomerAccount t)
    {
        a.insert_Streamable(new fe.bankserver.CustomerAccountHolder(t));
    }

    /**
     * Extract CustomerAccount from an any
     *
     * @param a an any
     * @return the extracted CustomerAccount value
     */
    public static fe.bankserver.CustomerAccount extract( org.omg.CORBA.Any a )
    {
        if ( !a.type().equivalent( type() ) )
        {
            throw new org.omg.CORBA.MARSHAL();
        }
        if (HAS_OPENORB && a instanceof org.openorb.orb.core.Any) {
            // streamable extraction. The jdk stubs incorrectly define the Any stub
            org.openorb.orb.core.Any any = (org.openorb.orb.core.Any)a;
            try {
                org.omg.CORBA.portable.Streamable s = any.extract_Streamable();
                if ( s instanceof fe.bankserver.CustomerAccountHolder )
                    return ( ( fe.bankserver.CustomerAccountHolder ) s ).value;
            }
            catch ( org.omg.CORBA.BAD_INV_ORDER ex )
            {
            }
            fe.bankserver.CustomerAccountHolder h = new fe.bankserver.CustomerAccountHolder( read( a.create_input_stream() ) );
            a.insert_Streamable( h );
            return h.value;
        }
        return read( a.create_input_stream() );
    }

    //
    // Internal TypeCode value
    //
    private static org.omg.CORBA.TypeCode _tc = null;
    private static boolean _working = false;

    /**
     * Return the CustomerAccount TypeCode
     * @return a TypeCode
     */
    public static org.omg.CORBA.TypeCode type()
    {
        if (_tc == null) {
            synchronized(org.omg.CORBA.TypeCode.class) {
                if (_tc != null)
                    return _tc;
                if (_working)
                    return org.omg.CORBA.ORB.init().create_recursive_tc(id());
                _working = true;
                org.omg.CORBA.ORB orb = org.omg.CORBA.ORB.init();
                org.omg.CORBA.StructMember _members[] = new org.omg.CORBA.StructMember[ 9 ];

                _members[ 0 ] = new org.omg.CORBA.StructMember();
                _members[ 0 ].name = "bankName";
                _members[ 0 ].type = orb.get_primitive_tc( org.omg.CORBA.TCKind.tk_string );
                _members[ 1 ] = new org.omg.CORBA.StructMember();
                _members[ 1 ].name = "customerAccountNumber";
                _members[ 1 ].type = orb.get_primitive_tc( org.omg.CORBA.TCKind.tk_string );
                _members[ 2 ] = new org.omg.CORBA.StructMember();
                _members[ 2 ].name = "firstName";
                _members[ 2 ].type = orb.get_primitive_tc( org.omg.CORBA.TCKind.tk_string );
                _members[ 3 ] = new org.omg.CORBA.StructMember();
                _members[ 3 ].name = "lastName";
                _members[ 3 ].type = orb.get_primitive_tc( org.omg.CORBA.TCKind.tk_string );
                _members[ 4 ] = new org.omg.CORBA.StructMember();
                _members[ 4 ].name = "emailAddress";
                _members[ 4 ].type = orb.get_primitive_tc( org.omg.CORBA.TCKind.tk_string );
                _members[ 5 ] = new org.omg.CORBA.StructMember();
                _members[ 5 ].name = "phoneNumber";
                _members[ 5 ].type = orb.get_primitive_tc( org.omg.CORBA.TCKind.tk_string );
                _members[ 6 ] = new org.omg.CORBA.StructMember();
                _members[ 6 ].name = "password";
                _members[ 6 ].type = orb.get_primitive_tc( org.omg.CORBA.TCKind.tk_string );
                _members[ 7 ] = new org.omg.CORBA.StructMember();
                _members[ 7 ].name = "creditLimit";
                _members[ 7 ].type = orb.get_primitive_tc( org.omg.CORBA.TCKind.tk_long );
                _members[ 8 ] = new org.omg.CORBA.StructMember();
                _members[ 8 ].name = "loansInfo";
                _members[ 8 ].type = orb.get_primitive_tc( org.omg.CORBA.TCKind.tk_string );
                _tc = orb.create_struct_tc( id(), "CustomerAccount", _members );
                _working = false;
            }
        }
        return _tc;
    }

    /**
     * Return the CustomerAccount IDL ID
     * @return an ID
     */
    public static String id()
    {
        return _id;
    }

    private final static String _id = "IDL:fe.bankserver/CustomerAccount:1.0";

    /**
     * Read CustomerAccount from a marshalled stream
     * @param istream the input stream
     * @return the readed CustomerAccount value
     */
    public static fe.bankserver.CustomerAccount read(org.omg.CORBA.portable.InputStream istream)
    {
        fe.bankserver.CustomerAccount new_one = new fe.bankserver.CustomerAccount();

        new_one.bankName = istream.read_string();
        new_one.customerAccountNumber = istream.read_string();
        new_one.firstName = istream.read_string();
        new_one.lastName = istream.read_string();
        new_one.emailAddress = istream.read_string();
        new_one.phoneNumber = istream.read_string();
        new_one.password = istream.read_string();
        new_one.creditLimit = istream.read_long();
        new_one.loansInfo = istream.read_string();

        return new_one;
    }

    /**
     * Write CustomerAccount into a marshalled stream
     * @param ostream the output stream
     * @param value CustomerAccount value
     */
    public static void write(org.omg.CORBA.portable.OutputStream ostream, fe.bankserver.CustomerAccount value)
    {
        ostream.write_string( value.bankName );
        ostream.write_string( value.customerAccountNumber );
        ostream.write_string( value.firstName );
        ostream.write_string( value.lastName );
        ostream.write_string( value.emailAddress );
        ostream.write_string( value.phoneNumber );
        ostream.write_string( value.password );
        ostream.write_long( value.creditLimit );
        ostream.write_string( value.loansInfo );
    }

}
