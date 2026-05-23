package com.manuonda.blog.agent;


import com.embabel.agent.api.annotation.LlmTool;
import org.springframework.stereotype.Component;

@Component
public class ReadingStatsTools {

    private static final int WORDS_PER_MINUTE = 200;


    @LlmTool(description = "Calculate the estimated reading time for a blog post based on its content.")
    public String calculateReadingTime(@LlmTool.Param(description = "The content of the blog post") String content) {
        if (content == null || content.isBlank()) {
            return "0 minutes";
        }
        int wordCount = content.split("\\s+").length;
        int readingTimeMinutes = (int) Math.ceil((double) wordCount / WORDS_PER_MINUTE);
        return String.format("%d words,%d minutes", wordCount, readingTimeMinutes);
    }

}
