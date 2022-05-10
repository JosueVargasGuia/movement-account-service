package com.nttdata.movement.account.service.consumer;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.nttdata.movement.account.service.entity.MovementAccount;
import com.nttdata.movement.account.service.entity.TypeMovementAccount;
import com.nttdata.movement.account.service.service.MovementAccountService;
import com.nttdata.movementwallet.model.OperationStatus;
import com.nttdata.movementwallet.model.TranfersResponse;
import com.nttdata.movementwallet.model.TypeMovement;

import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;

@Log4j2
@Component
public class MovementWalletConsumer {
	@Value("${api.kafka-uri.tranfers-wallet-topic-respose}")
	String tranfersWalletTopicRespose;
	@Autowired
	MovementAccountService accountService;
	@Autowired
	KafkaTemplate<String, TranfersResponse> kafkaTemplate;

	/** Kafka que realiza la operacion de tranferencia entre las dos cuentas */
	@KafkaListener(topics = "${api.kafka-uri.tranfers-wallet-topic}", groupId = "group_id")
	public void movementWalletConsumer(TranfersResponse tranfersResponse) {

		MovementAccount movementAccountOrigen = new MovementAccount();
		movementAccountOrigen.setAmount(tranfersResponse.getAmount());
		movementAccountOrigen.setIdBankAccount(tranfersResponse.getOriginIdBankAccount());
		movementAccountOrigen.setTypeMovementAccount(
				(tranfersResponse.getTypeMovement() == TypeMovement.charged ? TypeMovementAccount.withdrawal
						: TypeMovementAccount.deposit));
		Map<String, Object> resultOrigen = accountService.recordsMovement(movementAccountOrigen).blockOptional()
				.orElse(null);
		log.info("Result [Origen-recordsMovement]:" + resultOrigen);
		boolean success = false;
		if (resultOrigen != null && resultOrigen.get("status").toString().equalsIgnoreCase("success")) {
			MovementAccount movementAccountDestino = new MovementAccount();
			movementAccountDestino.setAmount(tranfersResponse.getAmount());
			movementAccountDestino.setIdBankAccount(tranfersResponse.getDestinyIdBankAccount());
			movementAccountOrigen.setTypeMovementAccount(
					(tranfersResponse.getTypeMovement() == TypeMovement.charged ? TypeMovementAccount.deposit
							: TypeMovementAccount.withdrawal));
			tranfersResponse.setOriginIdMovementAccount(Long.valueOf(resultOrigen.get("idMovementAccount").toString()));
			Map<String, Object> resultDestino = accountService.recordsMovement(movementAccountDestino).blockOptional()
					.orElse(null);
			log.info("Result [Destino-recordsMovement]:" + resultDestino);
			if (resultDestino != null && resultDestino.get("status").toString().equalsIgnoreCase("success")) {
				tranfersResponse
						.setDestinyIdMovementAccount(Long.valueOf(resultDestino.get("idMovementAccount").toString()));
				success = true;
			}
		}
		if (!success) {
			this.accountService.delete(tranfersResponse.getOriginIdMovementAccount());
			this.accountService.delete(tranfersResponse.getDestinyIdMovementAccount());
			tranfersResponse.setOperationStatus(OperationStatus.insufficientBalance);
		}else {
			tranfersResponse.setOperationStatus(OperationStatus.successfulOperation);
		}
		kafkaTemplate.send(tranfersWalletTopicRespose, tranfersResponse);
		log.info("movementWalletConsumer [MovementWalletResponse] tarnfes status:" + tranfersResponse.toString());
		// kafkaTemplate.send(tranfersWalletTopicRespose, walletResponse);
	}

}
