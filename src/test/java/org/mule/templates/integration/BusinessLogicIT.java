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

	private static final String CONTACTS_FROM_SALESFORCE = "contactsFromSalesforce";
	private static final String CONTACTS_FROM_SIEBEL = "contactsFromSiebel";

	private List<Map<String, Object>> createdContactsInSalesforce = new ArrayList<Map<String, Object>>();
	private List<Map<String, Object>> createdContactsInSiebel = new ArrayList<Map<String, Object>>();

	@Rule
	public DynamicPort port = new DynamicPort("http.port");

	@Before
	public void setUp() throws Exception {

		createContacts();
	}

	@SuppressWarnings("unchecked")
	private void createContacts() throws Exception {
		SubflowInterceptingChainLifecycleWrapper flow = getSubFlow("createContactInSalesforce");
		flow.initialise();

		Map<String, Object> contact = createContact("SF", 0);
		createdContactsInSalesforce.add(contact);

		MuleEvent event = flow.process(getTestEvent(createdContactsInSalesforce,	MessageExchangePattern.REQUEST_RESPONSE));
		List<SaveResult> results = (List<SaveResult>) event.getMessage().getPayload();
		for (int i = 0; i < results.size(); i++) {
			createdContactsInSalesforce.get(i).put("Id", results.get(i).getId());
		}

		flow = getSubFlow("createContactInSiebel");
		flow.initialise();

		contact = createSiebelContact("Siebel", 0);
		createdContactsInSiebel.add(contact);

		event = flow.process(getTestEvent(contact, MessageExchangePattern.REQUEST_RESPONSE));
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

		deleteTestContactFromSandBox(createdContactsInSalesforce, "deleteContactFromSalesforce");
		deleteTestContactFromSandBox(createdContactsInSiebel, "deleteContactFromSiebel");

	}

	protected void deleteTestContactFromSandBox(List<Map<String, Object>> createdContacts, String deleteFlow)
			throws Exception {
		List<String> idList = new ArrayList<String>();

		// Delete the created contacts in Salesforce
		SubflowInterceptingChainLifecycleWrapper flow = getSubFlow(deleteFlow);
		flow.initialise();

		for (Map<String, Object> c : createdContacts) {
			idList.add((String) c.get("Id"));
		}
		flow.process(getTestEvent(idList, MessageExchangePattern.REQUEST_RESPONSE));
		idList.clear();

	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGatherDataFlow() throws Exception {
		SubflowInterceptingChainLifecycleWrapper flow = getSubFlow("gatherDataFlow");
		flow.setMuleContext(muleContext);
		flow.initialise();
		flow.start();

		MuleEvent event = flow.process(getTestEvent("", MessageExchangePattern.REQUEST_RESPONSE));
		Iterator<Map<String, String>> mergedList = (Iterator<Map<String, String>>)event.getMessage().getPayload();
		Assert.assertTrue("There should be contacts from source A or source B.", mergedList.hasNext());
	}

}
