package com.moxi.lyra.Calendar;

import com.moxi.lyra.User.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CalendarRepository extends JpaRepository<Calendar , Long> {
    Calendar findByUser(User user);
}
