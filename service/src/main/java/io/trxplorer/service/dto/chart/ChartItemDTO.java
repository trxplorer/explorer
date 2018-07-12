package io.trxplorer.service.dto.chart;

public class ChartItemDTO {

	private String title;
	
	private String description;
	
	private String imgUrl;
	
	private ChartType type;
	
	public ChartItemDTO(String title,ChartType type,String description) {
		this.title = title;
		this.type = type;
		this.description = description;
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	public ChartType getType() {
		return type;
	}

	public void setType(ChartType type) {
		this.type = type;
	}
	
	@Override
	public boolean equals(Object obj) {
		
		ChartItemDTO o = (ChartItemDTO) obj;
		
		return o.getType().equals(this.getType());
	}
	
	
	
}
