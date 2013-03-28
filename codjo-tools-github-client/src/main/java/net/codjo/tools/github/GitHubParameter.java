package net.codjo.tools.github;
import java.util.ArrayList;
import java.util.List;
/**
 *
 */
public class GitHubParameter {
    private String method = "";
    private String user = "";
    private String password = "";
    private String issueTitle = "";
    private String issueFilePath = "";
    private String issueState = "";
    private String repoName = "";
    private List<String> labels = new ArrayList<String>();
    private boolean githubParamsAreValid;


    public GitHubParameter(String[] commandParameters) {
        //TODO To be improved
        githubParamsAreValid = commandParameters.length == 3 || commandParameters.length == 4
                               || commandParameters.length >= 7;

        if (githubParamsAreValid) {
            method = commandParameters[0];
            user = commandParameters[1];
            password = commandParameters[2];
            if (commandParameters.length >= 4) {
                repoName = commandParameters[3];
            }
            if (commandParameters.length >= 7) {//Issue Management
                issueTitle = commandParameters[4];
                issueState = commandParameters[5];
                issueFilePath = commandParameters[6];
                for (int i = 0; i < commandParameters.length - 7; i++) {
                    labels.add(commandParameters[7 + i]);
                }
            }
        }
    }


    boolean githubParamsAreValid() {
        return githubParamsAreValid;
    }


    public String getMethod() {
        return method;
    }


    public String getUser() {
        return user;
    }


    public String getPassword() {
        return password;
    }


    public String getIssueTitle() {
        return issueTitle;
    }


    public String getIssueFilePath() {
        return issueFilePath;
    }


    public String getRepoName() {
        return repoName;
    }


    public List<String> getLabels() {
        return labels;
    }


    public String getIssueState() {
        return issueState;
    }
}
