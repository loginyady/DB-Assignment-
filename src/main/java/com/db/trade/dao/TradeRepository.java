package com.db.trade.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.db.trade.model.Trade;

@Repository
public interface TradeRepository extends JpaRepository<Trade,String>  {

}
