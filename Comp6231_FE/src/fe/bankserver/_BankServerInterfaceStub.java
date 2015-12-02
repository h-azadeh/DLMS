package fe.bankserver;

/**
 * Interface definition: BankServerInterface.
 * 
 * @author OpenORB Compiler
 */
public class _BankServerInterfaceStub extends org.omg.CORBA.portable.ObjectImpl
        implements BankServerInterface
{
    static final String[] _ids_list =
    {
        "IDL:fe.bankserver/BankServerInterface:1.0"
    };

    public String[] _ids()
    {
     return _ids_list;
    }

    private final static Class _opsClass = fe.bankserver.BankServerInterfaceOperations.class;

    /**
     * Operation openAccount
     */
    public String openAccount(fe.bankserver.CustomerAccount account)
    {
        while(true)
        {
            if (!this._is_local())
            {
                org.omg.CORBA.portable.InputStream _input = null;
                try
                {
                    org.omg.CORBA.portable.OutputStream _output = this._request("openAccount",true);
                    fe.bankserver.CustomerAccountHelper.write(_output,account);
                    _input = this._invoke(_output);
                    String _arg_ret = _input.read_string();
                    return _arg_ret;
                }
                catch(org.omg.CORBA.portable.RemarshalException _exception)
                {
                    continue;
                }
                catch(org.omg.CORBA.portable.ApplicationException _exception)
                {
                    String _exception_id = _exception.getId();
                    throw new org.omg.CORBA.UNKNOWN("Unexpected User Exception: "+ _exception_id);
                }
                finally
                {
                    this._releaseReply(_input);
                }
            }
            else
            {
                org.omg.CORBA.portable.ServantObject _so = _servant_preinvoke("openAccount",_opsClass);
                if (_so == null)
                   continue;
                fe.bankserver.BankServerInterfaceOperations _self = (fe.bankserver.BankServerInterfaceOperations) _so.servant;
                try
                {
                    return _self.openAccount( account);
                }
                finally
                {
                    _servant_postinvoke(_so);
                }
            }
        }
    }

    /**
     * Operation getLoan
     */
    public boolean getLoan(String bankName, String accNumber, String password, int loanAmount)
    {
        while(true)
        {
            if (!this._is_local())
            {
                org.omg.CORBA.portable.InputStream _input = null;
                try
                {
                    org.omg.CORBA.portable.OutputStream _output = this._request("getLoan",true);
                    _output.write_string(bankName);
                    _output.write_string(accNumber);
                    _output.write_string(password);
                    _output.write_long(loanAmount);
                    _input = this._invoke(_output);
                    boolean _arg_ret = _input.read_boolean();
                    return _arg_ret;
                }
                catch(org.omg.CORBA.portable.RemarshalException _exception)
                {
                    continue;
                }
                catch(org.omg.CORBA.portable.ApplicationException _exception)
                {
                    String _exception_id = _exception.getId();
                    throw new org.omg.CORBA.UNKNOWN("Unexpected User Exception: "+ _exception_id);
                }
                finally
                {
                    this._releaseReply(_input);
                }
            }
            else
            {
                org.omg.CORBA.portable.ServantObject _so = _servant_preinvoke("getLoan",_opsClass);
                if (_so == null)
                   continue;
                fe.bankserver.BankServerInterfaceOperations _self = (fe.bankserver.BankServerInterfaceOperations) _so.servant;
                try
                {
                    return _self.getLoan( bankName,  accNumber,  password,  loanAmount);
                }
                finally
                {
                    _servant_postinvoke(_so);
                }
            }
        }
    }

    /**
     * Operation delayPayment
     */
    public String delayPayment(String bankName, int loanId, String curDueDate, String newDueDate)
    {
        while(true)
        {
            if (!this._is_local())
            {
                org.omg.CORBA.portable.InputStream _input = null;
                try
                {
                    org.omg.CORBA.portable.OutputStream _output = this._request("delayPayment",true);
                    _output.write_string(bankName);
                    _output.write_long(loanId);
                    _output.write_string(curDueDate);
                    _output.write_string(newDueDate);
                    _input = this._invoke(_output);
                    String _arg_ret = _input.read_string();
                    return _arg_ret;
                }
                catch(org.omg.CORBA.portable.RemarshalException _exception)
                {
                    continue;
                }
                catch(org.omg.CORBA.portable.ApplicationException _exception)
                {
                    String _exception_id = _exception.getId();
                    throw new org.omg.CORBA.UNKNOWN("Unexpected User Exception: "+ _exception_id);
                }
                finally
                {
                    this._releaseReply(_input);
                }
            }
            else
            {
                org.omg.CORBA.portable.ServantObject _so = _servant_preinvoke("delayPayment",_opsClass);
                if (_so == null)
                   continue;
                fe.bankserver.BankServerInterfaceOperations _self = (fe.bankserver.BankServerInterfaceOperations) _so.servant;
                try
                {
                    return _self.delayPayment( bankName,  loanId,  curDueDate,  newDueDate);
                }
                finally
                {
                    _servant_postinvoke(_so);
                }
            }
        }
    }

    /**
     * Operation printCustomerInfo
     */
    public fe.bankserver.CustomerAccount[] printCustomerInfo(String bankName)
    {
        while(true)
        {
            if (!this._is_local())
            {
                org.omg.CORBA.portable.InputStream _input = null;
                try
                {
                    org.omg.CORBA.portable.OutputStream _output = this._request("printCustomerInfo",true);
                    _output.write_string(bankName);
                    _input = this._invoke(_output);
                    fe.bankserver.CustomerAccount[] _arg_ret = fe.bankserver.AccountsMapHelper.read(_input);
                    return _arg_ret;
                }
                catch(org.omg.CORBA.portable.RemarshalException _exception)
                {
                    continue;
                }
                catch(org.omg.CORBA.portable.ApplicationException _exception)
                {
                    String _exception_id = _exception.getId();
                    throw new org.omg.CORBA.UNKNOWN("Unexpected User Exception: "+ _exception_id);
                }
                finally
                {
                    this._releaseReply(_input);
                }
            }
            else
            {
                org.omg.CORBA.portable.ServantObject _so = _servant_preinvoke("printCustomerInfo",_opsClass);
                if (_so == null)
                   continue;
                fe.bankserver.BankServerInterfaceOperations _self = (fe.bankserver.BankServerInterfaceOperations) _so.servant;
                try
                {
                    return _self.printCustomerInfo( bankName);
                }
                finally
                {
                    _servant_postinvoke(_so);
                }
            }
        }
    }

    /**
     * Operation transferLoan
     */
    public String transferLoan(int loanID, String CurrentBank, String OtherBank)
    {
        while(true)
        {
            if (!this._is_local())
            {
                org.omg.CORBA.portable.InputStream _input = null;
                try
                {
                    org.omg.CORBA.portable.OutputStream _output = this._request("transferLoan",true);
                    _output.write_long(loanID);
                    _output.write_string(CurrentBank);
                    _output.write_string(OtherBank);
                    _input = this._invoke(_output);
                    String _arg_ret = _input.read_string();
                    return _arg_ret;
                }
                catch(org.omg.CORBA.portable.RemarshalException _exception)
                {
                    continue;
                }
                catch(org.omg.CORBA.portable.ApplicationException _exception)
                {
                    String _exception_id = _exception.getId();
                    throw new org.omg.CORBA.UNKNOWN("Unexpected User Exception: "+ _exception_id);
                }
                finally
                {
                    this._releaseReply(_input);
                }
            }
            else
            {
                org.omg.CORBA.portable.ServantObject _so = _servant_preinvoke("transferLoan",_opsClass);
                if (_so == null)
                   continue;
                fe.bankserver.BankServerInterfaceOperations _self = (fe.bankserver.BankServerInterfaceOperations) _so.servant;
                try
                {
                    return _self.transferLoan( loanID,  CurrentBank,  OtherBank);
                }
                finally
                {
                    _servant_postinvoke(_so);
                }
            }
        }
    }

}
