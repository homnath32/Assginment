# Assignment Project

## Summary

This project contains the solution for the assignment involving:

- **Task 1:** UI Automation using Java + Selenium to automate ChatGPT and CodeChef interaction.
- **Task 2:** TestNG integration with parameterized tests and CSV test data.
- **Task 3:** SQLite database operations with JDBC and POJO class for salary management.

## Features

- Automated prompt entry on ChatGPT and Python code extraction.
- Running Python code on CodeChef online IDE with dynamic inputs.
- Generating CSV test data and parsing test results.
- TestNG HTML report generation for detailed test insights.
- SQLite DB creation, data insertion, queries, and export to CSV.

## How to run

1. Clone the repo:

   ```bash
   git clone https://github.com/homnath32/Assginment.git
Open the project in your preferred IDE (Eclipse, IntelliJ).

Import required Maven dependencies.

To run UI Automation and TestNG tests:

Run Task1 as java application.

Run Task2 as TestNG Test.

Reports will be generated automatically in /test-output/.

To run the SQLite JDBC program:

Run Task3DatabaseOperations.java from the src folder.

Test cases
UI Automation tests verify:

Prompt submission to ChatGPT.

Copy the Response and save as python function.(here This test is failed as chat gpt doesn't give response for automated broser), but custom python function is saved.

Code copy and paste into CodeChef.

Output validation based on test CSV data.

DB operations test:

Random data insertion.

Queries for highest salary and export CSV.

Sorting and filtering employee records.
