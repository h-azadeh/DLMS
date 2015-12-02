package fe.bankserver;

/**
 * Holder class for : AccountsMap
 * 
 * @author OpenORB Compiler
 */
final public class AccountsMapHolder
        implements org.omg.CORBA.portable.Streamable
{
    /**
     * Internal AccountsMap value
     */
    public fe.bankserver.CustomerAccount[] value;

    /**
     * Default constructor
     */
    public AccountsMapHolder()
    { }

    /**
     * Constructor with value initialisation
     * @param initial the initial value
     */
    public AccountsMapHolder(fe.bankserver.CustomerAccount[] initial)
    {
        value = initial;
    }

    /**
     * Read AccountsMap from a marshalled stream
     * @param istream the input stream
     */
    public void _read(org.omg.CORBA.portable.InputStream istream)
    {
        value = AccountsMapHelper.read(istream);
    }

    /**
     * Write AccountsMap into a marshalled stream
     * @param ostream the output stream
     */
    public void _write(org.omg.CORBA.portable.OutputStream ostream)
    {
        AccountsMapHelper.write(ostream,value);
    }

    /**
     * Return the AccountsMap TypeCode
     * @return a TypeCode
     */
    public org.omg.CORBA.TypeCode _type()
    {
        return AccountsMapHelper.type();
    }

}
