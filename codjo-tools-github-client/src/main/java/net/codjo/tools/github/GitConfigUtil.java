package net.codjo.tools.github;
import java.io.File;
import java.io.IOException;
import net.codjo.util.file.FileUtil;

import static java.lang.System.getProperty;
/**
 *
 */
public class GitConfigUtil {
    static final String NETRC_FILE_NAME = "_netrc";
    static final String NETRC_BACKUP_FILE_NAME = "_netrcBackup";
    private final static String GITHUB_ACCOUNT_NAME = "XXXXXX";
    public static final String CODJO_PASSWORD = "XXXXXXXXXXX";

    private String proxyUserName;
    private String proxyPasword;
    private String proxyHost;
    private int proxyPort;


    public GitConfigUtil() throws IOException {
        this(new File(getUserHome(), ".gitconfig"));
    }


    public GitConfigUtil(File file) throws IOException {
        String[] gitConfigFile = FileUtil.loadContentAsLines(file);
        boolean categoryFound = false;

        for (String configLine : gitConfigFile) {
            if (!categoryFound) {
                categoryFound = "[http]".equalsIgnoreCase(configLine.trim());
            }
            else {
                String lineNospace = configLine.trim().replaceAll(" ", "").replaceAll("\t", "");
                String prefix = "proxy=";
                if (lineNospace.startsWith(prefix)) {
                    int delimiterPosition = lineNospace.indexOf("@");

                    String httpProxyAuthChain = lineNospace.substring(prefix.length(), delimiterPosition);
                    String[] split = httpProxyAuthChain.split(":");
                    proxyUserName = split[0];
                    proxyPasword = split[1];

                    String httpProxyChain = lineNospace.substring(delimiterPosition + 1);
                    split = httpProxyChain.split(":");
                    proxyHost = split[0];
                    proxyPort = Integer.parseInt(split[1]);
                    break;
                }

                if (lineNospace.startsWith("[")) {
                    break;
                }
            }
        }
    }


    public String getProxyUserName() {
        return removeDuplicatesBackSlashes(proxyUserName);
    }


    public String getProxyPassword() {
        return proxyPasword;
    }


    public String getProxyHost() {
        return proxyHost;
    }


    public int getProxyPort() {
        return proxyPort;
    }


    static String removeDuplicatesBackSlashes(String s) {
        StringBuilder noDupes = new StringBuilder();
        String[] split = s.split("\\\\");
        String previous = null;
        for (String str : split) {
            if (!"".equals(str)) {
                noDupes.append(str);
            }
            if (previous != null && !"".equals(previous)) {
                noDupes.append("\\");
            }
            previous = str;
        }
        return noDupes.toString();
    }


    public static void installCodjoNetrc(String codjoPassword) throws Exception {
        installCodjoNetrc(getUserHome(), codjoPassword);
    }


    static void installCodjoNetrc(File baseDirectory, String codjoPassword) throws Exception {
        File netrc = new File(baseDirectory, NETRC_FILE_NAME);
        String netrcContent = FileUtil.loadContent(netrc);

        File netrcBackup = new File(baseDirectory, NETRC_BACKUP_FILE_NAME);
        FileUtil.copyFile(netrc, netrcBackup);

        String codjoNetrc =
              "#netrcFile generated by Farow. Your personal netrcfile has been backuped in ./_netrcBackup\n"
              + "machine github.com\n"
              + "login " + GITHUB_ACCOUNT_NAME + "\n"
              + "password " + codjoPassword + "\n";

        FileUtil.saveContent(netrc, codjoNetrc);
    }


    public static void restoreCodjoNetrc() {
        restoreCodjoNetrc(getUserHome());
    }


    static void restoreCodjoNetrc(File baseDirectory) {
        File netrc = new File(baseDirectory, NETRC_FILE_NAME);
        File netrcBackup = new File(baseDirectory, NETRC_BACKUP_FILE_NAME);
        try {
            if (netrcBackup.exists()) {
                FileUtil.copyFile(netrcBackup, netrc);
                FileUtil.deleteRecursively(netrcBackup);
            }
        }
        catch (IOException e) {
            e.printStackTrace();  // Todo
        }
    }


    private static File getUserHome() {
        return new File(getProperty("user.home"));
    }
}
