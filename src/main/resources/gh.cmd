
@set _GITHUB_METHOD=%1
@set _GITHTUB_REPO=%2
@set _GITHUB_USER_NAME=codjo-sandbox
@set _GITHUB_PASSWORD=XXXXXX

@call jdk16
@call java -jar %PLATFORM_DIR%\tools\codjo-tools-github\github.jar %_GITHUB_METHOD% %_GITHUB_USER_NAME% %_GITHUB_PASSWORD% %_GITHTUB_REPO%