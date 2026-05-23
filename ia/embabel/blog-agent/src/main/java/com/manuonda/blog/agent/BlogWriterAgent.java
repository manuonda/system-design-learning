package com.manuonda.blog.agent;


import com.embabel.agent.api.annotation.AchievesGoal;
import com.embabel.agent.api.annotation.Action;
import com.embabel.agent.api.annotation.Agent;
import com.embabel.agent.api.common.Ai;
import com.embabel.agent.api.identity.User;
import com.embabel.agent.core.CoreToolGroups;
import com.embabel.agent.domain.io.UserInput;
import com.embabel.common.ai.model.LlmOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Agent(description = "Write and review a blog post about given topic")
public class BlogWriterAgent {

    private Logger log = LoggerFactory.getLogger(BlogWriterAgent.class);

    private final  BlogAgentProperties properties;
    private final ReadingStatsTools readingStatsTool;

    public BlogWriterAgent(BlogAgentProperties props, ReadingStatsTools readingStatsTool) {
        this.properties = props;
        this.readingStatsTool = readingStatsTool;
    }



    @Action(description = "Research the topic using wikipedia and web search")
    public ResearchedTopic researchedTopic(UserInput userInput, Ai ai) {
        return ai.withDefaultLlm()
                .withId("blog-topic-researcher")
                .withToolGroup(CoreToolGroups.WEB)
                .creating(ResearchedTopic.class)
                .fromPrompt("""
                        Research the following topic using the available tools.

                        Follow this strategy:
                        1. Use the Wikipedia 'search' tool with language="en" to find relevant
                           English Wikipedia articles about the topic.
                        2. Use the Wikipedia 'fetch' tool with language="en" to retrieve the
                           full content of the most relevant article found.
                           IMPORTANT: always pass language="en" to both search and fetch —
                           other language editions may be unavailable.
                        3. If Wikipedia does not have enough information, supplement with up to
                           2 web searches using the web search tools.

                        Topic: %s

                        Provide the original topic and a concise summary of your findings
                        that would be useful for writing a blog post.
                        Focus on accuracy, technical depth, and relevant examples.
                        """.formatted(userInput.getContent()));
    }


    @Action(description = "Write a first draft of the blog post")
    public DraftPost writeDraft(ResearchedTopic research, Ai ai) {
        return ai
                .withLlm(LlmOptions.withDefaults().withMaxTokens(16384))
                .withId("blog-post-draft-writer")
                .withPromptContributors(List.of(Personas.WRITER, Personas.JSON_OUTPUT))
                .creating(DraftPost.class)
                .fromPrompt("""
                        Write a blog post about: %s

                        Use the following research to inform your writing:
                        %s

                        Keep it practical and beginner friendly.
                        Use short sentences and plain language.
                        Include code examples but keep them short and simple.
                        Write the content in Markdown.
                        """.formatted(research.topic(), research.research())
                );
    }

    @AchievesGoal(description = "A reviewed and polished blog post")
    @Action(description = "Review and improve the draft")
    public ReviewedPost reviewDraft(DraftPost draftPost, Ai ai) {

        ReviewedPost reviewedPost = ai
                .withLlmByRole("reviewer")
                .withPromptContributor(Personas.REVIEWER)
                .withId("blog-post-reviewer")
                .creating(ReviewedPost.class)
                .fromPrompt("""
                        You are a technical editor.Review and improve this blog post.

                        Title: %s
                        Content:
                        %s

                        Fix any technical errors. Tighten the writing.
                        Provide the revised title, revised content, and a brief
                        summary of the changes you made as feedback
                        """.formatted(draftPost.title(), draftPost.content()));
     writeToFile(reviewedPost);
     return reviewedPost;
    }


    @Action(description = "Add A TLDR suumary to the top of the blog post")
    public FinalPost addTldr(ReviewedPost post, Ai ai) {
             String tldr = ai.withDefaultLlm().withId("blog-post-tldr-generator")
                     .creating(String.class)
                     .fromPrompt("""
                             Write a one or two sentence TLDR summary for this blog post:
                             Return only the summary, no other text.
                             
                             Title: %s
                             Content:
                             %s
                             """.formatted(post.title(), post.content()));
             String contentWtihTldr = "> ** TLDR: ** " + tldr + "\n\n" +  post.content();
             return new FinalPost(post.title(), contentWtihTldr, post.content());
    }

    @AchievesGoal(description = "A reviewed and polished blog post with front matter")
    @Action(description = "Add front matter to the top of the blog post")
    public PublishedPost addFrontMatter(FinalPost post, Ai ai) {
        FrontMatter frontMatter = ai
                .withDefaultLlm()
                .withToolObject(readingStatsTool)
                .withId("blog-post-front-matter")
                .withPromptContributors(List.of(Personas.JSON_OUTPUT))
                .creating(FrontMatter.class)
                .fromPrompt("""
                        Generate front matter metadata for this blog post.
                        Provide a concise description (1-2 sentences), relevant tags, and up to %d keywords.

                        Use the calculateReadingStats tool on the post content below to compute
                        the read time. Put the tool's exact return string into the readTime field.

                        Title: %s
                        Content:
                        %s
                        """.formatted(properties.numberOfKeywords(), post.title(), post.content())
                );

        String slug = post.title()
                .toLowerCase()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-|-$", "");

        String tags = frontMatter.tags().stream()
                .map(tag -> "  - " + tag)
                .collect(Collectors.joining("\n"));

        String keywords = frontMatter.keywords().stream()
                .map(keyword -> "  - " + keyword)
                .collect(Collectors.joining("\n"));

        String frontMatterBlock = """
                ---
                title: "%s"
                slug: %s
                date: "%sT08:00:00.000Z"
                published: true
                description: "%s"
                author: "Dan Vega"
                readTime: "%s"
                tags:
                %s
                keywords:
                %s
                ---
                """.formatted(
                post.title(),
                slug,
                LocalDate.now(),
                frontMatter.description(),
                frontMatter.readTime(),
                tags,
                keywords
        );

        String contentWithFrontMatter = frontMatterBlock + "\n" + post.content();
        PublishedPost publishedPost = new PublishedPost(post.title(), contentWithFrontMatter, post.feedback());
        writeToFile(publishedPost);
        return publishedPost;
    }

    private void writeToFile(BlogPost post) {
        String filename = post.title()
                .toLowerCase()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-|-$", "")
                + ".md";

        Path outputDir = Path.of(properties.outputDir());
        Path filePath = outputDir.resolve(filename);

        try {
            Files.createDirectories(outputDir);
            Files.writeString(filePath, post.content());
            log.info("Blog post written to {}", filePath.toAbsolutePath());
        } catch (IOException e) {
            log.error("Failed to write blog post to {}: {}", filePath, e.getMessage());
        }
    }
}
