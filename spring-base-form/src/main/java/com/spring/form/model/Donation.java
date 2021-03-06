package com.spring.form.model;

import java.sql.Date;
import java.sql.Timestamp;

import org.springframework.web.multipart.MultipartFile;

public class Donation {
	// form:hidden - hidden value
	Integer id;

	// form:input - textbox
	Integer donor;

	// form:input - textbox
	String description;

	// form:input - textbox
	Double value;

	// form:input - textbox
	Date scheduledDate;

	// form:input - textbox
	Date completedDate;

	// form:input - textbox
	String address;

	// form:input - textbox
	String city;

	// form:select - form:option - dropdown - single select
	String province;

	// form:input - textbox
	String postalCode;

	// form:input - textbox
	Double dropFee;

	// form:input - textbox
	Integer receiver;

	// form:input - textbox
	Timestamp tacking;

	// form:checkbox - single checkbox
	boolean receipts;
	
	Integer numImages;

	private MultipartFile file1, file2, file3, file4;

	public MultipartFile getFile1() {
		return file1;
	}

	public void setFile1(MultipartFile file1) {
		this.file1 = file1;
	}
	
	public MultipartFile getFile2() {
		return file2;
	}

	public void setFile2(MultipartFile file2) {
		this.file2 = file2;
	}
	
	public MultipartFile getFile3() {
		return file3;
	}

	public void setFile3(MultipartFile file3) {
		this.file3 = file3;
	}
	
	public MultipartFile getFile4() {
		return file4;
	}

	public void setFile4(MultipartFile file4) {
		this.file4 = file4;
	}

	public boolean isNew() {
		return (this.id == null);
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getDonor() {
		return donor;
	}

	public void setDonor(Integer donor) {
		this.donor = donor;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Double getValue() {
		return value;
	}

	public void setValue(Double value) {
		this.value = value;
	}

	public Date getScheduledDate() {
		return scheduledDate;
	}

	public void setScheduledDate(Date scheduledDate) {
		this.scheduledDate = scheduledDate;
	}

	public Date getCompletedDate() {
		return completedDate;
	}

	public void setCompletedDate(Date completedDate) {
		this.completedDate = completedDate;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public Double getDropFee() {
		return dropFee;
	}

	public void setDropFee(Double dropFee) {
		this.dropFee = dropFee;
	}

	public Integer getReceiver() {
		return receiver;
	}

	public void setReceiver(Integer receiver) {
		this.receiver = receiver;
	}

	public Timestamp getTacking() {
		return tacking;
	}

	public void setTacking(Timestamp tacking) {
		this.tacking = tacking;
	}

	public boolean isReceipts() {
		return receipts;
	}

	public void setReceipts(boolean receipts) {
		this.receipts = receipts;
	}
	
	public Integer getNumImages() {
		return numImages;
	}

	public void setNumImages(Integer numImages) {
		this.numImages = numImages;
	}
	
	public void decreaseNumImages(Integer num) {
		numImages -= num;
	}
	
	@Override
	public String toString() {
		return "Donation [id=" + id + ", donor=" + donor + ", description=" + description + ", value=" + value
				+ ", scheduledDate=" + scheduledDate + ", completedDate=" + completedDate + ", address=" + address
				+ ", city=" + city + ", province=" + province + ", postalCode=" + postalCode + ", dropFee=" + dropFee
				+ ", receiver=" + receiver + ", tacking=" + tacking + ", receipts=" + receipts + ", numImages=" + numImages + "]" + isNew();
	}

}
