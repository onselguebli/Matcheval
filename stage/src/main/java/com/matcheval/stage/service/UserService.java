package com.matcheval.stage.service;

import com.matcheval.stage.interfaces.IUserService;
import com.matcheval.stage.dto.ReqRes;
import com.matcheval.stage.model.Roles;
import com.matcheval.stage.model.Users;
import com.matcheval.stage.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService implements IUserService {
    @Autowired
    MailService mailService;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    AuthenticationManager authManager;
    @Autowired
    private JWTService jwtService;
    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);


    @Override
public ReqRes login(ReqRes loginRequest) {
    ReqRes response = new ReqRes();

    try {
        System.out.println("Tentative de connexion pour l'utilisateur : " + loginRequest.getEmail());

        // Authentifier l'utilisateur
        authManager.authenticate(new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );
        Users user = userRepo.findByEmail(loginRequest.getEmail());
        if (user == null) {
            throw new UsernameNotFoundException("Utilisateur introuvable");
        }

        // Générer les tokens
        String token = jwtService.generateToken(user);
        //String refreshToken = jwtService.generateRefreshToken(new HashMap<>(), user); // facultatif


        response.setStatusCode(200);
        response.setEmail(loginRequest.getEmail());
        response.setPassword(loginRequest.getPassword());
        response.setToken(token);
       // response.setRefreshToken(refreshToken);
        response.setExpirationTime("24Hrs");
        response.setRole(user.getRole());
        response.setMessage("Connexion réussie");

    } catch (Exception e) {
        System.out.println("Erreur login : " + e.getMessage());
        response.setStatusCode(401);
        response.setMessage("Échec de l'authentification : " + e.getMessage());
    }

    return response;
}

    public ReqRes register(ReqRes registrationRequest) {
        ReqRes resp = new ReqRes();
        System.out.println("EMAIL = " + registrationRequest.getEmail());


        try {
            if (userRepo.existsByEmail(registrationRequest.getEmail())) {
                resp.setStatusCode(409);
                resp.setMessage("Email déjà utilisé");
                return resp;
            }

            Users user = new Users();
            user.setEmail(registrationRequest.getEmail());
            String rawPassword = registrationRequest.getPassword();
            user.setPassword(encoder.encode(registrationRequest.getPassword()));
            user.setFirstname(registrationRequest.getFirstname());
            user.setLastname(registrationRequest.getLastname());
            user.setCivility(registrationRequest.getCivility());
            user.setCreatedAt(new Date());
            user.setDnaiss(registrationRequest.getDnaiss());
            user.setPhonenumber(registrationRequest.getPhonenumber());
            user.setEnabled(registrationRequest.getEnabled());
            if (registrationRequest.getEnabled() != null && registrationRequest.getEnabled()) {
                user.setLocked(false);
            } else {
                user.setLocked(true);
            }
            user.setRole(registrationRequest.getRole());
            // ✅ Affecter un manager si le rôle est RECRUITER
            if (registrationRequest.getRole() == Roles.RECRUITER) {
                if (registrationRequest.getManagerId() != null) {
                    Users manager = userRepo.findById(registrationRequest.getManagerId())
                            .orElseThrow(() -> new RuntimeException("Manager non trouvé"));
                    if (manager.getRole() != Roles.MANAGER) {
                        throw new RuntimeException("L'utilisateur spécifié n'est pas un manager");
                    }
                    user.setManager(manager);
                } else {
                    throw new RuntimeException("Un recruteur doit avoir un manager affecté");
                }
            }

            Users savedUser = userRepo.save(user);

            if (savedUser.getId() > 0) {
                resp.setUser(savedUser);
                resp.setMessage("Utilisateur ajouté avec succès.");
                resp.setStatusCode(200);
                mailService.sendAccountCreatedEmail(savedUser.getEmail(), rawPassword);
            }

        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatusCode(500);
            resp.setError(e.getMessage());
        }

        return resp;
    }

    @Override
    public ReqRes getAllUsers() {
        ReqRes reqRes = new ReqRes();

        try {
            List<Users> result = userRepo.findAll();
            if (!result.isEmpty()) {
                reqRes.setListusers(result);
                reqRes.setStatusCode(200);
                reqRes.setMessage("Successful");
            } else {
                reqRes.setStatusCode(404);
                reqRes.setMessage("No users found");
            }
            return reqRes;
        } catch (Exception e) {
            reqRes.setStatusCode(500);
            reqRes.setMessage("Error occurred: " + e.getMessage());
            return reqRes;
        }
    }

    @Override
    public ReqRes getManagers() {
        ReqRes reqRes = new ReqRes();

        try {
            List<Users> result = userRepo.findByRole(Roles.MANAGER);
            if (!result.isEmpty()) {
                reqRes.setListusers(result);
                reqRes.setStatusCode(200);
                reqRes.setMessage("Successful");
            } else {
                reqRes.setStatusCode(404);
                reqRes.setMessage("No users found");
            }
            return reqRes;
        } catch (Exception e) {
            reqRes.setStatusCode(500);
            reqRes.setMessage("Error occurred: " + e.getMessage());
            return reqRes;
        }
    }

    @Override
    public ReqRes getUsersById(Long id) {
            ReqRes reqRes = new ReqRes();
            try {
                Users usersById = userRepo.findById(id).orElseThrow(() -> new RuntimeException("User Not found"));
                reqRes.setUser(usersById);
                reqRes.setStatusCode(200);
                reqRes.setMessage("Users with id '" + id + "' found successfully");
            } catch (Exception e) {
                reqRes.setStatusCode(500);
                reqRes.setMessage("Error occurred: " + e.getMessage());
            }
            return reqRes;
        }


    @Override
    public ReqRes updateUser(Long id, ReqRes updatedData) {
        ReqRes response = new ReqRes();

        try {
            Users existingUser = userRepo.findById(id).orElse(null);
            if (existingUser == null) {
                response.setStatusCode(404);
                response.setMessage("Utilisateur non trouvé");
                return response;
            }

            // Met à jour seulement les champs fournis (non nuls)
            if (updatedData.getFirstname() != null)
                existingUser.setFirstname(updatedData.getFirstname());

            if (updatedData.getLastname() != null)
                existingUser.setLastname(updatedData.getLastname());

            if (updatedData.getEmail() != null)
                existingUser.setEmail(updatedData.getEmail());

            if (updatedData.getDnaiss() != null)
                existingUser.setDnaiss(updatedData.getDnaiss());

            if (updatedData.getCivility() != null)
                existingUser.setCivility(updatedData.getCivility());

            if (updatedData.getPhonenumber() != null)
                existingUser.setPhonenumber(updatedData.getPhonenumber());

            if (updatedData.getRole() != null)
                existingUser.setRole(updatedData.getRole());

            if (updatedData.getEnabled() != null) {
                existingUser.setEnabled(updatedData.getEnabled());
                existingUser.setLocked(!updatedData.getEnabled());
            }

            Users savedUser = userRepo.save(existingUser);
            response.setStatusCode(200);
            response.setMessage("Utilisateur mis à jour avec succès");
            response.setUser(savedUser);

        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Erreur lors de la mise à jour : " + e.getMessage());
        }

        return response;
    }
    public ReqRes blockUser(Long userId) {
        ReqRes reqRes = new ReqRes();
        try {
            Users user = userRepo.findById(userId).orElse(null);
            if (user == null) {
                reqRes.setStatusCode(404);
                reqRes.setMessage("Utilisateur non trouvé");
                return reqRes;
            }

            boolean isEnabled = !user.isEnabled(); // inverser
            user.setEnabled(isEnabled);
            user.setLocked(!isEnabled); // logique opposée
            Users savedUser = userRepo.save(user);

            reqRes.setStatusCode(200);
            reqRes.setMessage("Statut mis à jour");
            reqRes.setUser(savedUser);

        } catch (Exception e) {
            reqRes.setStatusCode(500);
            reqRes.setMessage("Erreur: " + e.getMessage());
        }
        return reqRes;
    }
    public List<Users> findRecruteursByManagerId(Long managerId) {
        return userRepo.findByManagerId(managerId);  // Assure-toi que ce repo existe
    }

    @Override
    public Map<String, Long> countUsersByRole() {
        List<Object[]> results = userRepo.countUsersByRole();

        Map<String, Long> roleCounts = new HashMap<>();
        for (Object[] row : results) {
            String role = row[0].toString(); // u.role is an enum
            Long count = (Long) row[1];
            roleCounts.put(role, count);
        }

        return roleCounts;
    }

    @Override
    public Map<Integer, Long> countUsersByYear() {
        List<Object[]> results = userRepo.countUsersByYear();

        Map<Integer, Long> yearCounts = new HashMap<>();
        for (Object[] row : results) {
            Integer year = (Integer) row[0];
            Long count = (Long) row[1];
            yearCounts.put(year, count);
        }

        return yearCounts;
    }

}





