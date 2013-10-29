package com.bpm.bpmpayment;

public class Producto extends ElementoGrid {
	private String idPrducto;

	public Producto(String nombre, String idPrducto, int pic) {
		super(nombre, pic);
		this.idPrducto = idPrducto;
	}

	public String getNombre() {
		return super.nombre;
	}
	
	public String getIdProducto() {
		return this.idPrducto;
	}
}
