package fr.loul.sellpool;

public class ItemPool {

	public int max;
	public float max_price;
	public float min_price;
	public float actual;
	public float price;
	public int decr;
	public int am;
	public int i;
	public String name;
	
	public ItemPool(int maximum, double maxp, double minp, float act, int decrease, int amount, String n) {
		this.max = maximum;
		this.max_price = (float) maxp;
		this.min_price = (float) minp;
		this.actual = act;
		this.decr = decrease / 5;
		this.am = amount;
		this.i = 0;
		this.name = n;
		this.updatePrice();
	}
	
	public void poolIncr(int nb) {
		actual += nb;
		if (actual > max) {
			actual = max;
		}
	}
	
	public void poolDecr() {
		i++;
		if (i % decr != 0) return;
		if (actual > am) {
			actual = actual - am;
		}else {
			actual = 0;
		}
	}
	
	public float getMax() {
		return max;
	}
	
	public float getPool() {
		return actual;
	}
	
	public float getActualPrice() {
		return price;
	}
	
	public String getName() {
		return name;
	}
	
	public void updatePrice() {
		float price = ((max_price - min_price) * ((max - actual) / max) + min_price);
		this.price = Math.round(price * 100) / 100F;
	}
}
