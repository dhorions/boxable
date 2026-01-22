# Instructions for resolving issues
---
description: Instructions to help GitHub Copilot resolve issues in the codebase.

applyTo: **
---
When presented with an issue description, analyze if the described issue is legitimate or a false issue created by a user that doesn't have a full understanding of the library.
If the issue is legitimate, first create a unit test that reproduces the issue. Each unit test should create a clear pdf file that allows a human to visually verify the issue easily. The resulting pdf should always have a description of what the user should expect to see in order to confirm the issue is reproduced. Either describe in a separate column or a row what the expected behavior is, not what the failing behaviour is, but what the behaviour is when the test is successful.
Run the test case using mvn test -Dtest=<testclassname>.
After the user confirms the test case reproduces the issue, proceed to fix the issue in the code.
If the issue is not legitimate, politely explain to the user why the issue is not valid, referencing relevant parts of the codebase or documentation.  Ask the user if a unit test should be created to demonstrate the correct behavior.
Always ensure that any code changes are covered by unit tests that verify the correct behavior.
