codjo-tools-github
=================

A very simple tool for github to be used in command line.
It will be used to manage codjo framework project (https://github.com/codjo or http://www.codjo.net/)


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
         - gh postIssue REPO_NAME ISSUE_TITLE STATE ISSUE_CONTENT_FILE_PATH LABEL_LIST   : add a new issue in repository
         - gh events [ACCOUNT_NAME] [ACCOUNT_PASSWORD]    : list all events since last stabilisation (last pull request with 'For Release' title)
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
see : https://github.com/marcona/codjo-tools-github/issues?labels=&page=1&state=open


 Packaging :
-----------

  * mvn release:prepare
  * mvn release:perform -DconnectionUrl=scm:git:file:///c:/dev/projects/github/codjo-tools-github/.git
  * unzip /target/codjo-tools-github-assembly.zip where you want
