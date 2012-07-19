codjo-github-tool
=================

A very simple tool for github to be used in command line

Nested librairies are in lib directory. (github.core has been cutomized to get rateLimit c.f. Todo List).

TODO list :

 - enhance console display
 - deal with external dependencies
     - avoid if possible external dependencies (actually use egit from eclipse)
     - show the github quota
 - refactoring (package codjo.tools)
 - DONE Avoid delete repo on codjo account
 - add unit tests ... i know its bad ;-)
 - add .cmd and .ksh
 - avoid multiple if-else to select method
 - use netrc/_netrc by default
 - allow more options (skip proxy, use of _netrc file)
 - error management for program arguments
 - error management in github method calls e.g. delete repoThatDosNotExist
 - Pull request management
   - list actual opened pull requests for codjo-pom pull request ("include in release X.XX...")
 - Verify if a "chantier" is really opened (last commit) on codjo-sandbox
 - add interactive fork : "Wich codjo repository would you like to fork ?"