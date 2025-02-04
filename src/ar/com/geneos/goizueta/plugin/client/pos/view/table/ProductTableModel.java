package ar.com.geneos.goizueta.plugin.client.pos.view.table;

import java.util.ArrayList;
import java.util.List;

import org.openXpertya.pos.view.PoSMsgRepository;
import org.openXpertya.pos.view.table.AbstractPoSTableModel;
import org.openXpertya.util.Util;

import ar.com.geneos.goizueta.plugin.client.pos.custom.CGOrderProduct;
import ar.com.geneos.goizueta.plugin.client.pos.model.OrderProduct;

public class ProductTableModel extends AbstractPoSTableModel {

	private static final String OP_CHECKOUT_POS = PoSMsgRepository.getInstance().getMsg("POS");
	private static final String OP_CHECKOUT_WAREHOUSE = PoSMsgRepository.getInstance().getMsg("M_Warehouse_ID");

	private List<OrderProduct> orderProducts;

	public ProductTableModel() {
		super();
		setOrderProducts(new ArrayList());
	}

	/**
	 * @param orderProducts
	 */
	public ProductTableModel(ArrayList<OrderProduct> orderProducts) {
		this();
		this.orderProducts = orderProducts;
	}
	
	public void setValueAt(Object value, int row, int column) {
		CGOrderProduct orderProduct = (CGOrderProduct)getOrderProducts().get(row);
		switch (column) {
		case 7:
			orderProduct.setSenderDetails((String)value);
		default:
			return;
		}
	  }
	
	public Object getValueAt(int row, int col) {
		OrderProduct orderProduct = getOrderProducts().get(row);
		switch (col) {
		case 0:
			return orderProduct.getCount();
		case 1:
			return orderProduct.getProduct().getDescription()
					+ (Util.isEmpty(orderProduct.getLineDescription(), true) ? "" : " - " + orderProduct.getLineDescription());
		case 2:
			return orderProduct.getTaxRate();
		case 3:
			return orderProduct.getTaxedPrice(true);
		case 4:
			return orderProduct.getTotalTaxedPrice(true);
		case 5:
			return orderProduct.getTotalTaxedPrice();
		case 6:
			if (orderProduct.isWarehouseCheckout()) {
				return OP_CHECKOUT_WAREHOUSE;
			} else {
				return OP_CHECKOUT_POS;
			}
		case 7:
			return ((CGOrderProduct)orderProduct).getSenderDetails();
		default:
			return null;
		}
	}

	@Override
	public List getObjects() {
		return getOrderProducts();
	}

	/**
	 * @return Devuelve orderProducts.
	 */
	public List<OrderProduct> getOrderProducts() {
		return orderProducts;
	}

	/**
	 * @param orderProducts
	 *            Fija o asigna orderProducts.
	 */
	public void setOrderProducts(List<OrderProduct> orderProducts) {
		this.orderProducts = orderProducts;
	}
}
