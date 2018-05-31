package io.trxplorer.api.dto.common;

public class CommonCriteria {
	
	private Integer limit;

	private Integer page;
	
	
	public int getOffSet(){
		
		if (getPage()==1){
			return 0;
		}else{
			return getLimit() * (getPage()-1);
		}
		

		
	}
	
	public Integer getLimit() {
		return limit;
	}
	
	public void setLimit(Integer limit) {
		this.limit = limit;
	}
	
	
	public Integer getPage() {
		return page;
	}
	
	public void setPage(Integer page) {
		this.page = page;
	}

	
}
