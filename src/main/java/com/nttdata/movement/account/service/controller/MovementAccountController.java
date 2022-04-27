package com.nttdata.movement.account.service.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.nttdata.movement.account.service.entity.MovementAccount; 
import com.nttdata.movement.account.service.model.BankAccounts;
import com.nttdata.movement.account.service.service.MovementAccountService;

import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Log4j2
@RestController
@RequestMapping("/movement-account")
public class MovementAccountController {

	@Autowired
	private MovementAccountService service;

	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public Flux<MovementAccount> getAll() {
		return service.findAll();
	}

	@GetMapping(value = "/{idMovementAccount}", produces = MediaType.APPLICATION_JSON_VALUE)
	public Mono<ResponseEntity<MovementAccount>> getOneMovementAccount(
			@PathVariable("idMovementAccount") Long idMovementAccount) {
		return service.findById(idMovementAccount).map(_movement -> ResponseEntity.ok().body(_movement))
				.onErrorResume(e -> {
					log.info("Error:" + e.getMessage());
					return Mono.just(ResponseEntity.badRequest().build());
				}).defaultIfEmpty(ResponseEntity.noContent().build());
	}

	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	public Mono<ResponseEntity<MovementAccount>> saveMovementAccount(@RequestBody MovementAccount movementAccount) {
		return service.save(movementAccount).map(_movement -> ResponseEntity.ok().body(_movement)).onErrorResume(e -> {
			log.info("Error:" + e.getMessage());
			return Mono.just(ResponseEntity.badRequest().build());
		});
	}

	@PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	public Mono<ResponseEntity<MovementAccount>> updateMovementAccount(@RequestBody MovementAccount movementAccount) {
		Mono<MovementAccount> objMovementAccount = service.findById(movementAccount.getIdMovementAccount())
				.flatMap(_movement -> {
					log.info("Update: [new] " + movementAccount + "\n[Old]: " + _movement);
					return service.update(movementAccount);
				});

		return objMovementAccount.map(_movementAccount -> {
			log.info("Status: " + HttpStatus.OK);
			return ResponseEntity.ok().body(_movementAccount);
		}).onErrorResume(e -> {
			log.info("Status: " + HttpStatus.BAD_REQUEST + " Message:  " + e.getMessage());
			return Mono.just(ResponseEntity.badRequest().build());
		}).defaultIfEmpty(ResponseEntity.noContent().build());
	}

	@DeleteMapping("/{idMovementAccount}")
	public Mono<ResponseEntity<Void>> deleteMovementAccount(@PathVariable("idMovementAccount") Long idMovementAccount) {
		return service.findById(idMovementAccount).flatMap(_movement -> {
			return service.delete(_movement.getIdMovementAccount()).then(Mono.just(ResponseEntity.ok().build()));
		});
	}

	@PostMapping(value = "/recordAccount")
	public Mono<ResponseEntity<Map<String, Object>>> recordAccount(@RequestBody MovementAccount movementAccount) {
		return service.recordsMovement(movementAccount).map(obj -> ResponseEntity.ok().body(obj)).onErrorResume(e -> {
			log.info("Status: " + HttpStatus.BAD_REQUEST + "\nMessage: " + e.getMessage());
			Map<String, Object> hashMap = new HashMap<>();
			hashMap.put("Error: ", e.getMessage());
			return Mono.just(ResponseEntity.badRequest().body(hashMap));
		}).defaultIfEmpty(ResponseEntity.noContent().build());
	}

	@PostMapping(value = "/balanceInquiry", consumes = MediaType.APPLICATION_JSON_VALUE)
	public Mono<ResponseEntity<Map<String, Object>>> balanceInquiry(@RequestBody BankAccounts bankAccounts) {
		return service.balanceInquiry(bankAccounts).map(act -> ResponseEntity.ok().body(act)).onErrorResume(e -> {
			log.info("Status: " + HttpStatus.BAD_REQUEST + " Message: " + e.getMessage());
			Map<String, Object> hashMap = new HashMap<>();
			hashMap.put("Error: ", e.getMessage());
			return Mono.just(ResponseEntity.badRequest().body(hashMap));
		}).defaultIfEmpty(ResponseEntity.noContent().build());
	}

}
