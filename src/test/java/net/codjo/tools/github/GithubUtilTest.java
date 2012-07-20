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
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static net.codjo.test.common.matcher.JUnitMatchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class GithubUtilTest {
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private GithubUtil githubUtil;
    private GithubUtilService mockGithubService;
    private LogString logString = new LogString();


    @Before
    public void setUpStreams() {
        githubUtil = new GithubUtil();
        logString.clear();
        mockGithubService = buildMockService(logString);
        //TODO Testing System.out could be yeald to codjo-test ?
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
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
        assertThat(outContent.toString(), is(forkRepositoryInConsole("githubUser")));
        assertNoError();
    }


    @Test
    public void test_deleteRepository() {
        String[] args = new String[]{"delete", "githubUser", "githubPassword", "codjo-github-tools"};
        String data = "Yes\r\n";
        InputStream stdin = System.in;
        try {
            System.setIn(new ByteArrayInputStream(data.getBytes()));
            githubUtil.localMain(mockGithubService, args);
            logString.assertContent(
                  "initGithubClient(githubUser, githubPassword), deleteRepo(githubUser, githubPassword, codjo-github-tools)");
            assertThat(outContent.toString(), is(deleteRepositoryInConsole("githubUser")));
            assertNoError();
        }
        finally {
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


    private String helpInConsole(boolean wihtQuotas) {
        String result = ConsoleManager.OCTOPUS + "\r\n" +
                        " Did you mean :\r\n" +
                        "         - gh list [ACCOUNT_NAME] : list all repositories from ACCOUNT_NAME\r\n" +
                        "         - gh fork REPO_NAME      : fork a repository from codjo\r\n" +
                        "         - gh delete REPO_NAME    : delete a repository if exists\r\n";
        if (wihtQuotas) {
            result += "\n"
                      + "\n"
                      + "\tFor your information, you have 5 requests left\r\n";
        }
        return result;
    }


    private String repositoryListInConsole(String githubUser) {
        return ConsoleManager.OCTOPUS + "\r\n\n"
               + "Here are the repositories from " + githubUser + "\r\n"
               + "\tLast push\t\t\t\tName\r\n"
               + "\t19/07/2012 00:00\t\tcodjo-repoOne\r\n"
               + "\t05/07/2012 00:00\t\tcodjo-repoTwo\r\n"
               + "\n"
               + "\n"
               + "\tFor your information, you have 5 requests left\r\n";
    }


    private String forkRepositoryInConsole(String githubUser) {
        return ConsoleManager.OCTOPUS + "\r\n"
               + "\tRepository codjo-github-tools has been forked from codjo.\r\n"
               + "\n"
               + "\n"
               + "\tFor your information, you have 5 requests left\r\n";
    }


    private String deleteRepositoryInConsole(String githubUser) {
        return ConsoleManager.OCTOPUS + "\r\n"
               + "Do you really want to delete the repository codjo-github-tools on  githubUser account ? (y = yes / n = no/) : \n"
               + "\tRepository codjo-github-tools has been removed from githubUser account\r\n"
               + "\n"
               + "\n"
               + "\tFor your information, you have 5 requests left\r\n";
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
        };
    }
}
