package net.codjo.tools.github;


import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.RepositoryId;
import org.eclipse.egit.github.core.client.RequestException;
import org.eclipse.egit.github.core.service.RepositoryService;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import static org.eclipse.egit.github.core.client.IGitHubConstants.SEGMENT_REPOS;

public class ConsoleManager {
    final static SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");

     static void printRepositoryList(List<Repository> repoList, String githubUser){
        System.out.println("\nHere are the repositories from " + githubUser);
        System.out.println("\tLast push\t\t\t\tName");
        for (Repository repository : repoList) {
            Date pushedAt = repository.getPushedAt();
            System.out
                    .println("\t" + format.format(pushedAt) + "\t\t" + repository.getName());
        }
    }

    static void deleteRepositor(DeleteRepositoryHandler deleteHandler, String githubUser, String githubPassword, String repoName) throws IOException {
        if ("codjo".equalsIgnoreCase(githubUser)) {
            System.out.println("\tRepositoy deletion with codjo account is not allowed.");
            System.out.println("\t--> Please, use web interface instead.");
        }
        else {
            System.out
                    .print("Do you really want to delete the repository " + repoName + " on  " + githubUser
                            + " account ? (y = yes / n = no/) : ");
            String userInputAsString = new Scanner(System.in).next();
            char userInput = 'n';
            if (userInputAsString != null && !userInputAsString.trim().isEmpty()) {
                userInput = userInputAsString.toLowerCase().charAt(0);
            }
            if (userInput == 'y') {
                try {
                    deleteHandler.handleDelete(githubUser, githubPassword, repoName);
                    System.out.println("\tRepository " + repoName + " has been removed from " + githubUser + " account");
                }
                catch (RequestException e) {
                    System.err.println("\tRepository " + repoName + " doesn't exist\n\n");
                    e.printStackTrace();
                }

            }
        }
    }

    static void forkRepository(ForkRepositoryHandler handler, String githubUser, String githubPassword, String repoName) throws IOException {
        try {
            handler.handleFork(githubUser, githubPassword, repoName);
            System.out.println("\tRepository " + repoName + " has been forked from codjo.");
        }
        catch (RequestException e) {
            System.out.println("\tRepository " + repoName + " doesn't exist\n\n");
            e.printStackTrace();
        }
    }

    public static void printHelp(String header) {
        System.out.println(header);
        System.out.println(" Did you mean :");
        System.out.println("         - gh list [ACCOUNT_NAME] : list all repositories from ACCOUNT_NAME");
        System.out.println("         - gh fork REPO_NAME      : fork a repository from codjo");
        System.out.println("         - gh delete REPO_NAME    : delete a repository if exists");

    }

    public static void printQuotas(int gitHubQuota) {
        if (gitHubQuota != -1) {
            System.out.println("\n\n\tFor your information, you have " + gitHubQuota + " requests left");
        }
    }
}

interface DeleteRepositoryHandler{
    void handleDelete(String githubUser, String githubPassword, String repoName) throws IOException;
}

interface ForkRepositoryHandler{
    void handleFork(String githubUser, String githubPassword, String repoName) throws IOException;
}