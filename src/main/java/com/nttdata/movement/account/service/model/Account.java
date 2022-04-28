package com.nttdata.movement.account.service.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
 

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor 
@Data 
public abstract class Account {	 
	private Long idAccount;
	private Long idCustomer;
	private TypeOfCurrency typeOfCurrency;
	private String accountNumber;	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss") 
	private Date creationDate;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
	private Date dateModified;
}
//https://www.baeldung.com/jackson-jsonformat