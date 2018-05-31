package io.trxplorer.webapp.dto.chart;

public enum ChartType {

	TOTAL_TX("total-tx"),
	TOTAL_BLOCK("total-block"),
	AVG_TX_COUNT("transactions-per-block"),
	AVG_BLOCK_SIZE("block-size"),
	AVG_BLOCK_TIME("block-time")
	
	;
	
	private String slug;
	
	ChartType(String slug) {
		this.slug = slug;
	}
	
	public static ChartType from(String slug) {
		
		for(ChartType value:ChartType.values()) {
			
			if (value.slug.equals(slug)) {
				return value;
			}
			
		}
		
		return null;
	}
	
	
	public String getSlug() {
		return slug;
	}
	
}
