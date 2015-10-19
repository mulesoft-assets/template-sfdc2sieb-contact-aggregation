/**
 * Mule Anypoint Template
 * Copyright (c) MuleSoft, Inc.
 * All rights reserved.  http://www.mulesoft.com
 */

package org.mule.templates.integration;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mule.MessageExchangePattern;
import org.mule.api.MuleEvent;
import org.mule.api.lifecycle.InitialisationException;
import org.mule.modules.siebel.api.model.response.CreateResult;
import org.mule.processor.chain.SubflowInterceptingChainLifecycleWrapper;
import org.mule.tck.junit4.rule.DynamicPort;
import org.mule.templates.builders.SfdcObjectBuilder;

import com.sforce.soap.partner.SaveResult;

/**
 * The objective of this class is to validate the correct behavior of the flows
 * for this Mule Template that make calls to external systems.
 * 
 * @author cesar.garcia
 */
public class BusinessLogicIT extends AbstractTemplateTestCase {
	protected static final String TEMPLATE_NAME = "contact-aggregation";

	private List<Map<String, Object>> createdContactsInSalesforce = new ArrayList<Map<String, Object>>();
	private List<Map<String, Object>> createdContactsInSiebel = new ArrayList<Map<String, Object>>();
	
	private SubflowInterceptingChainLifecycleWrapper createContactInSalesforceSubflow;
	private SubflowInterceptingChainLifecycleWrapper createContactInSiebelSubflow;
	private SubflowInterceptingChainLifecycleWrapper deleteContactFromSalesforceSubflow;
	private SubflowInterceptingChainLifecycleWrapper deleteContactFromSiebelSubflow;
	
	
	@Rule
	public DynamicPort port = new DynamicPort("http.port");

	@Before
	public void setUp() throws Exception {
		initializeSubflows();
		createContacts();
	}

	private void initializeSubflows() throws InitialisationException {
		createContactInSalesforceSubflow = getSubFlow("createContactInSalesforce");
		createContactInSalesforceSubflow.initialise();
		
		createContactInSiebelSubflow = getSubFlow("createContactInSiebel");
		createContactInSiebelSubflow.initialise();
		
		deleteContactFromSalesforceSubflow = getSubFlow("deleteContactFromSalesforce");
		deleteContactFromSalesforceSubflow.initialise();
		
		deleteContactFromSiebelSubflow  = getSubFlow("deleteContactFromSiebel");
		deleteContactFromSiebelSubflow.initialise();
	}

	@SuppressWarnings("unchecked")
	private void createContacts() throws Exception {
		Map<String, Object> contact = createContact("SF", 0);
		createdContactsInSalesforce.add(contact);

		MuleEvent event = createContactInSalesforceSubflow.process(getTestEvent(createdContactsInSalesforce,	MessageExchangePattern.REQUEST_RESPONSE));
		List<SaveResult> results = (List<SaveResult>) event.getMessage().getPayload();
		for (int i = 0; i < results.size(); i++) {
			createdContactsInSalesforce.get(i).put("Id", results.get(i).getId());
		}

		contact = createSiebelContact("Siebel", 0);
		createdContactsInSiebel.add(contact);

		event = createContactInSiebelSubflow.process(getTestEvent(contact, MessageExchangePattern.REQUEST_RESPONSE));
		CreateResult cr = (CreateResult) event.getMessage().getPayload();
		contact.put("Id", cr.getCreatedObjects().get(0));
	}

	protected Map<String, Object> createContact(String orgId, int sequence) {
		return SfdcObjectBuilder
				.aContact()
				.with("FirstName", "FirstName_" + orgId + sequence)
				.with("LastName", buildUniqueName(TEMPLATE_NAME, "LastName_" + sequence + "_"))
				.with("Email", buildUniqueEmail("some.email." + sequence))
				.with("Description", "Some fake description")
				.with("MailingCity", "Denver").with("MailingCountry", "US")
				.with("MobilePhone", "123456789")
				.with("Department", "department_" + sequence + "_" + orgId)
				.with("Phone", "123456789").with("Title", "Dr").build();
	}

	protected Map<String, Object> createSiebelContact(String orgId, int sequence) {
		return SfdcObjectBuilder
				.aContact()
				.with("First Name", "FirstName_" + orgId + sequence)
				.with("Last Name", buildUniqueName(TEMPLATE_NAME, "LastName_" + sequence + "_"))
				.with("Email Address", buildUniqueEmail("some.email." + sequence)).build();
	}


	protected String buildUniqueName(String templateName, String name) {
		String timeStamp = new Long(new Date().getTime()).toString();

		StringBuilder builder = new StringBuilder();
		builder.append(name);
		builder.append(templateName);
		builder.append(timeStamp);

		return builder.toString();
	}

	protected String buildUniqueEmail(String contact) {
		String server = "fakemail";

		StringBuilder builder = new StringBuilder();
		builder.append(TEMPLATE_NAME + contact);
		builder.append("@");
		builder.append(server);
		builder.append(".com");

		return builder.toString();
	}

	@After
	public void tearDown() throws Exception {
		deleteTestContactFromSandbox(createdContactsInSalesforce, deleteContactFromSalesforceSubflow);
		deleteTestContactFromSandbox(createdContactsInSiebel, deleteContactFromSiebelSubflow);

	}

	protected void deleteTestContactFromSandbox(List<Map<String, Object>> createdContacts, SubflowInterceptingChainLifecycleWrapper flow)
			throws Exception {
		List<String> idList = new ArrayList<String>();

		// Delete the created contacts in Salesforce / Siebel
		for (Map<String, Object> c : createdContacts) {
			idList.add((String) c.get("Id"));
		}
		flow.process(getTestEvent(idList, MessageExchangePattern.REQUEST_RESPONSE));
		idList.clear();
	}
	

	@SuppressWarnings("unchecked")
	@Test
	public void testGatherDataFlow() throws Exception {
		MuleEvent event = runFlow("gatherDataFlow");
		Iterator<Map<String, String>> mergedList = (Iterator<Map<String, String>>)event.getMessage().getPayload();
		Assert.assertTrue("There should be contacts from source A or source B.", mergedList.hasNext());
	}

}
