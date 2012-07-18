package com.marc.onnet;
import java.io.IOException;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.util.List;
import java.util.Scanner;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.RepositoryId;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.client.RequestException;
import org.eclipse.egit.github.core.service.RepositoryService;

import static org.eclipse.egit.github.core.client.IGitHubConstants.SEGMENT_REPOS;
/**
 *
 */
public class GithubUtil {
    private GitHubClient client = new GitHubClient();


    public void forkRepo(String githubUser, String githubPassword, String repoName) throws IOException {
        client = initGithubClient(githubUser, githubPassword);
        RepositoryService repositoryService = new RepositoryService(client);
        try {
            repositoryService.forkRepository(new RepositoryId("codjo", repoName));
            System.out.println("\tRepository " + repoName + " has been forked from codjo.");
        }
        catch (RequestException e) {
            System.out.println("\tRepository " + repoName + " doesn't exist\n\n");
            e.printStackTrace();
        }
    }


    public void deleteRepo(String githubUser, String githubPassword, String repoName) throws IOException {
        client = initGithubClient(githubUser, githubPassword);
        try {
            client.delete(SEGMENT_REPOS + "/" + githubUser + "/" + repoName);
            System.out.println("\tRepository " + repoName + " has been removed from " + githubUser + " account");
        }
        catch (RequestException e) {
            System.err.println("\tRepository " + repoName + " doesn't exist\n\n");
            e.printStackTrace();
        }
    }


    public List<Repository> list(String githubUser, String githubPassword, String repoName) throws IOException {
        client = initGithubClient(githubUser, githubPassword);

        RepositoryService repositoryService = new RepositoryService(client);
        return repositoryService.getRepositories(githubUser);
    }


    private GitHubClient initGithubClient(String githubUser, String githubPassword) throws IOException {
        printOctopuss();
        GitConfigUtil configUtil = new GitConfigUtil();
        setProxyAuthentication(configUtil);
        client.setCredentials(githubUser, githubPassword);
        return client;
    }


    private void setProxyAuthentication(final GitConfigUtil configUtil) throws IOException {

        System.setProperty("http.proxyHost", configUtil.getProxyHost());
        System.setProperty("http.proxyPort", "" + configUtil.getProxyPort());
        System.setProperty("https.proxyHost", configUtil.getProxyHost());
        System.setProperty("https.proxyPort", "" + configUtil.getProxyPort());
        System.setProperty("http.nonProxyHosts", "localhost|127.0.0.1");
        System.setProperty("http.proxySet", "true");

        Authenticator.setDefault(new Authenticator() {
            @Override
            public PasswordAuthentication getPasswordAuthentication() {
                if (getRequestorType() == RequestorType.PROXY) {
                    return new PasswordAuthentication(configUtil.getProxyUserName(),
                                                      configUtil.getProxyPassword().toCharArray());
                }
                else {
                    return super.getPasswordAuthentication();
                }
            }
        });
    }


    public static void main(String[] args) {
        GithubUtil githubUtil = new GithubUtil();
        try {
            String method = args[0];
            String githubUser = args[1];
            String githubPassword = args[2];
            String repoName = "";
            if (args.length == 4) {
                repoName = args[3];
            }

            if ("list".equals(method)) {
                List<Repository> repoList = githubUtil.list(githubUser, githubPassword, repoName);
                System.out.println("\nHere are the repositories from " + githubUser);
                for (Repository repository : repoList) {
                    System.out.println("\t" + repository.getName());
                }
            }
            else if ("delete".equals(method)) {
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
                        githubUtil.deleteRepo(githubUser, githubPassword, repoName);
                    }
                }
            }
            else if ("fork".equals(method)) {
                githubUtil.forkRepo(githubUser, githubPassword, repoName);
            }
            else {
                System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%\n");
                System.out.println(" Vouliez vous dire :");
                System.out.println("         - gh list ");
                System.out.println("         - gh fork [REPO_NAME]");
                System.out.println("         - gh delete [REPO_NAME]");
                System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void printOctopuss() {
        String octopus = "          ,---.           **********************************************\n"
                         + "         ( @ @ )          *             Codjo Github Tool              *\n"
                         + "          ).-.(           * a really cool command line tool for github *\n"
                         + "        // |||\\\\          **********************************************\n";
        System.out.println(octopus);
    }
}
