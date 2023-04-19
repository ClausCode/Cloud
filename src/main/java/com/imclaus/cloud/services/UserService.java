package com.imclaus.cloud.services;

import com.imclaus.cloud.dto.request.UserChangeNameRequestDTO;
import com.imclaus.cloud.dto.request.UserChangePasswordRequestDTO;
import com.imclaus.cloud.dto.request.UserSignUpRequestDTO;
import com.imclaus.cloud.enums.Status;
import com.imclaus.cloud.models.UserModel;
import com.imclaus.cloud.repositories.RolesRepository;
import com.imclaus.cloud.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Instant;
import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final RolesRepository rolesRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final MFAService mfaService;

    @Autowired
    public UserService(
            UserRepository userRepository,
            RolesRepository rolesRepository,
            BCryptPasswordEncoder passwordEncoder,
            MFAService mfaService) {
        this.userRepository = userRepository;
        this.rolesRepository = rolesRepository;
        this.passwordEncoder = passwordEncoder;
        this.mfaService = mfaService;
    }

    public Mono<UserModel> findById(Long id) {
        return userRepository.findById(id)
                .flatMap(this::getRoles);
    }

    public Mono<UserModel> findByEmail(String email) {
        return userRepository.findByEmail(email)
                .flatMap(this::getRoles);
    }

    public Mono<UserModel> create(UserSignUpRequestDTO signUpRequestDTO) {
        UserModel model = new UserModel();

        model.setEmail(signUpRequestDTO.getEmail());
        model.setName(signUpRequestDTO.getName());
        model.setPassword(passwordEncoder.encode(signUpRequestDTO.getPassword()));
        model.setSecret(mfaService.generateSecret());
        model.setTfa(false);

        Instant now = Instant.now();
        model.setCreated(now);
        model.setUpdated(now);

        model.setStatus(Status.NOT_ACTIVE);
        return userRepository.save(model).publishOn(Schedulers.boundedElastic())
                .flatMap(user -> rolesRepository
                        .findByName("DEFAULT")
                        .flatMap(role -> {
                            user.setRoles(List.of(role));
                            return saveRoles(user);
                        }));
    }

    public Mono<UserModel> changePassword(UserModel user, UserChangePasswordRequestDTO changePasswordRequestDTO) {
        if (passwordEncoder.matches(changePasswordRequestDTO.getOldPassword(), user.getPassword())) {
            user.setPassword(passwordEncoder.encode(changePasswordRequestDTO.getNewPassword()));
            user.setUpdated(Instant.now());

            return userRepository.save(user);
        }
        throw new RuntimeException("Uncorrected old password!");
    }

    public Mono<UserModel> changeName(UserModel user, UserChangeNameRequestDTO changeNameRequestDTO) {
        user.setName(changeNameRequestDTO.getName());

        return userRepository.save(user);
    }

    public Mono<UserModel> getRoles(UserModel user) {
        return rolesRepository
                .findByUserId(user.getId())
                .collectList()
                .map(roles -> {
                    user.setRoles(roles);
                    return user;
                });
    }

    public Mono<UserModel> saveRoles(UserModel user) {
        return rolesRepository.deleteByUserId(user.getId())
                .publishOn(Schedulers.boundedElastic())
                .doFinally(unused -> {
                    try {
                        Flux.fromIterable(user.getRoles())
                                .flatMap(role ->
                                        rolesRepository.addByUserId(user.getId(), role.getId())
                                ).singleOrEmpty().block();
                    } catch (Exception ignored) {
                    }
                }).thenReturn(user);
    }

    public Mono<UserModel> switchMFA(UserModel user, Boolean state) {
        user.setTfa(state);
        return userRepository.save(user);
    }

    public Mono<Void> delete(UserModel user) {
        return userRepository.delete(user).then();
    }
}
