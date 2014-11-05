/**
 * Mule Anypoint Template
 * Copyright (c) MuleSoft, Inc.
 * All rights reserved.  http://www.mulesoft.com
 */

package org.mule.templates.transformers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;
import org.mule.transformer.AbstractMessageTransformer;

import com.google.common.collect.Lists;

/**
 * This transformer will take to list as input and create a third one that will
 * be the merge of the previous two. The identity of an element of the list is
 * defined by its email.
 * 
 * @author cesar.garcia
 */
public class SalesforceSiebelContactMerge extends AbstractMessageTransformer {

	private static final String QUERY_SALESFORCE = "contactsFromSalesforce";
	private static final String QUERY_SIEBEL = "contactsFromSiebel";

	@Override
	public Object transformMessage(MuleMessage message, String outputEncoding) throws TransformerException {

		List<Map<String, String>> mergedContactList = mergeList(getSalesforceContacts(message, QUERY_SALESFORCE),
				getSiebelContacts(message, QUERY_SIEBEL));

		return mergedContactList;
	}

	private List<Map<String, String>> getSalesforceContacts(MuleMessage message, String propertyName) {
		Iterator<Map<String, String>> iterator = message.getInvocationProperty(propertyName);
		return Lists.newArrayList(iterator);
	}

	private List<Map<String, String>> getSiebelContacts(MuleMessage message, String propertyName) {
		List<Map<String, String>> list = message.getInvocationProperty(propertyName);
		return Lists.newArrayList(list);
	}

	/**
	 * The method will merge the accounts from the two lists creating a new one.
	 * 
	 * @param contactsFromOrgA
	 *            contacts from organization A
	 * @param contactsFromOrgB
	 *            contacts from organization B
	 * @return a list with the merged content of the to input lists
	 */
	public List<Map<String, String>> mergeList(List<Map<String, String>> contactsFromOrgA, List<Map<String, String>> contactsFromOrgB) {
		List<Map<String, String>> mergedContactsList = new ArrayList<Map<String, String>>();

		// Put all contacts from A in the merged mergedContactsList
		for (Map<String, String> contactFromA : contactsFromOrgA) {
			Map<String, String> mergedContact = createContactListFromSalesforce(contactFromA);
			mergedContact.put("IDInA", contactFromA.get("Id"));
			mergedContactsList.add(mergedContact);
		}

		// Add the new contacts from B and update the exiting ones
		for (Map<String, String> contactsFromB : contactsFromOrgB) {
			Map<String, String> contactFromA = findContactInList(contactsFromB.get("Email Address"), mergedContactsList);
			if (contactFromA != null) {
				contactFromA.put("IDInB", contactsFromB.get("Id"));
			} else {
				Map<String, String> mergedAccount = createContactListFromSiebel(contactsFromB);
				mergedAccount.put("IDInB", contactsFromB.get("Id"));
				mergedContactsList.add(mergedAccount);
			}

		}
		return mergedContactsList;
	}

	private Map<String, String> createContactListFromSalesforce(Map<String, String> contact) {
		Map<String, String> mergedContact = new HashMap<String, String>();
		mergedContact.put("Name", contact.get("Name"));
		mergedContact.put("Email", contact.get("Email"));
		mergedContact.put("IDInA", "");
		mergedContact.put("IDInB", "");
		return mergedContact;
	}

	private Map<String, String> createContactListFromSiebel(Map<String, String> contact) {
		Map<String, String> mergedContact = new HashMap<String, String>();
		mergedContact.put("Name", contact.get("First Name") + " " + contact.get("Last Name"));
		mergedContact.put("Email", contact.get("Email Address"));
		mergedContact.put("IDInA", "");
		mergedContact.put("IDInB", "");
		return mergedContact;
	}

	private Map<String, String> findContactInList(String email, List<Map<String, String>> orgList) {
		if (StringUtils.isBlank(email)) {
			return null;
		}

		for (Map<String, String> account : orgList) {
			if (StringUtils.isNotBlank(account.get("Email")) && account.get("Email").equals(email)) {
				return account;
			}
		}
		return null;
	}
}
