# Instructions for resolving issues
---
description: Instructions to help GitHub Copilot resolve issues in the codebase.

applyTo: **
---
## Issue Resolution Guidelines
- When presented with an issue description, analyze if the described issue is legitimate or a false issue created by a user that doesn't have a full understanding of the library.
- If the issue is legitimate, first create a unit test that reproduces the issue. 
- Each unit test should create a clear pdf file that allows a human to visually verify the issue easily. 
```
File file = new File("target/CSVexampleAdvanced.pdf");
        System.out.println("Sample file saved at : " + file.getAbsolutePath());
        file.getParentFile().mkdirs();
        doc.save(file);
        doc.close();
```
- The resulting pdf should always have a description of what the user should expect to see in order to confirm the issue is reproduced. 
  - Either describe in a separate column or a row what the expected behavior is, not what the failing behaviour is, but what the behaviour is when the test is successful.
  - Identify different variants of the issue if applicable, and create separate tables inside the same pdf to demonstrate the different variants.
  - When the test cases are vastly different, create separate unit tests for each variant, that each provide a separate pdf file.
  - Run the test case using mvn test -Dtest=<testclassname>.
- After the user confirms the test case reproduces the issue, proceed to fix the issue in the code.
- If the issue is not legitimate, politely explain to the user why the issue is not valid, referencing relevant parts of the codebase or documentation.  
  - Ask the user if a unit test should be created to demonstrate the correct behavior.
## New Features
 -  Always ensure that any new features are covered by unit tests that verify the correct behavior and include a visual pdf output when applicable.
 -  Follow the same guidelines as for issue reproduction when creating unit tests for new features.
 