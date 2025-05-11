My grocery list
Requirements:

Java >= 21
Git + GitHub account
Useful commands
In the project, there is a maven wrapper. It is here to avoid having to install maven on your machine. Depending on your os, you may need the mvnw or mvnw.cmd script. I'll use mvnw in this readme.

All commands should be run from the project root directory.

Compile the project
./mvnw clean install
This will create a file ending with .jar files in the target directory.

Run the project
java -jar ./target/dp-2024-2025__grocery-list-1.0-SNAPSHOT.jar
Run the project tests
./mvnw test
Run the project with a specific test
./mvnw test -Dtest=com.fges.SmokeTest#should_allways_pass
What is this project about ?
The goal of this project is to create a simple grocery list application. It was built fast, without any concern about maintainability.

It is a command line application.

If you are not familiar with CLI applications, you need to know a few things:

Application starts with stdout, stderr and stdin
stderr: to log things for debug purpose
stdout: to output something
stdin: to communicate with the application while it is running
Application can be ran with arguments (see 1000's of examples on the internet)
Application terminated with a status code
0 for success
anything else for failure
Available commands at start
Everything is temporary, this is just the state of the application at start

The application takes track of a grocery list and store it in a json file. The file structure

The application might currently have some bugs 😱, but you are also here for that!

What you can change
Well... Almost anything except:

the commands in the readme should ALWAYS work
use another language than Java
use functional programming
remove the SmokeTest
remove the Main class
change the Main#main method
remove the exec method of change its return type to anything else other than the primitive int
and of course anything outside the common ethic
Features
Add an item to the list
java -jar ./target/dp-2024-2025__grocery-list-1.0-SNAPSHOT.jar -s groceries.json add "Milk" 10
List the items in the list
java -jar ./target/dp-2024-2025__grocery-list-1.0-SNAPSHOT.jar -s groceries.json list
Remove an item from the list
java -jar ./target/dp-2024-2025__grocery-list-1.0-SNAPSHOT.jar -s groceries.json remove "Milk"
About reports
Between EACH class there will be new things to add or change in the project. You will have to:

Do the new features
Find and implement the testcases
Write the report
Reports must be located in the report directory under the corresponding tp (ex: reports/tp1 for the first one, reports/tp2 for the second).

Each time you will have to:

Add a markdown file with the report content
Add a schema to explain how the project works (class diagram, sequence diagram, as you want) in PNG or SVG format. It should be readable
In the report, explain what you did, your difficulties, your successes, your failures and what points were irritating in the previous code that you add to fix.

Submit your work
On Icampus, you will have a link to a website where you will find forms to submit your work.

You can submit your work early and continue working on it before the deadline.

Anything submitted after the deadline will not be considered, unless you email me before the deadline.

Contact me if you have any questions
Email: quereantho+fges@gmail.com

Do not stay stuck on something, ask for help! I don't bite 😉