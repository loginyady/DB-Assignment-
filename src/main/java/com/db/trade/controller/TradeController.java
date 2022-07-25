package com.db.trade.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.db.trade.dao.TradeRepository;
import com.db.trade.exception.InvalidTradeException;
import com.db.trade.model.Trade;
import com.db.trade.service.TradeService;

@RestController
public class TradeController {
	
	@Autowired
	TradeRepository tradeRepository;
	
	@Autowired
    TradeService tradeService;
	
	@GetMapping("/trade")
	public List<Trade> retrieveAllTrades(){		
		return tradeRepository.findAll();
	}
	
	@PostMapping("/trade")
    public ResponseEntity<String> tradeValidateStore(@RequestBody Trade trade){
       if(tradeService.isValid(trade)) {
           tradeService.persist(trade);
		} 
			  else{ 
			  throw new
			  InvalidTradeException(trade.getTradeId()+"  Trade lower version is being received by the store, Not accepted the Trade "); }
			 
        return ResponseEntity.status(HttpStatus.OK).build();
    }

}
