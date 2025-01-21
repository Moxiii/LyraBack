package com.moxi.lyra;

import lombok.extern.slf4j.Slf4j;
import com.moxi.lyra.Calendar.Calendar;
import com.moxi.lyra.Calendar.CalendarRepository;
import com.moxi.lyra.Projets.Projets;
import com.moxi.lyra.Projets.ProjetsRepository;
import com.moxi.lyra.User.Provider;
import com.moxi.lyra.User.UserRole.UserRole;
import com.moxi.lyra.User.UserRole.UserRoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import com.moxi.lyra.User.User;
import com.moxi.lyra.User.UserRepository;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Slf4j
@SpringBootApplication
@EnableWebSecurity
public class LyraApplication {
    public static void main(String[] args) {
        SpringApplication.run(LyraApplication.class, args);
    }

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final ProjetsRepository projetsRepository;

    public LyraApplication(UserRepository userRepository, UserRoleRepository userRoleRepository,ProjetsRepository projetsRepository) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.projetsRepository = projetsRepository;
    }

    @Bean
    public CommandLineRunner defaultDataInitializer() {
        return args -> {
            Date aujourdhui = new Date();
            SimpleDateFormat formatedDate = new SimpleDateFormat("dd-MM-yyyy");
            String dateString = formatedDate.format(aujourdhui);
            log.info("formated date" + dateString);
            if (userRepository.count()==0){
                UserRole userRole = new UserRole("user","user",1l);
                UserRole LyraRole = new UserRole("Lyra","Lyra",2l);
               userRole = userRoleRepository.save(userRole);
               LyraRole = userRoleRepository.save(LyraRole);
                User Lyra = new User("Lyra" , "Lyra" , "Lyra.app.sav@gmail.com","ee",dateString , LyraRole);
                User moxi = new User("moxi","moxi","moxi@moxi.com","ee",dateString,userRole);
                User test = new User("test","test","test@e.e","ee",dateString,userRole);;
                User martindvt = new User("martin","martindvt","martin@martin.com","ee",dateString,userRole);
                moxi.setDescription("backend Dev of Gilbert");
                martindvt.setDescription("CEO of the world");
                moxi.setProvider(Provider.LOCAL);
                martindvt.setProvider(Provider.LOCAL);
                test.setProvider(Provider.LOCAL);
                List<User> users = Arrays.asList(moxi, martindvt, test , Lyra);
                users.forEach(user -> {
                    Calendar calendar = new Calendar();
                    calendar.setUser(user);
                    user.setCalendar(calendar);
                });
                userRepository.saveAll(users);
            }
            if(projetsRepository.count()==0){
                User martindvt = userRepository.findByUsername("martindvt");
                User moxi = userRepository.findByUsername("moxi");
                List<User> users = Arrays.asList(moxi, martindvt);
                List<String> links = Arrays.asList("https://www.google.com/" , "https://github.com/Martindvttt/wiveapp");
                Projets wive = new Projets("wive" , "Un super projet de fou" ,links  , users );
                wive.setId(moxi.getId()+System.currentTimeMillis());
                projetsRepository.save(wive);
            }

        };
    }

}
