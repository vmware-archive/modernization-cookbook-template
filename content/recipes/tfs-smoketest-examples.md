+++
date = "2018-01-22T09:49:41-05:00"
title = "TFS Smoketest Examples"
tags = ["tfs", "smoketest", "testing", "Apptxdojo"]

taxonomy=["CLIENT"]
+++

### TFS Smoketest Overview
The goal of our smoke tests are to ensure two things:

* Ensure that the server has started up.
* Ensure that each endpoint the server uses is available (ex. databases, external services)

### TFS Smoketest Strategy
At the moment, TFS lacks great functionality for performing smoke tests. Instead we need to be a little bit creative. The examples here leverage the powershell release task.

### TFS Getting Your Powershell Release Task
This documentation assumes you have already obtained a release definition. If you haven't yet please click here "put in release recipe".

* Click Add Tasks

![Add Task](/images/AddTask.PNG)

* Click **All** in the upper left corner and scroll down to **Powershell**. Then click **Add** to the right of the task and then **Close** to close the dialog.

![Powershell Smoktest Task](/images/PowershellSmoketest.PNG)

* You should now have an empty powershell script. Click the dropdown to the right of **Type** and change it from **File Path** to **Inline Script**.

![Empty Powershell Script](/images/EmptyPowershellScript.PNG)

### TFS Powershell Arguments
Powershell arguments can be inserted into the script. The variable name must start with a dash.
```
-sql $(dev-ReportingConnectionStringName)
```
After you define this variable, be sure to include it in your script on the first line like this:
```
param($sql)
```
The value of a variable can be set in the variable field or be an external variable which is configured on the Variables tab.

![Variable Groups](/images/VariableGroupsSmoketest.PNG)

### How to use these examples
I've included both an image and code block for each example. You can copy the code block and paste directly into the Inline Script section of your powershell task. Change the urls or database connection strings to be unique for your application.

### Using Powershell To Touch Your Server
This script sample will make a simple web request to a url and check the http status code that comes back. If it is a 200, then all is well. It is recommended that an endpoint be choisen that contacts a dependency to ensure the application can make a connection.
```
$HTTP_Request = [System.Net.WebRequest]::Create('https://xxx.apps.xxx.cloud.xxx.com/')
# We then get a response from the site.
$HTTP_Response = $HTTP_Request.GetResponse()
$HTTP_Status = [int]$HTTP_Response.StatusCode

If ($HTTP_Status -eq 200) {
    Write-Host "Site is OK!"
}Else {
    Write-Host "The Site may be down, please check!"
}
$HTTP_Response.Close()
```
![Finished WebRequest Smoketest](/images/FinishedSmoketest1.PNG)

```
$HTTP_Request = [System.Net.WebRequest]::Create('https://xxx-dev.apps.xxx.cloud.xxx.com/api/healthcheck')
# We then get a response from the site.
$HTTP_Response = $HTTP_Request.GetResponse()
$HTTP_Status = [int]$HTTP_Response.StatusCode

If ($HTTP_Status -ge 200 -and  $HTTP_Status -lt 300 ) {
    Write-Host "Site is OK!"
}Else {
    Write-Host "The Site may be down, please check!"
}
$HTTP_Response.Close()
```

### Using Powershell to Touch Your Database
This script sample attempts to make a database connection to your database and check if a table or view exists. This example connected to SQLServer.

```
param($sql)
$cn2 = new-object system.data.SqlClient.SQLConnection($sql);
$cmd = new-object system.data.sqlclient.sqlcommand("JAD_MasterData_EffectivePrograms_Get", $cn2);
$cn2.Open();
if ($cmd.ExecuteNonQuery() -ne -1)
{
    echo "Failed";
} else {
 echo "Connected to database successfully!"
}
$cn2.Close();
```
![Finished Database Smoketest](/images/FinishedSmoketest2.PNG)
