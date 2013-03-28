package net.codjo.tools.github;
import java.io.File;
import java.io.IOException;
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
/**
 *
 */
public class GithubUtilMockService implements GithubUtilService {
    private LogString logString = new LogString();


    public GithubUtilMockService(LogString logString) {
        this.logString = logString;
    }


    public GitHubClient initGithubClient(String githubUser, String githubPassword) {
        logString.call("initGithubClient", githubUser, githubPassword);
        return null;
    }


    public void forkRepo(String githubUser, String githubPassword, String repoName) throws IOException {
        logString.call("forkRepo", githubUser, githubPassword, repoName);
    }


    public void deleteRepo(String githubUser, String githubPassword, String repoName) throws IOException {
        logString.call("deleteRepo", githubUser, githubPassword, repoName);
    }


    public int getGitHubQuota() throws IOException {
        return 5;
    }


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


    public void addLabels(String githubUser,
                          String githubPassword,
                          String repoName,
                          Issue issue,
                          List<String> labels)
          throws IOException {
        logString.call("addLabels", githubUser, githubPassword, repoName, issue.getTitle(), labels);
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
}
