/*
 * Copyright 2020 Jörg Frommann (jfrommann.dev@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.jfrommann.gitlabcloner;

import org.apache.commons.lang3.SystemUtils;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.json.JacksonJsonProvider;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;

public class GitlabCloner {

    private final String url;
    private final String token;

    public GitlabCloner(String url, String token) {
        this.url = url;
        this.token = token;
    }

    public void cloneProjects(Path basePath) {
        final ClientConfig config = new ClientConfig(JacksonJsonProvider.class);
        final Client client = ClientBuilder.newClient(config);
        final WebTarget webTarget = client.target(url).path("api/v4/projects")
                .queryParam("private_token", token)
                .queryParam("simple", true)
                .queryParam("order_by", "id")
                .queryParam("sort", "asc")
                .queryParam("page", 1)
                .queryParam("per_page", 20);
        cloneProjects(client, webTarget, basePath);
    }

    private void cloneProjects(Client client, WebTarget webTarget, Path basePath) {
        final Response response = webTarget.request(MediaType.APPLICATION_JSON).get();
        final List<Project> projects = response.readEntity(new GenericType<>(){});
        projects.forEach(project -> {
            System.out.println(project);
            final Path path = createProjectPath(project, basePath);
            cloneProject(project, path);
        });

        final Set<Link> links = response.getLinks();
        links.forEach(link -> {
            if (link.getRel().equals("next")) {
                cloneProjects(client, client.target(link.getUri()), basePath);
            }
        });
    }

    private Path createProjectPath(Project project, Path basePath) {
        try {
            final Path path = basePath.resolve(project.getPath()).getParent();
            if (Files.notExists(path)) {
                Files.createDirectories(path);
            }
            return path;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create path for project: " + project, e);
        }
    }

    private void cloneProject(Project project, Path path) {
        try {
            final ProcessBuilder processBuilder = new ProcessBuilder().directory(path.toFile());
            if (SystemUtils.IS_OS_WINDOWS) {
                processBuilder.command("git.exe", "clone", project.getSshUrl());
            } else {
                processBuilder.command("git", "clone", project.getSshUrl());
            }
            final int exitCode = processBuilder.start().waitFor();
            assert exitCode == 0;
        } catch (Exception e) {
            throw new RuntimeException("Failed to clone project: " + project, e);
        }
    }

    public static void main(String[] args) {
        if (args == null || args.length < 2) {
            System.out.println("Usage: gitlab-cloner <gitlab_url> <api_token> (OPTIONAL: <base_path>)");
            System.exit(1);
        }

        final Path basePath = (args.length > 2) ? Paths.get(args[2]) : Paths.get(".");
        new GitlabCloner(args[0], args[1]).cloneProjects(basePath);
    }
}
