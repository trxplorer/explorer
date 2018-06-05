package io.trxplorer.syncnode.service;

import static io.trxplorer.model.Tables.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Iterator;

import org.jooq.DSLContext;
import org.jooq.types.UInteger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import io.trxplorer.model.tables.pojos.Market;

public class MarketSyncService {

	private DSLContext dslContext;

	private static final Logger logger = LoggerFactory.getLogger(MarketSyncService.class);
	
	@Inject
	public MarketSyncService(DSLContext dslContext) {
		this.dslContext = dslContext;
	}

	
	public void syncMarkets() {
		
		try {
			
			Document res = Jsoup.connect("https://coinmarketcap.com/currencies/tron").get();
			
			String price = res.select("#quote_price").first().attr("data-usd");
			String avolume = res.select("span[data-currency-volume]").first().attr("data-usd");
			
			Market amarket = new Market();
			
			amarket.setPair("TRX/USD");
			amarket.setSource("CMC");
			amarket.setVolume_24h(UInteger.valueOf((int)Double.parseDouble(avolume)));
			amarket.setPrice(BigDecimal.valueOf(Double.valueOf(price)));
			
			createOrUpdate(amarket);
			
			Elements elements = res.select("#markets-table tbody tr");
			
			Iterator<Element> it = elements.iterator();
			
			while(it.hasNext()) {
				
				Element tr = it.next();
				Elements columns = tr.select("td");
				
				String source = columns.get(1).text();
				String pair = columns.get(2).text();
				String mvolume = columns.get(3).text().replace("$", "").replace(",", "");
				String mprice =  columns.get(4).text().replace("$", "").replace(",", "").replace("*", "");
				String updated = columns.get(6).text();
				

				if (!updated.toLowerCase().equals("recently") ||mvolume.contains("*")) {
					continue;
				}
				
				Market market = new Market();
				
				market.setPair(pair);
				market.setSource(source);
				market.setVolume_24h(UInteger.valueOf((int)Double.parseDouble(mvolume)));
				market.setPrice(BigDecimal.valueOf(Double.valueOf(mprice)));				
				
				createOrUpdate(market);
			}
			

		}catch(IOException e) {
			logger.error("Error while fetching markets data",e);
		}
		
	}

	
	private void createOrUpdate(Market market) {
		
		int day = LocalDateTime.now().getDayOfMonth();
		int month = LocalDateTime.now().getDayOfMonth();
		int year = LocalDateTime.now().getYear();
		
		UInteger id = this.dslContext.select(MARKET.ID)
		.from(MARKET)
		.where(MARKET.DAY.eq(day)
				.and(MARKET.MONTH.eq(month))
				.and(MARKET.YEAR.eq(year)))
				.and(MARKET.SOURCE.eq(market.getSource()))
		.fetchOneInto(UInteger.class);
		
		if (id==null) {
			
			this.dslContext.insertInto(MARKET)
			.set(MARKET.DAY,day)
			.set(MARKET.MONTH,month)
			.set(MARKET.YEAR,year)
			.set(MARKET.PAIR,market.getPair())
			.set(MARKET.SOURCE,market.getSource())
			.set(MARKET.VOLUME_24H,market.getVolume_24h())
			.set(MARKET.PRICE,market.getPrice())
			.set(MARKET.LAST_UPDATE,Timestamp.valueOf(LocalDateTime.now()))
			.execute();
			
		}else {
			
			this.dslContext.update(MARKET)
			.set(MARKET.PAIR,market.getPair())
			.set(MARKET.SOURCE,market.getSource())
			.set(MARKET.VOLUME_24H,market.getVolume_24h())
			.set(MARKET.PRICE,market.getPrice())
			.set(MARKET.LAST_UPDATE,Timestamp.valueOf(LocalDateTime.now()))
			.where(MARKET.ID.eq(id))
			.execute();			
			
			
		}
		
	}

	

	
}
