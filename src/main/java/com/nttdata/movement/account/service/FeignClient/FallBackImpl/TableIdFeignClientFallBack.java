package com.nttdata.movement.account.service.FeignClient.FallBackImpl;

import org.springframework.stereotype.Component;

import com.nttdata.movement.account.service.FeignClient.TableIdFeignClient;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
public class TableIdFeignClientFallBack implements TableIdFeignClient {

	public Long generateKey(String nameTable) {
		log.error("[TableId -> FallBack]");
		return Long.valueOf(-1);
	}

}
