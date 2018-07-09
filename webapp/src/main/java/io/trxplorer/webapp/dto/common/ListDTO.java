package io.trxplorer.webapp.dto.common;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListDTO<T,C extends CommonCriteriaDTO> {

	private int totalCount;
	
	private List<T> items;
	
	private C criteria;

	/**
	 * Do not use only for serialization
	 */
	public ListDTO() {
	}
	
	public ListDTO(C criteria,List<T> items,int totalCount) {
		this.criteria = criteria;
		this.items = items;
		this.totalCount = totalCount;
	}
	
	public List<Integer> pages(){
		
		List<Integer> pages = new ArrayList<>(); 
		
		int currentPage = criteria.getPage();
		int limit = criteria.getLimit();
		
		int start = currentPage>5 ? currentPage-2 : 1; 
		
		int maxPage = new BigDecimal(totalCount).divide(new BigDecimal(limit), RoundingMode.CEILING).intValue(); 
		
		for (int i = start; i < start+5; i++) {
			
			if (i>maxPage){
				break;
			}
			
			pages.add(i);
			

			
		}
		
		
		return pages;
	}
	
	public int nextPage(){
		return criteria.getPage() +1;
	}

	public int previousPage(){
		return criteria.getPage() -1;
	}
	
	public int totalPages(){
		return (int)Math.round(Math.ceil((double)totalCount/(double)this.criteria.getLimit()));
	}
	
	public String params(){
		
		StringBuilder sb = new StringBuilder("&limit="+criteria.getLimit());
		
		
		for(String key:criteria.params().keySet()){
			sb.append("&"+key+"="+criteria.params().get(key));	
		}
		
		
		
		
		return sb.toString();
	}
	
	
	public String info(){
		
		int min = criteria.getPage() == 1 ? 1 : (criteria.getLimit()*(criteria.getPage()-1))+1;
		int max = min-1 +criteria.getLimit();
		return String.format("Page %s on %s ",criteria.getPage(), totalPages());
		
	}
	
	public Map<String,Object> toMap(){
		HashMap<String, Object> result = new HashMap<>();
		
		result.put("items", items);
		result.put("totalCount", totalCount);
		result.put("criteria", criteria);
		result.put("nextPage", nextPage());
		result.put("previousPage", previousPage());
		result.put("totalPages", totalPages());
		return result;
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
