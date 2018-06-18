package io.trxplorer.troncli;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tron.api.GrpcAPI.EmptyMessage;
import org.tron.api.GrpcAPI.NumberMessage;
import org.tron.api.GrpcAPI.WitnessList;
import org.tron.api.WalletSolidityGrpc;
import org.tron.core.Constant;
import org.tron.core.Wallet;
import org.tron.protos.Protocol.Account;
import org.tron.protos.Protocol.Block;
import org.tron.protos.Protocol.Witness;

import com.google.inject.Inject;
import com.google.protobuf.ByteString;
import com.typesafe.config.Config;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class TronSolidityNodeCli {
	
	private ManagedChannel channelFull = null;
	private WalletSolidityGrpc.WalletSolidityBlockingStub client = null;
	
	public static final byte ADD_PRE_FIX_BYTE_MAINNET = (byte) 0x41;   //41 + address
	   
	public static final byte ADD_PRE_FIX_BYTE_TESTNET = (byte) 0xa0;   //a0 + address
	
	private static final Logger logger = LoggerFactory.getLogger(TronSolidityNodeCli.class);
	
	@Inject
	public TronSolidityNodeCli(Config config) {
		this(config.getString("tron.soliditynode"),config.getBoolean("tron.mainNet"));
	}

	public TronSolidityNodeCli(String solidityNodeAddress,boolean mainNet) {
		
		if (mainNet) {
			Wallet.setAddressPreFixByte(Constant.ADD_PRE_FIX_BYTE_MAINNET);	
		}
		
		
		channelFull = ManagedChannelBuilder.forTarget(solidityNodeAddress)
	              .usePlaintext(true)
	              .build();
		
		this.client = WalletSolidityGrpc.newBlockingStub(channelFull);
	}

	
	public Account getAccountByAddress(String address) {
		try {
		ByteString addressBS = ByteString.copyFrom(Wallet.decodeFromBase58Check(address));
		
		Account request = Account.newBuilder().setAddress(addressBS).build();
		
		Account account = this.client.getAccount(request);

		return account;
		}catch(Exception e) {
			logger.error("Could not get account:"+address, e);
		}
		return null;
	}

	
	public Block getBlockByNum(Long blockNum) {

		return this.client.getBlockByNum(NumberMessage.newBuilder().setNum(blockNum).build());
	}

	
	public List<Witness> getAllWitnesses() {

		List<Witness> result = new ArrayList<>();

		WitnessList witnessList = this.client.listWitnesses(EmptyMessage.newBuilder().build());
		
		if (witnessList!=null) {
			result = witnessList.getWitnessesList();
		}

		return result;
	}
	
	
	
	public Block getLastBlock() {
		return client.getNowBlock(EmptyMessage.newBuilder().build());
	}



	



}
