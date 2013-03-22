package net.codjo.tools.github;
import java.io.IOException;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.event.Event;

/**
 *
 */
public class GithubUtil {
    static final String PROXY_CONFIG_MESSAGE =
          "There was a problem while loading proxy configuration in .gitconfig file\n"
          + " \tProxy configuration is ignored.";
    private final List<GitHubCommand> commands = new ArrayList<GitHubCommand>();


    public GithubUtil() {
        initCommands();
    }


    private void initCommands() {
        commands.add(new ListRepositoryCommand());
        commands.add(new ForkRepositoryCommand());
        commands.add(new DeleteRepositoryCommand());
        commands.add(new ListLastEventsSinceLastStabilisation());
        commands.add(new PostIssueCommand());
        commands.add(new HelpCommand());
    }


    private static void initProxyConfiguration() throws IOException {
        GitConfigUtil configUtil = tryToLoadProxyConfig();
        if (configUtil != null) {
            if (configUtil.getProxyHost() != null) {
                setProxyAuthentication(configUtil);
            }
        }
        else {
            System.out.println(PROXY_CONFIG_MESSAGE);
        }
    }


    static GitConfigUtil tryToLoadProxyConfig() {
        try {
            return new GitConfigUtil();
        }
        catch (Exception e) {
            return null;
        }
    }


    private static void setProxyAuthentication(final GitConfigUtil configUtil) throws IOException {

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
        GithubUtilService githubUtilService = new GithubUtilService();
        new GithubUtil().localMain(githubUtilService, args);
    }


    public void localMain(final GithubUtilService service, String[] args) {
        ConsoleManager.printHeader();
        try {
            //TODO Improve argument management
            if (args.length == 3 || args.length == 4 || args.length == 7) {
                String method = args[0];
                String githubUser = args[1];
                String githubPassword = args[2];
                String issueTitle = "";
                String issueFilePath = "";
                String repoName = "";
                String closedAt = "";
                if (args.length >= 4) {
                    repoName = args[3];
                }
                if (args.length == 7) {//Issue Management
                    issueTitle = args[4];
                    closedAt = args[5];
                    issueFilePath = args[6];
                }

                initProxyConfiguration();
                service.initGithubClient(githubUser, githubPassword);

                for (GitHubCommand command : commands) {
                    //TODO Improve argument management
                    command.doCommand(service, method, githubUser, githubPassword, repoName, issueTitle, issueFilePath,
                                      closedAt);
                }
                ConsoleManager.printQuotas(service.getGitHubQuota());
            }
            else {
                ConsoleManager.printHelp();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static class PostIssueCommand implements GitHubCommand {

        public void doCommand(final GithubUtilService service,
                              String method,
                              String githubUser,
                              String githubPassword,
                              String repoName, final String issueTitle, final String issueFilePath, String state)
              throws IOException {
            if ("postIssue".equals(method)) {
                final Issue issue = service.postIssue(githubUser, githubPassword, repoName, issueTitle, issueFilePath,
                                                      state);
                ConsoleManager.printPostIssueResult(githubUser, issue);
            }
        }
    }

    private static class DeleteRepositoryCommand implements GitHubCommand {

        public void doCommand(final GithubUtilService service,
                              String method,
                              String githubUser,
                              String githubPassword,
                              String repoName, String issueTitle, String issueFilePath, String state)
              throws IOException {
            if ("delete".equals(method)) {
                DeleteRepositoryHandler deleteHandler = new DeleteRepositoryHandler() {
                    public void handleDelete(String githubUser, String githubPassword, String repoName)
                          throws IOException {
                        service.deleteRepo(githubUser, githubPassword, repoName);
                    }
                };
                ConsoleManager.deleteRepositor(deleteHandler, githubUser, githubPassword, repoName);
            }
        }
    }

    private static class ListRepositoryCommand implements GitHubCommand {

        public void doCommand(final GithubUtilService service,
                              String method,
                              String githubUser,
                              String githubPassword,
                              String repoName, String issueTitle, String issueFilePath, String state)
              throws IOException {
            if ("list".equals(method)) {
                List<Repository> repoList = service.list(githubUser, githubPassword, repoName);
                ConsoleManager.printRepositoryList(repoList, githubUser);
            }
        }
    }

    private static class ListLastEventsSinceLastStabilisation implements GitHubCommand {

        public void doCommand(final GithubUtilService service,
                              String method,
                              String githubUser,
                              String githubPassword,
                              String repoName, String issueTitle, String issueFilePath, String state)
              throws IOException {
            if ("events".equals(method)) {
                //TODO ask for githubUser password
                //TODO get the status, for example if pull request has been merged
                List<Event> pullRequests = service.eventsSinceLastRelease(githubUser, githubPassword, repoName,
                                                                          "for release");
                ConsoleManager.printEvents(pullRequests, githubUser);
            }
        }
    }

    private static class ForkRepositoryCommand implements GitHubCommand {

        public void doCommand(final GithubUtilService service,
                              String method,
                              String githubUser,
                              String githubPassword,
                              String repoName, String issueTitle, String issueFilePath, String state)
              throws IOException {
            if ("fork".equals(method)) {
                ConsoleManager.forkRepository(new ForkRepositoryHandler() {
                    public void handleFork(String githubUser, String githubPassword, String repoName)
                          throws IOException {
                        service.forkRepo(githubUser, githubPassword, repoName);
                    }
                }, githubUser, githubPassword, repoName);
            }
        }
    }

    private static class HelpCommand implements GitHubCommand {

        public void doCommand(final GithubUtilService service,
                              String method,
                              String githubUser,
                              String githubPassword,
                              String repoName, String issueTitle, String issueFilePath, String state)
              throws IOException {
            if (!"fork".equals(method) && !"delete".equals(method) && !"list".equals(method)
                && !"events".equals(method) && !"postIssue".equals(method)) {
                ConsoleManager.printHelp();
            }
        }
    }
}
