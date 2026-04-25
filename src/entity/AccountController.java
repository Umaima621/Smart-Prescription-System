package entity;

public class AccountController {
    private UserRepository userRepo = new UserRepository();

    public boolean login(String id, String password) {
        return userRepo.validatePassword(id, password);
    }

    public String getRole(String id) {
        return userRepo.getRole(id);
    }
}