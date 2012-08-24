package net.codjo.tools.github;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.egit.github.core.PullRequest;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.RepositoryId;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.PullRequestService;
import org.eclipse.egit.github.core.service.RepositoryService;

import static org.eclipse.egit.github.core.client.IGitHubConstants.SEGMENT_REPOS;

/**
 * Created with IntelliJ IDEA. User: marcona Date: 20/07/12 Time: 09:02 To change this template use File | Settings |
 * File Templates.
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


    public void findPullRequest(String githubUser, String githubPassword, String repoName) throws IOException {
        client = initGithubClient(githubUser, githubPassword);

        List<PullRequest> pullRequests = new ArrayList<PullRequest>();
        RepositoryService repositoryService = new RepositoryService(client);
        List<Repository> repositories = repositoryService.getRepositories();
        PullRequestService service = new PullRequestService(client);

        for (Repository repository : repositories) {
            System.out.println("repository.getName() = " + repository.getName());
            List<PullRequest> pullRequestList = getClosedPullRequestsFor(service, repository.getName(), null);
            pullRequests.addAll(pullRequestList);
        }
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

        //TODO[refactoring]
        RepositoryService repositoryService = new RepositoryService(client);
        if (repoName != null && !repoName.trim().isEmpty()) {
            return repositoryService.getRepositories(repoName);
        }
        else {
            return repositoryService.getRepositories(githubUser);
        }
    }


    private List<PullRequest> getClosedPullRequestsFor(PullRequestService service,
                                                       String githubRepositoryId,
                                                       String githubLogin)
          throws IOException {
        RepositoryId repo = new RepositoryId(githubLogin, githubRepositoryId);
        return service.getPullRequests(repo, "closed");
    }
}
