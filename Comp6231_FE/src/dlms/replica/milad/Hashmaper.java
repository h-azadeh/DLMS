package dlms.replica.milad;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Hashmaper Class
 * 
 * @author Milad
 *
 */

public class Hashmaper implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Map<Character, List<Account>> aAccountDB;
    Map<Character, List<Loan>> aLoanDB;
    private Integer updateID;
    
    public Hashmaper(Map<Character, List<Account>> newAccountDB, Map<Character, List<Loan>> newLoanDB, Integer updateID){
    	aAccountDB = new HashMap<Character, List<Account>>();
    	aLoanDB = new HashMap<Character, List<Loan>>();
    	this.aAccountDB.putAll(newAccountDB);
    	this.aLoanDB.putAll(newLoanDB);   
    	this.updateID = updateID;
    }
    public Map<Character, List<Account>> getAccountHashmap() {
		return aAccountDB;
	}
	public void setAccountHashmap(Map<Character, List<Account>> aAccHashMap) {
		this.aAccountDB.putAll(aAccHashMap);
	}
	public Map<Character, List<Loan>> getLoanHashmap() {
		return aLoanDB;
	}
	public void setLoanHashmap(Map<Character, List<Loan>> aLoanHashMap) {
		this.aLoanDB.putAll(aLoanHashMap);
	}
	public Integer getUpdateID() {
		return updateID;
	}
	public void setUpdateID(Integer updateID) {
		this.updateID = updateID;
	}

}
