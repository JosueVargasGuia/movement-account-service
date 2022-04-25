package com.nttdata.movement.account.service.FeignClient.FallBackImpl;

import org.springframework.stereotype.Component;

import com.nttdata.movement.account.service.FeignClient.AccountFeignClient;
import com.nttdata.movement.account.service.model.Account;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
public class AccountFeignClientFallBack implements AccountFeignClient{

	@Override
	public Account accountFindById(Long id) {
		Account account = new Account();
		account.setIdAccount(Long.valueOf(-1));
		log.info("AccountFeignClientFallBack -> " + account);
		return account;
	}
}
