+++
categories = ["recipes"]
tags = ["[DNS]"]
taxonomy=["CLIENT"]
summary = "How to resolve the FQDN from a single level DNS record"
title = "Local Dns Resolver"
date = 2018-01-11T17:10:01-08:00
review_status = ["INCOMPLETE"]
+++

Deployment to the pre-phx PCF env may require changes to the Data Source(s) that back the EntityModel framework. The previously configured Data Source(s) may operate correctly in the local workstation environment, but may fail in PCF. The errors encountered will simply suggest that the service is unreachable. Although the problem could be related to a network or firewall configuration, in (at least) one case, we discovered that using the FQDN (Fully Qualified Domain Name) fixes the issue.

For Example:

 * `DOMAIN-A` may just work fine on a developer local workstation, but WILL NOT WORK in PCF
 
 * `DOMAIN-A.a.b.boeing.com` WILL PROBABLY WORK in both the developer local workstation and PCF

Discovering the FQDN for the `DOMAIN-A` single level record can be done through PowerShell:
(https://docs.microsoft.com/en-us/powershell/module/dnsclient/resolve-dnsname?view=winserver2012r2-ps)


```
# on Windows 8, PowerShell < 6
> Resolve-DnsName DOMAIN-A
```

```
# on Windows 10, PowerShell 6+
> nslookup DOMAIN-A
```

The output of either of these command will contain the FQDN that can be used to replace the Data Source domain in the DB Connection String.
