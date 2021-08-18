package org.infinity.ui.account.main;

import java.util.ArrayList;

import org.infinity.file.Accounts;

public class AccountManager {
                  
	private static Accounts accountsFile = new Accounts();
	public static Account lastAlt;
	public static ArrayList<Account> registry;

	public ArrayList<Account> getRegistry() {
		return registry;
	}
	
	public static void add(Account account) {
		if (!registry.contains(account)) {
			registry.add(account);
		}
	}

	public void setLastAlt(final Account alt) {
		lastAlt = alt;
	}
	
	public void load() {
		accountsFile.loadAccounts();
	}
	
	public void save() {
		accountsFile.saveAccounts();
	}

	static {
		registry = new ArrayList<Account>();
	}
}
