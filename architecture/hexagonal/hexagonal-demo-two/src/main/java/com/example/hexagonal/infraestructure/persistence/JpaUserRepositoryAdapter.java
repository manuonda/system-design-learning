package com.example.hexagonal.infraestructure.persistence;

import com.example.hexagonal.application.port.out.UserRepositoryPort;
import com.example.hexagonal.domain.model.User;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public class JpaUserRepositoryAdapter implements UserRepositoryPort {


    private final SpringDataUserRepository springDataUserRepository;

    public JpaUserRepositoryAdapter(SpringDataUserRepository springDataUserRepository) {
        this.springDataUserRepository = springDataUserRepository;
    }

    @Override
    public User createUser(User user) {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(user.id());
        userEntity.setFirstName(user.firstName());
        userEntity.setLastName(user.lastName());
        final UserEntity userSave = this.springDataUserRepository.save(userEntity);
        return new User(userSave.getId(), userSave.getFirstName(), userSave.getLastName());
    }

    @Override
    public Optional<User> findById(Long id) {
        return springDataUserRepository.findById(id)
                .map(u -> new User(u.getId(), u.getFirstName(), u.getLastName()));
    }
}
