package com.ran.demo.dubboproducer;

public interface UserService {
    User getUserById(int id);
    User getUserByUser(User user);
}
