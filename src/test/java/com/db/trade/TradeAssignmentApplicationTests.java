package com.db.trade;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.db.trade.controller.TradeController;
import com.db.trade.exception.InvalidTradeException;
import com.db.trade.model.Trade;

@SpringBootTest
class TradeAssignmentApplicationTests {

	@Test
	void contextLoads() {
	}

	@Autowired
	private TradeController tradeController;

	@Test
	void testTradeValidateAndStore_successful() {
		ResponseEntity responseEntity = tradeController.tradeValidateStore(createTrade("T1",1,LocalDate.now()));
		Assertions.assertEquals(ResponseEntity.status(HttpStatus.OK).build(),responseEntity);
		List<Trade> tradeList =tradeController.retrieveAllTrades();
		Assertions.assertEquals(3, tradeList.size());
		Assertions.assertEquals("T1",tradeList.get(0).getTradeId());
	}
	
	@Test
	void testTradeValidateAndStoreWhenMaturityDatePast() {
		try {
			LocalDate localDate = getLocalDate(2015, 05, 21);
			ResponseEntity responseEntity = tradeController.tradeValidateStore(createTrade("T2", 1, localDate));
		}catch (InvalidTradeException ie) {
			Assertions.assertEquals("Invalid Trade: T2  Trade lower version is being received by the store, Not accepted the Trade ", ie.getMessage());
		}
	}
	
	
	
	@Test
	void testTradeValidateAndStoreWhenOldVersion() {
		// create trade
		ResponseEntity responseEntity = tradeController.tradeValidateStore(createTrade("T1",2,LocalDate.now()));
		Assertions.assertEquals(ResponseEntity.status(HttpStatus.OK).build(),responseEntity);
		List<Trade> tradeList =tradeController.retrieveAllTrades();
		Assertions.assertEquals(3, tradeList.size());
		Assertions.assertEquals("T1",tradeList.get(0).getTradeId());
		Assertions.assertEquals(2,tradeList.get(0).getVersion());
		Assertions.assertEquals("B1",tradeList.get(0).getBookId());

		//create trade with old version
		try {
			ResponseEntity responseEntity1 = tradeController.tradeValidateStore(createTrade("T1", 1, LocalDate.now()));


		}catch (InvalidTradeException e){
			System.out.println(e.getId());
			System.out.println(e.getMessage());
		}
		List<Trade> tradeList1 =tradeController.retrieveAllTrades();
		Assertions.assertEquals(3, tradeList1.size());
		Assertions.assertEquals("T1",tradeList1.get(0).getTradeId());
		Assertions.assertEquals(2,tradeList1.get(0).getVersion());
		Assertions.assertEquals("B1",tradeList.get(0).getBookId());
	}
	
	
	
	@Test
	void testTradeValidateAndStoreWhenSameVersionTrade(){
		ResponseEntity responseEntity = tradeController.tradeValidateStore(createTrade("T1",2,LocalDate.now()));
		Assertions.assertEquals(ResponseEntity.status(HttpStatus.OK).build(),responseEntity);
		List<Trade> tradeList =tradeController.retrieveAllTrades();
		Assertions.assertEquals(3, tradeList.size());
		Assertions.assertEquals("T1",tradeList.get(0).getTradeId());
		Assertions.assertEquals(2,tradeList.get(0).getVersion());
		Assertions.assertEquals("B1",tradeList.get(0).getBookId());

		//create trade with same version
		Trade trade2 = createTrade("T1",2,LocalDate.now());
		trade2.setBookId("B1");
		ResponseEntity responseEntity2 = tradeController.tradeValidateStore(trade2);
		Assertions.assertEquals(ResponseEntity.status(HttpStatus.OK).build(),responseEntity2);
		List<Trade> tradeList2 =tradeController.retrieveAllTrades();
		Assertions.assertEquals(3, tradeList2.size());
		Assertions.assertEquals("T1",tradeList2.get(0).getTradeId());
		Assertions.assertEquals(2,tradeList2.get(0).getVersion());
		Assertions.assertEquals("B1",tradeList2.get(0).getBookId());

		//create trade with new version
		Trade trade3 = createTrade("T1",2,LocalDate.now());
		trade3.setBookId("B1");
		ResponseEntity responseEntity3 = tradeController.tradeValidateStore(trade3);
		Assertions.assertEquals(ResponseEntity.status(HttpStatus.OK).build(),responseEntity3);
		List<Trade> tradeList3 =tradeController.retrieveAllTrades();
		Assertions.assertEquals(3, tradeList3.size());
		Assertions.assertEquals("T1",tradeList3.get(0).getTradeId());
		Assertions.assertEquals(2,tradeList3.get(0).getVersion());
		Assertions.assertEquals("B1",tradeList3.get(0).getBookId());

	}

	
	
	
	private Trade createTrade(String tradeId,int version,LocalDate  maturityDate){
		Trade trade = new Trade();
		trade.setTradeId(tradeId);
		trade.setBookId("B1");
		trade.setVersion(version);
		trade.setCounterPartyId("CP-1");
		trade.setMaturityDate(maturityDate);
		trade.setExpired("Y");
		return trade;
	}
	
	public static LocalDate getLocalDate(int year,int month, int day){
		LocalDate localDate = LocalDate.of(year,month,day);
		return localDate;
	}
	
}
