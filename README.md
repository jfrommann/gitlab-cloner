# gitlab-cloner

Java tool to clone multiple GitLab projects at once

**Currently work in progress**

## Requirements 

- Java 11
- Maven 3.x to build project
- Git executable in path
- SSH Key provided in GitLab (set at /profile/keys)

## Build

`mvn package`

## Usage

`java -jar gitlab-cloner-{version}.jar <URL> <TOKEN> (OPTIONAL: <PATH>)`

| Parameter | Description | Required |
| --------- | ----------- | -------- |
| URL | Base URL of the GitLab instance to clone | __*__ |
| TOKEN | GitLab API token (create at /profile/personal_access_tokens) | __*__ |
| PATH | Filesystem base path destination to clone the projects to | |
