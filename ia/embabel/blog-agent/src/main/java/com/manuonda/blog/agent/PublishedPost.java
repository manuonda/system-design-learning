package com.manuonda.blog.agent;

public record PublishedPost(String title, String content, String feedback) implements BlogPost {
}
