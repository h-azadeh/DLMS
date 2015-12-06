package dlms.comp.common;

import dlms.replica.sai_sun.Loan;
import dlms.replica.sai_sun.Utility;

public class LoanTranslator
{
	public static dlms.replica.sai_sun.Loan convertFromMiladToSai(dlms.replica.milad.Loan loan)
	{
		dlms.replica.sai_sun.Loan ret = new Loan(loan.getLoanID(), Integer.toString(loan
				.getAccountNumber()), loan.getLoanAmount(), Utility.dateToString(loan
				.getLoanDueDate()));
		return ret;
	}

	public static dlms.replica.sai_sun.Loan convertFromAzadehToSai(dlms.replica.azadeh.Loan loan)
	{
		dlms.replica.sai_sun.Loan ret = new Loan(loan.GetLoanId(), loan.GetAccNumber(),
				loan.GetAmount(), Utility.dateToString(loan.GetDueDate()));
		return ret;
	}

	public static dlms.replica.azadeh.Loan convertFromSaiToAzadeh(dlms.replica.sai_sun.Loan loan)
	{
		dlms.replica.azadeh.Loan ret = new dlms.replica.azadeh.Loan();
		ret.SetAccNumber(loan.getAccount());
		ret.SetAmount(loan.getAmount());
		ret.SetDueDate(Utility.StringToDate(loan.getDueDate()));
		ret.SetLoanId(Integer.parseInt(loan.getId()));
		return ret;
	}

	public static dlms.replica.azadeh.Loan convertFromMiladToAzadeh(dlms.replica.milad.Loan loan)
	{
		dlms.replica.azadeh.Loan ret = new dlms.replica.azadeh.Loan();
		ret.SetAccNumber(Integer.toString(loan.getAccountNumber()));
		ret.SetAmount(loan.getLoanAmount());
		ret.SetDueDate(loan.getLoanDueDate());
		ret.SetLoanId(loan.getLoanID());
		return ret;
	}

	public static dlms.replica.milad.Loan convertFromAzadehToMilad(dlms.replica.azadeh.Loan loan)
	{
		dlms.replica.milad.Loan ret = new dlms.replica.milad.Loan(Integer.parseInt(loan
				.GetAccNumber()), loan.GetLoanId(), (int) loan.GetAmount(), loan.GetDueDate());
		return ret;
	}

	public static dlms.replica.milad.Loan convertFromSaiToMilad(dlms.replica.sai_sun.Loan loan)
	{
		dlms.replica.milad.Loan ret = new dlms.replica.milad.Loan(Integer.parseInt(loan
				.getAccount()), Integer.parseInt(loan.getId()), (int) loan.getAmount(),
				Utility.StringToDate(loan.getDueDate()));
		return ret;
	}
}
