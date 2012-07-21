package net.codjo.tools.github;
import java.io.IOException;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.util.List;
import org.eclipse.egit.github.core.Repository;

/**
 *
 */
public class GithubUtil {
    static final String PROXY_CONFIG_MESSAGE = "There was a problem while loading proxy configuration in .gitconfig file\n"
            + " \tProxy configuration is ignored.";

    private static void initProxyConfiguration() throws IOException {
        GitConfigUtil configUtil = tryToLoadProxyConfig();
        if (configUtil != null) {
            if (configUtil.getProxyHost()!=null){
               setProxyAuthentication(configUtil);
            }
        } else {
            System.out.println(PROXY_CONFIG_MESSAGE);
        }
    }

    static GitConfigUtil tryToLoadProxyConfig() {
        try {
            return new GitConfigUtil();
        } catch (Exception e) {
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
            if (args.length == 3 || args.length == 4) {
                String method = args[0];
                String githubUser = args[1];
                String githubPassword = args[2];
                String repoName = "";
                if (args.length == 4) {
                    repoName = args[3];
                }

                initProxyConfiguration();
                service.initGithubClient(githubUser, githubPassword);

                if ("list".equals(method)) {
                    List<Repository> repoList = service.list(githubUser, githubPassword, repoName);
                    ConsoleManager.printRepositoryList(repoList, githubUser);
                }
                else if ("delete".equals(method)) {
                    DeleteRepositoryHandler deleteHandler = new DeleteRepositoryHandler() {
                        public void handleDelete(String githubUser, String githubPassword, String repoName)
                              throws IOException {
                            service.deleteRepo(githubUser, githubPassword, repoName);
                        }
                    };
                    ConsoleManager.deleteRepositor(deleteHandler, githubUser, githubPassword, repoName);
                }
                else if ("fork".equals(method)) {

                    ConsoleManager.forkRepository(new ForkRepositoryHandler() {
                        public void handleFork(String githubUser, String githubPassword, String repoName)
                              throws IOException {
                            service.forkRepo(githubUser, githubPassword, repoName);
                        }
                    }, githubUser, githubPassword, repoName);
                }
                else {
                    ConsoleManager.printHelp();
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
}
