
# Anypoint Template: Salesforce and Siebel Contact Aggregation	

<!-- Header (start) -->
Aggregates contacts from Salesforce and Oracle Siebel into a CSV file. This basic pattern can be modified to collect from more or different sources and to produce formats other than CSV. You can trigger this manually or programmatically with an HTTP call. 

![34358fc4-aaba-417e-a0d2-0829f121c272-image.png](https://exchange2-file-upload-service-kprod.s3.us-east-1.amazonaws.com:443/34358fc4-aaba-417e-a0d2-0829f121c272-image.png)

Contacts are sorted such that the contacts only in Salesforce appear first, followed by contacts only in Siebel, and lastly by contacts found in both systems. The custom sort or merge logic can be easily modified to present the data as needed. This template also serves as a base for building APIs using the Anypoint Platform.

Aggregation templates can be easily extended to return a multitude of data in mobile friendly form to power your mobile initiatives by providing easily consumable data structures from otherwise complex backend systems.

![](https://www.youtube.com/embed/Ms7PquvCnHM?wmode=transparent)
[![YouTube Video](http://img.youtube.com/vi/Ms7PquvCnHM/0.jpg)](https://www.youtube.com/watch?v=Ms7PquvCnHM)

<!-- Header (end) -->

# License Agreement
This template is subject to the conditions of the <a href="https://s3.amazonaws.com/templates-examples/AnypointTemplateLicense.pdf">MuleSoft License Agreement</a>. Review the terms of the license before downloading and using this template. You can use this template for free with the Mule Enterprise Edition, CloudHub, or as a trial in Anypoint Studio. 
# Use Case
<!-- Use Case (start) -->
Aggregate contacts from Salesforce and Oracle Siebel Business Objects, compare them to see what contacts can only be found in one of the two and what contacts are in both instances. 

This template generates the result as a CSV report that is sent by email.

This template extracts data from two systems, aggregates data, compares values of fields for the objects, and generates a report on the differences. 
<!-- Use Case (end) -->

# Considerations
<!-- Default Considerations (start) -->
<!-- Default Considerations (end) -->
<!-- Considerations (start) -->
To make this template run, there are certain preconditions that must be considered. All of them deal with the preparations in both, that must be made for the template to run smoothly. Failing to do so can lead to unexpected behavior of the template.
<!-- Considerations (end) -->

## Salesforce Considerations

- Where can I check that the field configuration for my Salesforce instance is the right one? See: <a href="https://help.salesforce.com/HTViewHelpDoc?id=checking_field_accessibility_for_a_particular_field.htm&language=en_US">Salesforce: Checking Field Accessibility for a Particular Field</a>.
- Can I modify the Field Access Settings? How? See: <a href="https://help.salesforce.com/HTViewHelpDoc?id=modifying_field_access_settings.htm&language=en_US">Salesforce: Modifying Field Access Settings</a>.

### As a Data Source

If a user who configures the template for the source system does not have at least *read only* permissions for the fields that are fetched, then an *InvalidFieldFault* API fault displays.

```
java.lang.RuntimeException: [InvalidFieldFault [ApiQueryFault 
[ApiFault  exceptionCode='INVALID_FIELD'
exceptionMessage='Account.Phone, Account.Rating, Account.RecordTypeId, 
Account.ShippingCity
^
ERROR at Row:1:Column:486
No such column 'RecordTypeId' on entity 'Account'. If you are 
attempting to use a custom field, be sure to append the '__c' 
after the custom field name. Reference your WSDL or the 
describe call for the appropriate names.'
]
row='1'
column='486'
]
]
```

## Oracle Siebel Considerations

This template uses date time or timestamp fields from Oracle Siebel to do comparisons and take further actions. While the template handles the time zone by sending all such fields in a neutral time zone, it cannot discover the time zone in which the Siebel instance is in. It's up to you to provide this information. See [Oracle's Setting Time Zone Preferences](http://docs.oracle.com/cd/B40099_02/books/Fundamentals/Fund_settingoptions3.html).

### As a Data Destination

To make the Siebel connector work smoothly, you have to provide the correct version of the Siebel JAR files (Siebel.jar, SiebelJI_enu.jar) that work with your Siebel installation.

# Run it!
Simple steps to get this template running.
<!-- Run it (start) -->

<!-- Run it (end) -->

## Running On Premises
In this section we help you run this template on your computer.
<!-- Running on premise (start) -->
<!-- Running on premise (end) -->

### Where to Download Anypoint Studio and the Mule Runtime
If you are new to Mule, download this software:

+ [Download Anypoint Studio](https://www.mulesoft.com/platform/studio)
+ [Download Mule runtime](https://www.mulesoft.com/lp/dl/mule-esb-enterprise)

**Note:** Anypoint Studio requires JDK 8.
<!-- Where to download (start) -->

<!-- Where to download (end) -->

### Importing a Template into Studio
In Studio, click the Exchange X icon in the upper left of the taskbar, log in with your Anypoint Platform credentials, search for the template, and click Open.
<!-- Importing into Studio (start) -->

<!-- Importing into Studio (end) -->

### Running on Studio
After you import your template into Anypoint Studio, follow these steps to run it:

1. Locate the properties file `mule.dev.properties`, in src/main/resources.
2. Complete all the properties required per the examples in the "Properties to Configure" section.
3. Right click the template project folder.
4. Hover your mouse over `Run as`.
5. Click `Mule Application (configure)`.
6. Inside the dialog, select Environment and set the variable `mule.env` to the value `dev`.
7. Click `Run`.
<!-- Running on Studio (start) -->

<!-- Running on Studio (end) -->

### Running on Mule Standalone
Update the properties in one of the property files, for example in mule.prod.properties, and run your app with a corresponding environment variable. In this example, use `mule.env=prod`. 
After this, to trigger the use case you just need to browse to the local HTTP listener with the port you configured in your file. If this is, for instance, `9090` then you should browse to: `http://localhost:9090/generatereport` and this will create a CSV report and send it to the mails set.

## Running on CloudHub
When creating your application in CloudHub, go to Runtime Manager > Manage Application > Properties to set the environment variables listed in "Properties to Configure" as well as the mule.env value.
<!-- Running on Cloudhub (start) -->
Once your app is all set and started, supposing you choose as domain name `template-sfdc2sieb-contact-aggregation` to trigger the use case you just need to browse to `http://template-sfdc2sieb-contact-aggregation.cloudhub.io/generatereport` and report will be sent to the emails configured.
<!-- Running on Cloudhub (end) -->

### Deploying a Template in CloudHub
In Studio, right click your project name in Package Explorer and select Anypoint Platform > Deploy on CloudHub.
<!-- Deploying on Cloudhub (start) -->

<!-- Deploying on Cloudhub (end) -->

## Properties to Configure
To use this template, configure properties such as credentials, configurations, etc.) in the properties file or in CloudHub from Runtime Manager > Manage Application > Properties. The sections that follow list example values.
### Application Configuration
<!-- Application Configuration (start) -->
- http.port `9090` 

### Salesforce Connector Configuration
- sfdc.username `bob.dylan@sfdc`
- sfdc.password `DylanPassword123`
- sfdc.securityToken `avsfwCUl7apQs56Xq2AKi3X`

### Oracle Siebel Connector Configuration
- sieb.user `user`
- sieb.password `secret`
- sieb.server `server`
- sieb.serverName `serverName`
- sieb.objectManager `objectManager`
- sieb.port `2321`

### SMTP Services Configuration
- smtp.host `smtp.gmail.com`
- smtp.port `587`
- smtp.user `exampleuser@gmail.com`
- smtp.password `ExamplePassword456`

### Email Details
- mail.from `exampleuser@gmail.com`
- mail.to `woody.guthrie@gmail.com`
- mail.subject `Salesforce Contacts Report`
- mail.body `Contacts report comparing contacts from Salesforce and Siebel Contacts`
- attachment.name `OrderedReport.csv`
<!-- Application Configuration (end) -->

# API Calls
<!-- API Calls (start) -->
Salesforce imposes limits on the number of API Calls that can be made. However, we make a API call to Salesforce only once during aggregation.
<!-- API Calls (end) -->

# Customize It!
This brief guide provides a high level understanding of how this template is built and how you can change it according to your needs. As Mule applications are based on XML files, this page describes the XML files used with this template. More files are available such as test classes and Mule application files, but to keep it simple, we focus on these XML files:

* config.xml
* businessLogic.xml
* endpoints.xml
* errorHandling.xml
<!-- Customize it (start) -->
<!-- Customize it (end) -->

## config.xml
<!-- Default Config XML (start) -->
This file provides the configuration for connectors and configuration properties. Only change this file to make core changes to the connector processing logic. Otherwise, all parameters that can be modified should instead be in a properties file, which is the recommended place to make changes.
<!-- Default Config XML (end) -->
<!-- Config XML (start) -->
<!-- Config XML (end) -->

## businessLogic.xml
<!-- Default Business Logic XML (start) -->
The functional aspect of this template is implemented in this XML file, directed by a flow responsible for conducting the aggregation of data, comparing records, and finally formatting the output report.

Using the Scatter-Gather component this template queries the data from different systems. After that, the aggregation is implemented in a DataWeave 2 script using the Transform component. Aggregated results are sorted by source of existence:

1. Contacts only in Salesforce
2. Contacts only in Siebel
3. Contacts in both Salesforce and Siebel

The results are  transformed to CSV format. The final report in CSV format is sent to email to addresses you configure in the `mule.*.properties` file. Contacts are matched by email address - a record in both organizations with the same email address is considered the same contact.
<!-- Default Business Logic XML (end) -->
<!-- Business Logic XML (start) -->
<!-- Business Logic XML (end) -->

## endpoints.xml
<!-- Default Endpoints XML (start) -->
This file provides the endpoint to start the aggregation. This Template has an HTTP Inbound Endpoint as the way to trigger the use case.
### Trigger Flow
**HTTP Inbound Endpoint** - Start Report Generation

- `${http.port}` is set as a property to be defined either on a property file or in CloudHub environment variables.
- The path configured by default is `generatereport` and you are free to change for the one you prefer.
- The host name for all endpoints in your CloudHub configuration should be defined as `localhost`. CloudHub routes requests from your application domain URL to the endpoint.
<!-- Default Endpoints XML (end) -->
<!-- Endpoints XML (start) -->
<!-- Endpoints XML (end) -->

## errorHandling.xml
<!-- Default Error Handling XML (start) -->
This file handles how your integration reacts depending on the different exceptions. This file provides error handling that is referenced by the main flow in the business logic.
<!-- Default Error Handling XML (end) -->
<!-- Error Handling XML (start) -->
<!-- Error Handling XML (end) -->
<!-- Extras (start) -->
<!-- Extras (end) -->
