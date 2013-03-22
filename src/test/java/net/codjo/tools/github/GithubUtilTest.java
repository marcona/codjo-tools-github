package net.codjo.tools.github;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import net.codjo.test.common.LogString;
import net.codjo.util.date.DateUtil;
import net.codjo.util.file.FileUtil;
import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.PullRequest;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.User;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.event.Event;
import org.eclipse.egit.github.core.event.PullRequestPayload;
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
        if (gitConfigUtil == null || gitConfigUtil.getProxyHost() != null) {
            proxyMessage = "";
        }
        else {
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
        }
        finally {
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
        }
        finally {
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
        }
        finally {
            System.setIn(stdin);
        }
    }


    @Test
    public void test_postIssue() throws Exception {
        final String issueTitle = "codjo-administration+-+Bug+fix+in+administration+panel";
        File issueContentFile = new File(getClass().getResource("/" + issueTitle).toURI());
        String[] args = new String[]{"postIssue", "codjo", "githubPassword", "codjo-github-tools", issueTitle, "closed",
                                     issueContentFile.getCanonicalPath()};
        InputStream stdin = System.in;
        try {
            githubUtil.localMain(mockGithubService, args);
            logString.assertContent(
                  "initGithubClient(codjo, githubPassword), postIssue(codjo, githubPassword, codjo-github-tools, codjo-administration+-+Bug+fix+in+administration+panel, "
                  + issueContentFile.getPath() + ", closed)");
            assertThat(outContent.toString(),
                       is(postIssueWithCodjoAccountInConsole(issueTitle, FileUtil.loadContent(issueContentFile))));
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


    @Test
    public void test_listOpenedPullRequest() {
        String[] args = new String[]{"events", "codjo", "githubPassword"};
        githubUtil.localMain(mockGithubService, args);
        logString.assertContent("initGithubClient(codjo, githubPassword)");
        assertThat(outContent.toString(), is(listEventsSinceLastStabilisationInConsole()));
        assertNoError();
    }


    private String helpInConsole(boolean wihtQuotas) {
        String result = ConsoleManager.OCTOPUS + endOfLine
                        + proxyMessage +
                        " Did you mean :" + endOfLine +
                        "         - gh list [ACCOUNT_NAME] : list all repositories from ACCOUNT_NAME" + endOfLine +
                        "         - gh fork REPO_NAME      : fork a repository from codjo" + endOfLine +
                        "         - gh delete REPO_NAME    : delete a repository if exists" + endOfLine +
                        "         - gh postIssue REPO_NAME ISSUE_TITLE STATE ISSUE_CONTENT_FILE_PATH    : add a new issue in repository"
                        + endOfLine +
                        "         - gh events [ACCOUNT_NAME] [ACCOUNT_PASSWORD]    : list all events since last stabilisation (last pull request with 'For Release' title"
                        + endOfLine;

        if (wihtQuotas) {
            result += printApiQuota();
        }
        return result;
    }


    private String repositoryListInConsole(String githubUser) {
        return ConsoleManager.OCTOPUS + endOfLine + "\n"
               + "Here are the repositories from " + githubUser + endOfLine
               + "\tLast push\t\t\t\tName" + endOfLine
               + "\t19/07/2012 00:00\t\tcodjo-repoOne" + endOfLine
               + "\t05/07/2012 00:00\t\tcodjo-repoTwo" + endOfLine
               + printApiQuota();
    }


    private String forkRepositoryInConsole() {
        return ConsoleManager.OCTOPUS + "" + endOfLine
               + "\tRepository codjo-github-tools has been forked from codjo." + endOfLine
               + printApiQuota();
    }


    private String deleteRepositoryInConsole(String githubUser) {
        return ConsoleManager.OCTOPUS + "" + endOfLine
               + "Do you really want to delete the repository codjo-github-tools on  githubUser account ? (y = yes / n = no/) : \n"
               + "\tRepository codjo-github-tools has been removed from " + githubUser + " account" + endOfLine
               + printApiQuota();
    }


    private String deleteRepositoryCanceledByUserInConsole() {
        return ConsoleManager.OCTOPUS + "" + endOfLine
               + "Do you really want to delete the repository codjo-github-tools on  githubUser account ? (y = yes / n = no/) : "
               + printApiQuota();
    }


    private String deleteRepositoryWithCodjoAccountInConsole() {
        return ConsoleManager.OCTOPUS + "" + endOfLine
               + "\tRepositoy deletion with codjo account is not allowed." + endOfLine
               + "\t--> Please, use web interface instead." + endOfLine
               + printApiQuota();
    }


    private String postIssueWithCodjoAccountInConsole(String title, String content) {
        return ConsoleManager.OCTOPUS + "" + endOfLine
               + "\tIssue " + title + " has been created with codjo account" + endOfLine
               + "\twith the following content:" + endOfLine
               + content + endOfLine
               + printApiQuota();
    }


    private String listEventsSinceLastStabilisationInConsole() {
        return ConsoleManager.OCTOPUS + "" + endOfLine
               + "\tHere are the last events on codjo"
               + endOfLine
               + "\tUser\t\t\t\t\\tName\t\t\t\tUrl" + endOfLine
               + "\tcodjo-sandbox\t\tfirst pullRequest\t\thttp://urlr/pullRequest/1" + endOfLine
               + "\tgonnot\t\tSecond pullRequest\t\thttp://urlr/pullRequest/2/other " + endOfLine
               + printApiQuota();
    }


    private String printApiQuota() {
        return ConsoleManager.printApiQuota(5) + endOfLine;
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
            public List<Event> eventsSinceLastRelease(String githubUser,
                                                      String githubPassword,
                                                      String repoName, String codjoPomRequestPrefix)
                  throws IOException {
                String login = "codjo-sandbox";
                String pullRequestTitle = "first pullRequest";
                String date = "12/12/2010";
                String htmlUrl = "http://urlr/pullRequest/1";

                List<Event> list = new ArrayList<Event>();
                list.add(buildPullRequestEvent(login, pullRequestTitle, date, htmlUrl));

                login = "gonnot";
                pullRequestTitle = "Second pullRequest";
                date = "01/12/2010";
                htmlUrl = "http://urlr/pullRequest/2/other ";

                list.add(buildPullRequestEvent(login, pullRequestTitle, date, htmlUrl));

                return list;
            }


            @Override
            public Issue postIssue(String githubUser,
                                   String githubPassword,
                                   String repoName,
                                   String title,
                                   String contentFilePath, String state) throws IOException {
                logString.call("postIssue", githubUser, githubPassword, repoName, title, contentFilePath, state);
                final Issue issue = new Issue();
                issue.setTitle(title);
                issue.setBody(FileUtil.loadContent(new File(contentFilePath)));
                return issue;
            }


            private Event buildPullRequestEvent(String login, String pullRequestTitle, String date, String htmlUrl) {
                User user = new User();
                user.setLogin(login);

                PullRequest pullRequestOne = new PullRequest();
                pullRequestOne.setUser(user);
                pullRequestOne.setTitle(pullRequestTitle);
                pullRequestOne.setCreatedAt(DateUtil.parseFrenchDate(date));
                pullRequestOne.setHtmlUrl(htmlUrl);

                PullRequestPayload payload = new PullRequestPayload();
                payload.setPullRequest(pullRequestOne);

                Event eventOne = new Event();
                eventOne.setPayload(payload);
                return eventOne;
            }
        };
    }
}
