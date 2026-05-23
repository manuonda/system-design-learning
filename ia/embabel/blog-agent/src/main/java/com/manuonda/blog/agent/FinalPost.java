package com.manuonda.blog.agent;

public record FinalPost(String title, String content, String feedback)  implements  BlogPost{
}
