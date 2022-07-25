package com.db.trade.service;

import java.time.LocalDate;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.db.trade.dao.TradeRepository;
import com.db.trade.model.Trade;

@Service
public class TradeService {

	private static final Logger log = LoggerFactory.getLogger(TradeService.class);

	@Autowired
	TradeRepository tradeRepository;

	public boolean isValid(Trade trade) {
		if (validateMaturityDate(trade)) {
			// get the existing trade for validating the trade version.
			Optional<Trade> exsitingTrade = tradeRepository.findById(trade.getTradeId());
			if (exsitingTrade.isPresent()) {
				return validateVersion(trade, exsitingTrade.get());
			} else {
				return true;
			}
		}
		return false;
	}

	// Store should not allow the trade which has less maturity date then today
	// date.
	private boolean validateMaturityDate(Trade trade) {
		return trade.getMaturityDate().isBefore(LocalDate.now()) ? false : true;
	}

	private boolean validateVersion(Trade trade, Trade oldTrade) {
		// During transmission if the lower version is being received by the store it
		// will reject the trade and throw an exception. If the version is same it will
		// override the existing record.
		return trade.getVersion() >= oldTrade.getVersion() ? true : false;
	}

	// Store should automatically update the expire flag if in a store the trade
	// crosses the maturity date.
	public void updateExpiryFlagOfTrade() {

		tradeRepository.findAll().stream().forEach(t -> {
			if (!validateMaturityDate(t)) {
				t.setExpired("Y");
				log.info("Trade which needs to updated {}", t);
				tradeRepository.save(t);
			}
		});
	}

	public void persist(Trade trade) {
		// tradeDao.save(trade);
		trade.setCreatedDate(LocalDate.now());
		tradeRepository.save(trade);
	}

}
