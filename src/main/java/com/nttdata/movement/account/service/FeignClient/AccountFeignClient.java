package com.nttdata.movement.account.service.FeignClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.nttdata.movement.account.service.FeignClient.FallBackImpl.AccountFeignClientFallBack;
import com.nttdata.movement.account.service.model.Account;
import com.nttdata.movement.account.service.model.BankAccounts;

@FeignClient(name = "${api.account-service.uri}", fallback = AccountFeignClientFallBack.class)
public interface AccountFeignClient {
	
	@GetMapping("/{id}")
	BankAccounts accountFindById(@PathVariable("id") Long id);
	
}
