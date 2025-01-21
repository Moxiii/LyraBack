    package com.moxi.lyra.User;


    import com.moxi.lyra.Calendar.CalendarService;
    import com.moxi.lyra.Projets.ProjetService;
    import com.moxi.lyra.Todo.TodoService;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.stereotype.Service;

    import java.util.Collections;
    import java.util.List;


    @Service 
    public class UserService {
        @Autowired
        private UserRepository userRepository;
        @Autowired
        private CalendarService calendarService;
        @Autowired
        private TodoService todoService;
        @Autowired
        private ProjetService projetService;


        public User findByEmail(String email) {
            User user = userRepository.findByEmail(email);
            return user;
        }
        public void  deleteUser(User user){
            if(userRepository.existsById(user.getId())){
                calendarService.deleteCalendarByUser(user);
                todoService.deleteAllTodosByUSer(user);
                projetService.deleteAllByUser(user);
                userRepository.delete(user);
                }
        }

        public List<User> searchParticipants(String query) {
               User userByName = userRepository.findByUsername(query);
            if (userByName != null) {
                return Collections.singletonList(userByName);
            }

            List<User> usersByNameOrEmail = userRepository.findByUsernameContainingOrEmailContaining(query, query);
            return usersByNameOrEmail;
        }

        public User findByUsername(String username) {
            return userRepository.findByUsername(username);
        }
        public void saveUser(User user) {
            userRepository.save(user);
        }
        public User findById(Long id) {
            if(userRepository.existsById(id)){
                return userRepository.findById(id).get();
            }
            return null;
        }

    }
