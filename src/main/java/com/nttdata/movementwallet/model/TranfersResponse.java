package com.nttdata.movementwallet.model;

import java.util.Map;
 

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class TranfersResponse {
	private Long idMovementWallet;
	private Long idOriginWallet;
	private String originPhoneNumber;
	private Long idDestinyWallet;
	private String destinyPhoneNumber;
	private TypeMovement typeMovement;
	private Double amount;
	private Long idCard;
	private Long originIdBankAccount;
	private Long destinyIdBankAccount;
	private Long originIdMovementAccount;
	private Long destinyIdMovementAccount;
	private Map<String, Object> mensaje;
	private OperationStatus operationStatus;
}
