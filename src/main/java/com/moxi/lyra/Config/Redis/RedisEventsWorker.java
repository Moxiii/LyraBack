package com.moxi.lyra.Config.Redis;

import com.moxi.lyra.Calendar.Event.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class RedisEventsWorker {
@Autowired
private EventService eventService;

@Scheduled(fixedRate = 5000)
public void processRedisEvents() {
	eventService.transfertEventsFromRedisToMysql();
}
}
