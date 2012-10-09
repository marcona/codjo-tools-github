package net.codjo.tools.github;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: marcona
 * Date: 25/07/12
 * Time: 22:34
 * To change this template use File | Settings | File Templates.
 */
public interface GitHubCommand {
    void doCommand(GithubUtilService service, String method, String githubUser, String githubPassword, String repoName) throws IOException;
}
