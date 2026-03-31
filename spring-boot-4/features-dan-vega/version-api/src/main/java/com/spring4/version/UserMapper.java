package com.spring4.version;


import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserDTOv1 toV1(User user) {
        return new UserDTOv1(user.id(), user.name(), user.email(), user.webSite());
    }

    public UserDTOv2 toV2(User user) {
        String[] nameParts = user.name().split(" ");
        return new UserDTOv2(user.id(), nameParts[0], user.email(), nameParts.length > 1 ? nameParts[1] : "");
    }

}
