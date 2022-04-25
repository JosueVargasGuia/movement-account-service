package com.nttdata.movement.account.service.service;

import java.util.Map;

import com.nttdata.movement.account.service.entity.MovementAccount;
import com.nttdata.movement.account.service.model.Account;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface MovementAccountService {

	Flux<MovementAccount> findAll();

	Mono<MovementAccount> findById(Long id);

	Mono<MovementAccount> save(MovementAccount movementAccount);

	Mono<MovementAccount> update(MovementAccount movementAccount);

	Mono<Void> delete(Long id);

	Mono<Map<String, Object>> recordsMovement(MovementAccount movementAccount);

	Account findByIdAccount(Long idAccount);

	Mono<Map<String, Object>> balanceInquiry(Account account);

	Long generateKey(String nameTable);
}
