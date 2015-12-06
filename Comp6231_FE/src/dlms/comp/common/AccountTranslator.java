package dlms.comp.common;

import dlms.replica.azadeh.CustomerAccount;
import dlms.replica.milad.Account;
import dlms.replica.sai_sun.User;

public class AccountTranslator
{
	public static User convertFromMiladToSai(Account account, String bank)
	{
		User user = new User(Integer.toString(account.getAccountNumber()),
				account.getFirstName(), account.getLastName(),
				account.getPhoneNumber(), account.getEmail(),
				account.getPassword(), account.getCreditLimit(),
				account.getUserName().equalsIgnoreCase("manager"), bank);
		return user;
	}

	public static User convertFromAzadehToSai(CustomerAccount account,
			String bank)
	{
		User user = new User(account.GetAccNumber(), account.GetFirstName(),
				account.GetLastName(), account.GetPhoneNumber(),
				account.GetEmailAddress(), account.GetPassword(),
				account.GetCreditLimit(),
				account.GetFirstName().equalsIgnoreCase("manager"), bank);
		return user;
	}

	public static Account convertFromSaiToMilad(User account)
	{
		Account user = new Account(Integer.parseInt(account.getAccount()),
				account.getFirstName(), account.getLastName(),
				account.getEmail(), account.getPhone(), account.getPassword(),
				(int) account.getCreditLimit(), account.getUsr());

		return user;
	}

	public static CustomerAccount convertFromSaiToAzadeh(User account)
	{
		CustomerAccount user = new CustomerAccount();
		user.SetAccNumber(account.getAccount());
		user.SetCreditLimit((long) account.getCreditLimit());
		user.SetEmailAddress(account.getEmail());
		user.SetFirstName(account.getFirstName());
		user.SetLastName(account.getLastName());
		user.SetPassword(account.getPassword());
		user.SetPhoneNumber(account.getPassword());
		return user;
	}

	public static CustomerAccount convertFromMiladToAzadeh(Account account)
	{
		CustomerAccount user = new CustomerAccount();
		user.SetAccNumber(Integer.toString(account.getAccountNumber()));
		user.SetCreditLimit((long) account.getCreditLimit());
		user.SetEmailAddress(account.getEmail());
		user.SetFirstName(account.getFirstName());
		user.SetLastName(account.getLastName());
		user.SetPassword(account.getPassword());
		user.SetPhoneNumber(account.getPassword());
		return user;
	}

	public static Account convertFromAzadehToMilad(CustomerAccount account)
	{
		Account user = new Account(Integer.parseInt(account.GetAccNumber()),
				account.GetFirstName(), account.GetLastName(),
				account.GetEmailAddress(), account.GetPhoneNumber(),
				account.GetPassword(), (int) account.GetCreditLimit(),
				account.GetFirstName());

		return user;
	}
}
