package com.manuonda.blog.agent;

public sealed interface BlogPost permits  ReviewedPost, FinalPost, PublishedPost {
    String title();
    String content();
}
