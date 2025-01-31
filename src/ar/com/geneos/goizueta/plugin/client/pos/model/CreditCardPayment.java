package ar.com.geneos.goizueta.plugin.client.pos.model;

import java.math.BigDecimal;


public class CreditCardPayment extends Payment {

	private String creditCardNumber;

	private String couponNumber;
	
	private EntidadFinancieraPlan plan;

	private String bankName;
	
	private String posnet;
	
	private String couponBatchNumber;
	
	public CreditCardPayment() {
		super();
	}

	/**
	 * @param creditCardType
	 * @param creditCardNumber
	 * @param couponNumber
	 */
	public CreditCardPayment(EntidadFinancieraPlan plan, String creditCardNumber,
			String couponNumber, String bankName, String posnet, String couponBatchNumber,
			BigDecimal cashRetirementAmt) {
		this();
		this.plan = plan;
		this.creditCardNumber = creditCardNumber;
		this.couponNumber = couponNumber;
		this.bankName = bankName;
		this.posnet = posnet;
		this.couponBatchNumber = couponBatchNumber;
		setChangeAmt(cashRetirementAmt);
	}

	/**
	 * @return Devuelve couponNumber.
	 */
	public String getCouponNumber() {
		return couponNumber;
	}

	/**
	 * @param couponNumber
	 *            Fija o asigna couponNumber.
	 */
	public void setCouponNumber(String couponNumber) {
		this.couponNumber = couponNumber;
	}

	/**
	 * @return Devuelve creditCardNumber.
	 */
	public String getCreditCardNumber() {
		return creditCardNumber;
	}

	/**
	 * @param creditCardNumber
	 *            Fija o asigna creditCardNumber.
	 */
	public void setCreditCardNumber(String creditCardNumber) {
		this.creditCardNumber = creditCardNumber;
	}

	@Override
	public boolean isCreditCardPayment() {
		return true;
	}

	public EntidadFinancieraPlan getPlan() {
		return plan;
	}

	public void setPlan(EntidadFinancieraPlan plan) {
		this.plan = plan;
	}
	
	public int getEntidadFinancieraID() {
		return getPlan().getEntidadFinancieraID();
	}

	/**
	 * @return the bankName
	 */
	public String getBankName() {
		return bankName;
	}

	/**
	 * @param bankName the bankName to set
	 */
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	@Override
	public DiscountSchema getDiscountSchema() {
		return getPlan().getDiscountSchema();
	}

	@Override
	public IPaymentMediumInfo getPaymentMediumInfo() {
		return getPlan();
	}

	public void setPosnet(String posnet) {
		this.posnet = posnet;
	}

	public String getPosnet() {
		return posnet;
	}

	public String getCouponBatchNumber() {
		return couponBatchNumber;
	}

	public void setCouponBatchNumber(String couponBatchNumber) {
		this.couponBatchNumber = couponBatchNumber;
	}
}
