package io.trxplorer.webapp.service;

import static io.trxplorer.model.Tables.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.jooq.DSLContext;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import io.trxplorer.webapp.dto.market.MarketDTO;

@Singleton
public class MarketService {

	private DSLContext dslContext;

	@Inject
	public MarketService(DSLContext dslContext) {
		this.dslContext = dslContext;
	}
	
	public BigDecimal getCurrentAvgPrice() {
		
		int day = LocalDateTime.now().getDayOfMonth();
		int month = LocalDateTime.now().getDayOfMonth();
		int year = LocalDateTime.now().getYear();
		
		return this.dslContext.select(MARKET.PRICE)
				.from(MARKET)
				.where(MARKET.DAY.eq(day))
				.and(MARKET.MONTH.eq(month))
				.and(MARKET.YEAR.eq(year))
				.and(MARKET.SOURCE.eq("CMC"))
				.orderBy(MARKET.LAST_UPDATE.desc())
				.limit(1)
				.fetchOneInto(BigDecimal.class);
		
	}
	
	
	public List<MarketDTO> getMarkets(){
		
		int day = LocalDateTime.now().getDayOfMonth();
		int month = LocalDateTime.now().getDayOfMonth();
		int year = LocalDateTime.now().getYear();
		
		return this.dslContext.select(MARKET.SOURCE.as("market"),MARKET.PRICE,MARKET.PAIR,MARKET.VOLUME_24H)
				.from(MARKET)
				.where(MARKET.SOURCE.ne("CMC"))
				.and(MARKET.DAY.eq(day))
				.and(MARKET.MONTH.eq(month))
				.and(MARKET.YEAR.eq(year))
				.groupBy(MARKET.SOURCE,MARKET.PAIR)
				.orderBy(MARKET.VOLUME_24H.desc())
				.fetchInto(MarketDTO.class);
	}
	
	
}
