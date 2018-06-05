package io.trxplorer.webapp.service;

import static io.trxplorer.model.Tables.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.jooq.DSLContext;

import com.google.inject.Inject;
import com.google.inject.Singleton;

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
				.fetchOneInto(BigDecimal.class);
		
	}
	
	
}
