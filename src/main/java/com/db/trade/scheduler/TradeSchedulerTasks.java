package com.db.trade.scheduler;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.db.trade.service.TradeService;

@Component
public class TradeSchedulerTasks {
	
	private static final Logger log = LoggerFactory.getLogger(TradeSchedulerTasks.class);

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

	@Autowired
	TradeService tradeService;

	@Scheduled(cron = "${trade.expiry.schedule}")   //currently setup 30 min
	public void reportCurrentTime() {
		log.info("The time is now {}", dateFormat.format(new Date()));
		tradeService.updateExpiryFlagOfTrade();
	}

}
