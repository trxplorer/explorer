package io.trxplorer.service.dto.market;

import java.text.NumberFormat;
import java.util.Locale;

public class MarketDTO {

	private String market;
	
	private String volume_24h;
	
	private String pair;
	
	private String price;

	public String getMarket() {
		return market;
	}

	public void setMarket(String market) {
		this.market = market;
	}

	public String getVolume_24h() {
		return NumberFormat.getNumberInstance(Locale.US).format(Long.valueOf(volume_24h));
	}

	public void setVolume_24h(String volume_24h) {
		this.volume_24h = volume_24h;
	}

	public String getPair() {
		return pair;
	}

	public void setPair(String pair) {
		this.pair = pair;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}
	
}
