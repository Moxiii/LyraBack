package org.georges.georges.Calendar;

import jakarta.servlet.http.HttpServletRequest;
import org.georges.georges.Config.JwtUtil;
import org.georges.georges.Config.SecurityUtils;
import org.georges.georges.User.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/calendar")
public class CalendarController {
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
   private CalendarRepository calendarRepository;

    @GetMapping("/get")
    public ResponseEntity<?> getCalendar(HttpServletRequest request) {
        if (SecurityUtils.isAuthorized(request, jwtUtil)) {
            User currentUser = SecurityUtils.getCurrentUser();
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized access");
    }
    @PostMapping("/add")
    public ResponseEntity<?> addCalendar(HttpServletRequest request) {
        if (SecurityUtils.isAuthorized(request, jwtUtil)) {
            User currentUser = SecurityUtils.getCurrentUser();
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized access");
    }
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateCalendar(HttpServletRequest request , @PathVariable long id) {
        if (SecurityUtils.isAuthorized(request, jwtUtil)) {
            User currentUser = SecurityUtils.getCurrentUser();
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized access");
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteCalendar(HttpServletRequest request , @PathVariable long id) {
        if (SecurityUtils.isAuthorized(request, jwtUtil)) {
            User currentUser = SecurityUtils.getCurrentUser();
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized access");
    }
    //Gestion des tache /evenement journalier
    @GetMapping("/event/get")
    public ResponseEntity<?> getDailyEvent(HttpServletRequest request) {
        if (SecurityUtils.isAuthorized(request, jwtUtil)) {
            User currentUser = SecurityUtils.getCurrentUser();
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized access");
    }
    @GetMapping("/event/{id}")
    public ResponseEntity<?> getDailyEventByID(HttpServletRequest request , @PathVariable long id) {
        if (SecurityUtils.isAuthorized(request, jwtUtil)) {
            User currentUser = SecurityUtils.getCurrentUser();
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized access");
    }
    @PostMapping("/event/add")
    public ResponseEntity<?> addEvent(HttpServletRequest request) {
        if (SecurityUtils.isAuthorized(request, jwtUtil)) {
            User currentUser = SecurityUtils.getCurrentUser();
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized access");
    }
    @PutMapping("/event/update/{id}")
    public ResponseEntity<?> updateEvent(HttpServletRequest request , @PathVariable long id) {
        if (SecurityUtils.isAuthorized(request, jwtUtil)) {
            User currentUser = SecurityUtils.getCurrentUser();
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized access");
    }
    @DeleteMapping("/event/delete/{id}")
    public ResponseEntity<?> deleteEvent(HttpServletRequest request, @PathVariable long id) {
        if (SecurityUtils.isAuthorized(request, jwtUtil)) {
            User currentUser = SecurityUtils.getCurrentUser();
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized access");
    }
}
