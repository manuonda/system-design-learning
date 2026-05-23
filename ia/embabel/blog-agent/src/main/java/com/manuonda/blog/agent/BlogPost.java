package com.manuonda.blog.agent;

public sealed interface BlogPost permits  DraftPost,ReviewedPost, FinalPost, PublishedPost {
    String title();
    String content();
}
