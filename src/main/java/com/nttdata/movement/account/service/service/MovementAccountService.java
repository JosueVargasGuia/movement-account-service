package com.nttdata.movement.account.service.service;

import java.util.Map;

import com.nttdata.movement.account.service.entity.MovementAccount;
import com.nttdata.movement.account.service.model.Account;
import com.nttdata.movement.account.service.model.BankAccounts;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface MovementAccountService {

	Flux<MovementAccount> findAll();

	Mono<MovementAccount> findById(Long idMovementAccount);

	Mono<MovementAccount> save(MovementAccount movementAccount);

	Mono<MovementAccount> update(MovementAccount movementAccount);

	Mono<Void> delete(Long idMovementAccount);

	Mono<Map<String, Object>> recordsMovement(MovementAccount movementAccount);

	BankAccounts findByIdAccount(Long idBankAccount);

	Mono<Map<String, Object>> balanceInquiry(BankAccounts bankAccounts);

	Long generateKey(String nameTable);
}
