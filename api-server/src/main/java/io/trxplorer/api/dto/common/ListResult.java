package io.trxplorer.api.dto.common;

import java.util.List;

public class ListResult<T,C extends CommonCriteria> {

	private int totalCount;
	
	private List<T> items;
	
	private C criteria;

	/**
	 * Do not use only for serialization
	 */
	public ListResult() {
	}
	
	public ListResult(C criteria,List<T> items,int totalCount) {
		this.criteria = criteria;
		this.items = items;
		this.totalCount = totalCount;
	}
	
	public int getTotalPages(){
		return (int)Math.round(Math.ceil((double)totalCount/(double)this.criteria.getLimit()));
	}


	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public List<T> getItems() {
		return items;
	}

	public void setItems(List<T> items) {
		this.items = items;
	}

	public C getCriteria() {
		return criteria;
	}

	public void setCriteria(C criteria) {
		this.criteria = criteria;
	}

	
}
