# Introduction #
Some usefull conf/mo, mostly kept as a reminder for next time.

# Setups #
  * Git
  * Java JDK
  * Maven
  * Eclipse EE

# Git config #

```
git config --global user.name "John Doe"
git config --global user.email johndoe@example.com
git config --global http.proxy http://proxyuser:proxypwd@proxy.server.com:8080
git config --system http.sslcainfo /bin/curl-ca-bundle.crt
```

# Maven config #
in conf/settings.xml
```
<localRepository>path/to/repo</localRepository>
<proxies>
	<proxy>
		<active>true</active>
		<protocol>http</protocol>
		<host>proxy.server.com</host>
		<port>8080</port>
		<username>proxyuser</username>
		<password>proxypwd</password>
		<nonProxyHosts>www.google.com|*.somewhere.com</nonProxyHosts>
	</proxy>
</proxies>
<profile
	<profile>
		<id>aProfile</id>
		<activation>
			<activeByDefault>true</activeByDefault>
		</activation>

		<repositories>
			<repository>
				<id>release.repo</id>
				<url>http://release.url.repo</url>
				<releases>
					<enabled>true</enabled>
				</releases>
				<snapshots>
					<enabled>false</enabled>
				</snapshots>
			</repository>

			<repository>
				<id>snapshot.repo</id>
				<url>http://snapshot.url.repo</url>
				<releases>
					<enabled>true</enabled>
				</releases>
				<snapshots>
					<enabled>false</enabled>
				</snapshots>
			</repository>
		</repositories>

		<pluginRepositories>
			<pluginRepository>
				<id>release.pluginrepo</id>
				<url>http://release.url.pluginrepo</url>
				<releases>
					<enabled>true</enabled>
				</releases>
				<snapshots>
					<enabled>false</enabled>
				</snapshots>
			</pluginRepository>

			<pluginRepository>
				<id>snapshot.pluginrepo</id>
				<url>http://snapshot.url.pluginrepo</url>
				<releases>
					<enabled>true</enabled>
				</releases>
				<snapshots>
					<enabled>false</enabled>
				</snapshots>
			</pluginRepository>
		</pluginRepositories>
	</profile>
</profiles>
```

# Win config #

| GIT\_HOME | path\to\git |
|:----------|:------------|
| GIT\_BIN  | %GIT\_HOME%\bin  |
| MAVEN\_HOME | path\to\maven |
| MAVEN\_OPTS | some ops    |
| MAVEN\_BIN| %MAVEN\_HOME%\bin |
| JAVA\_HOME | path\to\jdk |
| JAVA\_OPTS | some ops    |
| JAVA\_BIN | %JAVA\_HOME%\bin |
| PATH      | %PATH%;%GIT\_BIN%;%JAVA\_BIN%;%MAVEN\_BIN% |

# Init Workspace #

```
mkdir workspace
cd workspace
git clone https://proxyuser:projectpwd@code.google.com/p/picture-archiver/
mvn eclipse:clean eclipse:eclipse
```


# Eclipse #
## Eclipse Pluggin ##

  * EGit
  * MoreUnit

## Eclipse Conf ##

  * Add downloaded Maven as default
  * Add downloaded JDK as default
  * Import existing project from workspace, Check the nested dir checkbox, get the projects(picture-archiver-app, picture-archiver-core)

## Eclipse Launch Configs ##

### Maven ###
For each maven conf, make sure the default JDK and maven install are the good ones.
| picture-archiver reset | Base Directory | ${workspace\_loc}/picture-archiver|
|:-----------------------|:---------------|:----------------------------------|
| picture-archiver reset | Goals          | eclipse:clean eclipse:eclipse     |
| picture-archiver build | Base Directory | ${workspace\_loc}/picture-archiver|
| picture-archiver build | Goals          | clean install                     |
| picture-archiver test  | Base Directory | ${workspace\_loc}/picture-archiver|
| picture-archiver  test | Goals          | test                              |
| picture-archiver update version | Base Directory | ${workspace\_loc}/picture-archiver|
| picture-archiver update version | Goals          | versions:set -DnewVersion=${version} |
| picture-archiver update version | parameters     |version : ${string\_prompt:the new version} |