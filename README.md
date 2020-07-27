# gitlab-cloner

Java tool to clone multiple GitLab projects at once

**Currently work in progress**

## Usage

`gitlab-cloner <gitlab_url> <api_token> (OPTIONAL: <base_path>)`

| Parameter | Description | Required |
| --------- | ----------- | -------- |
| gitlab_url | URL of the GitLab instance to clone | __*__ |
| api_token | GitLab API token (create at /profile/personal_access_tokens) | __*__ |
| base_path | Filesystem base path destination to clone the projects to | |

## Requirements 

- Java 11
- Git executable in path
- SSH Key provided in GitLab (set at /profile/keys)
