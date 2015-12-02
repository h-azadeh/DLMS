package fe.bankserver;

/**
 * Holder class for : CustomerAccount
 * 
 * @author OpenORB Compiler
 */
final public class CustomerAccountHolder
        implements org.omg.CORBA.portable.Streamable
{
    /**
     * Internal CustomerAccount value
     */
    public fe.bankserver.CustomerAccount value;

    /**
     * Default constructor
     */
    public CustomerAccountHolder()
    { }

    /**
     * Constructor with value initialisation
     * @param initial the initial value
     */
    public CustomerAccountHolder(fe.bankserver.CustomerAccount initial)
    {
        value = initial;
    }

    /**
     * Read CustomerAccount from a marshalled stream
     * @param istream the input stream
     */
    public void _read(org.omg.CORBA.portable.InputStream istream)
    {
        value = CustomerAccountHelper.read(istream);
    }

    /**
     * Write CustomerAccount into a marshalled stream
     * @param ostream the output stream
     */
    public void _write(org.omg.CORBA.portable.OutputStream ostream)
    {
        CustomerAccountHelper.write(ostream,value);
    }

    /**
     * Return the CustomerAccount TypeCode
     * @return a TypeCode
     */
    public org.omg.CORBA.TypeCode _type()
    {
        return CustomerAccountHelper.type();
    }

}
