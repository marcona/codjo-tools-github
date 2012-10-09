package net.codjo.tools.github;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import net.codjo.test.common.LogString;
import net.codjo.util.date.DateUtil;
import org.eclipse.egit.github.core.PullRequest;
import org.eclipse.egit.github.core.PullRequestMarker;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static net.codjo.test.common.matcher.JUnitMatchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class GithubUtilTest {
    private static final String endOfLine = System.getProperty("line.separator");
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private GithubUtil githubUtil;
    private GithubUtilService mockGithubService;
    private LogString logString = new LogString();
    private String proxyMessage;


    @Before
    public void setUpStreams() {
        githubUtil = new GithubUtil();
        logString.clear();
        mockGithubService = buildMockService(logString);
        //TODO Testing System.out could be yeald to codjo-test ?
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
        GitConfigUtil gitConfigUtil = GithubUtil.tryToLoadProxyConfig();
        if (gitConfigUtil == null || gitConfigUtil.getProxyHost() == null) {
            proxyMessage = "";
        } else {
            proxyMessage = GithubUtil.PROXY_CONFIG_MESSAGE;
        }
    }


    @After
    public void cleanUpStreams() {
        System.setOut(null);
        System.setErr(null);
    }


    @Test
    public void test_badMethodPrintsHelp() {
        String[] args = new String[]{"badMethod", "githubUser", "githubPassword"};

        githubUtil.localMain(mockGithubService, args);
        assertThat(outContent.toString(), is(helpInConsole(true)));
        assertNoError();
    }


    @Test
    public void test_listDefaultRepositories() {
        String[] args = new String[]{"list", "githubUser", "githubPassword"};
        githubUtil.localMain(mockGithubService, args);
        assertThat(outContent.toString(), is(repositoryListInConsole("githubUser")));
        assertNoError();
    }


    @Test
    public void test_listRepositoriesFromOtherUser() {
        String[] args = new String[]{"list", "githubUser", "githubPassword", "codjo-sandbox"};
        githubUtil.localMain(mockGithubService, args);
        assertThat(outContent.toString(), is(repositoryListInConsole("githubUser")));
        assertNoError();
    }


    @Test
    public void test_forkRepository() {
        String[] args = new String[]{"fork", "githubUser", "githubPassword", "codjo-github-tools"};
        githubUtil.localMain(mockGithubService, args);
        logString.assertContent(
                "initGithubClient(githubUser, githubPassword), forkRepo(githubUser, githubPassword, codjo-github-tools)");
        assertThat(outContent.toString(), is(forkRepositoryInConsole()));
        assertNoError();
    }


    @Test
    public void test_deleteRepository() {
        String[] args = new String[]{"delete", "githubUser", "githubPassword", "codjo-github-tools"};
        String data = "Yes" + endOfLine;
        InputStream stdin = System.in;
        try {
            System.setIn(new ByteArrayInputStream(data.getBytes()));
            githubUtil.localMain(mockGithubService, args);
            logString.assertContent(
                    "initGithubClient(githubUser, githubPassword), deleteRepo(githubUser, githubPassword, codjo-github-tools)");
            assertThat(outContent.toString(), is(deleteRepositoryInConsole("githubUser")));
            assertNoError();
        } finally {
            System.setIn(stdin);
        }
    }

    @Test
    public void test_deleteRepositoryCanceledByUser() {
        String[] args = new String[]{"delete", "githubUser", "githubPassword", "codjo-github-tools"};
        String data = "No" + endOfLine;
        InputStream stdin = System.in;
        try {
            System.setIn(new ByteArrayInputStream(data.getBytes()));
            githubUtil.localMain(mockGithubService, args);
            logString.assertContent(
                    "initGithubClient(githubUser, githubPassword)");
            assertThat(outContent.toString(), is(deleteRepositoryCanceledByUserInConsole()));
            assertNoError();
        } finally {
            System.setIn(stdin);
        }
    }

    @Test
    public void test_deleteWithCodjoAccount() {
        String[] args = new String[]{"delete", "codjo", "githubPassword", "codjo-github-tools"};
        String data = "Yes" + endOfLine;
        InputStream stdin = System.in;
        try {
            System.setIn(new ByteArrayInputStream(data.getBytes()));
            githubUtil.localMain(mockGithubService, args);
            logString.assertContent(
                    "initGithubClient(codjo, githubPassword)");
            assertThat(outContent.toString(), is(deleteRepositoryWithCodjoAccountInConsole()));
            assertNoError();
        } finally {
            System.setIn(stdin);
        }
    }


    @Test
    public void test_noParameterPrintsHelp
            () {
        String[] args = new String[]{};
        githubUtil.localMain(mockGithubService, args);
        assertEquals(helpInConsole(false), outContent.toString());
        assertNoError();
    }

    @Test
    public void test_listOpenedPullRequest() {
        String[] args = new String[]{"pull-requests", "codjo", "githubPassword"};
        githubUtil.localMain(mockGithubService, args);
        logString.assertContent("initGithubClient(codjo, githubPassword)");
        assertThat(outContent.toString(), is(listOpenedPullRequestWithCodjoAccountInConsole()));
        assertNoError();
    }


    private String helpInConsole(boolean wihtQuotas) {
        String result = ConsoleManager.OCTOPUS + endOfLine
                + proxyMessage +
                " Did you mean :" + endOfLine +
                "         - gh list [ACCOUNT_NAME] : list all repositories from ACCOUNT_NAME" + endOfLine +
                "         - gh fork REPO_NAME      : fork a repository from codjo" + endOfLine +
                "         - gh delete REPO_NAME    : delete a repository if exists" + endOfLine;
        if (wihtQuotas) {
            result += "\n"
                    + "\n"
                    + "\tFor your information, you have 5 requests left" + endOfLine;
        }
        return result;
    }


    private String repositoryListInConsole(String githubUser) {
        return ConsoleManager.OCTOPUS + endOfLine + "\n"
                + "Here are the repositories from " + githubUser + endOfLine
                + "\tLast push\t\t\t\tName" + endOfLine
                + "\t19/07/2012 00:00\t\tcodjo-repoOne" + endOfLine
                + "\t05/07/2012 00:00\t\tcodjo-repoTwo" + endOfLine
                + "\n"
                + "\n"
                + "\tFor your information, you have 5 requests left" + endOfLine;
    }


    private String forkRepositoryInConsole() {
        return ConsoleManager.OCTOPUS + "" + endOfLine
                + "\tRepository codjo-github-tools has been forked from codjo." + endOfLine
                + "\n"
                + "\n"
                + "\tFor your information, you have 5 requests left" + endOfLine;
    }


    private String deleteRepositoryInConsole(String githubUser) {
        return ConsoleManager.OCTOPUS + "" + endOfLine
                + "Do you really want to delete the repository codjo-github-tools on  githubUser account ? (y = yes / n = no/) : \n"
                + "\tRepository codjo-github-tools has been removed from " + githubUser + " account" + endOfLine
                + "\n"
                + "\n"
                + "\tFor your information, you have 5 requests left" + endOfLine;
    }

    private String deleteRepositoryCanceledByUserInConsole() {
        return ConsoleManager.OCTOPUS + "" + endOfLine
                + "Do you really want to delete the repository codjo-github-tools on  githubUser account ? (y = yes / n = no/) : "
                + "\n"
                + "\n"
                + "\tFor your information, you have 5 requests left" + endOfLine;
    }

    private String deleteRepositoryWithCodjoAccountInConsole() {
        return ConsoleManager.OCTOPUS + "" + endOfLine
                + "\tRepositoy deletion with codjo account is not allowed.\n"
                + "\t--> Please, use web interface instead." + endOfLine
                + "\n"
                + "\n"
                + "\tFor your information, you have 5 requests left" + endOfLine;
    }


    private String listOpenedPullRequestWithCodjoAccountInConsole() {
        return ConsoleManager.OCTOPUS + "" + endOfLine
                + "\tOpened pull requests from codjo :\n"
                + "\tRepo\t\t\t\tTitle\t\t\t\tDate\t\t\t\tUrl\n"
                + "\trepo1\t\tfirst pullRequest\t\t12/12/2010 00:00\t\thttp://urlr/pullRequest/1 " + endOfLine
                + "\trepository2\t\tSecond pullRequest\t\t01/12/2010 00:00\t\thttp://urlr/pullRequest/2/other " + endOfLine
                + "\n"
                + "\n"
                + "\tFor your information, you have 5 requests left" + endOfLine;
    }

    private void assertNoError() {
        assertEquals("", errContent.toString());
    }


    private GithubUtilService buildMockService(final LogString logString) {
        return new GithubUtilService() {
            @Override
            public GitHubClient initGithubClient(String githubUser, String githubPassword) {
                logString.call("initGithubClient", githubUser, githubPassword);
                return null;
            }


            @Override
            public void forkRepo(String githubUser, String githubPassword, String repoName) throws IOException {
                logString.call("forkRepo", githubUser, githubPassword, repoName);
            }


            @Override
            public void deleteRepo(String githubUser, String githubPassword, String repoName) throws IOException {
                logString.call("deleteRepo", githubUser, githubPassword, repoName);
            }


            @Override
            public int getGitHubQuota() throws IOException {
                return 5;
            }


            @Override
            public List<Repository> list(String githubUser, String githubPassword, String repoName) throws IOException {
                List<Repository> list = new ArrayList<Repository>();
                Repository repoOne = new Repository();
                repoOne.setName("codjo-repoOne");
                repoOne.setPushedAt(DateUtil.parseFrenchDate("19/07/2012"));
                list.add(repoOne);

                Repository repoTwo = new Repository();
                repoTwo.setName("codjo-repoTwo");
                repoTwo.setPushedAt(DateUtil.parseFrenchDate("05/07/2012"));
                list.add(repoTwo);
                return list;
            }

            @Override
            public List<PullRequest> listOpenedPullRequest(String githubUser, String githubPassword, String repoName) throws IOException {
                List<PullRequest> list = new ArrayList<PullRequest>();
                PullRequest repoOne = new PullRequest();
                repoOne.setTitle("first pullRequest");
                repoOne.setBase(setPullRequestRepoName("repo1"));
                repoOne.setCreatedAt(DateUtil.parseFrenchDate("12/12/2010"));
                repoOne.setUrl("http://urlr/pullRequest/1");
                list.add(repoOne);

                PullRequest repoTwo = new PullRequest();
                repoTwo.setTitle("Second pullRequest");
                repoTwo.setBase(setPullRequestRepoName("repository2"));
                repoTwo.setCreatedAt(DateUtil.parseFrenchDate("01/12/2010"));
                repoTwo.setUrl("http://urlr/pullRequest/2/other ");
                list.add(repoTwo);
                return list;
            }

            private PullRequestMarker setPullRequestRepoName(String repoName) {
                PullRequestMarker base = new PullRequestMarker();
                Repository repo = new Repository();
                repo.setName(repoName);
                base.setRepo(repo);
                return base;
            }
        };
    }
}
