package com.spring.form.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.spring.form.model.Donation;
import com.spring.form.service.DonationService;

@Component
public class DonationFormValidator implements Validator {

	@Autowired
	DonationService donationService;
	
	@Override
	public boolean supports(Class<?> clazz) {
		return Donation.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		@SuppressWarnings("unused")
		Donation donation = (Donation) target;
		
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "description", "NotEmpty.donationForm.description");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "value", "NotEmpty.donationForm.value");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "scheduledDate", "NotEmpty.donationForm.scheduledDate");
		
	}

}
