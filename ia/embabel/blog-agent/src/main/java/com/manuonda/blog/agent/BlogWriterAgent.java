package com.manuonda.blog.agent;


import com.embabel.agent.api.annotation.AchievesGoal;
import com.embabel.agent.api.annotation.Action;
import com.embabel.agent.api.annotation.Agent;
import com.embabel.agent.api.common.Ai;
import com.embabel.agent.domain.io.UserInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Agent(description = "Write and review a blog post about given topic")
public class BlogWriterAgent {

    private Logger log = LoggerFactory.getLogger(BlogWriterAgent.class);

    private final  BlogAgentProperties properties;
    private final ReadingStatsTools readingStatsTools;

    public BlogWriterAgent(BlogAgentProperties props, ReadingStatsTools readingStatsTools) {
        this.properties = props;
        this.readingStatsTools = readingStatsTools;
    }


    @Action(description = "Write a first draft of the blog post")
    public BlogDraft writeDaft(UserInput userInput, Ai ai) {
     return ai.withDefaultLlm()
             .withId("blog-post-draft-writer")
             .withPromptContributor(Personas.WRITER)
             . creating(BlogDraft.class)
             .fromPrompt("""
                     You are a software developer an educator writing a blog post.
                     Write a blog post about: %s
                     
                     Keep it practical and beginner friendly
                     Use short sentences and plain language.
                     Include code examples but keep them short and simple.
                     Writen the content in Markdown.
                     
                     """.formatted(userInput.getContent())
             );
    }

    @AchievesGoal(description = "A reviewed and polished blog post")
    @Action(description = "Review and improve the draft")
    public ReviewedPost reviewDraft(BlogDraft blogDraft, Ai ai) {

        System.out.println("Entrando reviewDraft : " + blogDraft.toString());

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
                        """.formatted(blogDraft.title(), blogDraft.content()));
     System.out.println("Salida reviewedPost : " + reviewedPost.toString());
     writeToFile(reviewedPost);
     return reviewedPost;
    }

    private void writeToFile(ReviewedPost post) {
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
