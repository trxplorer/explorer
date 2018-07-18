package io.trxplorer.api.route;

import java.util.Optional;

import org.jooby.mvc.GET;
import org.jooby.mvc.Path;

import com.google.inject.Inject;

import io.trxplorer.service.common.TransactionService;
import io.trxplorer.service.dto.common.ListModel;
import io.trxplorer.service.dto.transaction.TransactionCriteria;
import io.trxplorer.service.dto.transaction.TransactionModel;

public class TransactionRoutes {

	private TransactionService txService;

	@Inject
	public TransactionRoutes(TransactionService txService) {
		this.txService= txService;
	}
	

	@GET
	@Path(ApiAppRoutePaths.V1.TRANSACTIONS)
	public ListModel<TransactionModel, TransactionCriteria> listTransactions(Optional<Integer> block,Optional<Integer> page) throws Throwable {
		
		TransactionCriteria criteria = new TransactionCriteria();
		criteria.setLimit(50);
		criteria.setPage(page.orElse(1));
		criteria.setBlock(block.orElse(null));
		
		return this.txService.listTransactions(criteria);
		
	}
	
	
}
