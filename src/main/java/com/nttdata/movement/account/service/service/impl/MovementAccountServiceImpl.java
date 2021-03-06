package com.nttdata.movement.account.service.service.impl;

import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.nttdata.movement.account.service.FeignClient.AccountFeignClient;
import com.nttdata.movement.account.service.FeignClient.TableIdFeignClient;
import com.nttdata.movement.account.service.entity.MovementAccount;
import com.nttdata.movement.account.service.entity.TypeMovementAccount;
import com.nttdata.movement.account.service.model.BankAccounts;
import com.nttdata.movement.account.service.repository.MovementAccountRepository;
import com.nttdata.movement.account.service.service.MovementAccountService;

import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Log4j2
@Service
public class MovementAccountServiceImpl implements MovementAccountService {

	@Autowired
	private MovementAccountRepository repository;

	@Autowired
	RestTemplate restTemplate;

	@Autowired
	AccountFeignClient accountFeignClient;

	@Autowired
	TableIdFeignClient tableIdFeignClient;

	@Override
	public Flux<MovementAccount> findAll() {
		return repository.findAll();
	}

	@Override
	public Mono<MovementAccount> findById(Long idMovementAccount) {
		return repository.findById(idMovementAccount);
	}

	@Override
	public Mono<MovementAccount> save(MovementAccount movementAccount) {
		// Long key = generateKey(MovementAccount.class.getSimpleName());
		// log.info("KEY->> " + key);
		Long count = this.findAll().collect(Collectors.counting()).blockOptional().get();
		Long idMovementCredit;
		if (count != null) {
			if (count <= 0) {
				idMovementCredit = Long.valueOf(0);
			} else {
				idMovementCredit = this.findAll()
						.collect(Collectors.maxBy(Comparator.comparing(MovementAccount::getIdMovementAccount)))
						.blockOptional().get().get().getIdMovementAccount();
			}
		} else {
			idMovementCredit = Long.valueOf(0);
		}

		movementAccount.setIdMovementAccount(idMovementCredit + 1);
		movementAccount.setCreationDate(Calendar.getInstance().getTime());
		log.info("SAVE [MovementAccount]: " + movementAccount.toString());
		// if (key >= 1) {

		// } else {
		// return Mono
		// .error(new InterruptedException("Servicio no disponible:" +
		// MovementAccount.class.getSimpleName()));
		// }

		return repository.insert(movementAccount);
	}

	@Override
	public Mono<MovementAccount> update(MovementAccount movementAccount) {
		return repository.save(movementAccount);
	}

	@Override
	public Mono<Void> delete(Long idMovementAccount) {
		return repository.deleteById(idMovementAccount);
	}

	@Override
	public Mono<Map<String, Object>> recordsMovement(MovementAccount movementAccount) {
		// metodo para registrar los movimientos de la cuenta
		log.info("recordsMovement[MovementAccount]:"+movementAccount);
		Map<String, Object> hashMap = new HashMap<String, Object>();
		BankAccounts account = this.findByIdAccount(movementAccount.getIdBankAccount());
		if (account != null) {
			if (movementAccount.getTypeMovementAccount() == TypeMovementAccount.withdrawal) {
				
				Double _saldo= this.findAll().filter(c -> (c.getIdBankAccount() == movementAccount.getIdBankAccount()))
						.map(mov -> {
							//log.info("Amount:" + mov.getAmount());
							if (mov.getTypeMovementAccount() == TypeMovementAccount.withdrawal) {
								mov.setAmount(-1 * mov.getAmount());
							}
							return mov;
						})
						.collect(Collectors.summingDouble(MovementAccount::getAmount))
						 .blockOptional().get();
						log.info("Saldo[recordsMovement]:"+_saldo);
						//.collect(Collectors.summingDouble(MovementAccount::getAmount))
						
						//.map(_saldo -> {
							if (movementAccount.getAmount() <= (_saldo)) {
								movementAccount.setDateMovementAccount(Calendar.getInstance().getTime());
								//Mono<MovementAccount> tmp = this.save(movementAccount);
								//tmp.subscribe();

								MovementAccount ObjMovementAccount = this.save(movementAccount).blockOptional().get();
								// .subscribe(e -> log.info("Movimiento de retiro registrado: " +
								// e.toString()));
								hashMap.put("AccountSuccess: ", "Registro de movimiento de retiro. Valor retirado: "
										+ movementAccount.getAmount());
								hashMap.put("status", "success");
								hashMap.put("MovementAccount", ObjMovementAccount);
								hashMap.put("idMovementAccount", ObjMovementAccount.getIdMovementAccount());
								log.info("Saldo disponible: " + (_saldo - movementAccount.getAmount()));
								return Mono.just(hashMap);
							} else {
								hashMap.put("status", "error");
								hashMap.put("MessageAccount",
										"No cuenta con saldo suficiente para retiro. Saldo disponible: " + (_saldo));
								log.info("No cuenta con saldo suficiente para retiro." + " Saldo disponible: "
										+ (_saldo));
								return Mono.just(hashMap);
							}
							
						//});*/
				 //return null;
			} else {
				movementAccount.setDateMovementAccount(Calendar.getInstance().getTime());
				return this.save(movementAccount).map(_value -> {
					hashMap.put("AccountSuccess: ",
							"Registro de movimiento de dep??sito. Valor depositado: " + _value.getAmount());
					log.info("Registro de movimiento de dep??sito. Valor depositado: " + _value.getAmount());
					hashMap.put("status", "success");
					hashMap.put("idMovementAccount", _value.getIdMovementAccount());
					hashMap.put("MovementAccount", _value);
					return hashMap;
				});

			}
		} else {
			hashMap.put("MessageAccount", "Error. Cuenta no existe.");
			hashMap.put("status", "error");
			return Mono.just(hashMap);
		}

	}

	@Override
	public BankAccounts findByIdAccount(Long idBankAccount) {
		// buscar la cuenta donde se realizan los movimientos
		BankAccounts accountBankAccounts = accountFeignClient.accountFindById(idBankAccount);
		log.info("Account -> " + accountBankAccounts);
		return accountBankAccounts;
	}

	@Override
	public Mono<Map<String, Object>> balanceInquiry(BankAccounts bankAccounts) {
		// metodo para la consulta de saldo en la cuenta
		Map<String, Object> hashMap = new HashMap<String, Object>();

		BankAccounts _account = this.findByIdAccount(bankAccounts.getIdBankAccount());
		// log.info("Account encontrada: "+ _account.toString());
		if (_account != null) { // act obj MovementAccount
			return this.findAll().filter(act -> (act.getIdBankAccount() == _account.getIdBankAccount())).map(mov -> {
				if (mov.getTypeMovementAccount() == TypeMovementAccount.withdrawal) {
					mov.setAmount(-1 * mov.getAmount());
				}
				return mov;
			}).collect(Collectors.summingDouble(MovementAccount::getAmount)).map(_value -> {
				log.info("Consulta de saldo: " + _value);
				return _value;
			}).map(value -> {
				hashMap.put("StatusBalance", "El saldo de la cuenta es de:" + value);
				hashMap.put("accountBalance", value);
				hashMap.put("Account", _account);
				hashMap.put("status", "success");
//hashMap.put("creditBalance", value);
				log.info("Account", bankAccounts);
				return hashMap;
			});
		} else {
			hashMap.put("status", "error");
			hashMap.put("Account", "Cuenta corriente no existe.");
			return Mono.just(hashMap);
		}
	}

	@Override
	public Long generateKey(String nameTable) {
		/*
		 * log.info(tableIdService + "/generateKey/" + nameTable); ResponseEntity<Long>
		 * responseGet = restTemplate.exchange(tableIdService + "/generateKey/" +
		 * nameTable, HttpMethod.GET, null, new ParameterizedTypeReference<Long>() { });
		 * if (responseGet.getStatusCode() == HttpStatus.OK) { //log.info("Body:"+
		 * responseGet.getBody()); return responseGet.getBody(); } else { return
		 * Long.valueOf(0); }
		 */

		return tableIdFeignClient.generateKey(nameTable);
	}
}