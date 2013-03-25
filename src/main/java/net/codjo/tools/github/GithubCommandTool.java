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
public class GithubCommandTool {
    static final String PROXY_CONFIG_MESSAGE =
          "There was a problem while loading proxy configuration in .gitconfig file\n"
          + " \tProxy configuration is ignored.";
    private final List<GitHubCommand> commands = new ArrayList<GitHubCommand>();


    public GithubCommandTool() {
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
        new GithubCommandTool().localMain(new GithubUtilServiceImpl(), args);
    }


    public void localMain(final GithubUtilService service, String[] args) {
        ConsoleManager.printHeader();
        try {
            GitHubParameter commandParam = new GitHubParameter(args);

            if (commandParam.githubParamsAreValid()) {
                initProxyConfiguration();
                service.initGithubClient(commandParam.getUser(), commandParam.getPassword());

                for (GitHubCommand command : commands) {
                    command.doCommand(service, commandParam);
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

        public void doCommand(final GithubUtilService service, GitHubParameter ghParam)
              throws IOException {
            if ("postIssue".equals(ghParam.getMethod())) {
                final Issue issue = service.postIssue(ghParam.getUser(),
                                                      ghParam.getPassword(),
                                                      ghParam.getRepoName(),
                                                      ghParam.getIssueTitle(),
                                                      ghParam.getIssueFilePath(),
                                                      ghParam.getIssueState());
                service.addLabels(ghParam.getUser(),
                                  ghParam.getPassword(),
                                  ghParam.getRepoName(),
                                  issue,
                                  ghParam.getLabels());
                ConsoleManager.printPostIssueResult(ghParam.getUser(), issue);
            }
        }
    }

    private static class DeleteRepositoryCommand implements GitHubCommand {

        public void doCommand(final GithubUtilService service, GitHubParameter ghParam)
              throws IOException {
            if ("delete".equals(ghParam.getMethod())) {
                DeleteRepositoryHandler deleteHandler = new DeleteRepositoryHandler() {
                    public void handleDelete(String githubUser, String githubPassword, String repoName)
                          throws IOException {
                        service.deleteRepo(githubUser, githubPassword, repoName);
                    }
                };
                ConsoleManager.deleteRepositor(deleteHandler, ghParam.getUser(),
                                               ghParam.getPassword(),
                                               ghParam.getRepoName());
            }
        }
    }

    private static class ListRepositoryCommand implements GitHubCommand {

        public void doCommand(final GithubUtilService service, GitHubParameter ghParam)
              throws IOException {
            if ("list".equals(ghParam.getMethod())) {
                List<Repository> repoList = service.list(ghParam.getUser(),
                                                         ghParam.getPassword(),
                                                         ghParam.getRepoName());
                ConsoleManager.printRepositoryList(repoList, ghParam.getUser());
            }
        }
    }

    private static class ListLastEventsSinceLastStabilisation implements GitHubCommand {

        public void doCommand(final GithubUtilService service, GitHubParameter ghParam)
              throws IOException {
            if ("events".equals(ghParam.getMethod())) {
                //TODO ask for githubUser password
                //TODO get the status, for example if pull request has been merged
                List<Event> pullRequests = service.eventsSinceLastRelease(ghParam.getUser(),
                                                                          ghParam.getPassword(),
                                                                          ghParam.getRepoName(),
                                                                          "for release");
                ConsoleManager.printEvents(pullRequests, ghParam.getUser());
            }
        }
    }

    private static class ForkRepositoryCommand implements GitHubCommand {

        public void doCommand(final GithubUtilService service, GitHubParameter ghParam)
              throws IOException {
            if ("fork".equals(ghParam.getMethod())) {
                ConsoleManager.forkRepository(new ForkRepositoryHandler() {
                    public void handleFork(String githubUser, String githubPassword, String repoName)
                          throws IOException {
                        service.forkRepo(githubUser, githubPassword, repoName);
                    }
                }, ghParam.getUser(), ghParam.getPassword(), ghParam.getRepoName());
            }
        }
    }

    private static class HelpCommand implements GitHubCommand {

        public void doCommand(final GithubUtilService service, GitHubParameter ghParam)
              throws IOException {
            if (!"fork".equals(ghParam.getMethod()) && !"delete".equals(ghParam.getMethod())
                && !"list".equals(ghParam.getMethod())
                && !"events".equals(ghParam.getMethod()) && !"postIssue".equals(ghParam.getMethod())) {
                ConsoleManager.printHelp();
            }
        }
    }
}
