package io.trxplorer.troncli;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tron.api.GrpcAPI.EmptyMessage;
import org.tron.api.GrpcAPI.NumberMessage;
import org.tron.api.GrpcAPI.Return;
import org.tron.api.GrpcAPI.WitnessList;
import org.tron.api.WalletGrpc;
import org.tron.common.utils.ByteArray;
import org.tron.common.utils.Sha256Hash;
import org.tron.core.Constant;
import org.tron.core.Wallet;
import org.tron.core.capsule.BlockCapsule;
import org.tron.protos.Protocol.Account;
import org.tron.protos.Protocol.Block;
import org.tron.protos.Protocol.Transaction;
import org.tron.protos.Protocol.Transaction.Contract.Builder;
import org.tron.protos.Protocol.Transaction.Contract.ContractType;
import org.tron.protos.Protocol.Witness;

import com.google.inject.Inject;
import com.google.protobuf.Any;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.typesafe.config.Config;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.trxplorer.troncli.wallet.BroadcastResult;
import io.trxplorer.troncli.wallet.TransactionUtils;

public class TronCli {
	
	private ManagedChannel channelFull = null;
	private WalletGrpc.WalletBlockingStub client = null;
	
	   byte ADD_PRE_FIX_BYTE_MAINNET = (byte) 0x41;   //41 + address
	   
	   byte ADD_PRE_FIX_BYTE_TESTNET = (byte) 0xa0;   //a0 + address
	
	private static final Logger logger = LoggerFactory.getLogger(TronCli.class);
	
	@Inject
	public TronCli(Config config) {
		this(config.getString("tron.fullnode"),config.getBoolean("tron.mainNet"));
	}

	public TronCli(String fullNodeAddress,boolean mainNet) {
		
		if (mainNet) {
			Wallet.setAddressPreFixByte(Constant.ADD_PRE_FIX_BYTE_MAINNET);	
		}
		
		
		channelFull = ManagedChannelBuilder.forTarget(fullNodeAddress)
	              .usePlaintext(true)
	              .build();
		
		this.client = WalletGrpc.newBlockingStub(channelFull);
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


	
	private Transaction createTransaction(Message contract,ContractType contractType) {
		
		Builder contractMessage = Transaction.Contract.newBuilder().setType(contractType)
		.setParameter(Any.pack(contract));
		
		Transaction.raw.Builder transactionRawBuilder = Transaction.raw.newBuilder();
		transactionRawBuilder.addContract(contractMessage);
		
		Block lastBlock = this.getLastBlock();
		BlockCapsule capsule = new BlockCapsule(lastBlock);
	
		byte[] blockHash = capsule.getBlockId().getBytes();
		byte[] refBlockNum = ByteArray.fromLong(capsule.getNum());
		transactionRawBuilder.setRefBlockHash(ByteString.copyFrom(ByteArray.subArray(blockHash, 8, 16)));
		transactionRawBuilder.setRefBlockBytes(ByteString.copyFrom(ByteArray.subArray(refBlockNum, 6, 8)));
		transactionRawBuilder.setTimestamp(System.currentTimeMillis());
		transactionRawBuilder.setExpiration(lastBlock.getBlockHeader().getRawData().getTimestamp()+(60*1000));		
		
		Transaction transaction = Transaction.newBuilder().setRawData(transactionRawBuilder.build()).build();
		
		return transaction;
	
	}
	

	public BroadcastResult broadcastTransaction(byte[] bytes) {
		
		BroadcastResult result = new BroadcastResult();		
		try {

			Transaction transaction = Transaction.parseFrom(bytes);
			boolean validTx = TransactionUtils.validTransaction(transaction);

			
			if (!validTx) {
				result.setErrorMsg("Invalid transaction");
				result.setSuccess(false);
				return result;
			}
			
			Return bReturn = this.client.broadcastTransaction(transaction);
			
			result.setSuccess(bReturn.getResult());
			result.setErrorMsg(bReturn.getMessage().toStringUtf8());
			result.setCode(bReturn.getCode().getNumber());
			result.setTxId(Sha256Hash.of(transaction.getRawData().toByteArray()).toString());
			
			
			return result;
			
		} catch (InvalidProtocolBufferException e) {
			result.setSuccess(false);
			result.setErrorMsg("Could not parse transaction");
		}

		
		
		return result;
	}
	



}
