package net.codjo.tools.github;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.Assert.assertEquals;


public class GithubUtilTest {
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();

    @Before
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @After
    public void cleanUpStreams() {
        System.setOut(null);
        System.setErr(null);
    }

    @Test
    public void test_badMethodPrintsHelp(){
        String[] args = new String[]{"badMethod", "githubUser", "githubPassword"};

        GithubUtil.main(args);
        assertEquals(getHelp(), outContent.toString());
        assertNoError();

    }

    @Test
    public void test_noParameterPrintsHelp(){
        String[] args = new String[]{};

        GithubUtil.main(args);
        assertEquals(getHelp(), outContent.toString());
        assertNoError();

    }

    private String getHelp() {
        return GithubUtil.OCTOPUS + "\n"+
                " Did you mean :\n" +
                "         - gh list [ACCOUNT_NAME] : list all repositories from ACCOUNT_NAME\n" +
                "         - gh fork REPO_NAME      : fork a repository from codjo\n" +
                "         - gh delete REPO_NAME    : delete a repository if exists\n";
    }

    private void assertNoError() {
        assertEquals("", errContent.toString());
    }
}
