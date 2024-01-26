    package org.georges.georges.service;

    import org.georges.georges.pojos.User;
    import org.georges.georges.repository.UserRepository;
    import org.springframework.security.core.GrantedAuthority;
    import org.springframework.security.core.authority.SimpleGrantedAuthority;
    import org.springframework.stereotype.Service;

    import java.util.ArrayList;
    import java.util.Date;
    import java.util.Hashtable;
    import java.util.List;
    import java.util.stream.Collectors;


    @Service
    public class UserService {
        private final UserRepository userRepository;

        // creation dictionnaire user for local uses
        Hashtable<Long, User> membresDict = new Hashtable<Long, User>();

        public UserService(UserRepository userRepository) {
            this.userRepository = userRepository;

            //creation of member
            User user = new User("maxime", "moxi", "maxime.moxi@ipilyon.net", "12345", new Date("2019/11/13"), List.of("Admin"));
            user.setId(1);
            //add member on dict
            membresDict.put(1L, user);
        }

        public User findByUsername(String username) {
            User user = userRepository.findByPseudo(username);
            if (username == null || username.trim().isEmpty()) {
                return null;
            }
            if (user != null) {
                List<String> roles = determineRolesForUser(user);
                user.setRoles(roles);
            }
                return user;
        }

        private List<String> determineRolesForUser(User user) {
            List<String> roles = user.getRoles();

            // Si l'utilisateur n'a pas de rôle, attribuez-lui un rôle par défaut "USER".
            if (roles == null || roles.isEmpty()) {
                roles = new ArrayList<>();
                roles.add("USER");
            }
            return roles;
        }

        private List<String> determineRolesForChief(User user) {
            List<String> roles = new ArrayList<>();
            roles.add("CHIEF");
            return roles;
        }

        private List<String> determineRolesForAdmin(User user) {
            List<String> roles = new ArrayList<>();
            roles.add("ADMIN");
            return roles;
        }


        public List<GrantedAuthority> getAuthorities(User user) {
            return user.getRoles().stream()
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                    .collect(Collectors.toList());
        }

        public User getMembreFromId(int id) {

            return membresDict.get(id);
        }

        public User ajoutMembre(User user) {

            // génère l'id du membre
            user.setId(membresDict.size() + 1);

            // ajoute le membre dans le dictionnaire
            membresDict.put(user.getId(), user);
            userRepository.save(user);
            return user;
        }
        public List<User> getAllUsers() {
            return userRepository.findAll();
        }
        public void deleteUserByEmail(String Email){
            userRepository.deleteByEmail(Email);
        }
        public User deleteUserById(int id){
            return userRepository.deleteById(id);
        }
    }
