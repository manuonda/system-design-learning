package com.manuonda.blog.agent;

public record DraftPost(String title, String content) implements  BlogPost {
}
