package com.nttdata.movement.account.service.entity;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection="movements_accounts")
public class MovementAccount {
	
	@Id
	private Long idMovementAccount;
	private Double amount;
	private Date dateMovementAccount;
	private TypeMovementAccount typeMovementAccount;
	private Long idAccount;
	
	@Override
	public String toString() {
		return "MovementAccount [idMovementAccount=" + idMovementAccount + ", amount=" + amount
				+ ", dateMovementAccount=" + dateMovementAccount + ", typeMovementAccount=" + typeMovementAccount
				+ ", idAccount=" + idAccount + "]";
	}

}
