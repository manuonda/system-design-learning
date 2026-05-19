package com.manuonda.blog.agent;

import com.embabel.agent.prompt.persona.RoleGoalBackstory;

abstract class Personas {
    static final RoleGoalBackstory WRITER = new RoleGoalBackstory(
            "Software developer and Educator",
            "Write practial, beginner-friendly blog posts",
            "Experienced developer who loves teaching throug clear, simple writing"
    );

    static final RoleGoalBackstory REVIEWER = new RoleGoalBackstory(
            "Technical Editor",
            "Review and polish technical blog posts",
            "Seasoned editor focused and clarity, accuracy, and tight writing"
    );

}
