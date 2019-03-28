
![logo](docs/img/logo_squared_small.png)

# Remediate the Flag


### (Practical) Application Security Training Platform

Developers aren’t born knowing how to code securely and appsec training is often boring and does not provide practical examples. For the business it is usually not possible to assess competency in secure coding and difficult to calculate ROI on security training.

RTF is an open source Practical Application Security Training platform that *hosts* application security focused exercises. 

Candidates manually find, exploit, and manually remediate the code of a vulnerable application running in a disposable development environment accessed using a web browser. 100% hands-on training, no multiple choice questions involved.

![Picture1](docs/img/rtf_exercise.png)



### Functionalities

- Run vulnerable exercises in docker containers running in a sandboxed environment in the cloud
- Provide users *in seconds* with a turn-key development environment already configured with selected exercise
- Exercise focus on exploitation/remediation or secure coding and target the most common application security issues
- User is provided with automated results during the exercise to increase engagement
- Deployable on AWS through CloudFormation, scales horizontally and vertically
- Setup and Manage Organizations, Teams and Users
- Get Stats at Organization, Region, Team and User level to quickly identify gaps 
- Setup Challenges targeting programming language or specific vulnerability classes
- Reference the AppSecEU 2018 slides for a functional overview.



### AppSec Europe 2018 ###
![AppSecEU](docs/img/appseceu.png)

Slides: [AppSecEU 2018 - Remediate The Flag](docs/AppSecEU18_RemediateTheFlag.pdf) 



### How Does it Work?

Candidates select an exercise, the RTF platform provisions a dedicated environment accessed through a web browser. Candidates then find and manually remediate vulnerable code in the RTF instance by referencing the instructions.
Candidates can check in real time whether security issues were successfully remediated; they can take hints which affect their final score.
When the exercise is completed, the platform provides automated results including code diff and logs. An assessor reviews the exercise results and, if necessary (wrong remediation approach), provides additional feedback to the candidate. The platform can be also setup for automated-only scoring.
It is possible to setup time-boxed tournaments specifying programming languages, developer groups (frontend vs backend, web vs non-web) and target vulnerabilities. Points are used to rank candidates on a “Leaderboard” so that they can compare themselves to their peers.
Full stats are provided at candidate, team and organisation level indicating remediation ratio and time spent on each type of vulnerability and aggregated on category types.
The platform allows to add new exercises, and technologies and target any specific organization needs.



### Architecture
[RTF Platform Architecture](docs/architecture.md)



### Platform Installation

[RTF Platform Installation](docs/install.md)



### Platform Configuration ###

[RTF Platform Configuration](docs/configure.md)



### Create New Exercise

[Create New Exercise](docs/create.md)



### NEW RELEASE COMING SOON ###

RTF v1.5 Highlights:

  - Exercises in Java, .NET, Ruby, NodeJS, GoLang, Python, Solidity
  - SDK for easy exercise creation
  - Improved Tournaments
  - Support for cluster-less deployments (Fargate)



### Recent Changes ###

  - Removed AWS Access Keys requirement, implemented IAM role for Portal container.
  - Completed Challenges flows
  - Automated Region selection based on ping time
  - Completed Exercise Update/Import/Export flows
  - Completed User update use case
  - Completed Organization update use case
  - Completed Reference and Solution documents for existing exercise
  - Published existing exercises as importable JSON
  - Turned Portal-App into Maven project. 
  - Simplified IAM roles for Portal App
  - Configured Capabilities for Linux Containers (NET_ADMIN for iptables instead of privileged)
  - Updated RTF-Java-VApp-Exercise and RTF-Java Base docker images (better performances, bug fixes)
  - UX enhancements when running exercises (now it's all in one tab)



### Contacts ###

[info@remediatetheflag.com](mailto:info@remediatetheflag.com)

