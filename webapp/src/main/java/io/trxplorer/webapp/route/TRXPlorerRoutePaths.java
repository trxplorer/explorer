package io.trxplorer.webapp.route;

public interface TRXPlorerRoutePaths {

	
	public static interface Front{
		
		// HOME
		public static final String HOME = "/";
		
		// STATUS
		public static final String STATUS = "/status";
		
		// BLOCK
		public static final String BLOCK_DETAIL = "/block/:num";
		public static final String BLOCK_LIST = "/blocks";

		// TRANSACTION
		public static final String TRANSACTION_DETAIL = "/tx/:txid";
		public static final String TRANSACTION_LIST = "/transactions";
		
		//ACCOUNT
		public static final String ACCOUNT_DETAIL = "/address/:address";
		public static final String ACCOUNT_LIST = "/accounts";		
		public static final String ACCOUNT_LOGIN = "/login";
		
		//TOKEN
		public static final String ASSET_DETAIL = "/token/:assetName";
		public static final String ASSET_LIST = "/tokens";
		
		//WITNESS
		public static final String REPRESENTATIVE_LIST = "/representatives";
		public static final String SUPER_REPRESENTATIVE_LIST = "/super-representatives";
		
		//SEARCH
		public static final String SEARCH = "/search";
		public static final String SEARCH_WITNESS = SEARCH+"/witness";		
		public static final String SEARCH_TOKEN = SEARCH+"/token";
		
		//CHART
		public static final String CHART_LIST = "/charts";		
		public static final String CHART_DETAIL = "/chart/:name";
		public static final String CHART_API_DATA = "/chart/api/:type/:since";
		
		//NODE
		public static final String NODE_LIST = "/nodes";

		//API
		public static final String API = "/api";
		public static final String API_CHART_TX_SINCE = API+"/chart/tx/:since";

	
		public static final String TRON_API_BROADCAST_TX = API+"/tron/broadcast";
		
	}
	
}
