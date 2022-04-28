package com.nttdata.movement.account.service.model;

 

import org.springframework.data.annotation.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
 public final class BankAccounts extends Account{
	@Id
	private Long idBankAccount;
	private Long idProduct;
	//private Long idAccount;
}
