package io.trxplorer.syncnode.service;

import java.util.ArrayList;
import java.util.List;

import org.jooq.DSLContext;
import org.tron.protos.Protocol.Witness;

import com.google.inject.Inject;

import io.trxplorer.syncnode.utils.Base58;
import io.trxplorer.troncli.TronCli;

public class WitnessSyncService {

	private DSLContext dslContext;
	private WitnessService witnessService;
	private TronCli tronCli;

	@Inject
	public WitnessSyncService(DSLContext dslContext,WitnessService witnessService,TronCli tronCli) {
		this.dslContext = dslContext;
		this.witnessService = witnessService;
		this.tronCli = tronCli;
	}
	
	public void syncWitnesses() {
		
		List<String> currentWitnessAddresses = new ArrayList<>();
		
		//create of updated
		for(Witness witness:this.tronCli.getAllWitnesses()) {
			
			currentWitnessAddresses.add(Base58.encode58Check(witness.getAddress().toByteArray()));
			
			this.witnessService.createOrUpdateWitness(witness);
			
		}
		
		
		
	}
	
	
}
