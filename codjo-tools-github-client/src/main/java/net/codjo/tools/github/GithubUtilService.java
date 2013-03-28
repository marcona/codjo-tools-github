package net.codjo.tools.github;
import java.io.IOException;
import java.util.List;
import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.event.Event;
/**
 *
 */
public interface GithubUtilService {
    GitHubClient initGithubClient(String githubUser, String githubPassword);


    void forkRepo(String githubUser, String githubPassword, String repoName) throws IOException;


    void deleteRepo(String githubUser, String githubPassword, String repoName) throws IOException;


    Issue postIssue(String githubUser,
                    String githubPassword,
                    String repoName,
                    String title,
                    String contentFilePath, String state) throws IOException;


    void addLabels(String githubUser, String githubPassword, String repoName, Issue issue, List<String> labels)
                                 throws IOException;


    int getGitHubQuota() throws IOException;


    List<Repository> list(String githubUser, String githubPassword, String repoName) throws IOException;


    List<Event> eventsSinceLastRelease(String githubUser,
                                       String githubPassword,
                                       String repoName, String codjoPomRequestPrefix) throws IOException;
}
