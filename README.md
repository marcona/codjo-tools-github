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

Priority Asap:
 - Pull request management
   - list actual opened pull requests with the follwing display : Repo/user/Title/[URL?][Date Pull request?]
   - generate/create codjo-pom pull request: title + content with for each pull request user and title...

 - Accept name without codjo prefix ? e.g. ```gh fork imports``` instead of ```gh fork codjo-imports```


 - enhance "list" method : add fork date + nb days between fork date and last push to identify projects that have been
   forked without any modification.

 - enhance packaging
 - rename GithubUtil
 - Enhance console help (wich account is used..etc)

Priority later
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
 - use netrc/_netrc by default
 - allow more options (skip proxy, use of _netrc file)
 - error management for program arguments
 - error management in github method calls e.g. delete repoThatDosNotExist
 - add interactive fork : "Wich codjo repository would you like to fork ?"
    see : http://sourceforge.net/projects/javacurses/
          Java-Readline or jline2.
          http://cliche.sourceforge.net/?f=manual
 - avoid multiple if-else to select method in GithubUtil main
 - yeald System.out redirection for unit-test in codjo-test ?
 - Add search in pull request, or in code !
 - automatic download of last version: is it possible to use github to download last artifact, of course ;-)
 - Merge of a "super-pom pull request" which merges the pull requests listed in it.
 - Verify if a "chantier" is really opened (last commit) on codjo-sandbox

 Packaging :
-----------

  * mvn clean install and copy nested artifacts in the right place (precise , isn't it)
   e.g in a deploy directory copy codjo-tools-github.jar and the ./lib directory
  