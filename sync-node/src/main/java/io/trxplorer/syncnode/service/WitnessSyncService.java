package io.trxplorer.syncnode.service;

import java.util.ArrayList;
import java.util.List;

import org.jooq.DSLContext;
import org.tron.core.Wallet;
import org.tron.protos.Protocol.Witness;

import com.google.inject.Inject;

import io.trxplorer.troncli.TronFullNodeCli;

public class WitnessSyncService {

	private DSLContext dslContext;
	private WitnessService witnessService;
	private TronFullNodeCli tronFullNodeCli;

	@Inject
	public WitnessSyncService(DSLContext dslContext,WitnessService witnessService,TronFullNodeCli tronFullNodeCli) {
		this.dslContext = dslContext;
		this.witnessService = witnessService;
		this.tronFullNodeCli = tronFullNodeCli;
	}
	
	public void syncWitnesses() {
		
		List<String> currentWitnessAddresses = new ArrayList<>();
		
		//create of updated
		for(Witness witness:this.tronFullNodeCli.getAllWitnesses()) {
			
			currentWitnessAddresses.add(Wallet.encode58Check(witness.getAddress().toByteArray()));
			
			this.witnessService.createOrUpdateWitness(witness);
			
		}
		
		
		
	}
	
	
}
