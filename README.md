codjo-tools-github
=================

A very simple tool for github to be used in command line.
It will be used to manage codjo project


Example :
---------

```java -jar codjo-tools-github.jar```

```

          ,---.           **********************************************
         ( @ @ )          *             Codjo Github Tool              *
          ).-.(           * a really cool command line tool for github *
         //|||\\          **********************************************

 Did you mean :
         - gh list [ACCOUNT_NAME] : list all repositories from ACCOUNT_NAME
         - gh fork REPO_NAME      : fork a repository from codjo
         - gh delete REPO_NAME    : delete a repository if exists
```



```java -jar codjo-tools-github.jar list marcona XXXXX```

```
          ,---.           **********************************************
         ( @ @ )          *             Codjo Github Tool              *
          ).-.(           * a really cool command line tool for github *
         //|||\\          **********************************************


Here are the repositories from marcona
 Last push				Name
	10/07/2012 19:57		codjo-github
	21/07/2012 14:22		codjo-tools-github
	24/10/2011 00:18		experiment-jade-websocket
	19/07/2012 00:30		visual-formula
 ....


	For your information, you have 4999 requests left
```


TODO list :
-----------
 - Improve unit tests
      - DONE multiple case for repository deletion
      - commonalize console messages between production code and tests ?
 - DONE Make unit tests be independent from env :
      - DONE proxy trace if outside corporate proxy
      - DONE end of line in console are different between os
 - DONE enhance console display
 - deal with external dependencies
     - avoid if possible external dependencies (actually use egit from eclipse)
 - DONE show the github quota
 - DONE refactoring (package codjo.tools)
 - DONE Avoid delete repo on codjo account
 - DONE add unit tests ... i know its bad ;-)
 - cmd is not generic enough
 - add .cmd(DONE) and .ksh in resource
 - avoid multiple if-else to select method
 - use netrc/_netrc by default
 - allow more options (skip proxy, use of _netrc file)
 - error management for program arguments
 - error management in github method calls e.g. delete repoThatDosNotExist
 - Pull request management
   - list actual opened pull requests for codjo-pom pull request ("include in release X.XX...")
 - Verify if a "chantier" is really opened (last commit) on codjo-sandbox
 - add interactive fork : "Wich codjo repository would you like to fork ?"
 - enhance packaging
 - yeald System.out redirection for unit-test in codjo-test ?
 - Add search in pull request, or in code !
 - automatic download of last version: is it possible to use github to download last artifact, of course ;-)

 Packaging :
-----------

  * mvn install and copy nested artifacts in the right place (precise , isn't it)
  