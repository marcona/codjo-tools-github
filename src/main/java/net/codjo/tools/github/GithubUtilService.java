package net.codjo.tools.github;

import org.eclipse.egit.github.core.PullRequest;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.RepositoryId;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.PullRequestService;
import org.eclipse.egit.github.core.service.RepositoryService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.eclipse.egit.github.core.client.IGitHubConstants.SEGMENT_REPOS;

/**
 * Created with IntelliJ IDEA.
 * User: marcona
 * Date: 20/07/12
 * Time: 09:02
 * To change this template use File | Settings | File Templates.
 */
public class GithubUtilService {
    private GitHubClient client = new GitHubClient();

    public GitHubClient initGithubClient(String githubUser, String githubPassword) {
        client.setCredentials(githubUser, githubPassword);
        return client;
    }

    public void forkRepo(String githubUser, String githubPassword, String repoName) throws IOException {
        client = initGithubClient(githubUser, githubPassword);
        RepositoryService repositoryService = new RepositoryService(client);
        repositoryService.forkRepository(new RepositoryId("codjo", repoName));
    }


    public void deleteRepo(String githubUser, String githubPassword, String repoName) throws IOException {
        client = initGithubClient(githubUser, githubPassword);
        client.delete(SEGMENT_REPOS + "/" + githubUser + "/" + repoName);
    }


    public int getGitHubQuota() throws IOException {
        return client.getRemainingRequests();
    }


    public List<Repository> list(String githubUser, String githubPassword, String repoName) throws IOException {
        client = initGithubClient(githubUser, githubPassword);
        return getRepositoryList(githubUser, repoName, client);


    }


    public List<PullRequest> listOpenedPullRequest(String githubUser, String githubPassword, String repoName) throws IOException {
        List<PullRequest> resulList = new ArrayList<PullRequest>();

//        initGithubClient(githubUser, githubPassword);


        List<Repository> repositories = getRepositoryList(githubUser,repoName, client);

        PullRequestService pullRequestService = new PullRequestService(client);
        for (Repository repository : repositories) {
            List<PullRequest> pullRequests = pullRequestService.getPullRequests(repository, "open");
            resulList.addAll(pullRequests);
        }

        return resulList;
    }

    private List<Repository> getRepositoryList(String githubUser, String repoName, GitHubClient gitHubClient) throws IOException {
        RepositoryService repositoryService = new RepositoryService(gitHubClient);
        if (repoName != null && !repoName.trim().isEmpty()) {
            return repositoryService.getRepositories(repoName);
        }
        else {
            return repositoryService.getRepositories(githubUser);
        }
    }
}
